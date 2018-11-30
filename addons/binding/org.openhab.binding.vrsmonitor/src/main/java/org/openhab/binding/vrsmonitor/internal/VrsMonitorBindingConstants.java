/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link VrsMonitorBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault
public class VrsMonitorBindingConstants {

    /**
     * Id of the binding.
     */
    public static final String BINDING_ID = "vrsmonitor";

    // List of all Thing Type UIDs
    /**
     * The departure monitor thing.
     */
    public static final ThingTypeUID THING_TYPE = new ThingTypeUID(BINDING_ID, "departuremonitor");

    // List of all Channel Type IDs
    /**
     * Channel Typ for the Channel containing the last updated Time
     */
    public static final String UPDATED_TYPE_ID = "updatedType";

    // List of alle Channel Type IDs where a channel is created for every Depature
    /**
     * Channel Typ for the Channel containing the direction.
     */
    public static final String DIRECTION_TYPE_ID = "direction";

    /**
     * Channel Typ for the Channel containing the switch if the departure is delayed.
     */
    public static final String DELAYED_TYPE_ID = "delayed";

    /**
     * Channel Typ for the Channel containing the estimated time of departure
     */
    public static final String ESTIMATE_TYPE_ID = "estimate";

    /**
     * Channel Typ for the Channel containing the planned time of departure.
     */
    public static final String TIMETABLE_TYPE_ID = "timetable";

    /**
     * Channel Typ for the Channel containing the timestamp
     */
    public static final String TIMESTAMP_TYPE_ID = "timestamp";

    /**
     * Channel Typ for the Channel containing the line number
     */
    public static final String LINE_NUMBER_TYPE_ID = "lineNumber";

    /**
     * Channel Typ for the Channel containing the line product
     */
    public static final String LINE_PRODUCT_TYPE_ID = "lineProduct";

    /**
     * Channel Typ for the Channel containing the stop id
     */
    public static final String STOP_ID_TYPE_ID = "stopId";

    /**
     * Channel Typ for the Channel containing the stop name
     */
    public static final String STOP_NAME_TYPE_ID = "stopName";
}
