package de.gtarc.chariot.temperaturesensoragent;

import de.gtarc.chariot.connectionapi.ConnectionException;
import de.gtarc.chariot.connectionapi.ConnectionListener;
import de.gtarc.chariot.connectionapi.ConnectionStatus;
import de.gtarc.chariot.connectionapi.DeviceConnection;
import de.gtarc.chariot.connectionapi.impl.MqttConnectionImpl;
import de.gtarc.chariot.messageapi.AbstractMessage;
import de.gtarc.chariot.messageapi.AbstractPayload;
import de.gtarc.chariot.messageapi.IMessage;
import de.gtarc.chariot.messageapi.impl.MessageBuilder;
import org.eclipse.paho.client.mqttv3.MqttException;
//
//public class SensorServer implements ConnectionListener {
//
//    String host = "tcp://m24.cloudmqtt.com:10933";
//    String username = "plbwvpgf";
//    String password = "mJTPb6z12Bag";
//    String clientId = "SensorServer";
//    MqttConnectionImpl dc;
//    String subTopic;
//    String responseTopic;
//
//    public SensorServer(String subTopic, String responseTopic) throws MqttException, ConnectionException {
//        this.subTopic= subTopic;
//        this.responseTopic = responseTopic;
//        dc = new MqttConnectionImpl(host, username, password, clientId);
//
//        dc.addConnectionListener(this);
//        dc.connect();
//        dc.subscribeTopic(subTopic);
//    }
//
//    public void closeServer() throws ConnectionException {
//        this.dc.disconnect();
//    }
//
//    @Override
//    public void connectionStateChanged(DeviceConnection deviceConnection, ConnectionStatus connectionStatus) {
//
//    }
//
//    @Override
//    public void onMessageReceived(IMessage iMessage, DeviceConnection deviceConnection) throws ConnectionException {
//
//        AbstractPayload payload = ((AbstractMessage)iMessage).getPayload();
//        System.out.println(payload.getClass().getName());
//        if (payload instanceof PayloadTemperatureSensor) {
//
//            System.out.println("correct payload, sending response");
//            AbstractMessage response = createResponse((AbstractMessage)iMessage);
//            dc.send(response);
//        }
//    }
//
//    public AbstractMessage createResponse(AbstractMessage request) {
//        PayloadTemperatureSensorResult payload = new PayloadTemperatureSensorResult("Success", 23.23);
//        return new MessageBuilder()
//                .setUrl(request.getUrl())
//                .setTopic(request.getResponseTopic())
//                .setTo(request.getFrom())
//                .setFrom(request.getTo())
//                .setMessageType(payload.getClass().getName())
//                .addPayload(payload)
//                .build();
//    }
//}
