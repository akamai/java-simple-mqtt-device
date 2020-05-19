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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public interface Authentication {

    static Authentication authenticate(String token) {
        return new TokenAuthentication(token);
    }

    static Authentication authenticate(SandBox sandbox, Credentials credentials, String clientSuffix) {
        return Authentication.authenticate(new LoginProcessor(sandbox), credentials.username, credentials.password, sandbox.getClientIdPrefix() + clientSuffix);
    }

    static Authentication authenticate(LoginProcessor login, String username, String password, String clientId) {
        return authenticate(login, new Credentials(username, password), clientId);
    }

    static Authentication authenticate(LoginProcessor login, Credentials credentials, String clientId) {

        LoginResults results = login.login(credentials, clientId);
        if (results == null || results.isError())
            return new AuthenticationFailure(credentials.username, clientId, results);

        return new TokenAuthentication(results.getToken());
    }

    String getUsername();

    String getClientId();

    String getToken();

    boolean isAuthenticated();

    String getStatus();

    boolean isExpired();

    MqttConnectOptions createMqttOptions(boolean clean);

}
