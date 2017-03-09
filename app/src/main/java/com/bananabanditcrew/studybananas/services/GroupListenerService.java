package com.bananabanditcrew.studybananas.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionContract;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private Course mCourse;
    private ArrayList<String> mMembers;
    private Group mGroup;
    private User mUser;
    private DatabaseHandler mDatabase;
    private Date mEndDate;
    private PendingIntent mAlarmIntent;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public GroupListenerService getService() {
            // Return this instance of MyService so clients can call public methods
            return GroupListenerService.this;
        }
    }

    // Receiver for alarm manager to call once group time has expired
    BroadcastReceiver timeExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service", "Alarm triggered");
            // Disband the group
            removeExpiredGroup();
        }
    };

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

        // Register receiver for group expiry
        registerReceiver(timeExpiredReceiver, new IntentFilter("alarm_receiver"));
    }

    @Override
    public void onUserRetrieved(User user) {
        mUser = user;
    }

    private void removeExpiredGroup() {
        if (mCourse != null && mGroup != null) {
            // If is the leader, delete the group
            if (mGroup.getLeader().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                mDatabase.removeGroupFromCourse(mCourseName, mGroup);
            }
        }
    }

    @TargetApi(16)
    private void showNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification intent for all notifications
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mNotifyMgr.notify(0, new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_logobunches_solid)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .build());
    }

    @Override @TargetApi(16)
    public void onCourseRetrieved(Course course) {
        Log.d("Service", "Course updated in background service");

        mCourse = course;

        boolean fragmentIsActive = (mCallback != null);

        com.bananabanditcrew.studybananas.data.Group group =
                new com.bananabanditcrew.studybananas.data.Group(mPresenter.getGroupID());
        int groupIndex = course.getStudyGroups().indexOf(group);
        boolean groupExists = (groupIndex != -1);

        // If group doesn't exist, show a notification that the group has been disbanded, remove
        // the user's data relating to that group and kill the service.
        if (!groupExists && mUser != null) {

            Log.d("Service", "Removing group from service");

            // Update user info
            mUser.setGroupID(null);
            mUser.setGroupCourse(null);
            mDatabase.updateUser(mUser);

            // Remove group listener
            mDatabase.removeServiceValueEventListener(mCourseName);

            // Show notification
            showNotification("Your study group has been disbanded",
                    "Tap to create or join another group");

            // Kill activity and service if fragment not visible
            if (!fragmentIsActive) {
                sendBroadcast(new Intent("shutdown"));
                stopSelf();
            }

            // Useless return but we're gonna do it anyways
            return;
        }

        // Check for condition that leadership has changed
        String prevLeader = "";

        if (mGroup != null) {
            // Get previous leader
            prevLeader = mGroup.getLeader();
        }

        // Get previous date before update
        Date previousDate = null;
        if (mGroup != null) {
            previousDate = parseDate(mGroup);
        }

        // Assign new group
        mGroup = course.getGroupByIndex(groupIndex);

        // Parse end time and convert it into a date
        mEndDate = parseDate(mGroup);
        Log.d("Service", "Parsed date is " + mEndDate);

        // If user is group leader and the times are not the same create a new alarm
        if (previousDate == null || !mEndDate.equals(previousDate)) {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Cancel current alarm if it exists
            if (mAlarmIntent != null) {
                mAlarmIntent.cancel();
                manager.cancel(mAlarmIntent);
                mAlarmIntent = null;
            }

            Log.d("Service", "Registering intent with alarm manager");
            mAlarmIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent("alarm_receiver"), 0);
            manager.set(AlarmManager.RTC_WAKEUP, mEndDate.getTime(), mAlarmIntent);
        }

        // Show notification if ownership changed
        if (mGroup.getLeader().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                !prevLeader.equals(mGroup.getLeader()) && !prevLeader.equals("")) {

            showNotification("You are now the leader of your study group",
                    "Tap to open group management");
        }

        // Get members arraylist
        mMembers = mGroup.getGroupMembers();

        // If group exists but the current user is no longer part of the member list, leave the group
        if (!mMembers.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            Log.d("Service", "User kicked from group in service");

            // Update user info
            mUser.setGroupID(null);
            mUser.setGroupCourse(null);
            mDatabase.updateUser(mUser);

            // Remove group listener
            mDatabase.removeServiceValueEventListener(mCourseName);

            // Show a notification
            showNotification("You have been kicked from the study group",
                    "Tap to join to create a new group");

            // Kill activity and service if fragment not visible
            if (!fragmentIsActive) {
                sendBroadcast(new Intent("shutdown"));
                stopSelf();
            }
        }
    }

    private Date parseDate(Group group) {
        int startHour = group.getStartHour();
        int startMinute = group.getStartMinute();
        int endHour = group.getEndHour();
        int endMinute = group.getEndMinute();

        Calendar calendar = Calendar.getInstance();

        // If the time is less than the current time, it is tomorrow
        if (endHour < startHour || (endHour == startHour && startMinute > endMinute)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);

        return calendar.getTime();
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

        // Unregister receiver and remove any existing alarm manager intent
        unregisterReceiver(timeExpiredReceiver);
        if (mAlarmIntent != null) {
            Log.d("Service", "Removing alarm intent");
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmIntent.cancel();
            manager.cancel(mAlarmIntent);
            mAlarmIntent = null;
        }

        Log.d("Service", "Destroying service");
    }
}
