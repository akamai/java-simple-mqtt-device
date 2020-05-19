package com.akamai.iot.iec.model.auth;

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
