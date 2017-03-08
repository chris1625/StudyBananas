package com.bananabanditcrew.studybananas.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ToggleButton;
import android.view.View;
import com.bananabanditcrew.studybananas.R;

public class SettingsActivity extends AppCompatActivity {
    private static final int secret_threshold = 5;
    private Switch switch1, switch2, switch3, switch4;
    private Button secret_button;
    private ToggleButton secret_toggle_button;

    private int timesClicked;
    private static boolean showSecret = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Create all switches
        switch1 = (Switch) findViewById(R.id.switch6);
        switch2 = (Switch) findViewById(R.id.switch10);
        switch3 = (Switch) findViewById(R.id.switch9);
        switch4 = (Switch) findViewById(R.id.switch8);

        // Create secret buttons
        secret_button = (Button) findViewById(R.id.secret_button);
        secret_toggle_button = (ToggleButton) findViewById(R.id.secret_toggle_button);

        // Get Preferences
        SharedPreferences settings = getSharedPreferences("MyPrefs", 0);
        boolean allSettingState = settings.getBoolean("allSettings", true);
        boolean newJoinSettingState = settings.getBoolean("newJoinSettings", true);
        boolean leaveSettingState = settings.getBoolean("leaveSettings", true);
        boolean managementSettingState = settings.getBoolean("managementChangeSettings", true);

        // Update buttons based on preferences
        switch1.setChecked(allSettingState);
        switch2.setChecked(newJoinSettingState);
        switch3.setChecked(leaveSettingState);
        switch4.setChecked(managementSettingState);

        // Check to see if the secret button will be enabled.
        if (showSecret) {
            enableSecretButton();
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switch2.setChecked(true);
                    switch3.setChecked(true);
                    switch4.setChecked(true);
                }
                else{
                    switch2.setChecked(false);
                    switch3.setChecked(false);
                    switch4.setChecked(false);
                }
            }

        });

        secret_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timesClicked < secret_threshold) {
                    timesClicked++;
                }
                else {
                    // Disable secret button from being clickable, and show secret song button
                    secret_button.setEnabled(false);
                    showSecret = true;
                    enableSecretButton();
                }
            }
        });

    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getSharedPreferences("MyPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("allSettings", switch1.isChecked());
        editor.putBoolean("newJoinSettings", switch2.isChecked());
        editor.putBoolean("leaveSettings", switch3.isChecked());
        editor.putBoolean("managementChangeSettings", switch4.isChecked());

        editor.apply();
    }
    public void enableSecretButton() {
        secret_toggle_button.setEnabled(true);
        secret_toggle_button.setVisibility(View.VISIBLE);
    }

}