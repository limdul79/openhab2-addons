/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.openhab.binding.dwdunwetter.internal.DwdUnwetterBindingConstants;

/**
 * Cache of Warnings to update the {@link DwdUnwetterBindingConstants#CHANNEL_UPDATED} if a new warning is sent to a
 * channel.
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdWarningCache {

    // Remove Entries 30 Minutes after they expired
    private static final long WAIT_TIME = 60 * 30;

    private final Map<String, Long> idExpiresMap;

    public DwdWarningCache() {
        idExpiresMap = new HashMap<>();
    }

    private boolean isExpired(Entry<String, Long> entry) {
        long now = OffsetDateTime.now().toEpochSecond();
        return entry.getValue() + WAIT_TIME < now;
    }

    /**
     * Adds a Warning
     *
     * @param data The warning data
     * @return <code>true</code> if it is a new warning, <code>false</code> if the warning is not new.
     */
    public boolean addEntry(DwdWarningData data) {
        return idExpiresMap.put(data.getId(), data.getExpires()) == null;
    }

    /**
     * Removes the expired Entries
     */
    public void deleteOldEntries() {
        List<String> oldEntries = idExpiresMap.entrySet().stream().filter(this::isExpired).map(Entry::getKey)
                .collect(Collectors.toList());
        oldEntries.forEach(idExpiresMap::remove);
    }

}
