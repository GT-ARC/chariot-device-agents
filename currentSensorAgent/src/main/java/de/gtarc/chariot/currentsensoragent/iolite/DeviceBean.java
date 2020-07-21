package de.gtarc.chariot.currentsensoragent.iolite;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;

import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgentForRE;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public class DeviceBean extends DeviceAgentForRE {

    private final static String ACTION_GET_CURRENT = "de.gtarc.chariot.currentsensoragent.DeviceBean#getCurrent";

    @Override
    public void doStart() throws Exception {
        this.setEntity(
                new DeviceBuilder()
                        .setName("current-sensor")
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
                        .addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime()).setKey("status")
                                .setType(ValueTypes.BOOLEAN).setValue(false).setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime()).setKey("current")
                                .setType(ValueTypes.NUMBER).setValue(0).setUnit("A").setWritable(false)
                                .buildDeviceProperty())
                        .buildSensingDevice()
        );
        this.register();
    }

    @Override
    @Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
    public void handleProperty(String message) {
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        String command = jsonObject.get("command").getAsString();
        JsonObject inputs = jsonObject.get("inputs").getAsJsonObject();
        Set<String> keys = inputs.keySet();
        String value;
        for(String key : keys){
            value = inputs.get(key).getAsString();
            updateProperty(key,value);
            updateIoTEntityProperty(key,value);
        }
    }


    @Expose(name = ACTION_GET_CURRENT, scope = ActionScope.GLOBAL, returnTypes = {})
    public int getCurrent(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("current")).findFirst();
        return (Integer)(prop.get()).getValue();
    }

}
