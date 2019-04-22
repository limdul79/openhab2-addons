package org.openhab.binding.zigbee2mqtt.internal;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.mqtt.generic.MqttChannelStateDescriptionProvider;
import org.openhab.binding.zigbee2mqtt.Zigbee2MqttBindingConstants;
import org.openhab.binding.zigbee2mqtt.internal.handler.BulbThingHandler;

public class Zigbee2MqttThingHandlerFactory extends BaseThingHandlerFactory {

	private @NonNullByDefault({}) MqttChannelStateDescriptionProvider stateDescriptionProvider;
    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream
            .of(Zigbee2MqttBindingConstants.BULB_THING).collect(Collectors.toSet());

	@Override
	public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
	}

	@Override
	protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(Zigbee2MqttBindingConstants.BULB_THING)) {
            return new BulbThingHandler(thing, stateDescriptionProvider, 1500);
        }
        return null;
	}

}
