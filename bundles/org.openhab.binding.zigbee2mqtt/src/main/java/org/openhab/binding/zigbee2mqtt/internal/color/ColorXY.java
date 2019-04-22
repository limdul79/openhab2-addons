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
package org.openhab.binding.zigbee2mqtt.internal.color;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Martin Koehler
 *
 */
public class ColorXY {

    private BigDecimal x = BigDecimal.ZERO;
    private BigDecimal y = BigDecimal.ZERO;

    public ColorXY() {
    }

    public ColorXY(BigDecimal x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getXRounded(int scale) {
        return x.setScale(scale, RoundingMode.HALF_UP);
    }

    public BigDecimal getYRounded(int scale) {
        return y.setScale(scale, RoundingMode.HALF_UP);
    }

    public ColorXY getRounded(int scale) {
        return new ColorXY(getXRounded(scale), getYRounded(scale));
    }

}
