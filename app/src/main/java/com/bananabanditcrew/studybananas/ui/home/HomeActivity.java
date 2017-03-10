package com.bananabanditcrew.studybananas.ui.home;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.services.GroupListenerService;
import com.bananabanditcrew.studybananas.ui.settings.SettingsActivity;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionPresenter;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements DatabaseCallback.GetUserCallback,
        DatabaseCallback.ConnectionStateCallback, DatabaseCallback.RootCallback,
        HomeContract.HomeActivityCallback{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private HomePresenter mHomePresenter;
    private GroupInteractionPresenter mGroupInteractionPresenter;
    private GroupInteractionFragment mGroupInteractionFragment;
    private DatabaseHandler mDatabase;
    private ProgressDialog mProgressView;

    // Variables to hide or show edit and save buttons
    private boolean mSaveActionVisible = false;
    private boolean mEditActionVisible = false;

    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    syncActionBarArrowState();
                }
            };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout  = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View view) {
                syncActionBarArrowState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mToggle.setDrawerIndicatorEnabled(true);
            }
        };

        // Set up the navigation drawer
        mDrawerLayout.addDrawerListener(mToggle);
        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
        mToggle.syncState();

        // Setup menu button on drawer
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // We want to add our initial home page fragment to our frameView
        // Ensure the fragment container exists
        if (savedInstanceState != null) {
            return;
        }

        Log.d("homeactivity", "oncreate");

        // Create home fragment and presenter so we can setup our menu items
        createHomeFragment();

        // Initialize database handler
        mDatabase = DatabaseHandler.getInstance();

        // Get the user and hand off setup to onUserRetrieved
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);

        // Setup connection state listener
        mDatabase.addConnectionStateListener(this);

        // Setup root listener
        mDatabase.addRootListener(this);

        // Register shutdown receiver
        registerReceiver(shutdownReceiver, new IntentFilter("shutdown"));
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        mToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
    }

    private final BroadcastReceiver shutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public void onUserRetrieved(User user) {

        Log.d("Users", "Retrieved user " + user.getFirstName());

        // Check if user is in a group
        if (user.getGroupID() == null) {
            // Add the home fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mHomeFragment).commit();
        } else {
            // Setup groupInteraction fragment and presenter
            if (mGroupInteractionFragment == null) {
                mGroupInteractionFragment = new GroupInteractionFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mGroupInteractionFragment,
                            "group_interaction").commit();

            if (mGroupInteractionPresenter == null) {
                mGroupInteractionPresenter = new GroupInteractionPresenter(mGroupInteractionFragment,
                        user.getGroupCourse(),
                        user.getGroupID(),
                        this);
            }

            // Start background service if it is not already running
            if (!isServiceRunning(GroupListenerService.class)) {
                // Start background service
                Intent intent = new Intent(this, GroupListenerService.class);
                startService(intent);
                Log.d("Service", "Starting background service from main activity");
            }
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        MenuItem saveItem = menu.findItem(R.id.action_save);
        saveItem.setVisible(mSaveActionVisible);

        MenuItem editItem = menu.findItem(R.id.action_edit);
        editItem.setVisible(mEditActionVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            openSettings();
            return true;
        }
        if(item.getItemId() == R.id.action_edit){
            showSaveActionButton();
        }
        if(item.getItemId() == R.id.action_save){
            showEditActionButton();
            closeKeyboard();
        }
        if (mToggle.isDrawerIndicatorEnabled() && mToggle.onOptionsItemSelected(item)) {
            return true;

        } else if (item.getItemId() == android.R.id.home &&
                getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void setupDrawerContent(NavigationView navigationView, final HomePresenter presenter) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_about:
                        // Open about us dialog
                        mDrawerLayout.closeDrawer(Gravity.START);
                        showAboutDialog();
                        break;
                    case R.id.nav_settings:
                        // Open settings activity
                        mDrawerLayout.closeDrawer(Gravity.START);
                        openSettings();
                        break;
                    case R.id.nav_logout:
                        if (isServiceRunning(GroupListenerService.class)) {
                            showSignOutError();
                        } else {
                            presenter.signOut();
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public HomeFragment createHomeFragment() {
        Log.d("HomeActivity", "Creating home fragment");
        mHomeFragment = new HomeFragment();
        mHomePresenter = new HomePresenter(mHomeFragment, this);

        // Create onClick listeners for menu items
        mNavigationView = (NavigationView) findViewById(R.id.nav_drawer);
        if (mNavigationView != null)
            setupDrawerContent(mNavigationView, mHomePresenter);

        return mHomeFragment;
    }

    @Override
    public void showEditActionButton() {

        // Only switch fields if save button was shown before
        if (mSaveActionVisible) {

            // Call method in fragment to hide edit fields
            GroupInteractionFragment fragment =
                    (GroupInteractionFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
            fragment.setFieldsEditable(false);
        }

        mEditActionVisible = true;
        mSaveActionVisible = false;

        // Refresh action bar and call onCreateOptionsMenu
        invalidateOptionsMenu();
    }

    @Override
    public void showSaveActionButton() {
        mEditActionVisible = false;
        mSaveActionVisible = true;

        // Refresh action bar and call onCreateOptionsMenu
        invalidateOptionsMenu();

        // Call method in fragment to show edit fields
        GroupInteractionFragment fragment =
                (GroupInteractionFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        fragment.setFieldsEditable(true);
    }

    @Override
    public void hideActionButtons() {
        mEditActionVisible = false;
        mSaveActionVisible = false;

        // Refresh action bar and call onCreateOptionsMenu
        invalidateOptionsMenu();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSignOutError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.sign_out_error).setTitle(R.string.group_management);
        // Add ok button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked ok button
                dialog.dismiss();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.about_dialog, null);
        // Get version textView
        final TextView versionText = (TextView) dialogView.findViewById(R.id.about_version);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText.setText(getString(R.string.about_version, pInfo.versionName));
        } catch (Exception e) {
            Log.e("Version", "Could not find package");
        }

        builder.setView(dialogView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        if (connected) {
            hideProgressView();
        } else {
            showProgressView("Network Status", "Disconnected from Firebase, please check your internet connection. Reconnecting...");
        }
    }

    private void showProgressView(String title, String body) {
        mProgressView = ProgressDialog.show(this, title, body);
    }

    private void hideProgressView() {
        if (mProgressView != null) {
            mProgressView.dismiss();
            mProgressView = null;
        }
    }

    @Override
    public void onRootRetrieved() {
        // This method does nothing; is intended to keep the activity linked to firebase with a
        // listener. Prevents firebase disconnect
        Log.d("Root Listener", "onRootRetrieved in Home Activity");
    }

    @Override
    public void onDestroy() {
        mDatabase.removeConnectionStateListener();
        unregisterReceiver(shutdownReceiver);
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mDatabase.removeConnectionStateListener();
        hideProgressView();
        super.onPause();
    }

    @Override
    public void onStop() {
        mDatabase.removeRootListener();
        mDatabase.removeConnectionStateListener();
        hideProgressView();
        super.onStop();
    }

    @Override
    public void onResume() {
        mDatabase.addRootListener(this);
        hideProgressView();
        mDatabase.addConnectionStateListener(this);
        super.onResume();
    }
}