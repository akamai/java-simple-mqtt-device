package com.akamai.iot.iec.model;

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

import com.akamai.iot.iec.model.auth.Authentication;
import com.akamai.iot.iec.model.auth.Credentials;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SandBox {
    private static final Logger LOGGER = Logger.getLogger(SandBox.class.getName());
    private static SandBox LOADED = null;

    public static final String MQTT_HOST = "mqtt.host";
    public static final String CLIENT_ID_PREFIX = "client.id.prefix";
    public static final String TOPIC_PREFIX = "topic.prefix";

    private static final String MQTT_URL_PATTERN = "ssl://{0}:8883";
    private static final String LOGIN_URL_PATTERN = "https://{0}/login";


    private final String clientPrefix;
    private final String mqttHost;
    private final String topicPrefix;

    public static SandBox load(String mqttHost, String clientPrefix, String topicPrefix) {
        if (LOADED != null) {
            return LOADED;
        }

        LOADED = new SandBox(mqttHost, clientPrefix, topicPrefix);
        return LOADED;
    }

    public static SandBox load() {
        if (LOADED != null)
            return LOADED;

        InputStream input = ClassLoader.getSystemResourceAsStream("configuration.txt");
        if (input == null) {
            LOGGER.severe("configuration.txt could not be found");
            throw new IllegalArgumentException();
        }

        Properties p = new Properties();
        try {
            p.load(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading configuration file", e);
            throw new IllegalStateException("Error reading configuration file", e);
        }

        SandBox s = new SandBox(p.getProperty("mqtt.host"),
                p.getProperty("client.id.prefix"),
                p.getProperty("topic.prefix"));
        LOADED = s;

        s.log("loaded configuration\n");
        return s;
    }

    SandBox(String mqttHost, String clientPrefix, String topicPrefix) {
        this.clientPrefix = clientPrefix;
        this.mqttHost = mqttHost;
        this.topicPrefix = topicPrefix;
    }

    public Authentication authenticate(String token) {
        return Authentication.authenticate(token);
    }

    public Authentication authenticate(Credentials credentials, String clientSuffix) {
        return Authentication.authenticate(this, credentials, clientSuffix);
    }

    public String getClientIdPrefix() {
        return clientPrefix;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public String getConnectURL() {
        return MessageFormat.format(MQTT_URL_PATTERN, mqttHost);
    }

    public String getLoginURL() {
        return MessageFormat.format(LOGIN_URL_PATTERN, mqttHost);
    }

    public MqttClient connect(Authentication a, boolean clean, MqttCallback callback) throws MqttException {
        MqttClient client = new MqttClient(getConnectURL(), a.getClientId(), new MemoryPersistence());
        client.setCallback(callback);

        MqttConnectOptions options = a.createMqttOptions(clean);

        client.connectWithResult(options);

        return client;
    }

    public void log(String message) {
        if (message != null)
            LOGGER.severe(message);

        LOGGER.severe("Host: " + mqttHost);
        LOGGER.severe("Topic Prefix: " + topicPrefix);
        LOGGER.severe("The client id prefix: " + clientPrefix);
    }

    public String getUserPublishTopic(String username) {
        return getTopicPrefix() + "user/pub/" + username;
    }

    public String getUserSubscriptionTopic(String username) {
        return getTopicPrefix() + "user/sub/" + username;
    }
}
