package com.arneb.ledstripcontroller.mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttManager {
    private Context context;
    private MqttSettings mqttSettings;

    private MqttAndroidClient client;

    public MqttManager(Context context, MqttSettings mqttSettings, IMqttActionListener iMqttActionListener) {
        this.context = context;
        this.mqttSettings = mqttSettings;

        connectToChannel(iMqttActionListener);
    }

    private void connectToChannel(IMqttActionListener iMqttActionListener) {
        String clientID = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.context, mqttSettings.getFullServerAddress(), clientID);

        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(mqttSettings.getUsername());
            connectOptions.setPassword(mqttSettings.getPassword().toCharArray());

            IMqttToken token = client.connect(connectOptions);

            token.setActionCallback(iMqttActionListener);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}