package de.gtarc.chariot.hx711sensoragent2;
import de.gtarc.chariot.deviceapi.DeviceProperty;
import de.gtarc.chariot.messageapi.AbstractMessage;
import de.gtarc.chariot.messageapi.impl.MessageBuilder;
import de.gtarc.chariot.messageapi.payload.PayloadEntity;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/***
 * Web Socket Message Handler that is responsible for receiving/sending the messages over websocket.
 * @author cemakpolat
 * TODO: Please change the endpoint using the following link
 * https://stackoverflow.com/questions/33672736/is-there-any-way-creating-dynamic-serverendpoint-address-in-java
 * It should end up with a link like /hx711sensor/{id}
 */
@ServerEndpoint(value = "/hx711sensor2")
public class WSEndpoint {

    private DeviceBean bean;

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
        AbstractMessage result = MessageBuilder.fromJsonString(message);
        //AbstractMessage result = (AbstractMessage) mes;

        System.out.println(result.getJsonObject());

        new Thread(() -> {
            double weightAsDouble =  Double.parseDouble(((DeviceProperty) Arrays.stream(((PayloadEntity)
                    result.getPayload()).getProperties()).filter(item -> item.getKey().equals("weight")).findAny().get()).getValue().toString());

            double numberOfObjectsAsDouble =  Double.parseDouble(((DeviceProperty) Arrays.stream(((PayloadEntity)
                    result.getPayload()).getProperties()).filter(item -> item.getKey().equals("numOfObjects")).findAny().get()).getValue().toString());

            bean.updateProperty("weight",weightAsDouble);
            bean.updateProperty("numberOfObjects",numberOfObjectsAsDouble);

            System.out.println("Weight: " + weightAsDouble);
            System.out.println("Number of Objects: " + numberOfObjectsAsDouble);
        }).start();

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


}
