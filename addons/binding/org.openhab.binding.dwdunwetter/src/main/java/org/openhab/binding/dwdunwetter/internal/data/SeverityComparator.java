/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

/**
 * Comperator to sort a Warning first by Severity, second by the onSet date.
 *
 * @author Martin Koehler - Initial contribution
 */
public class SeverityComparator implements Comparator<DwdWarningData> {

    @Override
    public int compare(DwdWarningData o1, DwdWarningData o2) {
        int result = Integer.compare(o1.getSeverity().getOrder(), o2.getSeverity().getOrder());
        if (result == 0) {
            result = ObjectUtils.compare(o1.getOnset(), o2.getOnset());
        }
        return result;
    }

}
