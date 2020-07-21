package de.gtarc.chariot.infraredsensoragent.iolite;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgentForRE;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public class DeviceBean extends DeviceAgentForRE {

    public static final String ACTION_GET_STATUS = "de.gtarc.chariot.infraredsensoragent.DeviceBean#getStatus";
    public static final String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.infraredsensoragent.DeviceBean#updateEntityProperty";


    @Override
    public void doStart() throws Exception {
        this.setEntity(
                new DeviceBuilder()
                        .setName("infrared-sensor")
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
                        .addProperty(new DevicePropertyBuilder()
                                .setTimestamp(new Date().getTime()).setKey("status")
                                .setType(ValueTypes.BOOLEAN).setValue(false).setUnit("")
                                .setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder()
                                .setTimestamp(new Date().getTime()).setKey("detected")
                                .setType(ValueTypes.BOOLEAN).setValue(false)
                                .setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .buildSensingDevice()
        );
        this.register();
    }

    /**
     * Data on Device Update comes through the extern request
     * @param key
     * @param value
     */
    @Expose(name = ACTION_UPDATE_ENTITY_PROPERTY, scope = ActionScope.GLOBAL)
    public void updateEntityProperty(String key, Object value) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            ( i).setValue(value);
            updateProperty(i);
        });
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
//            updateIoTEntityProperty(key,value);
//        }
    }

    @Expose(name = ACTION_GET_STATUS, scope = ActionScope.GLOBAL, returnTypes = {})
    public String getStatus(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("detected")).findFirst();
        return (prop.get()).getValue().toString();

    }

}
