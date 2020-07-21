package de.gtarc.chariot.conveyorbeltactuatoragent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

@ClientEndpoint
public class WSEndpoint extends WebSocketClient {
    private DeviceBean bean;

    private static final Logger log = Logger.getLogger(WSEndpoint.class);

    private int requestIntervalCount = 0;
    private int maxRequestInterval = 30;
    public static String deviceRequesteeId = "conveyor-service-1";
    public static String deviceRequesterId = "dobot-conveyor-driver";

    private String deviceStatus = "on"; // on, of
    private Double speed = 0.0;

    private Double startingSpeed = 5.0;
    private Double endingSpeed = 35.0;
    private Double numberofsteps = 30.0;
    private Double stepTime = 2.0;

    private Double oneTimeSpeedReq = 30.0;
    // motor parameters
    private String motorData = "";

    //torqueRequest parameters
    private String torque_behavior = "";
    private String type = "log"; // options: log, exp, ramp, step
    private Double param1 = 0.1;
    private Double param2 = 1.0;
    private Double holdTime = 3.0;
    private Double normal = 0.2;

    private Double acceleration = 0.027874690631694;
    private Double temperature = 0.0;
    private Double current = 0.0;
    private Double power_in = 0.0;
    private Double load_torque = 0.0;

    private final CountDownLatch closeLatch;

    @SuppressWarnings("unused")
    private Session session;
    public WSEndpoint(DeviceBean bean) {
        log.info("CREATE WS ENDPOINT");
        this.closeLatch = new CountDownLatch(1);
        this.bean = bean;
        maxRequestInterval = 40*1000/bean.getExecutionInterval(); // every 40 seconds
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnOpen
    public void onWebSocketConnect(Session session) throws IOException {
        this.session = session;
    }

    @OnMessage
    public void onWebSocketText(String message, Session session) throws IOException {

        JsonObject messageAsJson = new JsonParser().parse(message).getAsJsonObject();
        JsonObject payload =  messageAsJson.getAsJsonObject("payload");
        log.info("MyRequestID {" + payload.getAsJsonObject("request").get("myRequestID").getAsString() + "}: Received Websocket Message: " + message);
        JsonElement element = payload.get("response");
        if (!(element instanceof JsonNull)) {
            if (element.isJsonObject()){
                JsonObject response = (JsonObject) element;
                new Thread(() -> updateResult(response)).start();
            }
        }
        this.closeLatch.countDown();
        this.session = null;
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        log.info("Connection closed: " + reason);
        this.session = null;
        this.closeLatch.countDown();
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    @SuppressWarnings("unchecked")

    public String createRequestMessage(String deviceId, String simulatedDeviceId, JsonObject request) {
        String result = "{}";
        JsonObject payload = new JsonObject();
        JsonObject obj = new JsonObject();
        JsonObject requestee = new JsonObject();
        JsonObject requester = new JsonObject();

        requestee.addProperty("id", simulatedDeviceId);
        requester.addProperty("id", deviceId);

        obj.add("requestee", requestee);
        obj.add("requester", requester);
        obj.add("request", request);
        obj.addProperty("response", "{}");

        payload.add("payload",obj);
        if (obj != null) {
            result = payload.toString();
        }

        return result;
    }

    public String sendRequestToDevice(String property, String value) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(value);
        log.info("sendRequestToDevice: " + value);
        switch (property){
            case Constants.contSpeedReq:
                JsonObject intern = jo.getAsJsonObject();
                this.startingSpeed =  intern.get("starting_speed").getAsDouble();
                this.endingSpeed =  intern.get("ending_speed").getAsDouble();
                this.numberofsteps = Double.parseDouble(intern.get("numberofsteps").getAsString());
                this.stepTime =  intern.get("step_time").getAsDouble();
                this.oneTimeSpeedReq =  intern.get("oneTimeSpeedReq").getAsDouble();
                break;
            case Constants.torqueRequest:
                JsonObject tr = jo.getAsJsonObject();
                this.torque_behavior = tr.get("torque_behaviour").getAsString();
                this.type = tr.get("type").getAsString();
                this.param1 = tr.get("param1").getAsDouble();
                this.param2 = tr.get("param2").getAsDouble();
                this.holdTime = tr.get("holdTime").getAsDouble();
                break;
            case Constants.speed:
                this.speed = Double.parseDouble(value); break;
            case Constants.switchTo:
                this.deviceStatus = value; break;
            case Constants.oneTimeSpeedReq:
                this.oneTimeSpeedReq = Double.parseDouble(value);
                break;
            default:
                log.info("ConveyorBeltAgent parameter unknown for the conveyor!");
                break;
            }

        return sendRequestToDevice();
    }

    public static int myRequestID = 0;

    public String sendRequestToDevice() {
        JsonObject request = new JsonObject();
        request.addProperty("myRequestID", WSEndpoint.myRequestID++);

        request.addProperty(Constants.status, this.deviceStatus);
        request.addProperty(Constants.switchTo,this.deviceStatus);
        //request.put(Constants.speed, Constants.empty);

        // speed
        JsonObject speed = new JsonObject();
        //JsonObject contSpeedReq = new JsonObject();
        //contSpeedReq.addProperty(Constants.starting_speed, Constants.empty);
        //contSpeedReq.addProperty(Constants.ending_speed, Constants.empty);
        //contSpeedReq.addProperty(Constants.numberofsteps, Constants.empty);
        //contSpeedReq.addProperty(Constants.step_time, Constants.empty);
        speed.addProperty(Constants.contSpeedReq, Constants.empty);
        speed.addProperty(Constants.oneTimeSpeedReq,this.oneTimeSpeedReq);
        request.add(Constants.speed, speed);
        // motor
        request.addProperty(Constants.motorData, Constants.empty);

        // torqueRequest
        //request.put(Constants.torqueRequest,Constants.empty );
        JsonObject torqueRequest = new JsonObject();
        JsonObject abnormal = new JsonObject();
        abnormal.addProperty(Constants.type, Constants.empty);  // others: exp, ramp, step
        abnormal.addProperty(Constants.param1, Constants.empty);
        abnormal.addProperty(Constants.param2, Constants.empty);
        abnormal.addProperty(Constants.holdTime, Constants.empty);
        torqueRequest.add(Constants.torque_behavior, abnormal);
        torqueRequest.addProperty(Constants.normal, Constants.empty);
        request.add(Constants.torqueRequest, torqueRequest);

        return createRequestMessage(deviceRequesterId, deviceRequesteeId, request);
    }


    public void updateResult(JsonObject obj) {

        requestIntervalCount ++;

        String switchOnOff = this.deviceStatus; Double currentSpeed = this.speed;
        Double   acceleration = this.acceleration; Double temperature = this.temperature;
        Double   current = this.current;Double power_in = this.power_in; Double load_torque = this.load_torque;
        if(obj != null){
            JsonObject service = obj.getAsJsonObject(deviceRequesteeId);
            JsonObject motorData = service.getAsJsonObject(Constants.motorData);
            if(motorData != null) {
                if (requestIntervalCount == maxRequestInterval){
                    log.info("NOW: Acceleration , Temperature and Load_Torque is being updated!");
                    acceleration = motorData.get(Constants.acceleration).getAsDouble();
                    if(acceleration != null) {
                        this.acceleration = acceleration;
                        bean.updateProperty("acceleration",acceleration);
                    }
                    temperature = motorData.get(Constants.temperature).getAsDouble();
                    if(temperature != null) {
                        this.temperature = temperature;
                        bean.updateProperty("temperature",temperature);
                    }
                    load_torque = motorData.get(Constants.load_torque).getAsDouble();
                    if(load_torque != null) {
                        this.load_torque = load_torque;
                        bean.updateProperty("load_torque",load_torque);
                    }
                    // update device status
                    this.deviceStatus = switchOnOff;
                    boolean state = false;
                    if (speed > 0 ) {
                        state = true;
                    }
                    bean.updateProperty("status",state);
                    requestIntervalCount = 0;
                }

                currentSpeed = motorData.get(Constants.speed).getAsDouble();
                if(currentSpeed != null) {
                    this.speed = currentSpeed;
                    bean.updateProperty("speed",currentSpeed);
                }

                current = motorData.get(Constants.current).getAsDouble();
                if(current != null) {
                    this.current = current;
                    bean.updateProperty("current", current);
                }
                power_in = motorData.get(Constants.power_in).getAsDouble();
                if(power_in != null) {
                    this.power_in = power_in;
                    bean.updateProperty("power_in",power_in);
                }
            }
        }
    }
}
