package com.akamai.iot.iec.console;

/*-
 * #%L
 * java-simple-mqtt-device
 * %%
 * Copyright (C) 2020 Akamai Technologies, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
