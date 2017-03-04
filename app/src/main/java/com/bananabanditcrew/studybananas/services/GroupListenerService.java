package com.bananabanditcrew.studybananas.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionContract;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by chris on 3/3/17.
 */

public class GroupListenerService extends Service implements DatabaseCallback.GetCourseCallback,
                                                             DatabaseCallback.GetUserCallback {

    private final IBinder mBinder = new LocalBinder();
    private GroupListenerServiceCallbacks mCallback;
    private GroupInteractionContract.Presenter mPresenter;
    private Notification mNotification;
    private String mCourseName;
    private String mGroupID;
    private ArrayList<String> mMembers;
    private Group mGroup;
    private User mUser;
    private DatabaseHandler mDatabase;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public GroupListenerService getService() {
            // Return this instance of MyService so clients can call public methods
            return GroupListenerService.this;
        }
    }

    @Override @TargetApi(16)
    public void onCreate() {
        // Create persistent notification here if it does not exist
        Log.d("Service", "Creating service");
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (mNotification == null) {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("You are currently in a study group")
                    .setContentText("Tap to re-open app")
                    .setSmallIcon(R.drawable.ic_logobunches_solid)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, mNotification);
        }

        // Get instance of database
        mDatabase = DatabaseHandler.getInstance();

        // Get user
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);
    }

    @Override
    public void onUserRetrieved(User user) {
        mUser = user;
    }

    @Override
    public void onCourseRetrieved(Course course, boolean uiIsActive) {
        Log.d("Service", "Course updated in background service");

        // Notification manager for all notifications
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification intent for all notifications
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        com.bananabanditcrew.studybananas.data.Group group =
                new com.bananabanditcrew.studybananas.data.Group(mPresenter.getGroupID());
        int groupIndex = course.getStudyGroups().indexOf(group);
        boolean groupExists = (groupIndex != -1);

//        mNotifyMgr.notify(2, new Notification.Builder(this)
//                .setContentTitle("StudyBananas")
//                .setContentText("Your study group has changed.")
//                .setSmallIcon(R.drawable.ic_logobunches_solid)
//                .setContentIntent(pendingIntent)
//                .build());
    }

    @Override
    public IBinder onBind (Intent intent) {
        return mBinder;
    }

    public void setCallbacks(GroupListenerServiceCallbacks callbacks) {
        mCallback = callbacks;
    }

    public void setPresenter(GroupInteractionContract.Presenter presenter) {
        mPresenter = presenter;
        mCourseName = presenter.getCourseName();
        mGroupID = presenter.getGroupID();
        // Add listener to this course specifically for this service
        if (mCourseName != null) {
            mDatabase.addServiceValueEventListener(mCourseName, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.removeServiceValueEventListener(mCourseName);
        Log.d("Service", "Destroying service");
    }
}
