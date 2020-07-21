package de.gtarc.chariot.orserviceagent.connection;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * IOLITE-Driver for mqtt communication with an ar-app.
 *
 * @author Paul Braun
 */
public class MQTTDriver {

	private static MqttClient client;
	private static String clientID;
	private static MemoryPersistence persistence;
	private static String serverURI;
	private boolean debug;
	MqttConnectOptions options;

	public MQTTDriver (boolean debug) {
		clientID = MqttClient.generateClientId();
		persistence = new MemoryPersistence();
		serverURI = Config.BROKERADDRESS + ":" + Config.BROKERPORT;

		options = new MqttConnectOptions();
		options.setUserName("adwhqtin");
		options.setPassword("yl80TdCbJ0CW".toCharArray());
		options.setCleanSession(true);
		options.setConnectionTimeout(5);


		this.debug = debug;

		//create Client
		try {
			System.out.print("Create MQTT Client: ["+clientID+"]");
			client = new MqttClient(serverURI, clientID, persistence);
			System.out.println("...created.");

			System.out.print("Connect to Server: [" + serverURI + "]");
			client.connect(options);
			System.out.println("...connected.");
		} catch (MqttException e) {
			e.printStackTrace();
		}

		client.setTimeToWait(200);
	}

	public void setCallback (MqttCallback callback) {
		client.setCallback(callback);
	}

	public void subscribe (String topic) {
		try {
			System.out.print("Subscribe to Topic: [" + topic + "]");
			client.subscribe(topic, 1);;
			System.out.println("...subscribed.");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void unsubscribe (String topic) {
		try {
			System.out.print("Unsubscribe from Topic: [" + topic + "]");
			client.unsubscribe(topic);
			System.out.println("...unsubscribed.");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void publish (String topic, String message) {
		try {
			System.out.print("Send to ["+topic+"]");
			if (debug) System.out.print(" message: ["+message+"]");
			client.publish(topic, message.getBytes(), 1, false);
			System.out.println("...done.");
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}
