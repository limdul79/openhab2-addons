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
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link VrsMonitorConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault
public class VrsMonitorConfiguration {

    /**
     * Show ID of the Departure monitor.
     */
    public @Nullable String showId = "";

    /**
     * Refresh in Seconds
     */
    public @Nullable Integer refresh = 69;

    /**
     * Number of departures
     */
    public @Nullable Integer departureCount = 1;

    public String getVrsShowId() {
        return showId != null ? showId : "";
    }

    /**
     * Returns the Refresh in Seconds.
     *
     * @return The Refresh in Seconds
     */
    public int getRefresh() {
        return refresh != null ? refresh.intValue() : 60;
    }

    /**
     * Return the number of departures to provide.
     *
     * @return The number of departures to provide.
     */
    public Integer getDepartureCount() {
        return departureCount != null ? departureCount.intValue() : 1;
    }
}
