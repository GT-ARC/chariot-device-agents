package de.gtarc.chariot.deliveryrobotactuatoragent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.*;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public class DeviceBean extends DeviceAgent {

    public static final String ACTION_GET_DRIVER_STATUS = "de.gtarc.chariot.deliveryrobotactuatoragent.DeviceBean#getDriverStatus";
    public static final String ACTION_GET_BATTERY_LEVEL = "de.gtarc.chariot.deliveryrobotactuatoragent.DeviceBean#getBatteryLevel";

    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";
    static private String serverPort = "8887";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;

    @Override
    public void doStart() throws Exception {
        setEntity(
                new DeviceBuilder()
                        .setName("delivery-robot")
                        .setUuid(getEntityId())
                        .setDeviceLocation(
                                new Location(
                                        "Warehouse", "Room",
                                        "Warehouse", 0,
                                        new Position(0, 0, "0"),
                                        new Indoorposition("0", 0, 0)
                                )
                        )
                        .setVendor("GT-ARC GmbH")
                        .setType(IoTEntity.SENSOR)
                        .setConnectionHandler(wsClient  = new WebSocketClientImpl((mhandler = new WSEndpoint(this))))
                        .addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime()).setKey("status")
                                .setType(ValueTypes.BOOLEAN).setValue(false).setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime()).setKey("batteryLevel")
                                .setType(ValueTypes.NUMBER).setValue(0.0).setUnit("percent").setWritable(false)
                                .buildDeviceProperty())
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
//        String value;
//        for(String key : keys){
//            value = inputs.get(key).getAsString();
//            updateProperty(key,value);
//        }
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // leave empty, since this is a sensor
    }

    @Expose(name = ACTION_GET_DRIVER_STATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getDriverStatus() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("status")).findFirst();
        return ((DeviceProperty)prop.get()).getValue().toString();
    }

    @Expose(name = ACTION_GET_BATTERY_LEVEL, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getBatteryLevel() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("batteryLevel")).findFirst();
        return (prop.get()).getValue().toString();
    }
}