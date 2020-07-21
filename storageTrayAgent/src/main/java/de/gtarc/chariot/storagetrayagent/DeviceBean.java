package de.gtarc.chariot.storagetrayagent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;

import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.*;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DeviceBean extends DeviceAgent {
    public static final String ACTION_GET_STORAGE_TRAY_STATUS = "de.gtarc.chariot.storagetrayagent.DeviceBean#getStorageTrayStatus";
    public static final String ACTION_GET_ALL_STORAGE_TRAY_STATUS = "de.gtarc.chariot.storagetrayagent.DeviceBean#getAllStorageTrayStatus";


    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";//"10.0.6.89";
    static private String serverPort = "8887";//"8887";

    String deviceId = "b1f114f5-f47f-449d-b579-6605bba06110";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;

    @Override
    public void doInit() throws Exception {
        Device device = new DeviceBuilder()
                .setName("storage-tray")
                .setUuid(getEntityId())
                .setDeviceLocation(
                        new Location(
                                "Warehouse", "Room",
                                "Warehouse", 0,
                                new Position(0, 0, "0"),
                                new Indoorposition("0", 0, 0)
                        )
                ).setVendor("GT-ARC GmbH")
                .setType(IoTEntity.SENSOR)
                .setConnectionHandler(wsClient = new WebSocketClientImpl((mhandler = new WSEndpoint(this))))
                .addProperty(new DevicePropertyImpl(0, "status", ValueTypes.BOOLEAN, true, "", false))
                .buildSensingDevice();

        for (int i = 1;i < 20; i++) {
            device.addProperty(new DevicePropertyImpl(0, "tray-"+i+"", ValueTypes.STRING, "empty", "", false));
        }
        setEntity( device);
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
//        for(String key : keys){
//            value = inputs.get(key).getAsString();
//            updateProperty(key,value);
//        }
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // leave empty, since this color device is a sensor
    }

    @Expose(name = ACTION_GET_ALL_STORAGE_TRAY_STATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getAllDeliveryTrayStatus() {
        List<String> list = new ArrayList<String>();
        getDevice().getProperties().stream().filter(i -> i.getKey().contains("tray-")).forEach(i ->{
            list.add((i).getValue().toString());
        });
        return list.toString();
    }
    @Expose(name = ACTION_GET_STORAGE_TRAY_STATUS, paramNames = {"trayNumber"}, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getDeliveryTrayStatus(int trayNumber) {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("tray-"+trayNumber)).findFirst();
        return (prop.get()).getValue().toString();
    }
}

