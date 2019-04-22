package org.openhab.binding.zigbee2mqtt.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Zigbee2MqttThingConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault
public class Zigbee2MqttThingConfiguration {
	
	private String topic;
	
	
	public String getTopic() {
		return topic;
	}

}
