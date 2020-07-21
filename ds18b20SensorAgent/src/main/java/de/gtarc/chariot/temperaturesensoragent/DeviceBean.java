package de.gtarc.chariot.temperaturesensoragent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.IMethodExposingBean;
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


    public static final String ACTION_GET_TEMPERATURE = "de.gtarc.chariot.temperaturesensoragent.DeviceBean#getCurrent";
    private final static String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.temperaturesensoragent.DeviceBean#updateProperty";
    public static final String ACTION_GET_HUMIDITY = "de.gtarc.chariot.temperaturesensoragent.DeviceBean#getHumidity";

    @Override
    public void doStart() throws Exception {
        this.setEntity(
                new DeviceBuilder()
                        .setName("ds18b20-sensor")
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
                        .addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("status").setType(ValueTypes.BOOLEAN).setValue(false)
                                .setUnit("").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("temperature").setType(ValueTypes.NUMBER)
                                .setValue(0).setUnit("celcius").setWritable(false)
                                .buildDeviceProperty())
                        .addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
                                .setKey("humidity").setType(ValueTypes.NUMBER)
                                .setValue(0).setUnit("percent").setWritable(false)
                                .buildDeviceProperty())
                        .buildSensingDevice()
        );
        register();
    }

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

    @Expose(name = ACTION_GET_TEMPERATURE, scope = ActionScope.GLOBAL, returnTypes = {})
    public int getTemperature(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("temperature")).findFirst();
        return (Integer)((DeviceProperty)prop.get()).getValue();
    }
    @Expose(name = ACTION_GET_HUMIDITY, scope = ActionScope.GLOBAL, returnTypes = {})
    public int getHumidity(){
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("humidity")).findFirst();
        return (Integer)((DeviceProperty)prop.get()).getValue();
    }


}
