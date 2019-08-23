package com.akamai.iot.iec.console;

import java.io.Console;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ConsoleWriter {
	private final PrintWriter writer = initializeWriter();

	public void print(String string) {
		writer.print(string);
	}

	public void println(String string) {
		writer.println(string);
		writer.flush();
	}

	public void println() {
		writer.println();
		writer.flush();
	}

	private PrintWriter initializeWriter() {
		Console c = System.console();
		return (c == null) ? new PrintWriter(System.out, true, StandardCharsets.UTF_8) : c.writer();
	}
}
