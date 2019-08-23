package com.akamai.iot.iec;

import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.akamai.iot.iec.console.ConsoleCredentialsLoader;
import com.akamai.iot.iec.console.ConsoleReader;
import com.akamai.iot.iec.model.SandBox;
import com.akamai.iot.iec.model.auth.Authentication;
import com.akamai.iot.iec.model.auth.Credentials;

public class SimplePublisher {
	private static Logger LOGGER = Logger.getLogger(SimplePublisher.class.getName());
	private static final String CLIENT_SUFFIX = SimpleConstants.PUB_CLIENT;
	private static final String TOPIC_SUFFIX = SimpleConstants.TOPIC;

	public static void main(String[] args) {
		MqttCallback callback = new MqttCallback() {

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				LOGGER.severe("UNEXPECTED MESSAGE FROM "+topic+": "+new String(message.getPayload())+" qos: "+Integer.toString(message.getQos()));
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) { }

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

			System.out.println("Enter to publish message to "+topic);
			System.out.println("Type blank line or 'exit' to quit");

			ConsoleReader r = new ConsoleReader();
			String line = r.readLine();
			while (line != null) {
				System.out.println("Publishing '"+line+"' to "+topic+" qos=1");
				client.publish(topic, line.getBytes(), 1, false);
				line = r.readLine();
			}

			System.out.println("Disconnecting...");
			client.disconnect();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (client != null) {
				try { client.close(); } catch (Exception e1) { }
			}
		}

	}
}
