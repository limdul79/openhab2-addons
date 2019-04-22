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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Examples are taken from https://www.easyrgb.com/en/convert.php
 *
 * @author Martin Koehler
 *
 */
public class RgbConverterTest {

    @Test
    public void testConvertWhiteToXY() {
        ColorRGB white = new ColorRGB(255, 255, 255);

        ColorXY xy = RgbConverter.rgbToXY(white, 4);

        assertThat(xy.getX().toString(), is("0.3127"));
        assertThat(xy.getY().toString(), is("0.3290"));
    }

    @Test
    public void testConvertWhiteToRGB() {
        ColorXY white = new ColorXY(new BigDecimal("0.3127"), new BigDecimal("0.3290"));

        ColorRGB rgb = RgbConverter.xyToRGB(white, 255, 4);

        assertThat(rgb.getRed(), is(255));
        assertThat(rgb.getGreen(), is(255));
        assertThat(rgb.getBlue(), is(255));
    }

    @Test
    public void testConvertRedToXY() {
        ColorRGB red = new ColorRGB(255, 0, 0);

        ColorXY xy = RgbConverter.rgbToXY(red, 4);

        assertThat(xy.getX().toString(), is("0.6401"));
        assertThat(xy.getY().toString(), is("0.3300"));
    }

    @Test
    public void testConvertRedToRGB() {
        ColorXY red = new ColorXY(new BigDecimal("0.6401"), new BigDecimal("0.3300"));

        ColorRGB rgb = RgbConverter.xyToRGB(red, 255, 4);

        assertThat(rgb.getRed(), is(255));
        // Rounding Problems
        assertThat(rgb.getGreen(), is(1));
        assertThat(rgb.getBlue(), is(0));
    }

    @Test
    public void testConvertGreenToXY() {
        ColorRGB green = new ColorRGB(0, 255, 0);

        ColorXY xy = RgbConverter.rgbToXY(green, 4);

        assertThat(xy.getX().toString(), is("0.3000"));
        assertThat(xy.getY().toString(), is("0.6000"));
    }

    @Test
    public void testConvertGreenToRGB() {
        ColorXY green = new ColorXY(new BigDecimal("0.3000"), new BigDecimal("0.6000"));

        ColorRGB rgb = RgbConverter.xyToRGB(green, 255, 4);

        assertThat(rgb.getRed(), is(0));
        assertThat(rgb.getGreen(), is(255));
        assertThat(rgb.getBlue(), is(0));
    }

    @Test
    public void testConvertBlueToXY() {
        ColorRGB blue = new ColorRGB(0, 0, 255);

        ColorXY xy = RgbConverter.rgbToXY(blue, 4);

        assertThat(xy.getX().toString(), is("0.1500"));
        assertThat(xy.getY().toString(), is("0.0600"));
    }

    @Test
    public void testConvertBlueToRGB() {
        ColorXY blue = new ColorXY(new BigDecimal("0.1500"), new BigDecimal("0.0600"));

        ColorRGB rgb = RgbConverter.xyToRGB(blue, 255, 4);

        assertThat(rgb.getRed(), is(0));
        assertThat(rgb.getGreen(), is(0));
        assertThat(rgb.getBlue(), is(255));
    }
}
