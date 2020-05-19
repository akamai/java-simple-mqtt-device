package com.akamai.iot.iec.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.Instant;

@JsonInclude(Include.NON_NULL)
public class LoginRequest {
    public final String username;
    public final String password;
    public final String clientId;

    public final Instant expiry;

    public LoginRequest(String username, String password, String clientId, Instant expiry) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.expiry = expiry;
    }

    public LoginRequest() { // for JSON
        this.username = null;
        this.password = null;
        this.clientId = null;
        this.expiry = null;
    }
}
