package org.jetlinks.mqtt.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * @author zhouhao
 * @since
 */
public class MqttClientImplTest {


    public static void main(String[] args) throws Exception {
        EventLoopGroup loop = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
            System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
        }));

        mqttClient.setEventLoop(loop);
        mqttClient.getClientConfig().setChannelClass(NioSocketChannel.class);
        mqttClient.getClientConfig().setClientId("test1");
        mqttClient.getClientConfig().setUsername("test1");
        mqttClient.getClientConfig().setPassword("test");
        mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);
        mqttClient.getClientConfig().setReconnect(false);
        mqttClient.setCallback(new MqttClientCallback() {
            @Override
            public void connectionLost(Throwable cause) {

                cause.printStackTrace();
            }

            @Override
            public void onSuccessfulReconnect() {

            }
        });
        mqttClient.connect("127.0.0.1", 1883)
                .addListener(future -> {
                    try {
                        MqttConnectResult result = (MqttConnectResult) future.get(15, TimeUnit.SECONDS);
                        if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                            System.out.println("error:" + result.getReturnCode() + "--");
                            mqttClient.disconnect();
                        } else {
                            System.out.println("success:");
//                mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).await(5, TimeUnit.SECONDS);

    }

}