package com.arneb.ledstripcontroller.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttManager {

    private final String LOG_TAG = this.getClass().getName();

    private static MqttManager instance;

    private Context context;
    private MqttSettings mqttSettings;

    private MqttAndroidClient client;

    public static MqttManager getInstance(Context context, MqttSettings mqttSettings, IMqttActionListener iMqttActionListener) {
        if (instance == null) {
            instance = new MqttManager(context, mqttSettings, iMqttActionListener);
        }

        return instance;
    }

    private MqttManager(Context context, MqttSettings mqttSettings, IMqttActionListener iMqttActionListener) {
        this.context = context;
        this.mqttSettings = mqttSettings;

        connectToChannel(iMqttActionListener);
    }

    private void connectToChannel(IMqttActionListener iMqttActionListener) {
        String clientID = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.context, mqttSettings.getFullServerAddress(), clientID);
        client.setCallback(new MqttCallBackHandler());
        MqttConnectOptions connectOptions = new MqttConnectOptions();

        if (!mqttSettings.getUsername().isEmpty())
            connectOptions.setUserName(mqttSettings.getUsername());

        if (!mqttSettings.getPassword().isEmpty())
            connectOptions.setPassword(mqttSettings.getPassword().toCharArray());

        try {
            IMqttToken token = client.connect(connectOptions);
            token.setActionCallback(iMqttActionListener);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttCallBackHandler implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.d(LOG_TAG, "Connected to MQTT server");
        }

        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }
}