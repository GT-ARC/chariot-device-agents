package de.gtarc.chariot.orserviceagent;

import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.action.ActionResult;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.messageapi.payload.PayloadEntityProperty;
import de.gtarc.chariot.orserviceagent.connection.Config;
import de.gtarc.chariot.orserviceagent.connection.MQTTDriver;
import de.gtarc.chariot.orserviceagent.utils.ActionNames;
import de.gtarc.chariot.orserviceagent.utils.GenericAction;
import de.gtarc.chariot.orserviceagent.utils.GenericState;
import de.gtarc.chariot.registrationapi.agents.ServiceAgent;
import de.gtarc.chariot.serviceapi.ServiceProperty;
import de.gtarc.chariot.serviceapi.impl.ServiceBuilder;
import de.gtarc.chariot.serviceapi.impl.ServicePropertyBuilder;
import de.gtarc.commonapi.utils.IoTEntity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServiceBean extends ServiceAgent  {
    private final static String ACTION_NAME_1 = "de.gtarc.chariot.registrationapi.agents.ServiceAgentExample#updateProperty";
    private final static String ACTION_NAME_2 = "de.gtarc.chariot.registrationapi.agents.ServiceAgentExample#addProperty";

    private ServiceProperty myServiceProperty;
    private MQTTDriver mqttDriver;
    JSONParser jsonParser;

    private String getTopic;
    private String putTopic;

    private String deviceID;
    private String deviceName;

    @SuppressWarnings("rawtypes")
    private List<GenericState> states;
    private List<GenericAction> actions;

    private GenericState<Integer> openPrintJobs;
    private int lastID;
    private int printJobCount;
    private boolean debug;

    private Action actionTemplate;
    private IActionDescription getPrinterStateAction;
    private IActionDescription getExtruderTemperatureAction;
    private IActionDescription getBedTemperatureAction;
    private IActionDescription getRemainingPrintTimeAction;
    private IActionDescription connectWithVirtualPortAction;
    private IActionDescription connectWithPortNameAction;
    private IActionDescription disconnectAction;
    private IActionDescription printAction;
    private IActionDescription getPrintingModelAction;
    private IActionDescription getEstimatedPrintTimeAction;
    private IActionDescription getEstimatedFilamentUsageAction;
    private IActionDescription getProgressAction;
    private IActionDescription resumePrintingAction;
    private IActionDescription restartPrintingAction;
    private IActionDescription pausePrintingAction;
    private IActionDescription cancelPrintingAction;

    private boolean allFound;
    private int currentStatus; // 0 disconnected 1 operational 2 printing


    @Override
    public void doStart() throws Exception {
        this.setEntity(
                new ServiceBuilder()
                        .setName("Object-Recognition-Service")
                        .buildService()
        );
        this.register();


        System.out.println("----  Mode ON ----");

        this.currentStatus = -1;

        this.deviceID = "3d_printer_01";
        this.deviceName = "3D Printer 01";

        this.states = new ArrayList<GenericState>();
        this.actions = new ArrayList<GenericAction>();

        this.mqttDriver = new MQTTDriver(debug);
        this.jsonParser = new JSONParser();

        this.getTopic = Config.GETTOPIC + this.deviceID;
        this.putTopic = Config.PUTTOPIC + this.deviceID;

        this.lastID = 0;
        this.debug = debug;
        if (debug) System.out.println("---- DEBUG Mode ON ----");

        //configureMQTT();
    }

    @Expose(name = ACTION_NAME_1, scope = ActionScope.GLOBAL)
    public void updatePropertyå(String key, Object value) {
        getService().getProperties().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().ifPresent(i -> {
            (i).setValue(value);
            updateProperty(i);
        });
        // Push updated value in db
        myServiceProperty.setValue(value);
        updateProperty(myServiceProperty);
    }

    @Expose(name = ACTION_NAME_2, scope = ActionScope.GLOBAL)
    public void addProperty() throws ConnectionException, ExecutionException, InterruptedException {
        this.myServiceProperty = new ServicePropertyBuilder()
                .setTimestamp(new Date().getTime())
                .setKey("sophisticated_key")
                .setType("boolean")
                .setValue(false)
                .setUnit("")
                .setWritable(false)
                .buildServiceProperty();
        addEntityProperty(myServiceProperty);
    }





    @Override
    public void execute(){
        actionTemplate = new Action(ActionNames.ACTION_GET_PRINTERSTATE);
        getPrinterStateAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_EXTRUDERTEMPERATURE);
        getExtruderTemperatureAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_BEDTEMPERATURE);
        getBedTemperatureAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_REMAININGPRINTTIME);
        getRemainingPrintTimeAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_CONNECT_VIRTUALPORT);
        connectWithVirtualPortAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_CONNECT_PORTNAME);
        connectWithPortNameAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_DISCONNECT);
        disconnectAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_PRINT_FILE);
        printAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_PRINTINGMODEL);
        getPrintingModelAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_ESTIMATEDPRINTTIME);
        getEstimatedPrintTimeAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_ESTIMATEDFILAMENTUSAGE);
        getEstimatedFilamentUsageAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_GET_PROGRESS);
        getProgressAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_SET_CANCEL);
        cancelPrintingAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_SET_PAUSED);
        pausePrintingAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_SET_RESTART);
        restartPrintingAction = thisAgent.searchAction(actionTemplate);

        actionTemplate = new Action(ActionNames.ACTION_SET_RESUME);
        resumePrintingAction = thisAgent.searchAction(actionTemplate);


        allFound =  getPrinterStateAction != null &&
                getExtruderTemperatureAction != null &&
                getBedTemperatureAction != null &&
                connectWithVirtualPortAction != null &&
                connectWithPortNameAction != null &&
                disconnectAction != null &&
                printAction != null &&
                getPrintingModelAction != null &&
                getEstimatedPrintTimeAction != null &&
                getEstimatedFilamentUsageAction != null &&
                getProgressAction != null &&
                cancelPrintingAction != null &&
                restartPrintingAction != null &&
                resumePrintingAction != null &&
                pausePrintingAction != null &&
                getRemainingPrintTimeAction != null;


        if(allFound) {
            this.setExecutionInterval(0);
            theProcess();
        }

    }


    public void theProcess(){
        while(true) {
            try
            {
                Thread.sleep(500);
                if(getPrinterState().startsWith("UNKNOWN")) {
                    atDisconnectedStatus();
                }
                else if(getPrinterState().startsWith("PAUSED") || getPrinterState().startsWith("PRINTING")) {
                    atPrintingStatus();
                }
                else {
                    atOperationalStatus();
                }
            }
            catch(InterruptedException e) { }
            publishMqttInformation();
        }

    }

    public void atDisconnectedStatus() { // 0
        if(currentStatus != 0) {
            states.clear();
            actions.clear();
            this.lastID = 0;

            addState("Owner", "Home", "DAI Lab","Owner");
            addState("Location","Home", "Room 1114","Location");
            addState("Status","Home", "Disconnected","Status");

            addAction("Connect with Virtual Port", "Home", "PUT", "{\"state\":"+0+",\"value\":\"connectWithVirtualPort\"}","Connect", 0);
            addAction("Connect with Port /dev/ttyUSB0", "Home", "PUT", "{\"state\":"+0+",\"value\":\"connectWithRealPort\"}","Connect", 0);

            currentStatus = 0;
        }
    }

    public void atOperationalStatus() { // 1
        if(currentStatus != 1) {
            states.clear();
            actions.clear();
            this.lastID = 0;

            addState("Status","Main", "Operational","Status"); // 1
            addState("Bed Temperature", "Main", 0d, "Bed Temperature"); //2
            addState("Extruder Temperature", "Main", 0d, "Extruder Temperature"); //3
            addState("Available Files", "Main", 1, "Available File Count"); //4

            addAction("Print   cube_l.gcode", "Print", "PUT", "{\"state\":"+0+",\"value\":\"printcubel\"}","Print Cubel", 0);

            currentStatus = 1;
        }
        try{
            updateState(1, "Status", getPrinterState());
            updateState(2, "Bed Temperature", getBedTemperature());
            updateState(3, "Extruder Temperature", getExtruderTemperature());
        }
        catch (Exception e) {}


    }

    public void atPrintingStatus() { // 2
        if(currentStatus != 2) {
            states.clear();
            actions.clear();
            this.lastID = 0;

            addState("Status","Main", "Printing","Status"); // 1
            addState("Bed Temperature", "Main", 0d, "Bed Temperature"); //2
            addState("Extruder Temperature", "Main", 0d, "Extruder Temperature"); //3
            addState("Printing Model", "Main", "abc", "Printing Model"); //4
            addState("Estimated Print Time", "Main", "--:--:--", "Estimated Print Time"); // 5
            addState("Remaining Print Time", "Main", "--:--:--", "Remaining Print Time"); // 6
            addState("Estimated Filament Usage", "Main", 0d, "Estimated Filament Usage"); // 7
            addState("Printing Model","Print", "abc","Printing Model"); // 8
            addState("Status","Print", "Printing","Status"); // 9
            addState("Progress", "Print", 0d, "Progress"); //10
            addState("Remaining Print Time", "Print", "--:--:--", "Remaining Print Time"); // 6

            addAction("Pause Printing", "Print", "PUT", "{\"state\":"+0+",\"value\":\"pausePrinting\"}","Pause Printing", 0);
            addAction("Resume Printing", "Print", "PUT", "{\"state\":"+0+",\"value\":\"resumePrinting\"}","Resume Printing", 0);
            addAction("Restart Printing", "Print", "PUT", "{\"state\":"+0+",\"value\":\"restartPrinting\"}","Restart Printing", 0);
            addAction("Cancel Printing", "Print", "PUT", "{\"state\":"+0+",\"value\":\"cancelPrinting\"}","Cancel Printing", 0);

            currentStatus = 2;
        }
        try {
            updateState(8, "Printing Model", getPrintingModel());
            updateState(9, "Status", getPrinterState());
            updateState(10, "Progress", getProgress());
            updateState(11, "Remaining Print Time", getRemainingPrintTime());
            updateState(1, "Status", getPrinterState());
            updateState(2, "Bed Temperature", getBedTemperature());
            updateState(3, "Extruder Temperature", getExtruderTemperature());
            updateState(4, "Printing Model", getPrintingModel());
            updateState(5, "Estimated Print Time", getEstimatedPrintTime());
            updateState(6, "Remaining Print Time", getRemainingPrintTime());
            updateState(7, "Estimated Filament Usage", getEstimatedFilamentUsage());

        }
        catch (Exception e) {}



    }

    private void configureMQTT () {

        mqttDriver.subscribe(getTopic);
        mqttDriver.subscribe(putTopic);

        mqttDriver.setCallback(new MqttCallback() {

            @SuppressWarnings({ "rawtypes" })
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if ((topic.startsWith(getTopic)) && (message.toString().isEmpty())) {
                    System.out.println("Received GET request for ["+topic+"]");
                    publishMqttInformation();
                }
                else if (topic.startsWith(putTopic)) {
                    System.out.print("Received PUT request for ["+topic+"] with ["+message.toString()+"]");
                    if (debug) System.out.print(" with ["+message.toString()+"]");
                    try {
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(message.toString());

                        if(jsonObject.get("value").toString().startsWith("connectWithVirtualPort")) {
                            connectWithVirtualPort();
                        }
                        else if(jsonObject.get("value").toString().equals("connectWithRealPort")) {
                            connectWithPortName("/dev/ttyUSB0");
                        }
                        else if(jsonObject.get("value").toString().equals("pausePrinting")) {
                            pausePrinting();
                        }
                        else if(jsonObject.get("value").toString().equals("resumePrinting")) {
                            resumePrinting();
                        }
                        else if(jsonObject.get("value").toString().equals("restartPrinting")) {
                            restartPrinting();
                        }
                        else if(jsonObject.get("value").toString().equals("cancelPrinting")) {
                            cancelPrinting();
                        }
                        else if(jsonObject.get("value").toString().equals("printcubel")) {
                            printFile("cube_l.gcode");
                        }
                    } catch (Exception e) {
                        System.err.println("...rejected.");
                        //e.printStackTrace();
                        return;
                    }
                    publishMqttInformation();
                }
            }

            public void deliveryComplete(IMqttDeliveryToken arg0) {}

            public void connectionLost(Throwable arg0) {
                System.out.println("Disconnected");
                //mqttDriver = new MQTTDriver(debug);
                //configureMQTT();
            }
        });
    }

    private void publishMqttInformation () {
        mqttDriver.publish(getTopic, deviceToJson());
        System.out.println(deviceToJson());
    }


    private int addState(String name, String category, String value, String description) {
        lastID ++;
        this.states.add(new GenericState<String>(lastID, name, category, value, description));
        return lastID;
    }

    private int addState(String name, String category, int value, String description) {
        lastID ++;
        this.states.add(new GenericState<Integer>(lastID, name, category, value, description));
        return lastID;
    }

    private int addState(String name, String category, Boolean value, String description) {
        lastID ++;
        this.states.add(new GenericState<Boolean>(lastID, name, category, value, description));
        return lastID;
    }

    private int addState(String name, String category, Double value, String description) {
        lastID++;
        this.states.add(new GenericState<Double>(lastID, name, category, value, description));
        return lastID;
    }

    @SuppressWarnings("unchecked")
    private void updateState(int id, String name, String value) {
        GenericState<String> state = getState(id);
        if (state != null) {
            if (!name.isEmpty()) state.stateName = name;
            state.stateValue = value;
        }
    }

    @SuppressWarnings("unchecked")
    private void updateState(int id, String name, int value) {
        GenericState<Integer> state = getState(id);
        if (state != null) {
            if (!name.isEmpty()) state.stateName = name;
            state.stateValue = value;
        }
    }

    @SuppressWarnings("unchecked")
    private void updateState(int id, String name, Boolean value) {
        GenericState<Boolean> state = getState(id);
        if (state != null) {
            if (!name.isEmpty()) state.stateName = name;
            state.stateValue = value;
        }
    }
    @SuppressWarnings("unchecked")
    private void updateState(int id, String name, Double value) {
        GenericState<Double> state = getState(id);
        if(state != null) {
            if(!name.isEmpty()) state.stateName = name;
            state.stateValue = value;
        }
    }

    private void removeState(int id) {
        states.remove(getState(id));
    }

    private void removeState(GenericState state) {
        states.remove(state);
    }

    private int addAction(String name, String category, String method, String message, String description, int associatedWith) {
        lastID ++;
        actions.add(new GenericAction(lastID, name, category, method, message, description, associatedWith));
        return lastID;
    }

    private void updateAction(int id, String name, String message, String description) {
        GenericAction action = getAction(id);
        if (action != null) {
            if (!name.isEmpty()) action.actionName = name;
            if (!description.isEmpty()) action.actionDescription = description;
            action.actionMessage = message;
        }
    }

    private void removeAction(int id) {
        actions.remove(getAction(id));
    }

    private void removeAction(GenericAction action) {
        actions.remove(action);
    }

    private GenericAction getAction(int id) {
        for(GenericAction action : actions) {
            if (id == action.actionID) {
                return action;
            }
        }
        return null;
    }

    private List<GenericAction> getActionsAssociatedWithState(int id) {
        List<GenericAction> actionList = new ArrayList<GenericAction>();

        for (GenericAction action : actions) {
            if (action.associatedWith == id) actionList.add(action);
        }

        return actionList;
    }

    @SuppressWarnings({ "rawtypes"})
    private GenericState getState(int id) {
        for(GenericState state : states) {
            if (id == state.stateID) {
                return state;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String deviceToJson() {
        JSONObject objectJSONObject = new JSONObject();
        objectJSONObject.put("deviceID", this.deviceID);
        objectJSONObject.put("objectName", this.deviceName);
        objectJSONObject.put("states", statesToJson());
        objectJSONObject.put("actions", actionsToJson());

        return objectJSONObject.toJSONString();
    }

    @SuppressWarnings({"rawtypes", "unchecked" })
    private JSONArray statesToJson () {

        JSONArray statesJSONArray = new JSONArray();
        for(GenericState state : states) {
            statesJSONArray.add(state.toJSON());
        }
        return statesJSONArray;
    }

    @SuppressWarnings("unchecked")
    private JSONArray actionsToJson() {
        JSONArray actionsJSONArray = new JSONArray();
        for(GenericAction action : actions) {
            actionsJSONArray.add(action.toJSON());
        }
        return actionsJSONArray;
    }
    // 3DPRINTER MODEL ACTIONS //

    private void printFile(String fileName) {
        if (printAction != null) {
            log.info("Send print command to the 3D Printer");
            invoke(printAction, new Serializable[]{fileName});
        }
    }

    private Double getBedTemperature() {
        if(getBedTemperatureAction != null) {
            this.setExecutionInterval(0);
            ActionResult actionResult = invokeAndWaitForResult(getBedTemperatureAction, null);
            return Double.valueOf(actionResult.getResults()[0].toString());
        }
        return null;
    }

    private Double getExtruderTemperature() {
        Action actionTemplate = new Action(ActionNames.ACTION_GET_EXTRUDERTEMPERATURE);
        IActionDescription getExtruderTemperatureAction = thisAgent.searchAction(actionTemplate);
        if(getExtruderTemperatureAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getExtruderTemperatureAction, null);
            return Double.valueOf(actionResult.getResults()[0].toString());
        }
        return null;
    }

    private String getEstimatedPrintTime() {
        if(getEstimatedPrintTimeAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getEstimatedPrintTimeAction, null);

            if (actionResult.getResults()[0] == null)
                return "--:--:--";

            Long timeAsLong = Long.valueOf(actionResult.getResults()[0].toString());
            Integer hour = timeAsLong.intValue() / 3600;
            timeAsLong = timeAsLong - hour * 3600;
            Integer minute = timeAsLong.intValue() / 60;
            timeAsLong = timeAsLong - minute * 60;
            Integer second = timeAsLong.intValue();
            String timeAsString = String.format("%02d", hour)
                    + ":" +
                    String.format("%02d", minute)
                    + ":" +
                    String.format("%02d", second);
            return timeAsString;
        }
        return "--:--:--";
    }

    private String getRemainingPrintTime() {
        if(getRemainingPrintTimeAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getRemainingPrintTimeAction, null);

            if(actionResult.getResults()[0] == null)
                return "--:--:--";

            Long timeAsLong = Long.valueOf(actionResult.getResults()[0].toString());
            Integer hour = timeAsLong.intValue()/3600;
            timeAsLong = timeAsLong - hour*3600;
            Integer minute = timeAsLong.intValue()/60;
            timeAsLong = timeAsLong - minute*60;
            Integer second = timeAsLong.intValue();
            String timeAsString = String.format("%02d", hour)
                    + ":" +
                    String.format("%02d", minute)
                    + ":" +
                    String.format("%02d", second);
            return timeAsString;
        }
        return "--:--:--";
    }

    private Double getProgress() {
        if(getProgressAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getProgressAction, null);
            return Double.valueOf(actionResult.getResults()[0].toString());
        }
        return 0d;
    }

    private Double getEstimatedFilamentUsage() {
//        if(getEstimatedFilamentUsageAction != null) {
//            ActionResult actionResult = invokeAndWaitForResult(getEstimatedFilamentUsageAction, null);
//            return Double.valueOf(actionResult.getResults()[0].toString());
//        }
        return 0d;
    }

    private String getPrintingModel() {
        if(getPrintingModelAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getPrintingModelAction, null);
            return actionResult.getResults()[0].toString();
        }
        return null;
    }

    private String getPrinterState() {
        if(getPrinterStateAction != null) {
            ActionResult actionResult = invokeAndWaitForResult(getPrinterStateAction, null);
            return actionResult.getResults()[0].toString();
        }
        return "UNKNOWN";
    }

    public void connectWithVirtualPort() {
        if(connectWithVirtualPortAction != null) {
            invoke(connectWithVirtualPortAction, new Serializable[]{});
        }
    }

    public void connectWithPortName(String portName) {
        if(connectWithPortNameAction != null) {
            invoke(connectWithPortNameAction, new Serializable[]{portName});
        }
    }

    public void disconnect() {
        if(disconnectAction != null) {
            invoke(disconnectAction, new Serializable[]{});
        }
    }

    public void cancelPrinting(){
        if(cancelPrintingAction != null) {
            invoke(cancelPrintingAction, new Serializable[]{});
        }
    }

    public void resumePrinting(){
        if(resumePrintingAction != null) {
            invoke(resumePrintingAction, new Serializable[]{});
        }
    }

    public void pausePrinting(){
        if(pausePrintingAction != null) {
            invoke(pausePrintingAction, new Serializable[]{});
        }
    }

    public void restartPrinting() {
        if(restartPrintingAction != null) {
            invoke(restartPrintingAction, new Serializable[]{});
        }
    }
}
