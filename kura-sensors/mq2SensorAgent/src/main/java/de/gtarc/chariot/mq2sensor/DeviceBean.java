package de.gtarc.chariot.mq2sensor;

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
	public static final String ACTION_GET_H2 = "de.gtarc.chariot.co2sensoragent.DeviceBean#getH2";
	public static final String ACTION_GET_PROPANE = "de.gtarc.chariot.co2sensoragent.DeviceBean#getPropane";
	public static final String ACTION_GET_SMOKE = "de.gtarc.chariot.co2sensoragent.DeviceBean#getSmoke";

	@Override
	public void doStart() throws Exception {
		setEntity(
				new DeviceBuilder()
						.setName("mq2-sensor")
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
								.setKey("H2").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("Propane").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
								.setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("Smoke").setType(ValueTypes.NUMBER).setValue(0).setUnit("ppm")
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
		String value;
		for(String key : keys){
			value = inputs.get(key).getAsString();
			updateProperty(key,value);
			updateIoTEntityProperty(key,value);
		}
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
	@Expose(name = ACTION_GET_H2, scope = ActionScope.GLOBAL)
	public String getH2(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("H2")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_PROPANE, scope = ActionScope.GLOBAL)
	public String getPropane(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("Propane")).findFirst();
		return (prop.get()).getValue().toString();
	}
	@Expose(name = ACTION_GET_SMOKE, scope = ActionScope.GLOBAL)
	public String getSmoke(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("Smoke")).findFirst();
		return (prop.get()).getValue().toString();
	}
}
