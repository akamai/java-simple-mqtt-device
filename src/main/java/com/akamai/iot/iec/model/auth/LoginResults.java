package com.akamai.iot.iec.model.auth;

/*-
 * #%L
 * java-simple-mqtt-device
 * %%
 * Copyright (C) 2020 Akamai Technologies, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginResults {
    private static final Logger LOGGER = Logger.getLogger(LoginResults.class.getName());
    private final LoginResponse response;
    private final int status;
    private final Throwable error;

    static LoginResults processError(HttpURLConnection con, IOException e) {
        // Try to get results if 401, 403, etc.
        if (con != null) {
            try {
                int responseCode = con.getResponseCode();
                if (responseCode >= 400 && responseCode <= 410) {
                    InputStream error = con.getErrorStream();
                    if (error == null) {
                        return new LoginResults(responseCode, e);
                    }

                    LoginResponse r = LoginResponse.parse(error, responseCode);
                    return new LoginResults(r, responseCode);
                }
            } catch (Exception t) {
                LOGGER.log(Level.SEVERE, "exception reading response", t);
            }
        }

        LOGGER.info("Could not try and read response, passing along exception");
        return new LoginResults(e);
    }


    LoginResults(LoginResponse response, int status) {
        this.response = response;
        this.status = status;
        this.error = null;
    }

    LoginResults(int status, Throwable error) {
        this.response = null;
        this.error = error;
        this.status = status;
    }

    LoginResults(Throwable error) {
        this.response = null;
        this.status = 500;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public String getToken() {
        return response == null ? null : response.token;
    }

    public String getMessage() {
        if (response != null)
            return response.message;
        return (error == null) ? null : error.getMessage();
    }

    public boolean isError() {
        return (error != null) || (status != 200);
    }
}
