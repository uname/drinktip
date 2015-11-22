package com.zsofware.androidMqttLib.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by uname on 2015/11/20.
 */
public class MqttClientHelper {

    private MqttClient mqttClient;

    private static MqttClientHelper instance;
    private MqttClientHelper() {

    }

    public interface OnConnectListener {
        void connectResult(boolean result);
    }
    private OnConnectListener onConnectListener;
    public void setOnConnectListener(OnConnectListener listener) {
        onConnectListener = listener;
    }

    public void setConnectResult(boolean result) {
        if(onConnectListener != null) {
            onConnectListener.connectResult(result);
        }
    }

    public synchronized static MqttClientHelper getInstance() {
        if(instance == null) {
            instance = new MqttClientHelper();
        }
        return instance;
    }

    public MqttClient createClient(String serverURI, String clientId, MqttClientPersistence persistence) {
        try {
            mqttClient = new MqttClient(serverURI, clientId, persistence);
            return mqttClient;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void publish(final String topic, final byte[] payload, final int qos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient.publish(topic, payload, qos, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void publish(String topic, String text) {
         publish(topic, text.getBytes(), 0);
    }
}
