/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.OffsetDateTime;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DwdWarningCache}
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdWarningCacheTest {

    private DwdWarningCache cache;

    @Before
    public void setUp() {
        cache = new DwdWarningCache();
    }

    @Test
    public void testAddEntry() {
        DwdWarningData data = createData("ID", 0);

        assertThat(cache.addEntry(data), is(true));
        assertThat(cache.addEntry(data), is(false));
    }

    @Test
    public void testDeleteOldEntries() {
        DwdWarningData data = createData("ID", 0);
        cache.addEntry(data);

        cache.deleteOldEntries();
        assertThat(cache.addEntry(data), is(false));

        data = createData("ID", 60 * 60);
        assertThat(cache.addEntry(data), is(false));
        cache.deleteOldEntries();
        assertThat(cache.addEntry(data), is(true));

    }

    private DwdWarningData createData(String id, long secondsBeforeNow) {
        DwdWarningData data = new DwdWarningData();
        data.setId(id);
        data.setExpires(OffsetDateTime.now().toEpochSecond() - secondsBeforeNow);
        return data;
    }
}
