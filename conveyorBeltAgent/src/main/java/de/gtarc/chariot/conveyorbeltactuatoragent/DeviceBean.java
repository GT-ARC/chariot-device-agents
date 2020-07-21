package de.gtarc.chariot.conveyorbeltactuatoragent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.*;
import de.gtarc.chariot.deviceapi.impl.ComplexDevicePropertyImpl;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.*;

public class DeviceBean extends DeviceAgent {

    private final static String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.conveyorbeltactuatoragent.DeviceBean#updateProperty";

    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.1.1.201";
    static private String serverPort = "10000";

    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;
    private boolean dontConnect = false;

    @Override
    public void doStart() throws Exception {
        long timestamp = new Date().getTime();
        this.setEntity(
                new DeviceBuilder()
                        .setName("conveyorbelt")
                        .setUuid(getEntityId())
                        .setDeviceLocation(
                                new Location(
                                        "PL-Line", "Room",
                                        "Production Line", 0,
                                        new Position(0, 0, "0"),
                                        new Indoorposition("0", 0, 0)
                                )
                        )
                        .setVendor("GT-ARC GmbH")
                        .setType(IoTEntity.ACTUATOR)
                        .setConnectionHandler(wsClient = new WebSocketClientImpl((mhandler = new WSEndpoint(this))))
                        .addProperty(new DevicePropertyImpl(timestamp, "status", ValueTypes.BOOLEAN, false, "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "speed", ValueTypes.NUMBER, 15.6444366027839, "r/min", true))
                        .addProperty(new DevicePropertyImpl(timestamp, "acceleration", ValueTypes.NUMBER, 0.027874690631694, "r/min2", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "current", ValueTypes.NUMBER, 0.221145663484359, "A", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "power_in", ValueTypes.NUMBER, 1.98004988138504, "W", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "temperature", ValueTypes.NUMBER, 50, "C", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "load_torque", ValueTypes.NUMBER, 0, "Nm", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "switch", ValueTypes.BOOLEAN, true, "", false))
                        .addProperty(new ComplexDevicePropertyImpl(timestamp, "contSpeedReq", ValueTypes.ARRAY,
                                Arrays.asList(
                                        new DevicePropertyImpl(timestamp, "starting_speed", ValueTypes.NUMBER, 5.0, "r/min", false),
                                        new DevicePropertyImpl(timestamp, "ending_speed", ValueTypes.NUMBER, 35.0, "r/min", true, 0d, 100d),
                                        new DevicePropertyImpl(timestamp, "numberofsteps", ValueTypes.NUMBER, 30.0, "", true, 0d, 100d),
                                        new DevicePropertyImpl(timestamp, "step_time", ValueTypes.NUMBER, 2.0, "secs", true, 0d, 100d),
                                        new DevicePropertyImpl(timestamp, "oneTimeSpeedReq", ValueTypes.NUMBER, 25.0, "r/min", true, 5d, 40d)
                                ), "", false))
                        .addProperty(new ComplexDevicePropertyImpl(timestamp, "torqueRequest", ValueTypes.ARRAY, Arrays.asList(
                                new DevicePropertyImpl(timestamp, "torque_behavior", ValueTypes.STRING, "abnormal", "", true),
                                new DevicePropertyImpl(timestamp, "type", ValueTypes.STRING, "log", "", true),
                                new DevicePropertyImpl(timestamp, "param1", ValueTypes.NUMBER, 0.1, "", true),
                                new DevicePropertyImpl(timestamp, "param2", ValueTypes.NUMBER, 1.0, "", true),
                                new DevicePropertyImpl(timestamp, "holdTime", ValueTypes.NUMBER, 3.0, "", true)
                        ), "", false))
                        .buildActuating()

        );
        register();
        wsClient.setConnectionURI("ws://" + serverIPAddress + ":" + serverPort + wsTopic);
        log.info("Device Bean started");
    }

    @Override
    public void execute() {
        if (dontConnect) return;

        try {
            wsClient.connect();
            System.out.println("\n\n");
            log.info("MyRequestID {" + WSEndpoint.myRequestID + "}: Send message to environment to receive data");
            wsClient.sendMessage(mhandler.sendRequestToDevice());
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        if (property != null) {
            DevicePropertyImpl entityProperty = (DevicePropertyImpl) property;
            log.info("\n\n"+entityProperty.getKey() +" "+ entityProperty.getValue());
            try {
                wsClient.connect();
                wsClient.sendMessage(mhandler.sendRequestToDevice(entityProperty.getKey(), entityProperty.getValue().toString()));
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateIoTEntityProperty(String key, String value) {
        try {
            wsClient.connect();
            wsClient.sendMessage(mhandler.sendRequestToDevice(key, value));
        } catch (ConnectionException e) {
            e.printStackTrace();
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
        log.info("Handle Property: " + jsonObject);
        for (String key : keys) {
            value = inputs.get(key).getAsString();
            Object insertValue = value;
            log.info(value);
            try {
                insertValue = Double.valueOf(value);
                updateProperty(key, insertValue);
                updateIoTEntityProperty(key, value);
                return;
            } catch (NumberFormatException e) {}
            try {
                if(value.equals("true") || value.equals("false")) {
                    insertValue = value.equals("true");
                    updateProperty(key, insertValue);
                    updateIoTEntityProperty(key, value);
                }
            } catch (NumberFormatException e) {}
            updateProperty(key, insertValue);
            updateIoTEntityProperty(key, value);
        }
    }

    /**
     * Data on Device Update comes through the extern request
     *
     * @param key
     * @param value
     */
    @Expose(name = ACTION_UPDATE_ENTITY_PROPERTY, scope = ActionScope.GLOBAL)
    public void updateEntityProperty(String key, Object value) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            (i).setValue(value);
            log.info("\n\n"+i.getKey() +" "+ i.getValue());
            updateProperty(i);
            //updateIoTEntityProperty(i);
        });
    }

    @Expose(name = Constants.ACTION_GET_CURRENT_DEVICESTATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getCurrentDeviceStatus() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("status")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    /// Agent actions to call the conveyor belt methods

    @Expose(name = Constants.ACTION_GET_SPEED, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getSpeed() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("speed")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_SPEED, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setSpeed(String newSpeed) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals("speed")).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newSpeed);
        });
    }

    // TODO: Finalize the following codes
    @Expose(name = Constants.ACTION_GET_STARTING_SPEED, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getStartingSpeed() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("starting_speed")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_STARTING_SPEED, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setStartingSpeed(String newStartingSpeed) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.starting_speed)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newStartingSpeed);
        });
    }

    @Expose(name = Constants.ACTION_GET_ENDING_SPEED, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getEndingSpeed() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("ending_speed")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_ENDING_SPEED, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setEndingSpeed(String newEndingSpeed) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.ending_speed)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newEndingSpeed);
        });
    }

    @Expose(name = Constants.ACTION_GET_NUMBEROF_STEPS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getNumberOfSteps() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("numberofsteps")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_NUMBEROF_STEPS, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setNumberOfSteps(String newNumberOfSteps) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.numberofsteps)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newNumberOfSteps);
        });
    }

    @Expose(name = Constants.ACTION_GET_STEPTIME, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getStepTime() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("step_time")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_STEPTIME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setStepTime(String newStepTime) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.step_time)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newStepTime);
        });
    }

    @Expose(name = Constants.ACTION_GET_ONETIME_STEPREQ, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getOneTimeSpeedReq() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("oneTimeSpeedReq")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_ONETIME_STEPREQ, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setOneTimeSpeedReq(String newOneTimeSpeedReq) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.oneTimeSpeedReq)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newOneTimeSpeedReq);
        });
    }

    @Expose(name = Constants.ACTION_GET_ABNORMAL, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getAbnormal() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("abnormal")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    @Expose(name = Constants.ACTION_SET_ABNORMAL, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setAbnormal(String newAbnormal) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.torque_behavior)).findFirst().ifPresent(i -> {
            ((DevicePropertyImpl) i).setValue(newAbnormal);
        });
    }

    @Expose(name = Constants.ACTION_GET_TYPE, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getType() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals("type")).findFirst();
        return ((DevicePropertyImpl) prop.get()).getValue().toString();
    }

    public static final String ACTION_STOP = "de.gtarc.chariot.DeviceBean#stopCB";
    public static final String ACTION_START = "de.gtarc.chariot.DeviceBean#startCB";
    public static final String ACTION_CONNECT = "de.gtarc.chariot.DeviceBean#connectCB";
    public static final String ACTION_DISCONNECT = "de.gtarc.chariot.DeviceBean#disconnectCB";

    @Expose(name = ACTION_STOP, scope = ActionScope.GLOBAL)
    public void stopCB() {
        log.info("Action stop is called!");
    }

    @Expose(name = ACTION_DISCONNECT, scope = ActionScope.GLOBAL)
    public void disconnect() {
        log.info("Action disconnect is called!");
        this.dontConnect = true;
        this.setSpeed("0");
        this.updateIoTEntityProperty(Constants.speed, "0");
    }

    @Expose(name = ACTION_CONNECT, scope = ActionScope.GLOBAL)
    public void connect() {
        log.info("Action connect is called!");
        this.dontConnect = false;
    }

    @Expose(name = ACTION_START, scope = ActionScope.GLOBAL)
    public void startCB() {
        log.info("Action start is called!");
    }

    /*
    @Expose(name = Constants.ACTION_SET_TYPE, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setType(String newType) {
        return getDevice().getProperties().stream().anyMatch(i -> i.getKey().equalsIgnoreCase(Constants.type)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newType);
        });
    }

    @Expose(name = Constants.ACTION_GET_PARAM1, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getParam1() {
        return deviceModel.getParam1();
    }
    @Expose(name = Constants.ACTION_SET_PARAM1, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setParam1(String newParam1) {
        return getDevice().getProperties().stream().anyMatch(i -> i.getKey().equalsIgnoreCase(Constants.param1)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newParam1);
        });
    }

    @Expose(name = Constants.ACTION_GET_PARAM2, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getParam2() {
        return deviceModel.getParam2();
    }
    @Expose(name = Constants.ACTION_SET_PARAM2, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setParam2(String newParam2) {
         return getDevice().getProperties().stream().anyMatch(i -> i.getKey().equalsIgnoreCase(Constants.param2)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newParam1);
        });
    }

    @Expose(name = Constants.ACTION_GET_HOLDTIME, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getHoldTime() {
        return deviceModel.getHoldTime();
    }
    @Expose(name = Constants.ACTION_SET_HOLDTIME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setHoldTime(String newHoldTime) {
         return getDevice().getProperties().stream().anyMatch(i -> i.getKey().equalsIgnoreCase(Constants.holdtime)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newParam1);
        });
    }

    @Expose(name = Constants.ACTION_GET_NORMAL, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getNormal() {
        return deviceModel.getNormal();
    }
    @Expose(name = Constants.ACTION_SET_NORMAL, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setNormal(String newNormal) {
         return getDevice().getProperties().stream().anyMatch(i -> i.getKey().equalsIgnoreCase(Constants.normal)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newNormal);
        });
    }
*/
}
