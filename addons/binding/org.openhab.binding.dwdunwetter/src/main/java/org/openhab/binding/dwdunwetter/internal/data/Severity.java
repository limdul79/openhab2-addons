/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * Severity enum to make the severity comparable
 *
 * @author Martin Koehler - Initial contribution
 */
public enum Severity {

    EXTREME(1, "Extreme"),
    SEVERE(2, "Severe"),
    MODERATE(3, "Moderate"),
    MINOR(4, "Minor"),
    UNKNOWN(5, "Unbekannt");

    private int order;
    private String text;

    private Severity(int order, String text) {
        this.order = order;
        this.text = text;
    }

    public int getOrder() {
        return order;
    }

    public String getText() {
        return text;
    }

    public static Severity getSeverity(String input) {
        return Arrays.asList(Severity.values()).stream()
                .filter(sev -> StringUtils.equalsIgnoreCase(input, sev.getText())).findAny().orElse(UNKNOWN);
    }

}
