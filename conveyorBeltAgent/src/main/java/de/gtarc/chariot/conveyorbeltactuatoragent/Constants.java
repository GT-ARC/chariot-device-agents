package de.gtarc.chariot.conveyorbeltactuatoragent;

public class Constants {
    // actions
    public static final String ACTION_HANDLE_REQUEST_MESSAGE = "de.gtarc.chariot.conveyorbeltactuatoragent.DeviceBean#handleMessageForProxyAgent";
    public static final String ACTION_GET_CURRENT_DEVICESTATUS = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getCurrentDeviceStatus";
    public static final String ACTION_GET_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getSpeed";
    public static final String ACTION_SET_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setSpeed";
    public static final String ACTION_GET_STARTING_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getStartingSpeed";
    public static final String ACTION_SET_STARTING_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setStartingSpeed";
    public static final String ACTION_GET_ENDING_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getEndSpeed";
    public static final String ACTION_SET_ENDING_SPEED = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setEndSpeed";
    public static final String ACTION_GET_NUMBEROF_STEPS = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getNumberOfSteps";
    public static final String ACTION_SET_NUMBEROF_STEPS = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setNumberOfSteps";
    public static final String ACTION_GET_STEPTIME = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getStepTime";
    public static final String ACTION_SET_STEPTIME = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setStepTime";
    public static final String ACTION_GET_ONETIME_STEPREQ = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getOneTimeSpeedReq";
    public static final String ACTION_SET_ONETIME_STEPREQ = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setOneTimeSpeedReq";
    public static final String ACTION_GET_ABNORMAL = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getAbnormal";
    public static final String ACTION_SET_ABNORMAL = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setAbnormal";
    public static final String ACTION_GET_TYPE = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getType";
    public static final String ACTION_SET_TYPE = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setType";
    public static final String ACTION_GET_PARAM1 = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getParam1";
    public static final String ACTION_SET_PARAM1 = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setParam1";
    public static final String ACTION_GET_PARAM2 = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getParam2";
    public static final String ACTION_SET_PARAM2 = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setParam2";
    public static final String ACTION_GET_HOLDTIME = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getHoldTime";
    public static final String ACTION_SET_HOLDTIME = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setHoldTime";
    public static final String ACTION_GET_NORMAL = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#getNormal";
    public static final String ACTION_SET_NORMAL = "de.gtarc.chariot.conveytorbeltactuatoragent.DeviceBean#setNormal";

    // conveyor belt features

    // actions to be done
    public static final String action_switch = "switch";
    public static final String action_change_speed = "change_speed";
    public static final String action_calibrate = "calibrate";
    public static final String action_placeRules = "placerules";
    public static final String action_humanInteraction = "humaninteraction";


    // request/response message formats
    public static final String switchTo = "switch";
    public static final String speed = "speed";
    public static final String status = "status";

    public static final String contSpeedReq = "contSpeedReq";

    // iterative speed
    public static final String starting_speed = "starting_speed";
    public static final String ending_speed = "ending_speed";
    public static final String numberofsteps = "numberofsteps";
    public static final String step_time = "step_time";

    public static final String oneTimeSpeedReq = "oneTimeSpeedReq";
    // motorData
    public static final String motorData = "motorData";
    public static final String sensor = "sensor";
    public static final String log_list_id = "log_list_id";
    public static final String timestamp = "timestamp";
    public static final String acceleration = "acceleration";
    public static final String temperature = "temperature";
    public static final String current = "current";
    public static final String power_in = "power_in";
    public static final String load_torque = "load_torque";

    //torqueRequest
    public static final String torqueRequest = "torqueRequest";
    public static final String torque_behavior = "abnormal";
    public static final String type = "type";
    public static final String param1 = "param1";
    public static final String param2 = "param2";
    public static final String holdTime = "holdTime";
    public static final String normal = "normal";

    public static final String deviceRequesteeId ="conveyor-service-";


    public static final String empty = "";




}
