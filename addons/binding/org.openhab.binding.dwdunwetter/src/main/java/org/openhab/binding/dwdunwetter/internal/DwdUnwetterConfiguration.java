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
package org.openhab.binding.dwdunwetter.internal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link DwdUnwetterConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdUnwetterConfiguration {

    private @Nullable Integer refresh = 15;

    private @Nullable Integer warningCount = 2;

    private @Nullable String cellId = null;

    /**
     * Returns the Refresh in minutes.
     *
     * @return The refresh in Minutes
     */
    public int getRefresh() {
        return refresh != null ? refresh.intValue() : 15;
    }

    /**
     * Returns the number of warnings to provide.
     *
     * @return The number of warnings to provide
     */
    public int getWarningCount() {
        return warningCount != null ? warningCount.intValue() : 2;
    }

    /**
     * Returns the cellId.
     *
     * @return The cellId
     */
    public String getCellId() {
        return cellId != null ? cellId : StringUtils.EMPTY;
    }
}
