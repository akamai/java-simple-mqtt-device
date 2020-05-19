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

import com.akamai.iot.iec.model.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginResponse {
    public final String token;
    public final String message;

    public LoginResponse() {
        // for jackson
        this(null, null);
    }

    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    static LoginResponse parse(InputStream input) {
        return parse(input, new JsonProcessor());
    }

    static LoginResponse parse(InputStream input, int responseCode) {
        return parse(input, new ErrorProcessor(responseCode));
    }

    private static LoginResponse parse(InputStream input, JsonProcessor processor) {
        Logger LOGGER = Logger.getLogger(LoginProcessor.class.getName());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int l = input.read(bytes);
            while (l > 0) {
                bos.write(bytes, 0, l);
                l = input.read(bytes);
            }

            String s = bos.toString(StandardCharsets.UTF_8);
            return processor.process(s);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not close input", e);
            }
        }
    }

    private static class JsonProcessor {

        LoginResponse process(String responseString) throws IOException {
            ObjectMapper om = Utility.createObjectMapper();
            return om.readValue(new StringReader(responseString), LoginResponse.class);
        }
    }

    private static class ErrorProcessor extends JsonProcessor {
        private final int responseCode;

        private ErrorProcessor(int responseCode) {
            this.responseCode = responseCode;
        }

        LoginResponse process(String responseString) throws IOException {
            if (responseString == null)
                return super.process(null);

            String s = responseString.trim();
            if (s.startsWith("<H")) {
                String b = "Error " +
                        responseCode +
                        "; " +
                        s;
                return new LoginResponse(null, b);
            }

            return super.process(responseString);
        }
    }
}
