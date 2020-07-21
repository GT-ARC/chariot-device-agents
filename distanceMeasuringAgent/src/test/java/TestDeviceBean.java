/*

import de.gtarc.chariot.deviceapi.ActuatingDevice;
import de.gtarc.chariot.deviceapi.DeviceAPIFactory;
import de.gtarc.chariot.distancesensoragent.DeviceBean;
import de.gtarc.chariot.distancesensoragent.connection.MqttConnection;
import junit.framework.TestCase;

public class TestDeviceBean extends TestCase {

    String host = "tcp://m24.cloudmqtt.com:10933";
    String username = "plbwvpgf";
    String password = "mJTPb6z12Bag";
    String clientId = "SensorAgent";


    public void testSendMessage() throws Exception {
        ActuatingDevice device = DeviceAPIFactory.INSTANCE.createActuatingDevice();
        MqttConnection dc = new MqttConnection(host, username, password, clientId);

        dc.connect();

        SensorServer server = new SensorServer("distance_sensor", "distance_sensor_result");

        DeviceBean dv= new DeviceBean();
        dv.doInit();
        assertEquals(dv.getDistance(),10);

        dv.doStop();
        server.closeServer();
    }
}

 */