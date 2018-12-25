/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
