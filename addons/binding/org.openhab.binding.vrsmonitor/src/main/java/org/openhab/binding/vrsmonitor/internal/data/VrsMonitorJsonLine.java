/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal.data;

/**
 * The parsed Json Data.
 *
 * @author Martin Koehler - Initial contribution
 */
public class VrsMonitorJsonLine {

    private String number;
    private String product;
    private String direction;

    public String getDirection() {
        return direction;
    }

    public String getNumber() {
        return number;
    }

    public String getProduct() {
        return product;
    }

}
