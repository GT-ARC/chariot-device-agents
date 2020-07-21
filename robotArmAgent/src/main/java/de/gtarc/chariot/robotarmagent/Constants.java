package de.gtarc.chariot.robotarmagent;

public class Constants {
    // agent action names
    public static final String ACTION_REGISTER_CLIENT = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#register";
    public static final String ACTION_GET_CURRENT_SPEED = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getCurrentSpeed";
    public static final String ACTION_SET_CURRENT_SPEED = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setCurrentSpeed";
    public static final String ACTION_GET_CURRENT_DEVICESTATUS = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getCurrentDeviceStatus";
    public static final String ACTION_SET_CURRENT_DEVICESTATUS = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setCurrentDeviceStatus";
    public static final String ACTION_SET_CALIBRATION_PARAMETERS = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setCalibrationParameters";
    public static final String ACTION_SET_PLACERULES_PARAMETERS = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceRulesParameters";
    public static final String ACTION_SEND_REQUEST = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#sendRequest";
    public static final String ACTION_SEND_REQUEST_WITHCALIBRATION = "de.gtarc.chariot.robotarmactuatoragent.DeviceBeam#sendRequestWithCalibration";
    public static final String ACTION_GET_HUMAN_INTERACTION = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getHumanInteraction";
    public static final String ACTION_SET_HUMAN_INTERACTION = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setHumanInteraction";
    public static final String ACTION_GET_PICK_X = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPickX";
    public static final String ACTION_SET_PICK_X = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPickX";
    public static final String ACTION_GET_PICK_Y = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPickY";
    public static final String ACTION_SET_PICK_Y = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPickY";
    public static final String ACTION_GET_PICK_Z = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPickZ";
    public static final String ACTION_SET_PICK_Z = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPickZ";
    public static final String ACTION_GET_PLACE_X = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceX";
    public static final String ACTION_SET_PLACE_X = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceX";
    public static final String ACTION_GET_PLACE_Y = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceY";
    public static final String ACTION_SET_PLACE_Y = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceY";
    public static final String ACTION_GET_PLACE_Z = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceZ";
    public static final String ACTION_SET_PLACE_Z = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceZ";
    public static final String ACTION_GET_PLACE_INT = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceInt";
    public static final String ACTION_SET_PLACE_INT = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceInt";
    public static final String ACTION_GET_PLACE_INTZ = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceIntZ";
    public static final String ACTION_SET_PLACE_INTZ = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceIntZ";
    public static final String ACTION_GET_PLACE_LOCR = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceLocR";
    public static final String ACTION_SET_PLACE_LOCR = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceLocR";
    public static final String ACTION_GET_PLACE_LOCG = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceLocG";
    public static final String ACTION_SET_PLACE_LOCG = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceLocG";
    public static final String ACTION_GET_PLACE_LOCB = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getPlaceLocB";
    public static final String ACTION_SET_PLACE_LOCB = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setPlaceLocB";
    public static final String ACTION_GET_BOX_LIMR = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getBoxLimR";
    public static final String ACTION_SET_BOX_LIMR = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setBoxLimR";
    public static final String ACTION_GET_BOX_LIMG = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getBoxLimG";
    public static final String ACTION_SET_BOX_LIMG = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setBoxLimG";
    public static final String ACTION_GET_BOX_LIMB = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getBoxLimB";
    public static final String ACTION_SET_BOX_LIMB = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setBoxLimB";
    public static final String ACTION_GET_STORAGE_LIM = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#getStorageLim";
    public static final String ACTION_SET_STORAGE_LIM = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#setStorageLim";
    public final static String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.robotarmactuatoragent.DeviceBean#updateProperty";
    // dobot features

    // actions to be done
    public static final String action_switch = "switch";
    public static final String action_change_speed = "change_speed";
    public static final String action_calibrate = "calibrate";
    public static final String action_placeRules = "placerules";
    public static final String action_humanInteraction = "humaninteraction";

    // request/response message formats
    public static final String switchTo = "switch";
    public static final String speed = "speed";
    public static final String calibration = "calibration";
    public static final String pickX = "pickX";
    public static final String pickY = "pickY";
    public static final String pickZ = "pickZ";
    public static final String placeX = "placeX";
    public static final String placeY = "placeY";
    public static final String placeZ = "placeZ";
    public static final String placeInt = "placeInt";
    public static final String placeIntZ = "placeIntZ";

    public static final String placeRules = "placeRules";
    public static final String placeLocR = "placeLocR";
    public static final String placeLocG = "placeLocG";
    public static final String placeLocB = "placeLocB";
    public static final String boxLimR = "boxLimR";
    public static final String boxLimG = "boxLimG";
    public static final String boxLimB = "boxLimB";
    public static final String storageLim = "storageLim";

    public static final String humanInteraction = "humanInteraction";

    public static final String empty = "";
    public static final String status = "status";
    public static final String calibrationParameters = "calibrationParameters";
}

