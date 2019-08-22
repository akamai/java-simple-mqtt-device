package com.akamai.iot.iec.model.auth;

public class Credentials {
	final String username;
	final String password;
	
	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
}
