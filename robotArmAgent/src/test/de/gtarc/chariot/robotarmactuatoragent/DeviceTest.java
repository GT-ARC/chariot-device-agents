package de.gtarc.chariot.robotarmagent;


import de.gtarc.chariot.connectionapi.impl.WebSocketServerImpl;
import de.gtarc.chariot.robotarmagent.testserver.WSServerSocket;
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
    public void testGetCurrentSpeed() throws Exception {
        DeviceBean device = new DeviceBean();
        device.doInit();
        Thread.sleep(1000); // Waiting the update of the conveytor belt model
        assertEquals(device.getCurrentSpeed(), "25");

        device.doStop();
    }

    @Test
    public void testSetCurrentSpeed() throws Exception {
        DeviceBean device = new DeviceBean();
        device.doInit();
        Thread.sleep(1000); // Waiting the update of conveytor belt model

        device.setCurrentSpeed("75.0");
        assertEquals(device.getCurrentSpeed(), "75.0");
        device.doStop();

    }


}
