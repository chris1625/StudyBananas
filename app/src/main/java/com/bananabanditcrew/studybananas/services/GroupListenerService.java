package com.bananabanditcrew.studybananas.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionContract;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionPresenter;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;

/**
 * Created by chris on 3/3/17.
 */

public class GroupListenerService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private GroupListenerServiceCallbacks mCallback;
    private GroupInteractionContract.Presenter mPresenter;
    private Notification mNotification;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public GroupListenerService getService() {
            // Return this instance of MyService so clients can call public methods
            return GroupListenerService.this;
        }
    }

    @Override
    public void onCreate() {
        // Create persistent notification here if it does not exist
        Log.d("Service", "Creating service");
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (mNotification == null) {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("StudyBananas")
                    .setContentText("You are currently a member of a group")
                    .setSmallIcon(R.drawable.ic_logobunches_solid)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, mNotification);
        }
    }

    @Override
    public IBinder onBind (Intent intent) {
        return mBinder;
    }

    public void setCallbacks(GroupListenerServiceCallbacks callbacks) {
        mCallback = callbacks;
    }

    public void setPresenter(GroupInteractionContract.Presenter presenter) {
        if (mPresenter == null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "Destroying service");
    }
}
