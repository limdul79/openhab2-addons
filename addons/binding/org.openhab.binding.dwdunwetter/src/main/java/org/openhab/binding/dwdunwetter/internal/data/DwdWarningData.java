/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * Data for one warning.
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdWarningData {

    private String id;

    private Severity severity;
    private String description;
    private long effective;
    private long expires;
    private long onset;
    private String event;
    private String status;
    private String msgType;
    private String headline;
    private BigDecimal altitude;
    private BigDecimal ceiling;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Severity getSeverity() {
        return severity == null ? Severity.UNKNOWN : severity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEffective(long effective) {
        this.effective = effective;
    }

    public long getEffective() {
        return effective;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getExpires() {
        return expires;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTest() {
        return StringUtils.equalsIgnoreCase(status, "Test");
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public boolean isCancel() {
        return StringUtils.equalsIgnoreCase(msgType, "Cancel");
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getHeadline() {
        return headline;
    }

    public long getOnset() {
        return onset;
    }

    public void setOnset(long onset) {
        this.onset = onset;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setCeiling(BigDecimal ceiling) {
        this.ceiling = ceiling;
    }

    public BigDecimal getCeiling() {
        return ceiling;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DwdWarningData other = (DwdWarningData) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
