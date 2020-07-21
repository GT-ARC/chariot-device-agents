package de.gtarc.chariot.orserviceagent.connection;

public class Config {
	//public static String BROKERADDRESS = "tcp://broker.hivemq.com";
	//public static String BROKERADDRESS = "tcp://192.168.178.22";
//	public static String BROKERADDRESS = "tcp://pi.paulbraun.de";
//	public static int BROKERPORT = 1883;

	//public static String BROKERADDRESS = "tcp://m20.cloudmqtt.com";
	public static String BROKERADDRESS = "130.149.232.235";
	public static int BROKERPORT = 1883;

	public static String TOPICBASE = "iolite/devices/";
	public static String GETTOPIC = "GET/"+TOPICBASE;
	public static String PUTTOPIC = "PUT/"+TOPICBASE;
}
