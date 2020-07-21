package de.gtarc.chariot.chargingstationsensoragent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.Device;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

/***
 * Charging Station Device is represented through this agent. The communication is performed over websocket with the device.
 * The message handling is dealth within the endpoint class, whereas this class is mainly focuses on the running agent, updating device values as well as the device in the database.
 * @author cemakpolat
 */
public class DeviceBean extends DeviceAgent {
    // actions
    public static final String ACTION_GET_CHARGING_STATUS = "de.gtarc.chariot.chargingstationagent.DeviceBean#getChargingStatus";
    public static final String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.chargingstationagent.DeviceBean#updateEntityProperty";

    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";//"10.0.6.89";
    static private String serverPort = "8887";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;
    
    @Override
    public void doStart() throws Exception {
        Device device = new DeviceBuilder()
                        .setName("charging-stations")
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
                        .addProperty(new DevicePropertyImpl(0, "status", ValueTypes.BOOLEAN, true, "", false))
                        .buildSensingDevice();

        for (int i = 1;i < 9; i++) {
            device.addProperty(new DevicePropertyImpl(0, "chargingstation-"+i+"", ValueTypes.STRING, "free", "", false));
        }
        setEntity(device);
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
        String value;
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // leave empty, since this color device is a sensor
    }

    @Expose(name = ACTION_GET_CHARGING_STATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getChargingStatus(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("occupancy")).findFirst();
        return (((DeviceProperty)prop.get()).getValue().toString());
    }

}