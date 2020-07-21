package de.gtarc.chariot.deliverytrayagent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@ClientEndpoint
public class WSEndpoint extends WebSocketClient {

    DeviceBean bean;

    public static final String simulatedTray = "delivery-container-service";
    public static final String trayDriver = "delivery-container-driver-id";

    public static final String getStatusOf = "getStatusOf";
    public static final String status = "status";
    public static final String type = "type";
    public static final String defaultStatus = "Free";
    public String trayModel = "delivery-container-tray";

    public static final String deliveryContainerStates = "deliverycontainerstates";
    Map<String, String> trayStatus = new HashMap<String, String>();

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
                new Thread(()->updateResult(response)).start();
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

    public String sendRequestToDevice(String message) { return null; }

    public String sendRequestToDevice() {
        JsonObject request = new JsonObject();
        request.addProperty(status, getStatusOf);
        request.addProperty(type, this.trayModel);
        return createRequestMessage(trayDriver, simulatedTray, request);
    }
    // TODO: Updates are operated at diffent timestamps, this might be an issue
    public void updateResult(JsonObject storage) {
        JsonObject obj = storage.get(deliveryContainerStates).getAsJsonObject();
        for(int i = 1; i < 6 ; i ++){
            String tray =  "tray-"+i;
            if (obj.get(tray).getAsJsonObject() != null) {
                String currentStatus = obj.get(tray).getAsJsonObject().get(status).getAsString();
                trayStatus.put(tray,currentStatus);
                bean.updateProperty(tray, currentStatus);
            }
        }
    }
}
