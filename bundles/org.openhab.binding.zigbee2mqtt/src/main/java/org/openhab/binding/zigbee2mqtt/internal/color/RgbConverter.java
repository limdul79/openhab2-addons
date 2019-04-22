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
 * This class is internaly using double (and not {@link BigDecimal} because {@link BigDecimal#pow(int)} doesn't support
 * fractional exponents. And as the results are rounded double provides enough precisions as this are only color values.
 *
 * @author Martin Koehler
 *
 */
public class RgbConverter {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private RgbConverter() {
        // Should not be instanzied
    }

    public static ColorXY rgbToXY(ColorRGB rgb, int scale) {
        double r = pivotValue(rgb.getRed() / 255.0);
        double g = pivotValue(rgb.getGreen() / 255.0);
        double b = pivotValue(rgb.getBlue() / 255.0);

        double x = r * 0.4124 + g * 0.3576 + b * 0.1805;
        double y = r * 0.2126 + g * 0.7152 + b * 0.0722;
        double z = r * 0.0193 + g * 0.1192 + b * 0.9505;

        return convertToXy(x, y, z, scale);
    }

    public static ColorRGB xyToRGB(ColorXY xy, int brightness, int scale) {
        if (isAlmostZero(xy.getX(), scale) && isAlmostZero(xy.getY(), scale)) {
            return new ColorRGB(255, 255, 255);
        }
        double y = brightness / 255.0 * 100;
        double x = xy.getX().doubleValue() * (y / xy.getY().doubleValue());
        double z = (1.0 - xy.getX().doubleValue() - xy.getY().doubleValue()) * (y / xy.getY().doubleValue());

        return xyzToRGB(x, y, z);

    }

    private static ColorRGB xyzToRGB(double x, double y, double z) {
        double xScaled = x / 100.0;
        double yScaled = y / 100.0;
        double zScaled = z / 100.0;

        double r = xScaled * 3.2406 + yScaled * -1.5372 + zScaled * -0.4986;
        double g = xScaled * -0.9689 + yScaled * 1.8758 + zScaled * 0.0415;
        double b = xScaled * 0.0557 + yScaled * -0.2040 + zScaled * 1.0570;

        r = r > 0.0031308 ? 1.055 * Math.pow(r, 1 / 2.4) - 0.055 : 12.92 * r;
        g = g > 0.0031308 ? 1.055 * Math.pow(g, 1 / 2.4) - 0.055 : 12.92 * g;
        b = b > 0.0031308 ? 1.055 * Math.pow(b, 1 / 2.4) - 0.055 : 12.92 * b;

        return new ColorRGB(scaleToRgb255(r), scaleToRgb255(g), scaleToRgb255(b));
    }

    private static int scaleToRgb255(double value) {
        int intValue = (int) Math.round(255.0 * value);
        return intValue < 0 ? 0 : //
                intValue > 255 ? 255 : intValue;
    }

    private static boolean isAlmostZero(BigDecimal value, int scale) {
        return isAlmostZero(value.setScale(scale, ROUNDING_MODE).doubleValue(), scale);
    }

    private static boolean isAlmostZero(double value, int scale) {
        return Math.abs(value) < (1.0 / Math.pow(10, scale));
    }

    private static ColorXY convertToXy(double inX, double inY, double inZ, int scale) {
        double dividend = inX + inY + inZ;
        if (isAlmostZero(dividend, scale)) {
            return new ColorXY(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal x = new BigDecimal(inX / dividend).setScale(scale, ROUNDING_MODE);
        BigDecimal y = new BigDecimal(inY / dividend).setScale(scale, ROUNDING_MODE);
        return new ColorXY(x, y);
    }

    private static final double pivotValue(double value) {
        return (value > 0.04045 ? Math.pow((value + 0.055) / 1.055, 2.4) : value / 12.92) * 100.0;
    }

}
