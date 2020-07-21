package de.gtarc.chariot.infraredtemperatureagent;

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
