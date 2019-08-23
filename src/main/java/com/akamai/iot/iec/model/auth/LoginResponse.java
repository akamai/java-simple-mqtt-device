package com.akamai.iot.iec.model.auth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.akamai.iot.iec.model.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginResponse {
	public final String token;
	public final String message;

	public LoginResponse() {
		// for jaxson
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

			String s = bos.toString();
			return processor.process(s);
		} catch (Exception e) {
			return null;
		} finally {
			try { input.close(); }
			catch (IOException e) { LOGGER.log(Level.SEVERE, "Could not close input", e); }
		}
	}

	private static class JsonProcessor {

		LoginResponse process(String responseString) throws IOException {
			ObjectMapper om = Utility.createObjectMapper();
			LoginResponse t = om.readValue(new StringReader(responseString), LoginResponse.class);
			return t;
		}
	}

	private static class ErrorProcessor extends JsonProcessor {
		private final int responseCode;

		private ErrorProcessor(int responseCode) {
			this.responseCode = responseCode;
		}

		LoginResponse process(String responseString) throws IOException {
			if (responseString == null)
				return super.process(responseString);

			String s = responseString.trim(); 
			if (s.startsWith("<H")) {
				StringBuilder b = new StringBuilder();
				b.append("Error ");
				b.append(Integer.toString(responseCode));
				b.append("; ");
				b.append(s);
				return new LoginResponse(null, b.toString());
			}

			return super.process(responseString);
		}
	}
}
