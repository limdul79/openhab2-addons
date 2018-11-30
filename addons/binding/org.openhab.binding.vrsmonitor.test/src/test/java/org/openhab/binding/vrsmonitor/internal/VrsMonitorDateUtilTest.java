package org.openhab.binding.vrsmonitor.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class VrsMonitorDateUtilTest {

    private int year;

    @Before
    public void setUp() {
        year = LocalDateTime.now().getYear();
    }

    @Test
    public void testPareLastUpdated_CorrectFormat() {
        assertThat(VrsMonitorDateUtil.pareLastUpdated("02.12. 11:13:25"),
                is(LocalDateTime.of(year, 12, 2, 11, 13, 25)));
        assertThat(VrsMonitorDateUtil.pareLastUpdated("31.01. 23:33:59"),
                is(LocalDateTime.of(year, 1, 31, 23, 33, 59)));
    }

    @Test
    public void testPareLastUpdated_WrongFormat() {
        assertThat(VrsMonitorDateUtil.pareLastUpdated("Nix"), is(LocalDateTime.MIN));
        assertThat(VrsMonitorDateUtil.pareLastUpdated(""), is(LocalDateTime.MIN));
        assertThat(VrsMonitorDateUtil.pareLastUpdated(null), is(LocalDateTime.MIN));
    }
}
