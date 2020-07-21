package de.gtarc.chariot.threedprinteragent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.gtarc.chariot.deviceapi.impl.ComplexDevicePropertyImpl;
import de.gtarc.chariot.deviceapi.impl.DeviceBuilder;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.chariot.registrationapi.agents.DeviceAgent;
import de.gtarc.commonapi.utils.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class DeviceBean extends DeviceAgent {

    public String apiKey;
    public String octoprintURL;

    private static OctoPrintInterface printer = null;

    @Override
    public void doStart() throws Exception {
        long timestamp = new Date().getTime();
        setEntity(
                new DeviceBuilder()
                        .setName("3D-Printer")
                        .setUuid(getEntityId())
                        .setDeviceLocation(
                                new Location(
                                        "PL-Location",
                                        "Room",
                                        "Production Line",
                                        0,
                                        new Position(0, 0, "0"),
                                        new Indoorposition("0", 0, 0)
                                )
                        )
                        .setVendor("GT-ARC GmbH")
                        .setType(IoTEntity.ACTUATOR)
                        .addProperty(new DevicePropertyImpl(timestamp, "status", ValueTypes.BOOLEAN, false, "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "printing-state", ValueTypes.STRING, "ready", "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "bed-temperature", ValueTypes.NUMBER, 1, "celcius", true, 0.0, 240.0))
                        .addProperty(new DevicePropertyImpl(timestamp, "extruder-temperature", ValueTypes.NUMBER, 1, "celcius", true, 0.0, 240.0))
                        .addProperty(new DevicePropertyImpl(timestamp, "estimated-print-time", ValueTypes.STRING, "time", "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "estimated-filament-usage", ValueTypes.STRING, "time", "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "remaining-print-time", ValueTypes.STRING, "time", "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "printing-progress", ValueTypes.NUMBER, 0, "", false))
                        .addProperty(new DevicePropertyImpl(timestamp, "printing-model", ValueTypes.STRING, "time", "", false))
                        .addProperty(new ComplexDevicePropertyImpl(timestamp, "xyzAxes", ValueTypes.ARRAY, Arrays.asList(
                                new DevicePropertyImpl(timestamp, "xaxis", ValueTypes.NUMBER, 0, "", true,0.0,0.0),
                                new DevicePropertyImpl(timestamp, "yaxis", ValueTypes.NUMBER, 0, "", true,0.0,0.0),
                                new DevicePropertyImpl(timestamp, "zaxis", ValueTypes.NUMBER, 0, "", true,0.0,0.0)
                        ), "", true))
                        .buildActuating()
        );
        register();
        printer = new OctoPrintInterface(this,octoprintURL,apiKey);
    }

    @Override
    public  void execute() {
        printer.getLatestMeasurements();
    }


    @Expose(name = Constants.ACTION_UPDATE_ENTITY_PROPERTY, scope = ActionScope.GLOBAL)
    public void updateProperty(String key, Object value) {
        getDevice().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            ( i).setValue(value);
            updateProperty(i);
            updateIoTEntityProperty(i);
        });
    }


    public void updateIoTEntityProperty(String key, String value) {
        // change value in device
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

    @Override
    public <T> void updateIoTEntityProperty(T property) {
        // change the value
    }

    // TODO: Update the following actions. The device object is not updated
    @Expose(name = Constants.ACTION_PRINT_FILE, scope = ActionScope.GLOBAL, returnTypes = {Boolean.class})
    public void printFile(String fileName) {
        printer.printModel(fileName);
    }

    @Expose(name = Constants.ACTION_GET_BEDTEMPERATURE, scope = ActionScope.GLOBAL, returnTypes = {Double.class})
    public Double getBedTemperature() {
        return printer.getBedTemperature();
    }

    @Expose(name = Constants.ACTION_SET_BEDTEMPERATURE, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setBedTemperature(Integer newBedTemp) {
        printer.setBedTemperature(newBedTemp);
    }

    @Expose(name = Constants.ACTION_GET_EXTRUDERTEMPERATURE, scope = ActionScope.GLOBAL, returnTypes = {Double.class})
    public Double getExtruderTemperature() {
        return printer.getExtruderTemp();
    }

    @Expose(name = Constants.ACTION_SET_EXTRUDERTEMPERATURE, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setExtruderTemperature(Integer newExtruderTemp) {
        printer.setExtruderTemperature(newExtruderTemp);
    }

    @Expose(name = Constants.ACTION_GET_ESTIMATEDPRINTTIME, scope = ActionScope.GLOBAL, returnTypes = {Long.class})
    public Long getEstimatedPrintTime() {
        return printer.getEstimatedPrintTime();
    }

    @Expose(name = Constants.ACTION_GET_ESTIMATEDFILAMENTUSAGE, scope = ActionScope.GLOBAL, returnTypes = {Double.class})
    public Double getEstimatedFilamentUsage() {
        return printer.getEstimatedFilamentUsage();
    }

    @Expose(name = Constants.ACTION_GET_REMAININGPRINTTIME, scope = ActionScope.GLOBAL, returnTypes = {Long.class})
    public Long getRemainingPrintTime() {
        return printer.getRemainingPrintTime();
    }

    @Expose(name = Constants.ACTION_GET_PROGRESS, scope = ActionScope.GLOBAL, returnTypes = {Double.class})
    public Double getProgress() {
        return printer.getProgress();
    }

    @Expose(name = Constants.ACTION_GET_PRINTINGMODEL, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPrintingModel() {
        return printer.getPrintingModel();
    }

    @Expose(name = Constants.ACTION_GET_PRINTERSTATE, scope = ActionScope.GLOBAL, returnTypes = {String.class})
    public String getPrinterState() {
        return printer.getPrinterCurrentState();
    }

    @Expose(name = Constants.ACTION_SET_PRINTERSTATE, scope = ActionScope.GLOBAL, returnTypes = {})
    public void setPrinterState(String newState) {
        printer.setPrinterState(newState);
    }

    @Expose(name = Constants.ACTION_MOVE_TOHOME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void moveToHome() {
        printer.moveToHome();
    }

    @Expose(name = Constants.ACTION_MOVE_RELATIVETOHOME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void moveRelativeToHome(Double x, Double y, Double z) throws InterruptedException {
        printer.moveAxesRelativeToHome(x, y, z);
    }

    @Expose(name = Constants.ACTION_MOVE_RELATIVETOCURRENTPOSITION, scope = ActionScope.GLOBAL, returnTypes = {})
    public void moveRelativeToCurrentPosition(Double x, Double y, Double z) throws InterruptedException {
        printer.moveAxesRelativeToCurrentPosition(x, y, z);
    }

    @Expose(name = Constants.ACTION_CONNECT_VIRTUALPORT, scope = ActionScope.GLOBAL, returnTypes = {})
    public void connectWithVirtualPort()
    {
        printer.connectWithVirtualPort();
    }

    @Expose(name = Constants.ACTION_CONNECT_PORTNAME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void connectWithPortName(String portName) {
        printer.connectWithPortName(portName);
    }

    @Expose(name = Constants.ACTION_DISCONNECT, scope = ActionScope.GLOBAL, returnTypes = {})
    public void disconnect() {
        printer.disconnect();
    }

    @Expose(name = Constants.ACTION_SET_RESUME, scope = ActionScope.GLOBAL, returnTypes = {})
    public void resumePrinting() {
        printer.resumePrinting();
    }

    @Expose(name = Constants.ACTION_SET_RESTART, scope = ActionScope.GLOBAL, returnTypes = {})
    public void restartPrinting() {
        printer.restartPrinting();
    }

    @Expose(name = Constants.ACTION_SET_PAUSED, scope = ActionScope.GLOBAL, returnTypes = {})
    public void pausePrinting() {
        printer.pausePrinting();
    }

    @Expose(name = Constants.ACTION_SET_CANCEL, scope = ActionScope.GLOBAL, returnTypes = {})
    public void cancelPrinting() {
        printer.cancelPrinting();
    }


    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setOctoprintURL(String baseUrl) {
        this.octoprintURL = baseUrl;
    }

}