package com.bananabanditcrew.studybananas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToLDAP("username", "password", "ad.ucsd.edu");
    }

    /* Test for Android LDAP library */
    public void connectToLDAP(String username, String password, String domain) {
        final String threadUsername = username;
        final String threadPassword = password;
        final String threadDomain = domain;

        // Create a new thread to authenticate the user. Necessary, as network operations could
        // potentially hold up the app without async operations
        new Thread() {
            public void run() {
                Log.d("LDAP Auth", "Starting LDAP method");
                LDAPConnection connection;
                try {
                    String user = threadUsername + "@" + threadDomain;
                    connection = new LDAPConnection(threadDomain, 389, user, threadPassword);
                    Log.d("LDAP Auth", "Authentication successful");
                } catch (LDAPException e) {
                    Log.e("LDAP Auth", "Authentication failed");
                    e.printStackTrace();
                }
            }
        }.start();
        return;
    }

    /* Opens login activity */
    public void openLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

