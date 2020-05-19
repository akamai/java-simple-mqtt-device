package com.akamai.iot.iec;

import com.akamai.iot.iec.console.ConsoleCredentialsLoader;
import com.akamai.iot.iec.console.ConsoleReader;
import com.akamai.iot.iec.model.SandBox;
import com.akamai.iot.iec.model.auth.Authentication;
import com.akamai.iot.iec.model.auth.Credentials;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimplePublisher {
    private static final Logger LOGGER = Logger.getLogger(SimplePublisher.class.getName());
    private static final String CLIENT_SUFFIX = SimpleConstants.PUB_CLIENT;
    private static final String TOPIC_SUFFIX = SimpleConstants.TOPIC;

    public static void main(String[] args) {
        MqttCallback callback = new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                LOGGER.severe("UNEXPECTED MESSAGE FROM " + topic + ": " + new String(message.getPayload(), StandardCharsets.UTF_8) + " qos: " + message.getQos());
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
         * Read the credentials from the console
         */
        Credentials credentials = ConsoleCredentialsLoader.create().loadCredentials();

        /*
         * Load the configuration received via an email...
         */
        SandBox sandbox = SandBox.load();


        MqttClient client = null;

        try {
            /*
             * Use the credentials to login to JWT server
             */
            Authentication a = sandbox.authenticate(credentials, CLIENT_SUFFIX);

            client = sandbox.connect(a, true, callback);

            // can build the sand box directly if preferred
            // 	String host = ;
            // 	String clientPrefix = ;
            // 	String topicPrefix = ;
            // 	String connectURL = "ssl://"+host+":8883";
            //	String loginURL = "https://"+host"+"/api/v1/auth";
            //
            //	LoginProcessor l = new LoginProcessor(loginURL, clientPrefix);
            // 	Authentication a = Authentication.authenticate(l, credentials, CLIENT_SUFFIX);

            //	client = new MqttClient(sandBox.getMqttConnectUrl(), clientId);
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

            ConsoleReader r = new ConsoleReader();
            showConsolePrompt(topic);

            String line = r.readLine();
            while (line != null) {
                LOGGER.severe("Publishing '" + line + "' to " + topic + " qos=1");
                client.publish(topic, line.getBytes(StandardCharsets.UTF_8), 1, false);
                showConsolePrompt(topic);
                line = r.readLine();
            }

            LOGGER.severe("Disconnecting...");
            client.disconnect();
            client.close();
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

    private static void showConsolePrompt(String topic) {
        System.out.println("Type blank line or 'exit' to quit...");
        System.out.print("or enter to publish message to " + topic + ": ");
    }
}
