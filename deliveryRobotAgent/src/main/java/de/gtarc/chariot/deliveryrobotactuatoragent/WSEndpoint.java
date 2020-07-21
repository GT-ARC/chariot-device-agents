package de.gtarc.chariot.deliveryrobotactuatoragent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.gtarc.chariot.deliveryrobotactuatoragent.DeviceBean;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@ClientEndpoint
public class WSEndpoint extends WebSocketClient {

    private DeviceBean bean;
    public static final String simulatedDeliveryRobot = "deliveryrobot-service";
    public static final String deliveryRobotDriver = "deliveryrobot-driver-id";

    public static final String getStatusAll = "getStatusAll";
    public static final String status = "status";
    public static final String batteryLevel = "batteryLevel";
    public static String deliveryRobotId = "deliveryrobot-2";
    public static String deliveryRobotDriverId = "deliveryrobot-driver-2";

    private final CountDownLatch closeLatch;

    @SuppressWarnings("unused")
    private Session session;
    public WSEndpoint(DeviceBean bean) {
        this.closeLatch = new CountDownLatch(1);
        this.bean = bean;
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
        JsonElement element = payload.get("response");
        if (!(element instanceof JsonNull)) {
            if (element.isJsonObject()){
                JsonObject response = (JsonObject) element;
                updateResult(response);
                new Thread(() -> updateResult(response));
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
        return null;
    }
    public String sendRequestToDevice(String message) {
        // handle custom message
        return null;
    }
    public String sendRequestToDevice() {
        JsonObject request = new JsonObject();
        request.addProperty(status, getStatusAll);
        return createRequestMessage(deliveryRobotDriverId, simulatedDeliveryRobot, request);
    }

    public void updateResult(JsonObject obj) {
        if(obj != null){
            JsonObject robot = obj.getAsJsonObject(deliveryRobotId);
            Double currentBatteryLevel = robot.get(batteryLevel).getAsDouble();
            if(!currentBatteryLevel.isNaN()){
                bean.updateProperty("batteryLevel",currentBatteryLevel);
                bean.updateProperty("status",true);
            }else{
                bean.updateProperty("status",false);
            }
        }
    }
}
