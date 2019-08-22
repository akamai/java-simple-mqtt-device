package com.akamai.iot.iec.model;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Utility {

	public static void configureLogger() {
		try {
			LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logger.txt"));;
		} catch (IOException e) {
			System.err.println("Could not initialize logger - "+e.getMessage());
		}
	}
	
	public static ObjectMapper createObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		om.setDateFormat(new StdDateFormat());
		om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return om;
	}
	
	public static void check(String message, String param) {
		if (param == null || param.isEmpty())
			throw new IllegalArgumentException(message+" cannot be empty");
	}

	public static void check(String message, Object param) {
		if (param == null)
			throw new IllegalArgumentException(message+" cannot be null");
	}

	public static <T> boolean isSingle(T[] param) {
		return param != null && param.length == 1 && param[0] != null;
	}

	public static <T> void check(String message, T[] param) {
		if (param == null || param.length == 0)
			throw new IllegalArgumentException(message+" cannot be null");
		
		for (T t : param) {
			if (t == null)
				throw new IllegalArgumentException(message+" cannot contain null elements");
		}
	}

	public static String asTimestampString(long timestamp) {
		Instant i = Instant.ofEpochMilli(timestamp);
		return i.toString();
	}
}
