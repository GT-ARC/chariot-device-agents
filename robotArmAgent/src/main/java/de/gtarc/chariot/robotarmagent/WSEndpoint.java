package de.gtarc.chariot.robotarmagent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.gtarc.chariot.deviceapi.impl.ComplexDevicePropertyImpl;
import de.gtarc.chariot.deviceapi.impl.DevicePropertyImpl;
import de.gtarc.commonapi.utils.ValueTypes;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@ClientEndpoint
public class WSEndpoint extends WebSocketClient {
    DeviceBean bean;

    public static String deviceRequesteeId = "dobotarm-service";
    public static String deviceRequesterId = "dobotarm-driver";

    private String deviceStatus = "off"; // on, of
    private Double currentSpeed = 0.0;

    // calibration parameters
    private boolean isCalibrated = false;

    private Double pickX = 264.11;
    private Double pickY = 52.79;
    private Double pickZ = 12.72;
    private Double placeX = 244.50;
    private Double placeY = -206.10;
    private Double placeZ = -35.85;
    private int placeInt = 90;
    private int placeIntZ = 26;

    // place rules parameters
    private int placeLocR = 0;
    private int placeLocG = 1;
    private int placeLocB = 2;
    private int boxLimR = 3;
    private int boxLimG = 3;
    private int boxLimB = 3;
    private int storageLim = 3;
    // human interaction
    private boolean humanInteraction = false;

    private final CountDownLatch closeLatch;

    @SuppressWarnings("unused")
    private Session session;
    public WSEndpoint(DeviceBean bean) {
        this.closeLatch = new CountDownLatch(1);
        this.bean = bean;
    }
    public WSEndpoint() {
        this.closeLatch = new CountDownLatch(1);
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
        System.out.println("Received Message: " + message);
        JsonObject messageAsJson = new JsonParser().parse(message).getAsJsonObject();
        JsonObject payload =  messageAsJson.getAsJsonObject("payload");
        JsonElement element = payload.getAsJsonObject("response");
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
        System.out.println("Connection closed: " + reason);
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
        switch (property){
            case Constants.speed:
                this.currentSpeed = Double.parseDouble(value); break;
            case Constants.switchTo:
                this.deviceStatus = value; break;
            default:
                System.out.println("ConveyorBeltAgent parameter unknown for the conveyor!");
                break;
        }
        return sendRequestToDevice();
    }

    public String sendRequestToDevice(String message) {
        // handle custom message
        return null;
    }
    public String sendRequestToDevice() {
        JsonObject request = new JsonObject();
        request.addProperty(Constants.switchTo, this.deviceStatus);
        request.addProperty(Constants.speed, Constants.empty);

        request.addProperty(Constants.calibration, Constants.empty);

        JsonObject placeRules = new JsonObject();
        placeRules.addProperty(Constants.placeLocR, this.placeLocR);
        placeRules.addProperty(Constants.placeLocG, this.placeLocG);
        placeRules.addProperty(Constants.placeLocB, this.placeLocB);
        placeRules.addProperty(Constants.boxLimR, this.boxLimR);
        placeRules.addProperty(Constants.boxLimG, this.boxLimG);
        placeRules.addProperty(Constants.boxLimB, this.boxLimB);
        placeRules.addProperty(Constants.storageLim, this.storageLim);

        request.add(Constants.placeRules, placeRules);
        request.addProperty(Constants.humanInteraction, this.humanInteraction);

        return createRequestMessage(deviceRequesterId, deviceRequesteeId, request);
    }

    public String sendRequestWithCalibration() {
        JsonObject request = new JsonObject();
        request.addProperty(Constants.switchTo, this.deviceStatus);
        request.addProperty(Constants.speed, Constants.empty);

        JsonObject calibration = new JsonObject();
        calibration.addProperty(Constants.pickX, this.pickX);
        calibration.addProperty(Constants.pickY, this.pickY);
        calibration.addProperty(Constants.pickZ, this.pickZ);
        calibration.addProperty(Constants.placeX, this.placeX);
        calibration.addProperty(Constants.placeY, this.placeY);
        calibration.addProperty(Constants.placeZ, this.placeZ);
        calibration.addProperty(Constants.placeInt, this.placeInt);
        calibration.addProperty(Constants.placeIntZ, this.placeIntZ);

        request.add(Constants.calibration, calibration);

        JsonObject placeRules = new JsonObject();
        placeRules.addProperty(Constants.placeLocR, this.placeLocR);
        placeRules.addProperty(Constants.placeLocG, this.placeLocG);
        placeRules.addProperty(Constants.placeLocB, this.placeLocB);
        placeRules.addProperty(Constants.boxLimR, this.boxLimR);
        placeRules.addProperty(Constants.boxLimG, this.boxLimG);
        placeRules.addProperty(Constants.boxLimB, this.boxLimB);
        placeRules.addProperty(Constants.storageLim, this.storageLim);

        request.add(Constants.placeRules, placeRules);
        request.addProperty(Constants.humanInteraction, this.humanInteraction);

        return createRequestMessage(deviceRequesterId, deviceRequesteeId, request);
    }
    public void updateResult(JsonObject obj) {
        String switchOnOff;
        Double currentSpeed;
        Boolean isCalibrated;
        Boolean humanInteraction = this.humanInteraction;

        if(obj != null){
            long timestamp = new Date().getTime();
            JsonObject service = obj.getAsJsonObject(deviceRequesteeId);
            switchOnOff = service.get(Constants.status).getAsString();
            if(switchOnOff != null) {
                this.deviceStatus = switchOnOff;
                boolean state = false;
                if (switchOnOff.equalsIgnoreCase("on")) {
                    state = true;
                }
                bean.updateProperty(Constants.status,state);
            }
            currentSpeed = service.get(Constants.speed).getAsDouble();
            if(currentSpeed != null) {
                this.currentSpeed = currentSpeed;
                bean.updateProperty(Constants.speed,currentSpeed);
            }
            isCalibrated = service.get(Constants.calibration).getAsBoolean();

            if(isCalibrated != null) {
                bean.updateProperty(Constants.calibration,isCalibrated);
                if(this.isCalibrated == true) {
                    JsonObject calibrated = service.get(Constants.calibration).getAsJsonObject();
                    ComplexDevicePropertyImpl calibrationComplexProperty = new ComplexDevicePropertyImpl(timestamp, Constants.calibration, ValueTypes.ARRAY, Arrays.asList(
                            new DevicePropertyImpl(timestamp, "calibration", ValueTypes.BOOLEAN, calibrated.get(Constants.calibration).getAsBoolean(), "", true),
                            new DevicePropertyImpl(timestamp, "pickX", ValueTypes.NUMBER, calibrated.get(Constants.pickX).getAsDouble(), "", false, 0.0, 300.0),
                            new DevicePropertyImpl(timestamp, "pickY", ValueTypes.NUMBER, calibrated.get(Constants.pickY).getAsDouble(), "", false, 0.0, 60.0),
                            new DevicePropertyImpl(timestamp, "pickZ", ValueTypes.NUMBER, calibrated.get(Constants.pickZ).getAsDouble(), "", true, 0.0, 20.0),
                            new DevicePropertyImpl(timestamp, "placeX", ValueTypes.NUMBER, calibrated.get(Constants.placeX).getAsDouble(), "", true, 0.0, 300.0),
                            new DevicePropertyImpl(timestamp, "placeY", ValueTypes.NUMBER, calibrated.get(Constants.placeY).getAsDouble(), "", true, -300.0, 300.0),
                            new DevicePropertyImpl(timestamp, "placeZ", ValueTypes.NUMBER, calibrated.get(Constants.placeZ).getAsDouble(), "", true, -100.0, 100.0),
                            new DevicePropertyImpl(timestamp, "placeInt", ValueTypes.NUMBER, calibrated.get(Constants.placeInt).getAsInt(), "", false, 0.0, 100.0),
                            new DevicePropertyImpl(timestamp, "placeIntZ", ValueTypes.NUMBER, calibrated.get(Constants.placeIntZ).getAsInt(), "", false, 0.0, 50.0)
                            ),"",true);
                    bean.updateProperty("calibration",calibrationComplexProperty);
                }
            }
            JsonObject placeRules = service.get(Constants.placeRules).getAsJsonObject();
            if(placeRules != null) {
                ComplexDevicePropertyImpl placeRulesComplexProperty = new ComplexDevicePropertyImpl(timestamp, Constants.placeRules, ValueTypes.ARRAY,  Arrays.asList(
                        new DevicePropertyImpl(timestamp, "placeLocR",  ValueTypes.NUMBER, this.placeLocR  = placeRules.get(Constants.placeLocR).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "placeLocG",  ValueTypes.NUMBER, this.placeLocG = placeRules.get(Constants.placeLocG).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "placeLocB",  ValueTypes.NUMBER, this.placeLocB =placeRules.get(Constants.placeLocB).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimR",  ValueTypes.NUMBER,  this.boxLimR = placeRules.get(Constants.boxLimR).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimG",  ValueTypes.NUMBER,  this.boxLimR = placeRules.get(Constants.boxLimG).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "boxLimB",  ValueTypes.NUMBER, this.boxLimB =placeRules.get(Constants.boxLimB).getAsInt(), "", true, 0.0, 3.0),
                        new DevicePropertyImpl(timestamp, "storageLim",  ValueTypes.NUMBER, this.storageLim = placeRules.get(Constants.storageLim).getAsInt(), "", true, 0.0, 3.0)
                ), "", true);
                bean.updateProperty(Constants.placeRules,placeRulesComplexProperty);
            }
            if(humanInteraction != null) {
                bean.updateProperty(Constants.humanInteraction, this.humanInteraction = service.get(Constants.humanInteraction).getAsBoolean());
            }
        }

    }

}
