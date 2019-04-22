/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.zigbee2mqtt.internal.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.openhab.binding.mqtt.generic.AbstractMQTTThingHandler;
import org.openhab.binding.mqtt.generic.ChannelConfig;
import org.openhab.binding.mqtt.generic.ChannelState;
import org.openhab.binding.mqtt.generic.ChannelStateTransformation;
import org.openhab.binding.mqtt.generic.MqttChannelStateDescriptionProvider;
import org.openhab.binding.mqtt.generic.TransformationServiceProvider;
import org.openhab.binding.mqtt.generic.internal.handler.GenericMQTTThingHandler;
import org.openhab.binding.mqtt.generic.values.Value;
import org.openhab.binding.mqtt.generic.values.ValueFactory;
import org.openhab.binding.zigbee2mqtt.internal.Zigbee2MqttThingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Koehler
 *
 */
public class BulbThingHandler extends AbstractMQTTThingHandler {

    private Zigbee2MqttThingConfiguration config;

    private final Logger logger = LoggerFactory.getLogger(GenericMQTTThingHandler.class);
    final Map<ChannelUID, ChannelState> channelStateByChannelUID = new HashMap<>();
    protected final MqttChannelStateDescriptionProvider stateDescProvider;

    /**
     * Creates a new Thing handler for generic MQTT channels.
     *
     * @param thing The thing of this handler
     * @param stateDescProvider A channel state provider
     * @param transformationServiceProvider The transformation service provider
     * @param subscribeTimeout The subscribe timeout
     */
    public BulbThingHandler(Thing thing, MqttChannelStateDescriptionProvider stateDescProvider,
            int subscribeTimeout) {
        super(thing, subscribeTimeout);
        this.stateDescProvider = stateDescProvider;
    }

    @Override
    public @Nullable ChannelState getChannelState(ChannelUID channelUID) {
        return channelStateByChannelUID.get(channelUID);
    }

    /**
     * Subscribe on all channel static topics on all {@link ChannelState}s.
     * If subscribing on all channels worked, the thing is put ONLINE, else OFFLINE.
     *
     * @param connection A started broker connection
     */
    @Override
    protected CompletableFuture<@Nullable Void> start(MqttBrokerConnection connection) {
        List<CompletableFuture<@Nullable Void>> futures = channelStateByChannelUID.values().stream()
                .map(c -> c.start(connection, scheduler, 0)).collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenRun(() -> {
            updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE);
        });
    }

    @Override
    protected void stop() {
        channelStateByChannelUID.values().forEach(c -> c.getCache().resetState());
    }

    @Override
    public void dispose() {
        // Stop all MQTT subscriptions
        try {
            channelStateByChannelUID.values().stream().map(e -> e.stop())
                    .reduce(CompletableFuture.completedFuture(null), (a, v) -> a.thenCompose(b -> v))
                    .get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignore) {
        }
        // Remove all state descriptions of this handler
        channelStateByChannelUID.forEach((uid, state) -> stateDescProvider.remove(uid));
        connection = null;
        channelStateByChannelUID.clear();
        super.dispose();
    }

    /**
     * For every Thing channel there exists a corresponding {@link ChannelState}. It consists of the MQTT state
     * and MQTT command topic, the ChannelUID and a value state.
     *
     * @param channelConfig The channel configuration that contains MQTT state and command topic and multiple other
     *            configurations.
     * @param channelUID The channel UID
     * @param valueState The channel value state
     * @return
     */
    protected ChannelState createChannelState(ChannelConfig channelConfig, ChannelUID channelUID, Value valueState) {
        ChannelState state = new ChannelState(channelConfig, channelUID, valueState, this);
        return state;
    }

    @Override
    public void initialize() {
    	config = getConfigAs(Zigbee2MqttThingConfiguration.class);
        List<ChannelUID> configErrors = new ArrayList<>();
        for (Channel channel : thing.getChannels()) {
            final ChannelTypeUID channelTypeUID = channel.getChannelTypeUID();
            if (channelTypeUID == null) {
                logger.warn("Channel {} has no type", channel.getLabel());
                continue;
            }
            final ChannelConfig channelConfig = channel.getConfiguration().as(ChannelConfig.class);
            try {
                Value value = ValueFactory.createValueState(channelConfig, channelTypeUID.getId());
                ChannelState channelState = createChannelState(channelConfig, channel.getUID(), value);
                channelStateByChannelUID.put(channel.getUID(), channelState);
                StateDescription description = value.createStateDescription(channelConfig.unit,
                        StringUtils.isBlank(channelConfig.commandTopic));
                stateDescProvider.setDescription(channel.getUID(), description);
            } catch (IllegalArgumentException e) {
                logger.warn("Channel configuration error", e);
                configErrors.add(channel.getUID());
            }
        }

        // If some channels could not start up, put the entire thing offline and display the channels
        // in question to the user.
        if (configErrors.isEmpty()) {
            super.initialize();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Remove and recreate: "
                    + configErrors.stream().map(e -> e.getAsString()).collect(Collectors.joining(",")));
        }
    }
}
