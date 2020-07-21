package de.gtarc.chariot.colorsensoragent.re;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class DeviceBean extends DeviceAgentForRE {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static final String ACTION_GET_COLOR = "de.gtarc.chariot.colorsensoragent.DeviceBean#getColor";

	@Override
	public void doStart() throws Exception {
		this.setEntity(
				new DeviceBuilder()
						.setName("color-sensor")
						.setType(IoTEntity.SENSOR)
						.setUuid(getEntityId())
						.setDeviceLocation(
								new Location(
										"PL-Location", "Room", "Production Line", 11,
										new Position(0, 0, "0"),
										new Indoorposition("0", 0, 0)
								)
						).
						addProperty(new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("status").setType(ValueTypes.BOOLEAN).setValue(false)
								.setUnit("").setWritable(false).buildDeviceProperty())
						.addProperty( new DevicePropertyBuilder().setTimestamp(new Date().getTime())
								.setKey("color").setType(ValueTypes.STRING).setValue("red").setUnit("")
								.setWritable(false).buildDeviceProperty())
						.buildSensingDevice()
		);

		register();
		// The following scheduler updates the value in CHARIOT RE
		//scheduler.scheduleAtFixedRate(() -> updateIoTEntityProperty("color", "green"), 5, 5, TimeUnit.SECONDS);

	}


	@Override
	@Expose(name = PROPERTY_ACTION, scope = ActionScope.GLOBAL)
	public void handleProperty(String message) {
		JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
		String command = jsonObject.get("command").getAsString();
		JsonObject inputs = jsonObject.get("inputs").getAsJsonObject();
		Set<String> keys = inputs.keySet();

	}

	@Expose(name = ACTION_GET_COLOR, scope = ActionScope.GLOBAL)
	public String getColor(){
		Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("color")).findFirst();
		return (prop.get()).getValue().toString();
	}
}
