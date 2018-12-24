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
package org.openhab.binding.dwdunwetter.internal.data;

import java.io.StringReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.measure.quantity.Length;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.cache.ExpiringCache;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.library.unit.ImperialUnits;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the Data for all retrieved warnings for one thing.
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdWarningsData {

    private static final int MIN_REFRESH_WAIT_MINUTES = 5;

    private final Logger logger = LoggerFactory.getLogger(DwdWarningsData.class);

    private List<DwdWarningData> gemeindenData = new LinkedList<>();

    private DwdWarningCache cache = new DwdWarningCache();

    private ExpiringCache<String> dataAccessCached;

    private DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            // date/time
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            // offset (hh:mm - "+00:00" when it's zero)
            .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
            // offset (hhmm - "+0000" when it's zero)
            .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
            // offset (hh - "Z" when it's zero)
            .optionalStart().appendOffset("+HH", "Z").optionalEnd()
            // create formatter
            .toFormatter();

    public DwdWarningsData(String cellId) {
        DwdWarningDataAccess dataAccess = new DwdWarningDataAccess();
        this.dataAccessCached = new ExpiringCache<>(Duration.ofMinutes(MIN_REFRESH_WAIT_MINUTES),
                () -> dataAccess.getDataFromEndpoint(cellId));
    }

    private String getValue(XMLEventReader eventReader) throws XMLStreamException {
        XMLEvent event = eventReader.nextEvent();
        return event.asCharacters().getData();
    }

    private BigDecimal getBigDecimalValue(XMLEventReader eventReader) throws XMLStreamException {
        XMLEvent event = eventReader.nextEvent();
        try {
            return new BigDecimal(event.asCharacters().getData());
        } catch (NumberFormatException e) {
            logger.warn("Exception while parsing a BigDecimal", e);
            return BigDecimal.ZERO;
        }
    }

    private long getTimestampValue(XMLEventReader eventReader) throws XMLStreamException {
        XMLEvent event = eventReader.nextEvent();
        String dateTimeString = event.asCharacters().getData();
        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(dateTimeString, formatter);
            return dateTime.toEpochSecond();
        } catch (DateTimeParseException e) {
            logger.warn("Exception while parsing a a DateTime", e);
            return 0;
        }
    }

    /**
     * Refreshes the Warnings Data
     */
    public boolean refresh() {
        String rawData = dataAccessCached.getValue();
        if (StringUtils.isEmpty(rawData)) {
            logger.warn("No Data from Endpoint");
            return false;
        }

        gemeindenData.clear();

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(rawData));
            XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
            DwdWarningData gemeindeData = new DwdWarningData();
            boolean insideGemeinde = false;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (!insideGemeinde && event.isStartElement()) {
                    DwdXmlTag xmlTag = DwdXmlTag.getDwdXmlTag(event.asStartElement().getName().getLocalPart());
                    switch (xmlTag) {
                        case WARNUNGEN_GEMEINDEN:
                            gemeindeData = new DwdWarningData();
                            insideGemeinde = true;
                            break;
                        default:
                            break;
                    }
                } else if (insideGemeinde && event.isStartElement()) {
                    DwdXmlTag xmlTag = DwdXmlTag.getDwdXmlTag(event.asStartElement().getName().getLocalPart());
                    switch (xmlTag) {
                        case SEVERITY:
                            gemeindeData.setSeverity(Severity.getSeverity(getValue(eventReader)));
                            break;
                        case DESCRIPTION:
                            gemeindeData.setDescription(getValue(eventReader));
                            break;
                        case EFFECTIVE:
                            gemeindeData.setEffective(getTimestampValue(eventReader));
                            break;
                        case EXPIRES:
                            gemeindeData.setExpires(getTimestampValue(eventReader));
                            break;
                        case EVENT:
                            gemeindeData.setEvent(getValue(eventReader));
                            break;
                        case STATUS:
                            gemeindeData.setStatus(getValue(eventReader));
                            break;
                        case MSGTYPE:
                            gemeindeData.setMsgType(getValue(eventReader));
                            break;
                        case HEADLINE:
                            gemeindeData.setHeadline(getValue(eventReader));
                            break;
                        case ONSET:
                            gemeindeData.setOnset(getTimestampValue(eventReader));
                            break;
                        case ALTITUDE:
                            gemeindeData.setAltitude(getBigDecimalValue(eventReader));
                            break;
                        case CEILING:
                            gemeindeData.setCeiling(getBigDecimalValue(eventReader));
                            break;
                        case IDENTIFIER:
                            gemeindeData.setId(getValue(eventReader));
                            break;
                        default:
                            break;
                    }
                } else if (insideGemeinde && event.isEndElement()) {
                    DwdXmlTag xmlTag = DwdXmlTag.getDwdXmlTag(event.asEndElement().getName().getLocalPart());
                    switch (xmlTag) {
                        case WARNUNGEN_GEMEINDEN:
                            if (!gemeindeData.isTest() && !gemeindeData.isCancel()) {
                                gemeindenData.add(gemeindeData);
                            }
                            insideGemeinde = false;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            logger.warn("Exception while parsing the XML Response", e);
            return false;
        }
        Collections.sort(gemeindenData, new SeverityComparator());
        return true;
    }

    private DwdWarningData getGemeindeData(int number) {
        return gemeindenData.size() <= number ? null : gemeindenData.get(number);
    }

    public State getWarning(int number) {
        DwdWarningData data = getGemeindeData(number);
        return data == null ? OnOffType.OFF : OnOffType.ON;
    }

    public State getSeverity(int number) {
        DwdWarningData data = getGemeindeData(number);
        return data == null ? UnDefType.NULL : StringType.valueOf(data.getSeverity().getText());
    }

    public State getDescription(int number) {
        DwdWarningData data = getGemeindeData(number);
        return data == null ? UnDefType.NULL : StringType.valueOf(data.getDescription());
    }

    public State getEffective(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return UnDefType.NULL;
        }
        ZonedDateTime zoned = ZonedDateTime.ofInstant(Instant.ofEpochSecond(data.getEffective()),
                ZoneId.systemDefault());
        return new DateTimeType(zoned);
    }

    public State getExpires(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return UnDefType.NULL;
        }
        ZonedDateTime zoned = ZonedDateTime.ofInstant(Instant.ofEpochSecond(data.getExpires()), ZoneId.systemDefault());
        return new DateTimeType(zoned);
    }

    public State getOnset(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return UnDefType.NULL;
        }
        ZonedDateTime zoned = ZonedDateTime.ofInstant(Instant.ofEpochSecond(data.getOnset()), ZoneId.systemDefault());
        return new DateTimeType(zoned);
    }

    public State getEvent(int number) {
        DwdWarningData data = getGemeindeData(number);
        return data == null ? UnDefType.NULL : StringType.valueOf(data.getEvent());
    }

    public State getHeadline(int number) {
        DwdWarningData data = getGemeindeData(number);
        return data == null ? UnDefType.NULL : StringType.valueOf(data.getHeadline());
    }

    public State getAltitude(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return UnDefType.NULL;
        }
        return new QuantityType<Length>(data.getAltitude(), ImperialUnits.FOOT);
    }

    public State getCeiling(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return UnDefType.NULL;
        }
        return new QuantityType<Length>(data.getCeiling(), ImperialUnits.FOOT);
    }

    public boolean isNew(int number) {
        DwdWarningData data = getGemeindeData(number);
        if (data == null) {
            return false;
        }
        return cache.addEntry(data);
    }

    public void updateCache() {
        cache.deleteOldEntries();
    }

    /**
     * Only for Tests
     */
    protected void setDataAccess(DwdWarningDataAccess dataAccess) {
        dataAccessCached = new ExpiringCache<String>(Duration.ofMinutes(MIN_REFRESH_WAIT_MINUTES),
                () -> dataAccess.getDataFromEndpoint(""));
    }
}
