package com.arneb.ledstripcontroller;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

class LedstripState {

    public static final String ON_KEY = "is_on";
    public static final String BRIGHTNESS_KEY = "brightness";
    public static final String COLOR_KEY = "colour";
    public static final String FADE_TIME_KEY = "fade_time";
    public static final String PATTERN_KEY = "pattern";

    private boolean isOn;
    private int brightness;
    private String color;
    private int fadeTime;
    private String pattern;

    public LedstripState(boolean isOn, int brightness, String color, int fadeTime, String pattern) {
        this.isOn = isOn;
        this.brightness = brightness;
        this.color = color;
        this.fadeTime = fadeTime;
        this.pattern = pattern;
    }

    public LedstripState(JSONObject jsonObject) {
        // TODO: 30/05/2020 Create state from json
        try {
            isOn = jsonObject.getBoolean(ON_KEY);
            brightness = jsonObject.getInt(BRIGHTNESS_KEY);
            color = jsonObject.getString(COLOR_KEY);
            fadeTime = jsonObject.getInt(FADE_TIME_KEY);
            pattern = jsonObject.getString(PATTERN_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getFadeTime() {
        return fadeTime;
    }

    public void setFadeTime(int fadeTime) {
        this.fadeTime = fadeTime;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ON_KEY, this.isOn);
            jsonObject.put(BRIGHTNESS_KEY, this.brightness);
            jsonObject.put(COLOR_KEY, this.color);
            jsonObject.put(FADE_TIME_KEY, this.fadeTime);
            jsonObject.put(PATTERN_KEY, this.pattern);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}