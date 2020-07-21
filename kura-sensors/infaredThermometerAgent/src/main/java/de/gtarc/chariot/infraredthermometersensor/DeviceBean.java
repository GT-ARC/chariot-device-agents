
package de.gtarc.chariot.infraredthermometersensor;

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

	public static final String ACTION_GET_INFRARED_TEMPERATURE = "de.gtarc.chariot.infraredtemperaturesensor.DeviceBean#getInfraredTemperature";
	public static final String ACTION_GET_AMBIENT_TEMPERATURE = "de.gtarc.chariot.infraredtemperaturesensor.DeviceBean#getAmbientTemperature";


	@Override
	public void doStart() throws Exception {
		setEntity(
				new DeviceBuilder()
						.setName("Infrared-MLX90614-sensor")
						.setUuid(getEntityId())
						.setType(IoTEntity.SENSOR)
						.setVendor("GT-ARC")
						.setDeviceLocation(
								new Location(
										"Warehouse", "Room", "Warehouse", 11,
										new Position(0, 0, "0"),
										new Indoorposition("0", 0, 0)
								)
						).
						addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("status").setType(ValueTypes.BOOLEAN).setValue(false)
								.setUnit("").setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("infraredTemperature").setType(ValueTypes.NUMBER).setValue(0).setUnit("celcius")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("ambientTemperature").setType(ValueTypes.NUMBER).setValue(0).setUnit("celcius")
								.setWritable(false).buildDeviceProperty())
						.buildSensingDevice()
		);
		register();
	}

	@Override
	@Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
	public void handleProperty(String message) {
		JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
		String command = jsonObject.get("command").getAsString();
		JsonObject inputs = jsonObject.get("inputs").getAsJsonObject();
		Set<String> keys = inputs.keySet();

	}

	@Expose(name = ACTION_GET_INFRARED_TEMPERATURE, scope = ActionScope.GLOBAL)
	public String getInfraredTemperature(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("infraredTemperature")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_AMBIENT_TEMPERATURE, scope = ActionScope.GLOBAL)
	public String getAmbientTemperature(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("ambientTemperature")).findFirst();
		return (prop.get()).getValue().toString();
	}
}
