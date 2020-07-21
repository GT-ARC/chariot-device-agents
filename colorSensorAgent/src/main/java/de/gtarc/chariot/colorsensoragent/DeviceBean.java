package de.gtarc.chariot.colorsensoragent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;
import org.apache.jena.base.Sys;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

/***
 * Current Sensor integrated in the conveyorbelt motor communicates with its device over websocket.
 * @author cemakpolat
 */
public class DeviceBean extends DeviceAgent {
    // actions
    public static final String ACTION_GET_COLOR = "de.gtarc.chariot.colorsensoragent.DeviceBean#getColor";

    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";//"10.0.6.89";
    static private String serverPort = "10000";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;
    
    @Override
    public void doStart() throws Exception {

        this.setEntity(
                new DeviceBuilder()
                        .setName("current-sensor")
                        .setUuid(getEntityId())
                        .setVendor("GT-ARC GmbH")
                        .setType(IoTEntity.SENSOR)
                        .setDeviceLocation(
                                new Location("PL-Location", "Room",
                                        "Production Line",
                                        0,
                                        new Position(0, 0, "0"),
                                        new Indoorposition("0", 0, 0)
                                )
                        )
                        .setConnectionHandler(wsClient  = new WebSocketClientImpl((mhandler = new WSEndpoint(this))))
                        .addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("status").setType(ValueTypes.BOOLEAN).setValue(false)
                                .setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("color").setType(ValueTypes.STRING)
                                .setValue("red").setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .buildSensingDevice()
        );
        register();
        wsClient.setConnectionURI("ws://"+serverIPAddress+":"+serverPort + wsTopic);
    }

	@Override
    public  void execute() {
        try {
            wsClient.connect();
            wsClient.sendMessage(mhandler.sendRequestToDevice());
        } catch (ConnectionException e) {
            log.error(e.getMessage());
        }
	}

    @Override
    @Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
    public void handleProperty(String message) {
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String command = jsonObject.get("command").getAsString();
        JsonObject inputs = jsonObject.get("inputs").getAsJsonObject();
        Set<String> keys = inputs.keySet();
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // leave empty, since this color device is a sensor
    }


    @Expose(name = ACTION_GET_COLOR, scope = ActionScope.GLOBAL, returnTypes = {})
    public String getColor(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("color")).findFirst();
        return (prop.get()).getValue().toString();
    }


}