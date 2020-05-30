package com.arneb.ledstripcontroller.mqtt;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.arneb.ledstripcontroller.Ledstrip;

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

import java.nio.charset.StandardCharsets;

public class MqttManager {

    private final String LOG_TAG = this.getClass().getName();

    private static MqttManager instance;

    private MqttSettings mqttSettings;

    private MqttAndroidClient client;

    public static MqttManager getInstance() {
        if (instance == null) {
            instance = new MqttManager();
        }

        return instance;
    }

//    private MqttManager(Context context, MqttSettings mqttSettings, IMqttActionListener iMqttActionListener) {
//        this.context = context;
//        this.mqttSettings = mqttSettings;
//
//        connectToChannel(iMqttActionListener);
//    }

    private MqttManager() { }

    public void connect(Context context) {
        String clientID = MqttClient.generateClientId();

        client = new MqttAndroidClient(context, mqttSettings.getFullServerAddress(), clientID);
        client.setCallback(new MqttCallBackHandler());

        MqttConnectOptions connectOptions = new MqttConnectOptions();

        if (!mqttSettings.getUsername().isEmpty())
            connectOptions.setUserName(mqttSettings.getUsername());

        if (!mqttSettings.getPassword().isEmpty())
            connectOptions.setPassword(mqttSettings.getPassword().toCharArray());

        try {
            Log.d(LOG_TAG, "Connecting to MQTT server...");
            IMqttToken token = client.connect(connectOptions);
            token.setActionCallback(new MqttConnectActionListener());

        } catch (MqttException e) {
            Log.e(LOG_TAG, String.format("Failed connecting to MQTT server due to: %s", e.getMessage()));
        }
    }

    public void subscribeToTopic(String topic, @Nullable IMqttActionListener iMqttActionListener) {
        try {
            Log.d(LOG_TAG, String.format("Subscribing to topic \"%s\"...", topic));
            IMqttToken token = client.subscribe(topic, this.mqttSettings.getQos());
            token.setActionCallback(iMqttActionListener);

        } catch (MqttException e) {
            Log.e(LOG_TAG, String.format("Failed subscribing to topic \"%s\" due to: %s", topic, e.getMessage()));
        }
    }

    private void publishToTopic(String topic, String message) {
        MqttMessage newMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));

        try {
            Log.i(LOG_TAG, String.format("Publishing message \"%s\" to topic \"%s\"", message, topic));
            client.publish(topic, newMessage);

        } catch (MqttException e) {
            Log.e(LOG_TAG, String.format("Failed publishing message \"%s\" to topic \"%s\" due to: %s", message, topic, e.getMessage()));
        }
    }

    public void publish(String ledstripTopic, String message) {
        this.publishToTopic(this.mqttSettings.getBaseTopic() + "/" + ledstripTopic, message);
    }

    public void disconnect() {
        if (!isConnected()) return;

        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void setMqttSettings(MqttSettings mqttSettings) {
        this.mqttSettings = mqttSettings;
    }

    public MqttSettings getMqttSettings() {
        return mqttSettings;
    }

    class MqttConnectActionListener implements IMqttActionListener {

        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d(LOG_TAG, "Successfully connected to MQTT server!");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(LOG_TAG, "Failed whilst trying to connect to MQTT server due to: " + exception.getMessage());
        }
    }

    class MqttCallBackHandler implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.d(LOG_TAG, "Connected to MQTT server at " + serverURI);
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.d(LOG_TAG, String.format("Connection with MQTT server lost due to: %s", cause.getMessage()));
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            Log.d(LOG_TAG, String.format("Message (\"%s\") arrived on topic \"%s\"", topic, message.toString()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.d(LOG_TAG, "Delivery of " + token.toString() + " was successful");
        }
    }
}