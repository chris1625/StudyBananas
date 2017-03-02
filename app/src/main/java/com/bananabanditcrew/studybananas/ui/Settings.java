package com.bananabanditcrew.studybananas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.bananabanditcrew.studybananas.R;

public class Settings extends AppCompatActivity {
    Switch switch1, switch2, switch3, switch4;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switch1 = (Switch) findViewById(R.id.switch6);
        switch2 = (Switch) findViewById(R.id.switch10);
        switch3 = (Switch) findViewById(R.id.switch9);
        switch4 = (Switch) findViewById(R.id.switch8);
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
    }


}
