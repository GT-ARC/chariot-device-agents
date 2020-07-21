package de.gtarc.chariot.hx711sensoragent2.iolite;

import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.impl.WebSocketServerImpl;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.chariot.registrationapi.agents.DeviceAgentForRE;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;

public class DeviceBean extends DeviceAgentForRE {
    WebSocketServerImpl wsServer = null;
    static private int serverPort = 10000;

    private final static String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.hx711sensoragent2.DeviceBean#updateProperty";
    public static final String ACTION_GET_WEIGHT = "de.gtarc.chariot.hx711sensoragent1.DeviceBean#getWeight";
    public static final String ACTION_GET_NUMBER_OF_OBJECTS = "de.gtarc.chariot.hx711sensoragent2.DeviceBean#getNumberOfObjects";
    @Override
    public void doStart() throws Exception {
        setEntity(
                new DeviceBuilder()
                        .setName("hx711-load-sensor-2")
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
    }

    /**
     * Data on Device Update comes through the extern request
     * @param key
     * @param value
     */
    @Expose(name = ACTION_UPDATE_ENTITY_PROPERTY, scope = ActionScope.GLOBAL)
    public void updateProperty(String key, Object value) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(value);
            updateProperty(i);
            updateIoTEntityProperty(i);
        });
    }

    @Override
    @Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
    public void handleProperty(String message) {

    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // no update required for sensor
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
