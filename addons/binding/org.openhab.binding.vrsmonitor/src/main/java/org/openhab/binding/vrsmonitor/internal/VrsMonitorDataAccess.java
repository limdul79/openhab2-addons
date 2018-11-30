/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.vrsmonitor.internal;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the access to the end point.
 * Uses {@link HttpUtil} to load the data.
 *
 * @author Martin Koehler - Initial contribution
 */
@NonNullByDefault
public class VrsMonitorDataAccess {

    private static final String REFRESH_URL = "https://www.vrsinfo.de/index.php?eID=tx_vrsinfo_ass2_departuremonitor&i=";
    private static final int REFRESH_TIMEOUT_MS = 5000;

    private final Logger logger = LoggerFactory.getLogger(VrsMonitorDataAccess.class);

    /**
     * Returns the raw Data from the Endpoint.
     * In case of errors or empty HashValue the Method returns an {@link StringUtils#EMPTY Empty String}.
     *
     * @param showId The showId for which the data should be returned.
     * @return The raw data.
     */
    public String getDataFromEndpoint(String showId) {
        try {
            if (StringUtils.isBlank(showId)) {
                logger.warn("No showId provided");
                return StringUtils.EMPTY;
            }
            String rawData = HttpUtil.executeUrl("GET",
                    REFRESH_URL + URLEncoder.encode(showId, StandardCharsets.UTF_8.toString()), REFRESH_TIMEOUT_MS);
            return rawData;
        } catch (IOException e) {
            logger.warn("Communication error occurred while getting data: {}", e.getMessage());
        }
        return StringUtils.EMPTY;
    }

}
