package de.gtarc.chariot.deliverytrayagent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.Device;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.Indoorposition;
import de.gtarc.commonapi.utils.Location;
import de.gtarc.commonapi.utils.Position;
import de.gtarc.commonapi.utils.ValueTypes;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class DeviceBean extends DeviceAgent {

    public static final String ACTION_GET_DELIVERY_TRAY_STATUS = "de.gtarc.chariot.deliverytrayagent.DeviceBean#getDeliveryTrayStatus";
    public static final String ACTION_GET_ALL_DELIVERY_TRAY_STATUS = "de.gtarc.chariot.deliverytrayagent.DeviceBean#getAllDeliveryTrayStatus";


    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";//""10.0.6.89";
    static private String serverPort = "8887";//"8887";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;

    @Override
    public void doInit() throws Exception {
        long timestamp = new Date().getTime();
        setEntity(new DeviceBuilder()
                .setName("delivery-tray")
                .setUuid(getEntityId())
                .setDeviceLocation(
                        new Location(
                                "Warehouse", "Room",
                                "Warehouse", 0,
                                new Position(0, 0, "0"),
                                new Indoorposition("0", 0, 0)
                        )
                ).setVendor("GT-ARC GmbH")
                .setType("sensor")
                .setConnectionHandler(wsClient = new WebSocketClientImpl((mhandler = new WSEndpoint(this))))
                .addProperty(new DevicePropertyImpl(timestamp, "status", ValueTypes.BOOLEAN, true, "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "tray-1", ValueTypes.STRING, "Free", "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "tray-2", ValueTypes.STRING, "Free", "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "tray-3", ValueTypes.STRING, "Free", "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "tray-4", ValueTypes.STRING, "Free", "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "tray-5", ValueTypes.STRING, "Free", "", false))
                .buildSensingDevice()
        );
    	register();
    	wsClient.setConnectionURI("ws://"+serverIPAddress+":"+serverPort + wsTopic);
        log.info("Device Bean started");
    }

    @Override
    public  void execute() {
        try {
            wsClient.connect();
            wsClient.sendMessage(mhandler.sendRequestToDevice());
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    @Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
    public void handleProperty(String message) {
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String command = jsonObject.get("command").getAsString();
        JsonObject inputs = jsonObject.get("inputs").getAsJsonObject();
        Set<String> keys = inputs.keySet();
        String value;
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // leave empty, since this color device is a sensor
    }


    @Expose(name = ACTION_GET_ALL_DELIVERY_TRAY_STATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getAllDeliveryTrayStatus() {
        List<String> list = new ArrayList<String>();
        getDevice().getProperties().stream().filter(i -> i.getKey().contains("tray-")).forEach(i ->{
            list.add(((DeviceProperty)i).getValue().toString());
        });
        return list.toString();
    }
    @Expose(name = ACTION_GET_DELIVERY_TRAY_STATUS, paramNames = {"trayNumber"}, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getDeliveryTrayStatus(int trayNumber) {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("tray-"+trayNumber)).findFirst();
        return ((DeviceProperty)prop.get()).getValue().toString();
    }
}