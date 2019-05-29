# netty-mqtt-client

[![Maven Central](https://img.shields.io/maven-central/v/org.jetlinks/netty-mqtt-client.svg)](http://search.maven.org/#search%7Cga%7C1%7Cnetty-mqtt-client)


```java
EventLoopGroup loop = new NioEventLoopGroup();
MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
    System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
}));
mqttClient.setEventLoop(loop);
mqttClient.getClientConfig().setClientId("{clientId}");
mqttClient.getClientConfig().setUsername("{username}");
mqttClient.getClientConfig().setPassword("{password}");
mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);
mqttClient.getClientConfig().setReconnect(true);
mqttClient.setCallback(new MqttClientCallback() {
    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void onSuccessfulReconnect() {

    }
});
MqttConnectResult result = mqttClient.connect("127.0.0.1", 1883).await().get();
if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
    System.out.println("error:" + result.getReturnCode());
    mqttClient.disconnect();
} else {
    System.out.println("success");
//    mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"event\"}", StandardCharsets.UTF_8));
}
```