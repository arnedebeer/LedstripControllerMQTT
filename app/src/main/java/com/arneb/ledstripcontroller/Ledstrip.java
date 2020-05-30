package com.arneb.ledstripcontroller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Ledstrip {

    private final String LOG_TAG = this.getClass().getName();

    public final static String LEDSTRIP_NAME_KEY = "ledstrip_name";
    public final static String LEDSTRIP_SUPPORTS_PATTERNS_KEY = "ledstrip_supports_patterns";
    public final static String LEDSTRIP_MQTT_TOPIC_KEY = "ledstrip_mqtt_topic";

    private String name;
    private boolean supportsPatterns;

    private String mqttTopic;

    public Ledstrip(String name, boolean supportsPatterns, String mqttTopic) {
        this.name = name;
        this.supportsPatterns = supportsPatterns;
        this.mqttTopic = mqttTopic;
    }

    public Ledstrip(JSONObject jsonObject) {
        try {
            name = jsonObject.getString(LEDSTRIP_NAME_KEY);
            supportsPatterns = jsonObject.getBoolean(LEDSTRIP_SUPPORTS_PATTERNS_KEY);
            mqttTopic = jsonObject.getString(LEDSTRIP_MQTT_TOPIC_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSupportsPatterns() {
        return supportsPatterns;
    }

    public void setSupportsPatterns(boolean supportsPatterns) {
        this.supportsPatterns = supportsPatterns;
    }

    public String getMqttTopic() {
        return mqttTopic.replaceAll("/", "");
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public static String[] getLedstripNames(List<Ledstrip> ledstrips) {
        String[] ledstripNames = new String[ledstrips.size()];
        for (int i = 0; i < ledstrips.size(); i++) {
            ledstripNames[i] = ledstrips.get(i).getName();
        }

        return ledstripNames;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put(LEDSTRIP_NAME_KEY, this.name);
            jsonObject.put(LEDSTRIP_SUPPORTS_PATTERNS_KEY, this.supportsPatterns);
            jsonObject.put(LEDSTRIP_MQTT_TOPIC_KEY, this.mqttTopic);
        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }
}