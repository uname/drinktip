package com.uname.drinktip;

import android.content.Context;
import android.util.Log;

import com.zsofware.androidMqttLib.service.AbsMQTTReceive;

import org.eclipse.paho.client.mqttv3.MqttClient;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MessageReceiver extends AbsMQTTReceive {
    private static final String TAG = MessageReceiver.class.getSimpleName();
    @Override
    public void MsgReceive(Context context, String topicName, String msg, boolean notifyShowing, MqttClient client) {
        Log.d(TAG, String.format("MsgReceive [%s]  [%s] [%s]", topicName,
                msg, notifyShowing));
    }
}
