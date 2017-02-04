package com.bananabanditcrew.studybananas;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* Opens dialogue for the user */
    public void showCreationDialogue(View view) {
        FragmentManager fm = getSupportFragmentManager();
        final CreationDialogFragment creationDialogFragment = CreationDialogFragment.newInstance("Some Title");
        creationDialogFragment.show(fm, "fragment_creation_dialogue");
    }

    /* Sign out */
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent myIntent = new Intent(MainActivity.this,LoginActivity.class);
        MainActivity.this.startActivity(myIntent);
        finish();
    }

    /* Callback method for dialogue */
    public void setName(String name) {
        Log.d("Accounts", name);
    }
}