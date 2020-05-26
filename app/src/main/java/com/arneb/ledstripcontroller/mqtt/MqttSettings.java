package com.arneb.ledstripcontroller.mqtt;

import android.content.SharedPreferences;
import android.util.Log;

public class MqttSettings {

    private final String SERVER_ADDRESS_KEY = "mqtt_server_address";
    private final String PORT_KEY = "mqtt_port";
    private final String USERNAME_KEY = "mqtt_username";
    private final String PASSWORD_KEY = "mqtt_password";
    private final String BASE_TOPIC_KEY = "mqtt_base_topic";
    private final String QOS_KEY = "mqtt_qos";

    private String serverAddress;
    private String port;
    private String username;
    private String password;
    private String baseTopic;
    private int qos;

    public MqttSettings(SharedPreferences sharedPreferences) {
        updateValues(sharedPreferences);

        Log.d("MqttSettings", String.format("Initialized MqttSettings: %s, %s, %s, %s, %s, %s", serverAddress, port, username, password, baseTopic, qos));
    }

    // TODO: 22/05/2020 Update values
    public void updateValues(SharedPreferences sharedPreferences){
        this.serverAddress = sharedPreferences.getString(SERVER_ADDRESS_KEY, "192.168.178.199");
        this.port = sharedPreferences.getString(PORT_KEY, "1883");
        this.username = sharedPreferences.getString(USERNAME_KEY, "");
        this.password = sharedPreferences.getString(PASSWORD_KEY, "");
        this.baseTopic = sharedPreferences.getString(BASE_TOPIC_KEY, "ledstripcontroller");
        this.qos = Integer.parseInt(sharedPreferences.getString(QOS_KEY, "1"));
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFullServerAddress(){
        return String.format("%s:%s", serverAddress, port);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseTopic() {
        return baseTopic;
    }

    public void setBaseTopic(String baseTopic) {
        this.baseTopic = baseTopic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}