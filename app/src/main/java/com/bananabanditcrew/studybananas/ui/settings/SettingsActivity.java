package com.bananabanditcrew.studybananas.ui.settings;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ToggleButton;
import android.view.View;
import com.bananabanditcrew.studybananas.R;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREF_FILE = "MyPrefs";
    public static final String ALL_SETTINGS_PREF = "allSettings";
    public static final String JOIN_SETTINGS_PREF= "newJoinSettings";
    public static final String LEAVE_SETTINGS_PREF= "leaveSettings";
    public static final String INFO_SETTINGS_PREF = "infoChangeSettings";
    public static final String SECRET_SONG_PREF = "secretSongPreference";

    private static final int secret_threshold = 10;
    private Switch switch1, switch2, switch3, switch4;
    private Button secret_button;
    private ToggleButton secret_toggle_button;
    private Toolbar mToolbar;

    private static MediaPlayer songPlayer;

    private static int timesClicked;
    private static boolean showSecret = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);

        // Back button for easy navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create all switches
        switch1 = (Switch) findViewById(R.id.switch6);
        switch2 = (Switch) findViewById(R.id.switch10);
        switch3 = (Switch) findViewById(R.id.switch9);
        switch4 = (Switch) findViewById(R.id.switch8);

        // Create secret buttons
        secret_button = (Button) findViewById(R.id.secret_button);
        secret_toggle_button = (ToggleButton) findViewById(R.id.secret_toggle_button);

        // Get Preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREF_FILE, 0);
        boolean allSettingState = settings.getBoolean(ALL_SETTINGS_PREF, true);
        boolean newJoinSettingState = settings.getBoolean(JOIN_SETTINGS_PREF, true);
        boolean leaveSettingState = settings.getBoolean(LEAVE_SETTINGS_PREF, true);
        boolean infoSettingState = settings.getBoolean(INFO_SETTINGS_PREF, true);

        // Possibly move this into if (showSecret) statement
        boolean secretSongState = settings.getBoolean(SECRET_SONG_PREF, false);

        // Update buttons based on preferences
        switch1.setChecked(allSettingState);
        switch2.setChecked(newJoinSettingState);
        switch3.setChecked(leaveSettingState);
        switch4.setChecked(infoSettingState);

        // Create a media player to load the secret song, and have it ready to loop.
        // Do this only upon the first creation of the activity, and the player is static.
        if (songPlayer == null) {
            songPlayer = MediaPlayer.create(SettingsActivity.this, R.raw.secret_message);
            songPlayer.setLooping(true);
        }

        // Check to see if the secret button will be enabled. If enabled, keep its
        // preferences updated.
        if (showSecret) {
            enableSecretButton();
            secret_toggle_button.setChecked(secretSongState);
        }

        // Switch1 controls the state of all three other switches
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    switch2.setChecked(isChecked);
                    switch3.setChecked(isChecked);
                    switch4.setChecked(isChecked);

            }

        });

        // Listen for enough secret button presses before enabling super secret button
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

        // Listen for enough secret button presses before enabling super secret button
        secret_toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    // Start song when button is enabled.
                    songPlayer.start();
                }
                else {
                    // Stop song when button is disabled.
                    songPlayer.stop();
                    try {
                        songPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Go back when back is pressed
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // Update preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ALL_SETTINGS_PREF, switch1.isChecked());
        editor.putBoolean(JOIN_SETTINGS_PREF, switch2.isChecked());
        editor.putBoolean(LEAVE_SETTINGS_PREF, switch3.isChecked());
        editor.putBoolean(INFO_SETTINGS_PREF, switch4.isChecked());
        editor.putBoolean(SECRET_SONG_PREF, secret_toggle_button.isChecked());
        // Apply preference updates * MUST DO THIS LINE AFTER EVERY UPDATE OF PREFERENCE *
        editor.apply();
    }

    // Enable and show secret button
    public void enableSecretButton() {
        secret_toggle_button.setEnabled(true);
        secret_toggle_button.setVisibility(View.VISIBLE);
    }

}