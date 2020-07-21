/*
import de.gtarc.chariot.deviceapi.ActuatingDevice;
import de.gtarc.chariot.deviceapi.DeviceAPIFactory;
import de.gtarc.chariot.distancesensoragent.payload.PayloadDistanceSensor;
import de.gtarc.chariot.distancesensoragent.payload.PayloadDistanceSensorResult;
import de.gtarc.chariot.distancesensoragent.connection.MqttConnection;
import de.gtarc.chariot.distancesensoragent.payload.util.ExtendedLocationDistanceSensor;
import de.gtarc.chariot.distancesensoragent.payload.util.ExtendedPropertyDistanceSensor;
import de.gtarc.chariot.distancesensoragent.payload.util.Indoorposition;
import de.gtarc.chariot.messageapi.AbstractMessage;
import de.gtarc.chariot.messageapi.impl.MessageBuilder;
import de.gtarc.chariot.registrationapi.messages.Position;
import junit.framework.TestCase;

public class DeviceTest extends TestCase {

    String host = "tcp://m24.cloudmqtt.com:10933";
    String username = "plbwvpgf";
    String password = "mJTPb6z12Bag";
    String clientId = "SensorAgent";


    public void testSendMessage() throws Exception {
        ActuatingDevice device = DeviceAPIFactory.INSTANCE.createActuatingDevice();
        MqttConnection dc = new MqttConnection(host, username, password, clientId);

        dc.connect();

        SensorServer server = new SensorServer("distance_sensor", "distance_sensor_result");

        device.setConnectionHandler(dc);

        //Instances
        ExtendedLocationDistanceSensor loc = new ExtendedLocationDistanceSensor("1000","Loc","Budapest",new Position("start_point","end_point"),"",new Indoorposition("",""));
        ExtendedPropertyDistanceSensor[] properties = new ExtendedPropertyDistanceSensor[2];
        properties[0] = new ExtendedPropertyDistanceSensor("","status","","","","","");
        properties[1] = new ExtendedPropertyDistanceSensor("","speed","","","","","");
        PayloadDistanceSensor payload = new PayloadDistanceSensor("PayloadDistanceSensor",1,"10","",1,2,3,4,"", loc, properties);

        AbstractMessage message = new MessageBuilder()
                .setUrl("m24.cloudmqtt.com:10933")
                .setTopic("distance_sensor")
                .setResponseTopic("distance_sensor_result")
                .setTo("1")
                .setFrom("2")
                .setMessageType(payload.getClass().getName())
                .addPayload(payload)
                .build();
        AbstractMessage result = ((MqttConnection)device.getConnectionHandler()).sendAndWaitResponse(message);

        assertEquals(((PayloadDistanceSensorResult)result.getPayload()).getValue(),10);
        server.closeServer();
    }
}

 */