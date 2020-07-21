package de.gtarc.chariot.infraredsensoragent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/***
 * Web Socket Message Handler that is responsible for receiving/sending the messages over websocket.
 * @author cemakpolat
 */
@ClientEndpoint
public class WSEndpoint extends WebSocketClient {

    private DeviceBean bean;
    public static final String infraredSensorRequestee = "infrared-sensor-service";
    public static final String infraredSensorDriver = "";
    public static String infraredSensorId = "infrared-sensor-service";
    public static String infraredSensorRequesterId = "infrared-sensor-driver";

    // request
    public static final String detected = "detected";
    public static final String empty = "";
    // response
    public String status = ""; //

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
        return null;
    }
    public String sendRequest(String json) {
        return null;
    }
    public String sendRequestToDevice() {
        JsonObject request = new JsonObject();
        request.addProperty(detected, empty);
        return createRequestMessage(infraredSensorRequesterId, infraredSensorRequestee, request);
    }

    public void updateResult(JsonObject obj) {
        if(obj.isJsonObject()){
            JsonObject currentsensor = obj.getAsJsonObject(infraredSensorRequestee);
            String detectedValue = currentsensor.get(detected).getAsString();
            bean.updateProperty("detected",detectedValue);
            if (!detectedValue.isEmpty())
                bean.updateProperty("status",true);
        }
    }

}
