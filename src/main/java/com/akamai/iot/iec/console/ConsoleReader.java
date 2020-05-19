package com.akamai.iot.iec.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleReader {

    private final Console console;
    private BufferedReader reader;

    public ConsoleReader() {
        console = System.console();
        if (console == null)
            reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    public String readLine() {
        String s = doReadLine();
        if (s == null)
            return null;

        s = s.trim();
        if (s.isEmpty() || "exit".equals(s))
            return null;

        return s;
    }

    private String doReadLine() {
        if (console != null)
            return console.readLine("");
        else {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
