package de.gtarc.chariot.threedprinteragent;

public class Constants {
    // octoprint messages
    public static final String action_changePrinterState = "ChangePrinterState";
    public static final String action_setBedExtrudertemp = "BedExtruderTemperature";
    public static final String action_setAxes = "XYZAxes";
    public static final String action_changeModelToBePrinted = "change3DModel";

    public static final String status = "status";
    public static final String bedTemperature = "bedTemperature";
    public static final String extruderTemperature = "extruderTemperature";
    public static final String estimatedPrintTime = "estimatedPrintTime";
    public static final String estimatedFilamentUsage = "estimatedFilamentUsage";
    public static final String remainingPrintTime = "remainingPrintTime";
    public static final String progress = "progress";

    // actions
    public static final String PROPERTY_ACTION = "de.gtarc.chariot.DeviceBean#handleProperty";
    public static final String ACTION_UPDATE_ENTITY_PROPERTY = "de.gtarc.chariot.threedprinteragent.DeviceBean#updateProperty";
    public static final String ACTION_UPDATE_ENTITY = "de.gtarc.chariot.threedprinteragent.DeviceBean#updateEntity";
    public static final String ACTION_PRINT_FILE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#printFile";
    public static final String ACTION_GET_BEDTEMPERATURE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getBedTemperature";
    public static final String ACTION_SET_BEDTEMPERATURE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#setBedTemperature";
    public static final String ACTION_GET_EXTRUDERTEMPERATURE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getExtruderTemperature";
    public static final String ACTION_SET_EXTRUDERTEMPERATURE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#setExtruderTemperature";
    public static final String ACTION_GET_ESTIMATEDPRINTTIME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getEstimatedPrintTime";
    public static final String ACTION_GET_ESTIMATEDFILAMENTUSAGE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getEstimatedFilamentUsage";
    public static final String ACTION_GET_REMAININGPRINTTIME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getRemainingPrintTime";
    public static final String ACTION_GET_PROGRESS = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getProgress";
    public static final String ACTION_GET_PRINTINGMODEL = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getPrintingModel";
    public static final String ACTION_GET_PRINTERSTATE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#getPrinterState";
    public static final String ACTION_SET_PRINTERSTATE = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#setPrinterState";
    public static final String ACTION_MOVE_TOHOME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#moveToHome";
    public static final String ACTION_MOVE_RELATIVETOHOME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#moveRelativeToHome";
    public static final String ACTION_MOVE_RELATIVETOCURRENTPOSITION = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#moveRelativeToCurrentPosition";
    public static final String ACTION_CONNECT_VIRTUALPORT = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#connectWithVirtualPort";
    public static final String ACTION_CONNECT_PORTNAME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#connectWithPortName";
    public static final String ACTION_DISCONNECT = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#disconnect";
    public static final String ACTION_SET_PAUSED = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#pausePrinting";
    public static final String ACTION_SET_RESUME = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#resumePrinting";
    public static final String ACTION_SET_RESTART = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#restartPrinting";
    public static final String ACTION_SET_CANCEL = "de.gtarc.chariot.threedprinteragent.DeviceAgent.DeviceBean#cancelPrinting";

}
