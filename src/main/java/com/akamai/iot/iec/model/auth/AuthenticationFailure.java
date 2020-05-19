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

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class AuthenticationFailure implements Authentication {
    private final LoginResults results;
    private final String username;
    private final String clientId;

    AuthenticationFailure(final String username, final String clientId, final LoginResults results) {
        this.results = results;
        this.username = username;
        this.clientId = clientId;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    public String getStatus() {
        StringBuilder b = new StringBuilder();
        b.append("Not authenticated; ");
        if (results == null) {
            b.append("unknown error");
        } else {
            b.append("status = ");
            b.append(results.getStatus());
            if (results.getMessage() != null && !results.getMessage().isBlank()) {
                b.append("; message = ");
                b.append(results.getMessage());
            }
        }

        return b.toString();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public MqttConnectOptions createMqttOptions(boolean clean) {
        throw new UnsupportedOperationException("Not authenticated");
    }
}
