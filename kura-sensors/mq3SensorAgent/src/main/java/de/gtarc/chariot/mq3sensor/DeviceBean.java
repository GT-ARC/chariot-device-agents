package de.gtarc.chariot.mq3sensor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyBuilder;
import de.gtarc.chariot.registrationapi.agents.DeviceAgentForRE;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.Indoorposition;
import de.gtarc.commonapi.utils.Location;
import de.gtarc.commonapi.utils.Position;
import de.gtarc.commonapi.utils.ValueTypes;

import java.util.Date;
import java.util.Optional;
import java.util.Set;


public class DeviceBean extends DeviceAgentForRE {

	public static final String ACTION_GET_CO = "de.gtarc.chariot.co2sensoragent.DeviceBean#getCO";
	public static final String ACTION_GET_ALCOHOL = "de.gtarc.chariot.co2sensoragent.DeviceBean#getAlcohol";
	public static final String ACTION_GET_CH4 = "de.gtarc.chariot.co2sensoragent.DeviceBean#getCH4";
	public static final String ACTION_GET_LPG = "de.gtarc.chariot.co2sensoragent.DeviceBean#getLPG";
	public static final String ACTION_GET_BENZINE = "de.gtarc.chariot.co2sensoragent.DeviceBean#getBenzine";
	public static final String ACTION_GET_HEXANE = "de.gtarc.chariot.co2sensoragent.DeviceBean#getHexane";


	@Override
	public void doStart() throws Exception {
		setEntity(
				new DeviceBuilder()
						.setName("mq3-sensor")
						.setUuid(getEntityId())
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
								.setKey("CO").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("Alcohol").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("CH4").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("LPG").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("Benzine").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("Hexane").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
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

	@Expose(name = ACTION_GET_CO, scope = ActionScope.GLOBAL)
	public String getCO(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("CO")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_ALCOHOL, scope = ActionScope.GLOBAL)
	public String getAlcohol(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("Alcohol")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_CH4, scope = ActionScope.GLOBAL)
	public String getCH4(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("CH4")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_LPG, scope = ActionScope.GLOBAL)
	public String getLPG(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("LPG")).findFirst();
		return (prop.get()).getValue().toString();
	}

	@Expose(name = ACTION_GET_BENZINE, scope = ActionScope.GLOBAL)
	public String getBenzine(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("Bentine")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_HEXANE, scope = ActionScope.GLOBAL)
	public String getHexane(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("Hexane")).findFirst();
		return (prop.get()).getValue().toString();
	}
}
