package com.arneb.ledstripcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.arneb.ledstripcontroller.mqtt.MqttManager;
import com.arneb.ledstripcontroller.mqtt.MqttSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IMqttActionListener {

    public final String LEDSTRIPS_KEY = "ledstrips";
    public final String SELECTED_LEDSTRIP_KEY = "selected";

    private SharedPreferences sharedPreferences;

    private List<Ledstrip> ledstrips;
    private Ledstrip currentLedstrip;

    MqttManager mqttManager;
//    View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::switchLedstrip);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        this.progressView = View.inflate(this.getApplicationContext(), R.layout.progressbar_connecting, null);
        this.mqttManager = new MqttManager(this, new MqttSettings(sharedPreferences), this);

        loadLedstrips();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mqttManager.disconnect();

        save();
    }

    private void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();

        for (Ledstrip ledstrip : ledstrips)
            jsonArray.put(ledstrip.toJson());

        editor.putString(LEDSTRIPS_KEY, jsonArray.toString());
        editor.putInt(SELECTED_LEDSTRIP_KEY, ledstrips.indexOf(currentLedstrip));
        editor.apply();
    }

    private void loadLedstrips() {
        ledstrips = new ArrayList<>();

        String jsonArrayString = sharedPreferences.getString(LEDSTRIPS_KEY, getDefaultLedstripsString());

        try {
            JSONArray ledstripJsonArray = new JSONArray(jsonArrayString);

            for (int i = 0; i < ledstripJsonArray.length(); i++) {
                JSONObject jsonObject = ledstripJsonArray.getJSONObject(i);
                ledstrips.add(new Ledstrip(jsonObject));
            }

            currentLedstrip = ledstrips.get(sharedPreferences.getInt(SELECTED_LEDSTRIP_KEY, 0));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addNewLedstrip() {
        EditText ledstripName = findViewById(R.id.dialog_name);
        Switch supportsPattern = findViewById(R.id.dialog_supports_pattern);
        EditText mqttTopic = findViewById(R.id.dialog_mqtt_topic);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.action_add_ledstrip);
        dialog.setView(R.layout.dialog_configure_ledstrip);
        dialog.setPositiveButton(R.string.dialog_confirm, (dialog1, which) -> {
            String newName = ledstripName.getText().toString();
            boolean supportsPatterns = supportsPattern.isChecked();
            String newMqttTopic = mqttTopic.getText().toString();
            ledstrips.add(new Ledstrip(newName, supportsPatterns, newMqttTopic));
            save();
        });

        dialog.create().show();
    }

    private void editCurrentLedstrip() {
        EditText ledstripName = findViewById(R.id.dialog_name);
        Switch supportsPattern = findViewById(R.id.dialog_supports_pattern);
        EditText mqttTopic = findViewById(R.id.dialog_mqtt_topic);

        ledstripName.setText(currentLedstrip.getName());
        supportsPattern.setChecked(currentLedstrip.isSupportsPatterns());
        mqttTopic.setText(currentLedstrip.getMqttTopic());

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.action_edit_ledstrip);
        dialog.setView(R.layout.dialog_configure_ledstrip);
        dialog.setPositiveButton(R.string.dialog_confirm, (dialog1, which) -> {
            String newName = ledstripName.getText().toString();
            boolean supportsPatterns = supportsPattern.isChecked();
            String newMqttTopic = mqttTopic.getText().toString();

            Ledstrip editedLedstrip = ledstrips.get(which);
            editedLedstrip.setName(newName);
            editedLedstrip.setSupportsPatterns(supportsPatterns);
            editedLedstrip.setMqttTopic(newMqttTopic);

            save();
        });

        dialog.create().show();
    }

    private void deleteLedstrip(){
        // TODO: 23/05/2020 implement deleting ledstrips (don't forget to check index of curr selected ledstrip)
    }

    private void switchLedstrip(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_switch_ledstrip);

        dialog.setItems(Ledstrip.getLedstripNames(ledstrips), (dialog1, selectedPos) -> currentLedstrip = ledstrips.get(selectedPos));

        dialog.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add_ledstrip:
                addNewLedstrip();
                return true;
            case R.id.action_edit_ledstrip:
                editCurrentLedstrip();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getDefaultLedstripsString() {
        return "[{\"" + Ledstrip.LEDSTRIP_NAME_KEY + "\": \"Ledstrip 1\", " +
                "\"" + Ledstrip.LEDSTRIP_SUPPORTS_PATTERNS_KEY + "\": true, " +
                "\"" + Ledstrip.LEDSTRIP_MQTT_TOPIC_KEY + "\": \"ledstrip1\"}]";
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Toast.makeText(this, "Successfully connected to MQTT server", Toast.LENGTH_LONG)
                .show();
        System.out.println("Successfully connected to MQTT server");
//        runOnUiThread(() -> progressView.setVisibility(View.GONE));
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Toast.makeText(this, "Failed whilst trying to connect to MQTT server", Toast.LENGTH_LONG)
                .show();
        // TODO: 25/05/2020 Make use of the progressbar
        System.out.println("Failed whilst trying to connect to MQTT server");
//        runOnUiThread(() -> {
//            TextView progressTextView = progressView.findViewById(R.id.progress_textview);
//            progressTextView.setText(R.string.failed_connection);
//            progressTextView.invalidate();
//        });
    }
}