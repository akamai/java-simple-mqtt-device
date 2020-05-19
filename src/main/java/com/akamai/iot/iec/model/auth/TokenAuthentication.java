package com.akamai.iot.iec.model.auth;

import com.auth0.jwt.JWT;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.text.DateFormat;
import java.util.Date;

class TokenAuthentication implements Authentication {
    private final String username;
    private final String clientId;
    private final String token;
    private final Date expires;

    TokenAuthentication(String token) {
        this.token = token;

        if (token != null) {
            JWT jwt = JWT.decode(token);
            this.username = jwt.getClaim("sub").asString();
            this.clientId = jwt.getClaim("clientId").asString();
            this.expires = jwt.getExpiresAt();
        } else {
            this.username = "";
            this.clientId = "";
            this.expires = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getClientId() {
        return clientId;
    }

    public String getToken() {
        return token;
    }

    public boolean isExpired() {
        return expires != null && expires.before(new Date());
    }

    public boolean isAuthenticated() {
        return true;
    }

    public String getStatus() {
        StringBuilder b = new StringBuilder();
        b.append("User ");
        b.append(getUsername() == null || getUsername().isBlank() ? "?" : getUsername());
        if (expires == null) {
            b.append(" connected");
        } else if (isExpired()) {
            b.append(" expired");
        } else {
            b.append(" expires ");
            DateFormat formatter = DateFormat.getDateTimeInstance();
            b.append(formatter.format(this));
        }

        return b.toString();
    }

    public MqttConnectOptions createMqttOptions(boolean clean) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username == null ? "user" : username);
        options.setPassword(token.toCharArray());
        options.setCleanSession(clean);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        return options;
    }
}
