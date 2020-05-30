package com.arneb.ledstripcontroller.mqtt;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class MqttSettings {

    private final String LOG_TAG = this.getClass().getName();

    private final String SERVER_ADDRESS_KEY = "mqtt_server_address";
    private final String PORT_KEY = "mqtt_port";
    private final String CONNECTION_TYPE_KEY = "mqtt_connection_type";
    private final String USERNAME_KEY = "mqtt_username";
    private final String PASSWORD_KEY = "mqtt_password";
    private final String BASE_TOPIC_KEY = "mqtt_base_topic";
    private final String QOS_KEY = "mqtt_qos";

    private String serverAddress;
    private String port;
    private String connectionType;
    private String username;
    private String password;
    private String baseTopic;
    private int qos;

    public MqttSettings(SharedPreferences sharedPreferences) {
        updateValues(sharedPreferences);

        Log.d(LOG_TAG, String.format("Initialized MqttSettings: %s, %s, %s, %s, %s, %s, %s", serverAddress, port, connectionType, username, password, baseTopic, qos));
    }

    public void updateValues(SharedPreferences sharedPreferences) {
        this.serverAddress = sharedPreferences.getString(SERVER_ADDRESS_KEY, "192.168.178.199");
        this.port = sharedPreferences.getString(PORT_KEY, "1883");
        this.connectionType = sharedPreferences.getString(CONNECTION_TYPE_KEY, "tcp://");
        this.username = sharedPreferences.getString(USERNAME_KEY, "");
        this.password = sharedPreferences.getString(PASSWORD_KEY, "");
        this.baseTopic = sharedPreferences.getString(BASE_TOPIC_KEY, "ledstripcontroller");
        this.qos = Integer.parseInt(sharedPreferences.getString(QOS_KEY, "1"));
    }

    public String getFullServerAddress() {
        return String.format("%s%s:%s", connectionType, serverAddress, port);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBaseTopic() {
        return baseTopic;
    }

    public int getQos() {
        return qos;
    }
}