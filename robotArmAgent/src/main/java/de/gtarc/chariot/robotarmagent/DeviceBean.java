package de.gtarc.chariot.robotarmagent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.impl.WebSocketClientImpl;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.deviceapi.impl.ComplexDevicePropertyImpl;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.messageapi.payload.PayloadEntityProperty;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.Property;
import de.gtarc.commonapi.utils.*;

import java.util.*;

public class DeviceBean extends DeviceAgent {

    // web socket communication between device and agent
    static public String wsTopic = "/";
    static private String serverIPAddress = "10.0.6.89";
    static private String serverPort = "10000";
    
    WebSocketClientImpl wsClient = null;
    WSEndpoint mhandler = null;

    @Override
    public void doInit() throws Exception {
        long timestamp = new Date().getTime();
        setEntity( new DeviceBuilder()
                .setUuid(getEntityId())
                .setName("robot-arm")
                .setDeviceLocation(
                        new Location(
                                "PL-Location", "Room",
                                "Production Line", 0,
                                new Position(0, 0, "0"),
                                new Indoorposition("0", 0, 0)
                        )
                )
                .setVendor("GT-ARC GmbH")
                .setType(IoTEntity.ACTUATOR)
                .setConnectionHandler(wsClient = new WebSocketClientImpl(mhandler = new WSEndpoint(this)))
                .addProperty(new DevicePropertyImpl(timestamp, "status", ValueTypes.BOOLEAN, false, "", false))
                .addProperty(new DevicePropertyImpl(timestamp, "switch", ValueTypes.BOOLEAN, false, "", true))
                .addProperty(new DevicePropertyImpl(timestamp, "speed", ValueTypes.NUMBER, 1, "Hz",true, 0.0, 10.0))
                .addProperty(new ComplexDevicePropertyImpl(timestamp, "calibration", ValueTypes.ARRAY, Arrays.asList(
                        new DevicePropertyImpl(timestamp, "calibration", ValueTypes.BOOLEAN, false, "", true),
                        new DevicePropertyImpl(timestamp, "pickX", ValueTypes.NUMBER, 264.11, "", false, 0.0, 300.0),
                        new DevicePropertyImpl(timestamp, "pickY", ValueTypes.NUMBER, 52.79, "", false, 0.0, 60.0),
                        new DevicePropertyImpl(timestamp, "pickZ", ValueTypes.NUMBER, 12.72, "", true, 0.0, 20.0),
                        new DevicePropertyImpl(timestamp, "placeX", ValueTypes.NUMBER, 244.50, "", true, 0.0, 300.0),
                        new DevicePropertyImpl(timestamp, "placeY", ValueTypes.NUMBER, -206.10, "", true, -300.0, 300.0),
                        new DevicePropertyImpl(timestamp, "placeZ", ValueTypes.NUMBER, -35.85, "", true, -100.0, 100.0),
                        new DevicePropertyImpl(timestamp, "placeInt", ValueTypes.NUMBER, 90.0, "", false, 0.0, 100.0),
                        new DevicePropertyImpl(timestamp, "placeIntZ", ValueTypes.NUMBER, 26, "", false, 0.0, 50.0)
                ), "", true))

                .addProperty( new ComplexDevicePropertyImpl(timestamp, "placeRules", ValueTypes.ARRAY, Arrays.asList(
                        new DevicePropertyImpl(timestamp, "placeLocR", ValueTypes.NUMBER, 0, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "placeLocG", ValueTypes.NUMBER, 1, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "placeLocB", ValueTypes.NUMBER, 2, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimR", ValueTypes.NUMBER, 3, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimG", ValueTypes.NUMBER, 3, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimB", ValueTypes.NUMBER, 3, "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "storageLim", ValueTypes.NUMBER, 3, "", true, 0.0, 3.0)
            ), "", true))
                .addProperty(new DevicePropertyImpl(0, "humanInteraction", ValueTypes.BOOLEAN, false, "", true))
                .buildActuating());

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
    @Expose(name = Constants.ACTION_UPDATE_ENTITY_PROPERTY, scope = ActionScope.GLOBAL)
    public void updateEntityProperty(String key, Object value) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            (i).setValue(value);
            log.info("\n\n"+i.getKey() +" "+ i.getValue());
            updateProperty(i);
            //updateIoTEntityProperty(i);
        });
    }

    @Expose(name = Constants.ACTION_GET_CURRENT_SPEED, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getCurrentSpeed() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.speed)).findFirst();
        return ((DeviceProperty)prop.get()).getValue().toString();

    }
    @Expose(name = Constants.ACTION_SET_CURRENT_SPEED, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setCurrentSpeed(String newCurrentSpeed) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.speed)).findFirst().ifPresent(i -> {
            ((DeviceProperty) i).setValue(newCurrentSpeed);
        });

    }

    @Expose(name = Constants.ACTION_GET_CURRENT_DEVICESTATUS, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getCurrentDeviceStatus() {
        Optional<Property> prop = getDevice().getProperties().stream().filter(i -> i.getKey().equals(Constants.status)).findFirst();
        return ((DeviceProperty)prop.get()).getValue().toString();
    }

    /*
    @Expose(name = Constants.ACTION_SEND_REQUEST_WITHCALIBRATION, scope = ActionScope.GLOBAL, returnTypes = {})
    public void sendRequestWithCalibration() {
        dobotArmDeviceModel.sendRequestWithCalibration();
    }
        @Expose(name = Constants.ACTION_SET_CALIBRATION_PARAMETERS, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setCalibrationParameters(String x, String y, String z, String px, String py, String pz, String pint, String pintz) {
        dobotArmDeviceModel.setCalibrationParameters(x, y, z, px, py, pz, pint, pintz);
    }

    @Expose(name = Constants.ACTION_SET_PLACERULES_PARAMETERS, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceRulesParameters(String r, String g, String b, String limr, String limg, String limb, String slim) {
        dobotArmDeviceModel.setPlaceRulesParameters(r, g, b, limr, limg, limb, slim);
    }
    @Expose(name = Constants.ACTION_GET_HUMAN_INTERACTION, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getHumanInteraction() {
        return dobotArmDeviceModel.getHumanInteraction();
    }
    @Expose(name = Constants.ACTION_SET_HUMAN_INTERACTION, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setHumanInteraction(String newHumanInteraction) {
        dobotArmDeviceModel.setHumanInteraction(newHumanInteraction);
    }

    @Expose(name = Constants.ACTION_GET_PICK_X, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPickX() {
        return dobotArmDeviceModel.getPickX();
    }
    @Expose(name = Constants.ACTION_SET_PICK_X, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPickX(String newPickX) {
        dobotArmDeviceModel.setPickX(newPickX);
    }

    @Expose(name = Constants.ACTION_GET_PICK_Y, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPickY() {
        return dobotArmDeviceModel.getPickY();
    }
    @Expose(name = Constants.ACTION_SET_PICK_Y, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPickY(String newPickY) {
        dobotArmDeviceModel.setPickY(newPickY);
    }

    @Expose(name = Constants.ACTION_GET_PICK_Z, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPickZ() {
        return dobotArmDeviceModel.getPickZ();
    }
    @Expose(name = Constants.ACTION_SET_PICK_Z, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPickZ(String newPickZ) {
        dobotArmDeviceModel.setPickZ(newPickZ);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_X, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceX() {
        return dobotArmDeviceModel.getPlaceX();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_X, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceX(String newPlaceX) {
        dobotArmDeviceModel.setPlaceX(newPlaceX);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_Y, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceY() {
        return dobotArmDeviceModel.getPlaceY();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_Y, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceY(String newPlaceY) {
        dobotArmDeviceModel.setPlaceY(newPlaceY);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_Z, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceZ() {
        return dobotArmDeviceModel.getPlaceZ();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_Z, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceZ(String newPlaceZ) {
        dobotArmDeviceModel.setPlaceZ(newPlaceZ);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_INT, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceInt() {
        return dobotArmDeviceModel.getPlaceInt();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_INT, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceInt(String newPlaceInt) {
        dobotArmDeviceModel.setPlaceInt(newPlaceInt);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_INTZ, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceIntZ() {
        return dobotArmDeviceModel.getPlaceIntZ();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_INTZ, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceIntZ(String newPlaceIntZ) {
        dobotArmDeviceModel.setPlaceIntZ(newPlaceIntZ);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_LOCR, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceLocR() {
        return dobotArmDeviceModel.getPlaceLocR();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_LOCR, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceLocR(String newPlaceLocR) {
        dobotArmDeviceModel.setPlaceLocR(newPlaceLocR);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_LOCG, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceLocG() {
        return dobotArmDeviceModel.getPlaceLocG();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_LOCG, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceLocG(String newPlaceLocG) {
        dobotArmDeviceModel.setPlaceLocG(newPlaceLocG);
    }

    @Expose(name = Constants.ACTION_GET_PLACE_LOCB, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPlaceLocB() {
        return dobotArmDeviceModel.getPlaceLocB();
    }
    @Expose(name = Constants.ACTION_SET_PLACE_LOCB, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPlaceLocB(String newPlaceLocB) {
        dobotArmDeviceModel.setPlaceLocB(newPlaceLocB);
    }

    @Expose(name = Constants.ACTION_GET_BOX_LIMR, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getBoxLimR() {
        return dobotArmDeviceModel.getBoxLimR();
    }
    @Expose(name = Constants.ACTION_SET_BOX_LIMR, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setBoxLimR(String newBoxLimR) {
        dobotArmDeviceModel.setBoxLimR(newBoxLimR);
    }

    @Expose(name = Constants.ACTION_GET_BOX_LIMG, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getBoxLimG() {
        return dobotArmDeviceModel.getBoxLimG();
    }
    @Expose(name = Constants.ACTION_SET_BOX_LIMG, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setBoxLimG(String newBoxLimG) {
        dobotArmDeviceModel.setBoxLimG(newBoxLimG);
    }

    @Expose(name = Constants.ACTION_GET_BOX_LIMB, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getBoxLimB() {
        return dobotArmDeviceModel.getBoxLimB();
    }
    @Expose(name = Constants.ACTION_SET_BOX_LIMB, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setBoxLimB(String newBoxLimB) {
        dobotArmDeviceModel.setBoxLimB(newBoxLimB);
    }

    @Expose(name = Constants.ACTION_GET_STORAGE_LIM, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getStorageLim() {
        return dobotArmDeviceModel.getStorageLim();
    }
    @Expose(name = Constants.ACTION_SET_STORAGE_LIM, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setStorageLim(String newStorageLim) {
        dobotArmDeviceModel.setStorageLim(newStorageLim);
    }

     */

}