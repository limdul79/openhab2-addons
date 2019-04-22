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

/**
 *
 * @author Martin Koehler
 *
 */
public class ColorRGB {

    private int red = 255;
    private int green = 255;
    private int blue = 255;

    public ColorRGB(int r, int g, int b) {
        // Preconditions.checkArgument(r >= 0 && r <= 255, "r not in range [0;255]: " + r);
        // Preconditions.checkArgument(g >= 0 && g <= 255, "g not in range [0;255]: " + g);
        // Preconditions.checkArgument(b >= 0 && b <= 255, "b not in range [0;255]: " + b);
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

}
