/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.vrsmonitor.internal.data.VrsMonitorJsonData;
import org.openhab.binding.vrsmonitor.internal.data.VrsMonitorJsonDeparture;
import org.openhab.binding.vrsmonitor.internal.data.VrsMonitorJsonEvent;
import org.openhab.binding.vrsmonitor.internal.data.VrsMonitorJsonLine;
import org.openhab.binding.vrsmonitor.internal.data.VrsMonitorJsonStopPoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The Data for one departure monitor. Handles the conversion from the Raw Data to a Channel State.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault({ DefaultLocation.FIELD, DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })
public class VrsMonitorData {

    private static final int MIN_REFRESH_WAIT_SECONDS = 30;

    private VrsMonitorJsonData jsonData = new VrsMonitorJsonData();

    private VrsMonitorDataAccess dataAccess = new VrsMonitorDataAccess();

    private LocalDateTime lastRefresh = LocalDateTime.MIN;

    /**
     * Refreshes the data for the given showId.
     *
     * @param showId The showId for which the data should be refreshed.
     *
     */
    public void refresh(String showId) {
        if (lastRefresh.plus(MIN_REFRESH_WAIT_SECONDS, ChronoUnit.SECONDS).isAfter(LocalDateTime.now())) {
            return;
        }
        String rawData = dataAccess.getDataFromEndpoint(showId);
        VrsMonitorJsonData localJsonData = convert(rawData);
        if (localJsonData != null) {
            jsonData = localJsonData;
        } else {
            jsonData = new VrsMonitorJsonData();
        }
        lastRefresh = LocalDateTime.now();
    }

    /**
     * Returns the converted Data. Despite the NonNull Assumption
     * {@link Gson#fromJson(com.google.gson.JsonElement, Class)} returns <code>null</code> if rawData is
     * empty.
     *
     * @param rawData The raw Json Data
     * @return The converted Data
     */
    private @Nullable VrsMonitorJsonData convert(String rawData) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(rawData, VrsMonitorJsonData.class);
    }

    /**
     * Get the number of available departures.
     *
     * @return The number of available departures.
     */
    public int getNumberOfDepartures() {
        return jsonData.getNumOfEvents();
    }

    /**
     * Returns the Delayed State for the departure.
     *
     * @param departureNumber The departure number
     * @return The delayed state as {@link OnOffType} or {@link UnDefType#UNDEF} if there is no departure with that
     *         number.
     */
    public State isDelayed(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonDeparture departure = getDeparture(departureNumber);
        return departure == null ? OnOffType.OFF : OnOffType.from(departure.getDelayed());
    }

    /**
     * Returns the timetable departure time of the departure.
     *
     * @param departureNumber The departure number
     * @return The timetable time as {@link StringType} in the format HH:MM or {@link UnDefType#UNDEF} if there is no
     *         departure with that
     *         number.
     */
    public State getTimetable(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonDeparture departure = getDeparture(departureNumber);
        return departure == null ? StringType.EMPTY : StringType.valueOf(departure.getTimetable());
    }

    /**
     * Returns the estimated departure time of the departure.
     * If there is no estimated time departure, but a {{@link #getTimetable(int) timetable time of departure}, the
     * timetable time is returned.
     *
     * @param departureNumber The departure number
     * @return The estimated time as {@link StringType} in the format HH:MM or {@link UnDefType#UNDEF} if there is no
     *         departure with that number.
     */
    public State getEstimate(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonDeparture departure = getDeparture(departureNumber);
        return departure == null ? StringType.EMPTY : StringType.valueOf(departure.getEstimate());
    }

    /**
     * Returns the direction (end station) of the departure.
     *
     * @param departureNumber The departure number
     * @return The direction/end station of the departure as {@link StringType} or {@link UnDefType#UNDEF} if there is
     *         no departure with that number.
     */
    public State getDirection(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonLine line = getLine(departureNumber);
        return line == null ? StringType.EMPTY : StringType.valueOf(line.getDirection());
    }

    public State getTimestamp(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonDeparture departure = getDeparture(departureNumber);

        if (departure == null || departure.getTimestamp() == null) {
            return UnDefType.NULL;
        }
        ZonedDateTime zoned = ZonedDateTime.ofInstant(Instant.ofEpochSecond(departure.getTimestamp()),
                ZoneId.systemDefault());
        return new DateTimeType(zoned);
    }

    public State getLineNumber(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonLine line = getLine(departureNumber);
        return line == null ? StringType.EMPTY : StringType.valueOf(line.getNumber());
    }

    public State getLineProduct(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonLine line = getLine(departureNumber);
        return line == null ? StringType.EMPTY : StringType.valueOf(line.getProduct());
    }

    public State getStopId(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonStopPoint stopPoint = getStopPoint(departureNumber);
        return stopPoint == null ? StringType.EMPTY : StringType.valueOf(stopPoint.getIfopt());
    }

    public State getStopName(int departureNumber) {
        if (!exists(departureNumber)) {
            return UnDefType.UNDEF;
        }
        VrsMonitorJsonStopPoint stopPoint = getStopPoint(departureNumber);
        return stopPoint == null ? StringType.EMPTY : StringType.valueOf(stopPoint.getName());
    }

    public State getLastUpdated() {
        LocalDateTime dateTime = VrsMonitorDateUtil.pareLastUpdated(jsonData.getUpdated());
        ZonedDateTime zoned = ZonedDateTime.ofLocal(dateTime, ZoneId.systemDefault(), null);
        return new DateTimeType(zoned);
    }

    /**
     * Sets the data Access which is used to get the data from the end point.
     * This method should only be used in tests.
     *
     * @param dataAccess The data access
     */
    void setDataAccess(VrsMonitorDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private @Nullable VrsMonitorJsonEvent getEvent(int departureNumber) {
        return jsonData.getEvent(departureNumber);

    }

    private @Nullable VrsMonitorJsonDeparture getDeparture(int departureNumber) {
        VrsMonitorJsonEvent event = getEvent(departureNumber);
        return event == null ? null : event.getDeparture();
    }

    private @Nullable VrsMonitorJsonLine getLine(int departureNumber) {
        VrsMonitorJsonEvent event = getEvent(departureNumber);
        return event == null ? null : event.getLine();
    }

    private @Nullable VrsMonitorJsonStopPoint getStopPoint(int departureNumber) {
        VrsMonitorJsonEvent event = getEvent(departureNumber);
        return event == null ? null : event.getStopPoint();
    }

    private boolean exists(int depatureNumber) {
        return jsonData.getNumOfEvents() > depatureNumber;
    }

}
