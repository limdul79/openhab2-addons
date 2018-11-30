/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.UnDefType;
import org.junit.Before;
import org.junit.Test;

public class VrsMonitorDataTest {

    private VrsMonitorData data;
    private TestDataProvider testDataProvider;

    @Before
    public void setUp() {
        data = new VrsMonitorData();
        testDataProvider = new TestDataProvider();
        data.setDataAccess(testDataProvider);
    }

    @Test
    public void testEmptyRawData_NoException() {
        testDataProvider.rawData = " ";
        data.refresh("");

        assertThat(data.getNumberOfDepartures(), is(0));
    }

    @Test
    public void testNoDeparturesReturnUndefStates() {
        testDataProvider.rawData = " ";
        data.refresh("");

        assertThat(data.getDirection(0), is(UnDefType.UNDEF));
        assertThat(data.getEstimate(0), is(UnDefType.UNDEF));
        assertThat(data.getTimetable(0), is(UnDefType.UNDEF));
        assertThat(data.isDelayed(0), is(UnDefType.UNDEF));
    }

    @Test
    public void testNumberOfDeparturesFromExample() throws IOException {
        loadJsonFromFile();
        data.refresh("");

        assertThat(data.getNumberOfDepartures(), is(5));
    }

    @Test
    public void testDeparture0FromExample() throws IOException {
        loadJsonFromFile();
        data.refresh("");

        assertThat(data.getDirection(0), is(StringType.valueOf("Bad Godesberg Stadthalle")));
        assertThat(data.getEstimate(0), is(StringType.valueOf("14:28")));
        assertThat(data.getTimetable(0), is(StringType.valueOf("14:25")));
        assertThat(data.isDelayed(0), is(OnOffType.ON));
    }

    @Test
    public void testDeparture1FromExample() throws IOException {
        loadJsonFromFile();
        data.refresh("");

        assertThat(data.getDirection(1), is(StringType.valueOf("Bad Godesberg Stadthalle")));
        assertThat(data.getEstimate(1), is(StringType.valueOf("14:45")));
        assertThat(data.getTimetable(1), is(StringType.valueOf("14:45")));
        assertThat(data.isDelayed(1), is(OnOffType.OFF));
    }

    @Test
    public void testDeparture2_NoEstimatedDelayed_FromExample() throws IOException {
        loadJsonFromFile();
        data.refresh("");

        assertThat(data.getDirection(2), is(StringType.valueOf("Bad Godesberg Stadthalle")));
        assertThat(data.getEstimate(2), is(StringType.valueOf("15:05")));
        assertThat(data.getTimetable(2), is(StringType.valueOf("15:05")));
        assertThat(data.isDelayed(2), is(OnOffType.OFF));
    }

    private void loadJsonFromFile() throws IOException {
        InputStream stream = getClass().getResourceAsStream("exampleInput.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
        String line = null;

        StringWriter stringWriter = new StringWriter();
        while ((line = reader.readLine()) != null) {
            stringWriter.write(line);
        }
        reader.close();
        testDataProvider.rawData = stringWriter.toString();
    }

    private class TestDataProvider extends VrsMonitorDataAccess {

        private String rawData = "";

        @Override
        public @NonNull String getDataFromEndpoint(@NonNull String hashValue) {
            return rawData;
        }
    }

}
