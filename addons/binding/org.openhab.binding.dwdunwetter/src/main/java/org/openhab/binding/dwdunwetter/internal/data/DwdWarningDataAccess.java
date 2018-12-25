/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dwdunwetter.internal.data;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the access to the API Endpoint
 *
 * @author Martin Koehler - Initial contribution
 */
public class DwdWarningDataAccess {

    private final Logger logger = LoggerFactory.getLogger(DwdWarningDataAccess.class);

    // URL of the Service
    private static final String DWD_URL = "https://maps.dwd.de/geoserver/dwd/ows?service=WFS&version=2.0.0&request=GetFeature&typeName=dwd:Warnungen_Gemeinden";

    /**
     * Returns the raw Data from the Endpoint.
     * In case of errors or empty cellId hValue the Method returns an {@link StringUtils#EMPTY Empty String}.
     *
     * @param cellId The warnCell-Id for which the warnings should be returned
     * @return The raw data.
     */
    public String getDataFromEndpoint(String cellId) {
        try {
            if (StringUtils.isBlank(cellId)) {
                logger.warn("No cellId provided");
                return StringUtils.EMPTY;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(DWD_URL);
            stringBuilder.append("&CQL_FILTER=");
            stringBuilder
                    .append(URLEncoder.encode("WARNCELLID LIKE '" + cellId + "'", StandardCharsets.UTF_8.toString()));
            logger.debug("Refreshing Data: " + stringBuilder.toString());
            String rawData = HttpUtil.executeUrl("GET", stringBuilder.toString(), 5000);
            return rawData;
        } catch (IOException e) {
            logger.warn("Communication error occurred while getting data: {}", e.getMessage());
        }
        return StringUtils.EMPTY;
    }

}
