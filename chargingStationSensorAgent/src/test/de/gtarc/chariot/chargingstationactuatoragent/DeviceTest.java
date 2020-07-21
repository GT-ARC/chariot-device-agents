package de.gtarc.chariot.chargingstationactuatoragent;

import de.gtarc.chariot.chargingstationactuatoragent.DeviceBean;
import de.gtarc.chariot.chargingstationactuatoragent.testserver.WSServerSocket;
import de.gtarc.chariot.connectionapi.DeviceConnection;
import de.gtarc.chariot.connectionapi.impl.MqttConnectionImpl;
import de.gtarc.chariot.connectionapi.impl.WebSocketServerImpl;

import de.gtarc.chariot.messageapi.AbstractMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DeviceTest {

    WebSocketServerImpl wsServer = null;

    class WSClass implements Runnable {

        @Override
        public void run() {
            wsServer = new WebSocketServerImpl();
            wsServer.addResource(WSServerSocket.class);
            wsServer.connect();
        }
    }

    @Before
    public void createWSServer() {
        Thread obj = new Thread(new WSClass());
        obj.start();
    }

    @After
    public void closeWSServer() {
        wsServer.disconnect();
    }

    @Test
    public void testGetStatus() throws Exception {
        DeviceBean device = new DeviceBean();
        device.doInit();
        Thread.sleep(1000); // Waiting the update of the conveytor belt model
        assertEquals(device.getDriverStatus(), "occupied");

        device.doStop();
    }

    @Test
    public void testSetStatus() throws Exception {
        DeviceBean device = new DeviceBean();
        device.doInit();
        Thread.sleep(1000); // Waiting the update of conveytor belt model

        device.setDriverStatus("non-occupied");
        assertEquals(device.getDriverStatus(), "non-occupied");
        device.doStop();

    }


}
