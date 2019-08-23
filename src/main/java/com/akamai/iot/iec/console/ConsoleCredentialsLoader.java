package com.akamai.iot.iec.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.akamai.iot.iec.model.auth.Credentials;

public class ConsoleCredentialsLoader {
	private static final Logger LOGGER = Logger.getLogger(ConsoleCredentialsLoader.class.getSimpleName());

	private final Console console;

	public static ConsoleCredentialsLoader create() {
		Console c = System.console();
		return (c == null) ? new _IDELoader() : new ConsoleCredentialsLoader(c);
	}

	private ConsoleCredentialsLoader(Console console) {
		this.console = console;
	}

	public Credentials loadCredentials() {
		String u = readUsername();
		if (u == null) {
			LOGGER.severe("username is empty");
			return null;
		}

		String p = readPassword();
		if (p == null) {
			LOGGER.severe("password is empty");
			return null;
		}

		return new Credentials(u, p);
	}

	protected String readUsername() {
		return trim(console.readLine("Username: "));
	}

	protected String readPassword() {
		char[] p = console.readPassword("Password: ");
		if (p == null || p.length == 0)
			return null;

		return new String(p);
	}

	private String trim(String string) {
		if (string == null)
			return null;

		String s = string.trim();
		return (s.isEmpty()) ? null : s;
	}

	private static class _IDELoader extends ConsoleCredentialsLoader {
		private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));;

		private _IDELoader() {
			super(null);
			LOGGER.warning("The console wasn't available, switching to less impressive loader");
		}

		@Override
		public String readUsername() {
			try {
				System.out.print("Username: ");
				return reader.readLine();
			} catch (IOException e) { 
				LOGGER.severe("Error reading username "+e.getMessage());
				return null;
			}
		}

		@Override
		public String readPassword() {
			try {
				System.out.print("Password: ");
				return reader.readLine();
			} catch (IOException e) { 
				LOGGER.severe("Error reading password "+e.getMessage());
				return null;
			}
		}
	}
}
