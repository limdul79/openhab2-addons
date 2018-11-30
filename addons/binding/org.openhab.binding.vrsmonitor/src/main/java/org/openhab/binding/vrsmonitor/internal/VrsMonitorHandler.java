/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import static org.openhab.binding.vrsmonitor.internal.VrsMonitorBindingConstants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VrsMonitorHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault({ DefaultLocation.FIELD, DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })
public class VrsMonitorHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(VrsMonitorHandler.class);

    private @Nullable VrsMonitorConfiguration config;

    private VrsMonitorData data = new VrsMonitorData();

    private @Nullable ScheduledFuture<?> refreshJob;

    public VrsMonitorHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            refresh();
        }
    }

    private void refresh() {
        try {
            data.refresh(getVrsConfig().getVrsShowId());
            updateState(getChannelUuid(UPDATED_TYPE_ID), data.getLastUpdated());
            int departureCount = getVrsConfig().getDepartureCount();
            for (int i = 0; i < departureCount; i++) {
                updateState(getChannelUuid(DIRECTION_TYPE_ID, i), data.getDirection(i));
                updateState(getChannelUuid(DELAYED_TYPE_ID, i), data.isDelayed(i));
                updateState(getChannelUuid(ESTIMATE_TYPE_ID, i), data.getEstimate(i));
                updateState(getChannelUuid(TIMETABLE_TYPE_ID, i), data.getTimetable(i));
                updateState(getChannelUuid(TIMESTAMP_TYPE_ID, i), data.getTimestamp(i));
                updateState(getChannelUuid(LINE_NUMBER_TYPE_ID, i), data.getLineNumber(i));
                updateState(getChannelUuid(LINE_PRODUCT_TYPE_ID, i), data.getLineProduct(i));
                updateState(getChannelUuid(STOP_ID_TYPE_ID, i), data.getStopId(i));
                updateState(getChannelUuid(STOP_NAME_TYPE_ID, i), data.getStopName(i));
            }
        } catch (Exception e) {
            logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
        }

    }

    /**
     * Gets the config. If there is no config, return a Default Config.
     *
     * @return The Config.
     */
    private VrsMonitorConfiguration getVrsConfig() {
        return config != null ? config : new VrsMonitorConfiguration();
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        updateStatus(ThingStatus.UNKNOWN);
        config = getConfigAs(VrsMonitorConfiguration.class);

        List<Channel> channels = createChannels();
        updateThing(editThing().withChannels(channels).build());

        startAutomaticRefresh();

        updateStatus(ThingStatus.ONLINE);
        logger.debug("Finished initializing!");
    }

    /**
     * Starts the automatic refresh.
     */
    private void startAutomaticRefresh() {
        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                refresh();
            } catch (Exception e) {
                logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
            }
        }, 0, getVrsConfig().getRefresh(), TimeUnit.SECONDS);
    }

    /**
     * Create the channels for the departures.
     *
     * @return The List of Channels
     */
    private List<Channel> createChannels() {
        List<Channel> channels = new LinkedList<>();
        Optional<Channel> channel = Optional.ofNullable(getThing().getChannel("updated"));
        channel.ifPresent(channels::add);
        int departureCount = getVrsConfig().getDepartureCount();
        channels.add(createChannel(UPDATED_TYPE_ID, "DateTime", "Last updated"));
        for (int i = 0; i < departureCount; i++) {
            channels.add(createChannel(DIRECTION_TYPE_ID, "String", "Richtung", i));
            channels.add(createChannel(DELAYED_TYPE_ID, "Switch", "Verspätet", i));
            channels.add(createChannel(ESTIMATE_TYPE_ID, "String", "Abfahrt", i));
            channels.add(createChannel(TIMETABLE_TYPE_ID, "String", "Plan", i));
            channels.add(createChannel(TIMESTAMP_TYPE_ID, "DateTime", "Timestamp", i));
            channels.add(createChannel(LINE_NUMBER_TYPE_ID, "String", "Line Number", i));
            channels.add(createChannel(LINE_PRODUCT_TYPE_ID, "String", "Line Product", i));
            channels.add(createChannel(STOP_ID_TYPE_ID, "String", "Stop Id", i));
            channels.add(createChannel(STOP_NAME_TYPE_ID, "String", "Stop name", i));
        }
        return channels;
    }

    /**
     * Creates a channel.
     *
     * @param typeId          The Channel Typ
     * @param itemType        The item Typ
     * @param label           The Label
     * @param departureNumber The departure number
     * @return The created Channel
     */
    private Channel createChannel(String typeId, String itemType, String label) {
        ChannelUID channelUID = getChannelUuid(typeId);
        return ChannelBuilder.create(channelUID, itemType) //
                .withType(new ChannelTypeUID(BINDING_ID, typeId)) //
                .withLabel(label)//
                .build();
    }

    /**
     * Creates a channel for a departure.
     *
     * @param typeId          The Channel Typ
     * @param itemType        The item Typ
     * @param label           The Label
     * @param departureNumber The departure number
     * @return The created Channel
     */
    private Channel createChannel(String typeId, String itemType, String label, int departureNumber) {
        ChannelUID channelUID = getChannelUuid(typeId, departureNumber);
        return ChannelBuilder.create(channelUID, itemType) //
                .withType(new ChannelTypeUID(BINDING_ID, typeId)) //
                .withLabel(label + " (" + (departureNumber + 1) + ")")//
                .build();
    }

    /**
     * Gets the Channel-UUID for the Channel Typ and Departure Number.
     *
     * @param typeId          The Channel Type.
     * @param departureNumber The departure Number
     * @return The Channel-UUID.
     */
    private ChannelUID getChannelUuid(String typeId, int departureNumber) {
        return new ChannelUID(getThing().getUID(), typeId + (departureNumber + 1));
    }

    /**
     * Gets the Channel-UUID for the Channel Typ
     *
     * @param typeId The Channel Type.
     * @return The Channel-UUID.
     */
    private ChannelUID getChannelUuid(String typeId) {
        return new ChannelUID(getThing().getUID(), typeId);
    }

    @Override
    public void dispose() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
        }
        super.dispose();
    }
}
