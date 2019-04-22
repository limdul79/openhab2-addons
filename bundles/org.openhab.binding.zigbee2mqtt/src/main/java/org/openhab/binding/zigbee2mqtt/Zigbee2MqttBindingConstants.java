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
package org.openhab.binding.zigbee2mqtt;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 *
 * @author Martin Koehler
 *
 */
public class Zigbee2MqttBindingConstants {

    public static final String BINDING_ID = "zigbee2mqtt";

    // List of all Thing Type UIDs
    public static final ThingTypeUID BULB_THING = new ThingTypeUID(BINDING_ID, "bulb");

}
