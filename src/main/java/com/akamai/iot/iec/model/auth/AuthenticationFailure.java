package com.akamai.iot.iec.model.auth;

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
