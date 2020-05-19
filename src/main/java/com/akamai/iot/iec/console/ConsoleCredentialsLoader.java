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

import com.akamai.iot.iec.model.auth.Credentials;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ConsoleCredentialsLoader {
    private static final Logger LOGGER = Logger.getLogger(ConsoleCredentialsLoader.class.getSimpleName());

    private final Console console;

    public static ConsoleCredentialsLoader create() {
        Console c = System.console();
        return (c == null) ? new ConsoleInputReader() : new ConsoleCredentialsLoader(c);
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

    private static class ConsoleInputReader extends ConsoleCredentialsLoader {
        private final BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        private ConsoleInputReader() {
            super(null);
            LOGGER.warning("The console wasn't available, switching to less impressive loader");
        }

        @Override
        public String readUsername() {
            try {
                System.out.print("Username: ");
                return reader.readLine();
            } catch (IOException e) {
                LOGGER.severe("Error reading username " + e.getMessage());
                return null;
            }
        }

        @Override
        public String readPassword() {
            try {
                System.out.print("Password: ");
                return reader.readLine();
            } catch (IOException e) {
                LOGGER.severe("Error reading password " + e.getMessage());
                return null;
            }
        }
    }
}
