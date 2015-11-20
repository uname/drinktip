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

    public boolean publish(String topic, byte[] payload, int qos) {
        try {
            mqttClient.publish(topic, payload, qos, false);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean publish(String topic, String text) {
        return publish(topic, text.getBytes(), 0);
    }
}
