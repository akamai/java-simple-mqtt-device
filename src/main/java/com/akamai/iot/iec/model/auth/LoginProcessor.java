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

import com.akamai.iot.iec.model.SandBox;
import com.akamai.iot.iec.model.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class LoginProcessor {
    private final String loginURL;

    public LoginProcessor(SandBox sandbox) {
        this(sandbox.getLoginURL());
    }

    public LoginProcessor(String loginURL) {
        this.loginURL = loginURL;
    }

    public LoginResults login(Credentials credentials, String clientId) {
        Instant i = Instant.now().plus(1, ChronoUnit.HOURS);
        return login(credentials, clientId, i);
    }

    public LoginResults login(Credentials credentials, String clientId, Instant expiry) {
        LoginRequest request = new LoginRequest(credentials.username, credentials.password, clientId, expiry);
        StringWriter w = new StringWriter();

        HttpsURLConnection con = null;

        try {
            ObjectMapper om = Utility.createObjectMapper();
            om.writeValue(w, request);
            byte[] out = w.toString().getBytes(StandardCharsets.UTF_8);

            URL url = new URL(loginURL);
            con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setFixedLengthStreamingMode(out.length);

            con.connect();

            OutputStream output = con.getOutputStream();
            output.write(out);
            output.flush();

            InputStream input = con.getInputStream();
            LoginResponse t = LoginResponse.parse(input);
            return new LoginResults(t, (t.token == null || t.token.isEmpty()) ? 403 : 200);
        } catch (IOException e) {
            return LoginResults.processError(con, e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
