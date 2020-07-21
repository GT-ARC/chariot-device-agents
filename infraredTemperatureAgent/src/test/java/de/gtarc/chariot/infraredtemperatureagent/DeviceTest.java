//package de.gtarc.chariot.infraredtemperatureagent;
//
//import junit.framework.TestCase;
//
//public class DeviceTest extends TestCase {
//
//    String host = "tcp://m24.cloudmqtt.com:10933";
//    String username = "plbwvpgf";
//    String password = "mJTPb6z12Bag";
//    String clientId = "SensorAgent";
//
//
////    public void testSendMessage() throws MqttException, ConnectionException {
////        ActuatingDevice device = DeviceAPIFactory.INSTANCE.createActuatingDevice();
////        MqttConnectionImpl dc = new MqttConnectionImpl(host, username, password, Thread.currentThread().getStackTrace()[1].getMethodName());
////
////        dc.connect();
////
////        SensorServer server = new SensorServer("temperature_sensor", "temperature_sensor_result");
////
////
////
////        device.setConnectionHandler(dc);
////
////        Location location = new Location("","","", new Position("",""));
////        Property[] properties = new Property[2];
////        properties[0] = new DevicePropertyImpl("","status", "boolean", "on/off", "", "true");
////        properties[0] = new DevicePropertyImpl("", "temperature", "Number", "0.0", "", "false");
////        PayloadTemperatureSensor payload = new PayloadTemperatureSensor("Sensor", "",
////                "temperature-sensor", "temperature-sensor", "","","",
////                "xx", new String[]{"temperature, humidity"}, location, properties);
////
////        AbstractMessage message = new MessageBuilder()
////                .setUrl("m24.cloudmqtt.com:10933")
////                .setTopic("temperature_sensor")
////                .setResponseTopic("temperature_sensor_result")
////                .setTo("1")
////                .setFrom("2")
////                .setMessageType(payload.getClass().getName())
////                .addPayload(payload)
////                .build();
////        AbstractMessage result = ((MqttConnectionImpl)device.getConnectionHandler()).sendAndWaitResponse(message);
////
////        assertEquals(((PayloadTemperatureSensorResult)result.getPayload()).getValue(),23.23);
////        server.closeServer();
////    }
////
////
////    public void testAction() throws Exception {
////        SensorServer server = new SensorServer("temperature_sensor", "temperature_sensor_result");
////
////        DeviceBean device = new DeviceBean();
////        device.doInit();
////        assertEquals(23.23, device.getTemperature());
////
////        device.doStop();
////        server.closeServer();
////    }
//}