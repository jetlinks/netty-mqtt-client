package org.jetlinks.mqtt.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
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
        EventLoopGroup loop = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        for (int i = 20000; i < 50000; i++) {
            MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
                System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
            }));
            mqttClient.setEventLoop(loop);
            mqttClient.getClientConfig().setChannelClass(EpollSocketChannel.class);
            mqttClient.getClientConfig().setClientId("test" + i);
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
            int fi = i;
            mqttClient.connect("192.168.0.13", 1883)
                    .addListener(future -> {
                        try {
                            MqttConnectResult result = (MqttConnectResult) future.get(15, TimeUnit.SECONDS);
                            if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                                System.out.println("error:" + result.getReturnCode() + "--" + fi);
                                mqttClient.disconnect();
                            } else {
                                System.out.println("success:" + fi);
//                mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).await(5,TimeUnit.SECONDS);

        }

    }

}