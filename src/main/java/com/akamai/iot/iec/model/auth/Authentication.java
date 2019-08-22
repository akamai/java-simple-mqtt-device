package com.akamai.iot.iec.model.auth;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import com.akamai.iot.iec.model.SandBox;

public interface Authentication {

	public static Authentication authenticate(String token) {
		return new TokenAuthentication(token);
	}

	public static Authentication authenticate(SandBox sandbox, Credentials credentials, String clientSuffix) {
		return Authentication.authenticate(new LoginProcessor(sandbox), credentials.username, credentials.password, sandbox.getClientIdPrefix()+clientSuffix);
	}

	public static Authentication authenticate(LoginProcessor login, String username, String password, String clientId) {
		return authenticate(login, new Credentials(username, password), clientId);
	}

	public static Authentication authenticate(LoginProcessor login, Credentials credentials, String clientId) {

		LoginResults results = login.login(credentials, clientId);
		if (results == null || results.isError())
			return new AuthenticationFailure(credentials.username, clientId, results);

		return new TokenAuthentication(results.getToken());
	}

	public String getUsername();
	public String getClientId();
	public String getToken();
	public boolean isExpired();
	public boolean isAuthenticated();

	public String getStatus();

	public MqttConnectOptions createMqttOptions(boolean clean);

	public static boolean canConnect(Authentication a) {
		return a != null && !a.isExpired() && a.isAuthenticated();
	}
}
