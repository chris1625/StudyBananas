package com.bananabanditcrew.studybananas;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout  = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // Create onClick listeners for menu items
        navigation = (NavigationView) findViewById(R.id.nav_drawer);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_account:
                        // TODO
                        break;
                    case R.id.nav_logout:
                        signOut();
                        break;
                }
                return false;
            }
        });

        // TODO figure out why it doesn't like this
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // We want to add our initial home page fragment to our frameView
        // Ensure the fragment container exists
        if (findViewById(R.id.fragment_container) != null) {

            // If being restored from previous state, don't do anything
            if (savedInstanceState != null) {
                return;
            }

            // Create new Fragment to be placed in the activity layout
            HomePageFragment homePageFragment = new HomePageFragment();

            // In case this activity was started with special instructions from an Intent, pass the
            // Intent's extras to the fragment as arguments
            homePageFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the fragment container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, homePageFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Sign out */
    public void signOut() {
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