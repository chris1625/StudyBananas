package com.bananabanditcrew.studybananas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
        new Thread() {
            public void run() {
                Log.d("LDAP Auth", "Starting LDAP method");
                LDAPConnection connection;
                try

                {
                    String user = threadUsername + "@" + threadDomain;
                    connection = new LDAPConnection(threadDomain, 389, user, threadPassword);
                    Log.d("LDAP Auth", "Authentication successful");
                } catch (
                        LDAPException e
                        )

                {
                    Log.e("LDAP Auth", "Authentication failed");
                    e.printStackTrace();
                }
            }
        }.start();
        return;
    }
}

