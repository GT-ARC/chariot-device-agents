/*
package de.gtarc.chariot.colorsensoragent;

import de.gtarc.chariot.deviceapi.ActuatingDevice;
import de.gtarc.chariot.deviceapi.DeviceAPIFactory;
import de.gtarc.chariot.messageapi.AbstractMessage;
import de.gtarc.chariot.messageapi.impl.MessageBuilder;
import de.gtarc.chariot.registrationapi.messages.Location;
import de.gtarc.chariot.registrationapi.messages.Position;
import de.gtarc.chariot.registrationapi.messages.Property;
import de.gtarc.chariot.colorsensoragent.payload.PayloadColorSensor;
import de.gtarc.chariot.colorsensoragent.payload.PayloadColorSensorResult;
import junit.framework.TestCase;
import org.eclipse.paho.client.mqttv3.MqttException;

public class DeviceTest extends TestCase {

    String host = "tcp://m24.cloudmqtt.com:10933";
    String username = "plbwvpgf";
    String password = "mJTPb6z12Bag";
    String clientId = "SensorAgent";


    public void testSendMessage() throws MqttException {
        ActuatingDevice device = DeviceAPIFactory.INSTANCE.createActuatingDevice();
        MqttConnection dc = new MqttConnection(host, username, password, Thread.currentThread().getStackTrace()[1].getMethodName());

        dc.connect();

        SensorServer server = new SensorServer("color_sensor", "color_sensor_result");



        device.setConnectionHandler(dc);

        Location location = new Location("","","", new Position("",""));
        Property[] properties = new Property[2];
        properties[0] = new Property("","status", "boolean", "on/off", "");
        properties[0] = new Property("","speed", "float", "0.020", "Hz");
        PayloadColorSensor payload = new PayloadColorSensor("Sensor", "",
                "color-sensor", "color-sensor", "","","",
                "xx", new String[]{"temperature, humidity"}, location, properties);

        AbstractMessage message = new MessageBuilder()
                .setUrl("m24.cloudmqtt.com:10933")
                .setTopic("color_sensor")
                .setResponseTopic("color_sensor_result")
                .setTo("1")
                .setFrom("2")
                .setMessageType(payload.getClass().getName())
                .addPayload(payload)
                .build();
        AbstractMessage result = ((MqttConnection)device.getConnectionHandler()).sendAndWaitResponse(message);

        assertEquals(((PayloadColorSensorResult)result.getPayload()).getValue(),10);
        server.closeServer();
    }


    public void testAction() throws Exception {
        SensorServer server = new SensorServer("color_sensor", "color_sensor_result");

        DeviceBean device = new DeviceBean();
        device.doInit();
        assertEquals(device.getColor(), 10);

        device.doStop();
        server.closeServer();
    }
}

 */