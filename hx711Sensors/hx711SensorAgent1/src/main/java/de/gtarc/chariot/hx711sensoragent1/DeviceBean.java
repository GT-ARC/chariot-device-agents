package de.gtarc.chariot.hx711sensoragent1;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.impl.WebSocketServerImpl;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/***
 * Current Sensor integrated in the conveyorbelt motor communicates with its device over websocket.
 * @author cemakpolat
 */
public class DeviceBean extends DeviceAgent {
    // actions
    public static final String ACTION_GET_WEIGHT = "de.gtarc.chariot.hx711sensoragent1.DeviceBean#getWeight";
    public static final String ACTION_GET_NUMBER_OF_OBJECTS = "de.gtarc.chariot.hx711sensoragent1.DeviceBean#getNumberOfObjects";

    // web socket communication between device and agent
    //static public String wsTopic = "/";
    //static private String serverIPAddress = "10.0.6.89";
    static private int serverPort = 10000;
    WebSocketServerImpl wsServer = null;

    @Override
    public void doStart() throws Exception {

        setEntity(
                new DeviceBuilder()
                        .setName("load-sensor-1")
                        .setUuid(getEntityId())
                        .setDeviceLocation(
                                new Location(
                                        "PL-Location", "Room",
                                        "Production Line", 0,
                                        new Position(0, 0, "0"),
                                        new Indoorposition("0", 0, 0)
                                )
                        )
                        .setVendor("GT-ARC GmbH")
                        .setType(IoTEntity.SENSOR)
                        .setConnectionHandler(wsServer  = new WebSocketServerImpl(serverPort))
                        .addProperty(new DevicePropertyBuilder()
                                .setTimestamp(new Date().getTime()).setKey("status")
                                .setType(ValueTypes.BOOLEAN).setValue(false).setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("weight").setType(ValueTypes.NUMBER).setValue(0)
                                .setUnit("gr").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("numOfObjects").setType(ValueTypes.NUMBER).setValue(0)
                                .setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .buildSensingDevice()

        );
        register();

        wsServer.addResource(WSEndpoint.class);
        wsServer.connect();
    }

	@Override
    public  void execute() {
        // server is open and the  measurements will be incoming
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


    @Expose(name = ACTION_GET_WEIGHT, scope = ActionScope.NODE, returnTypes = {Integer.class})
    public int getWeight(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("weight")).findFirst();
        return (Integer) ((DeviceProperty)prop.get()).getValue();
    }

    @Expose(name = ACTION_GET_NUMBER_OF_OBJECTS, scope = ActionScope.NODE, returnTypes = {Integer.class})
    public int getNumberOfObjects() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("numOfObjects")).findFirst();
        return (Integer) ((DeviceProperty)prop.get()).getValue();

    }

}