package com.akamai.iot.iec;

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

import com.akamai.iot.iec.console.ConsoleCredentialsLoader;
import com.akamai.iot.iec.model.SandBox;
import com.akamai.iot.iec.model.Utility;
import com.akamai.iot.iec.model.auth.Authentication;
import com.akamai.iot.iec.model.auth.Credentials;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSubscriber {
    private static final Logger LOGGER = Logger.getLogger(SimpleSubscriber.class.getName());
    private static final String CLIENT_SUFFIX = SimpleConstants.SUB_CLIENT;
    private static final String TOPIC_SUFFIX = SimpleConstants.TOPIC;

    public static void main(String[] args) {
        Utility.configureLogger();

        MqttCallback callback = new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                LOGGER.severe("MESSAGE FROM " + topic + ": '" + new String(message.getPayload(), StandardCharsets.UTF_8) + "' qos: " + message.getQos());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }

            @Override
            public void connectionLost(Throwable cause) {
                LOGGER.severe("The connect has been lost, closing down");
                System.exit(0);
            }
        };


        /*
         * Load the configuration received via an email...
         */
        SandBox sandbox = SandBox.load();

        /*
         * Read the credentials from the console
         */
        Credentials credentials = ConsoleCredentialsLoader.create().loadCredentials();

        /*
         * Use the credentials to login to JWT server
         */
        Authentication a = sandbox.authenticate(credentials, CLIENT_SUFFIX);
        if (!a.isAuthenticated()) {
            LOGGER.severe(a.getStatus());
            return;
        }

        MqttClient client = null;

        try {

            client = sandbox.connect(a, true, callback);

            // can build the sand box directly if preferred
            // String host = ;
            // String clientPrefix = ;
            // String topicPrefix = ;

            //	client = new MqttClient(sandBox.getMqttConnectUrl(), clientId, new MemoryPersistence());
            //	client.setCallback(callback);

            //	MqttConnectOptions options = new MqttConnectOptions();
            //	options.setUserName(credentials.getUsername());
            //	options.setPassword(token.toCharArray());
            //	options.setCleanSession(true);
            //	options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            //	IMqttToken t = client.connectWithResult(options);
            //	LOGGER.log(Level.INFO, (t.getSessionPresent() ? "Reconnected" : "Connected")+" as "+clientId);

            String topic = sandbox.getTopicPrefix() + TOPIC_SUFFIX;
            //	String topic = topicPrefix + TOPIC_SUFFIX;

            client.subscribe(topic);

            LOGGER.severe("Subscribed to topic " + topic);
            LOGGER.severe("  waiting 30 seconds for data to arrive");

            Thread.sleep(30000);

            client.disconnect();
            client.close();

            LOGGER.severe("goodbye");
        } catch (Exception e) {
            e.printStackTrace();
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e1) {
                    LOGGER.log(Level.WARNING, "Cannot close MQTT client connection", e1);
                }
            }
        }

    }
}
