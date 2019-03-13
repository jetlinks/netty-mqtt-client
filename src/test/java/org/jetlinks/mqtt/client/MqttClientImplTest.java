package org.jetlinks.mqtt.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.Future;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * @author zhouhao
 * @since
 */
public class MqttClientImplTest {


    public static void main(String[] args) throws Exception {

        EventLoopGroup loop = new NioEventLoopGroup();

        for (int i = 0; i < 20000; i++) {
            MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
                System.out.println(topic + "=>" + payload.toString());
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
            Future<MqttConnectResult> future = mqttClient.connect("127.0.0.1", 1883);
            boolean success = future.await(4, TimeUnit.SECONDS);
            System.out.println(i + " connect:" + success);
            mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
        }
    }

}