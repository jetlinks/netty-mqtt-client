package org.jetlinks.mqtt.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * @author zhouhao
 * @since
 */
public class MqttClientImplTest {


    public static void main(String[] args) throws Exception {

        EventLoopGroup loop = new NioEventLoopGroup();

        for (int i = 0; i < 1; i++) {
            MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
                System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
            }));

            mqttClient.setEventLoop(loop);
            mqttClient.getClientConfig().setClientId("test");
            mqttClient.getClientConfig().setUsername("test");
            mqttClient.getClientConfig().setPassword("test");
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
            MqttConnectResult result = mqttClient.connect("127.0.0.1", 1883)
                    .await()
                    .get();
            if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                System.out.println("error:" + result.getReturnCode());
                mqttClient.disconnect();
            } else {
                System.out.println("success");
                mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
            }
        }

    }

}