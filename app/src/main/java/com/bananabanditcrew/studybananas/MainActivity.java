package com.bananabanditcrew.studybananas;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* Opens login activity */
    public void openLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /* Opens dialogue for the user */
    public void showCreationDialogue(View view) {
        FragmentManager fm = getSupportFragmentManager();
        final CreationDialogFragment creationDialogFragment = CreationDialogFragment.newInstance("Some Title");
        creationDialogFragment.show(fm, "fragment_creation_dialogue");
    }

    /* Callback method for dialogue */
    public void setName(String name) {
        Log.d("Accounts", name);
    }
}