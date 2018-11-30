/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal.data;

import org.apache.commons.lang.StringUtils;

/**
 * The parsed Json Data.
 *
 * @author Martin Koehler - Initial contribution
 */
public class VrsMonitorJsonDeparture {

    private String estimate;
    private String timetable;
    private Long timestamp;
    private Boolean delayed;
    private Integer day;

    public String getEstimate() {
        return StringUtils.isEmpty(estimate) ? timetable : estimate;
    }

    public String getTimetable() {
        return timetable;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean getDelayed() {
        if (delayed == null) {
            return false;
        }
        return delayed.booleanValue();
    }

    public Integer getDay() {
        return day;
    }

}
