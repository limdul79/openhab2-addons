/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Util Class for Date conversion which a specific to the data provided by the endpoint.
 *
 * @author Martin Koehler - Initial contribution
 */
public class VrsMonitorDateUtil {

    private static final String REGEX_GROUP = "([0-9]{2})";

    private VrsMonitorDateUtil() {
        // Utility Class
    }

    public static LocalDateTime pareLastUpdated(@Nullable String input) {
        if (StringUtils.isBlank(input)) {
            return LocalDateTime.MIN;
        }
        StringBuilder b = new StringBuilder();
        b.append(REGEX_GROUP) //
                .append("\\.") //
                .append(REGEX_GROUP) //
                .append("\\. ") //
                .append(REGEX_GROUP) //
                .append(":") //
                .append(REGEX_GROUP) //
                .append(":") //
                .append(REGEX_GROUP);
        Pattern p = Pattern.compile(b.toString());
        Matcher matcher = p.matcher(input);
        if (!matcher.matches()) {
            return LocalDateTime.MIN;
        }
        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = LocalDateTime.now().getYear();
        int hour = Integer.parseInt(matcher.group(3));
        int minute = Integer.parseInt(matcher.group(4));
        int second = Integer.parseInt(matcher.group(5));
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

}
