package com.akamai.iot.iec.model.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.net.ssl.HttpsURLConnection;

import com.akamai.iot.iec.model.SandBox;
import com.akamai.iot.iec.model.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginProcessor {
	private final String loginURL;

	public LoginProcessor(SandBox sandbox) {
		this(sandbox.getLoginURL(), sandbox.getClientIdPrefix());
	}

	public LoginProcessor(String loginURL, String clientPrefix) {
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
			con = (HttpsURLConnection)url.openConnection();

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
