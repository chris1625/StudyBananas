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
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionContract;
import com.bananabanditcrew.studybananas.ui.settings.SettingsActivity;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chris on 3/3/17.
 */

public class GroupListenerService extends Service implements DatabaseCallback.GetCourseCallback,
                                                             DatabaseCallback.GetUserCallback {

    // Constants for notification IDs
    private static final int NOTIFY_DISBANDED = 0;
    private static final int NOTIFY_FOREGROUND = 1;
    private static final int NOTIFY_NEW_LEADER = 2;
    private static final int NOTIFY_NEW_ENDTIME = 3;
    private static final int NOTIFY_NEW_CAPACITY = 4;
    private static final int NOTIFY_KICKED = 5;
    private static final int NOTIFY_MEMBER_JOIN = 6;
    private static final int NOTIFY_MEMBER_LEAVE = 7;
    private static final int NOTIFY_DESCRIPTION_CHANGE = 8;
    private static final int NOTIFY_TIME_WARNING = 9;

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
    private PendingIntent mWarningIntent;

    // Object to synchronize on
    private static final Object lock = new Object();

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
            synchronized (lock) {
                Log.d("Service", "Expiry alarm triggered");
                // Disband the group
                removeExpiredGroup();
            }
        }
    };

    // Receiver for alarm manager to call 5 minutes before group expires
    BroadcastReceiver timeWarningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service", "Warning alarm triggered");
            // Create notification if user is leader
            if (mGroup != null &&
                    FirebaseAuth.getInstance().getCurrentUser().getEmail().
                            equals(mGroup.getLeader())) {
                showTimeWarning();
            }
        }
    };

    // Receiver for yes button in alarm warning
    BroadcastReceiver doExtendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (lock) {
                Log.d("Service", "Extending time by 30 minutes");
                dismissTimeWarning();

                if (mGroup != null && mCourse != null) {
                    Date endTime = parseDate(mGroup);
                    Log.d("Service", "Time before extension is " + endTime);
                    Date extendedTime = extendByThirtyMinutes(endTime);

                    // Update end time to extended time
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(extendedTime);
                    Log.d("Service", "Extension time parsed is " + extendedTime);

                    // Create a copy of our group so as to not modify the current one
                    Group newGroup = new Group(mGroup);
                    newGroup.setEndHour(calendar.get(Calendar.HOUR_OF_DAY));
                    newGroup.setEndMinute(calendar.get(Calendar.MINUTE));
                    Log.d("Service", "Group's updated time is " + parseDate(newGroup));

                    // Update group in database
                    ArrayList<Group> studyGroups = mCourse.getStudyGroups();
                    int groupIndex = studyGroups.indexOf(mGroup);
                    studyGroups.set(groupIndex, newGroup);
                    mCourse.setStudyGroups(studyGroups);
                    mDatabase.updateCourse(mCourse);
                }
            }
        }
    };

    // Receiver for no button in alarm warning
    BroadcastReceiver noExtendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service", "Not extending time by 30 minutes");
            dismissTimeWarning();
        }
    };

    private void dismissTimeWarning() {
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(NOTIFY_TIME_WARNING);
    }

    @TargetApi(16)
    private void showTimeWarning() {
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // Intents for yes and no buttons
        PendingIntent doExtendIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("do_extend"), 0);
        PendingIntent noExtendIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("no_extend"), 0);
        mNotifyMgr.notify(NOTIFY_TIME_WARNING, new Notification.Builder(this)
                .setContentTitle("Your group is expiring soon")
                .setContentText("Extend end time by 30 minutes?")
                .setSmallIcon(R.drawable.ic_logobunches_solid)
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.ic_logobunches_solid, "Yes", doExtendIntent)
                .addAction(R.drawable.ic_logobunches_solid, "No", noExtendIntent)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .build());
    }

    @Override @TargetApi(16)
    public void onCreate() {
        // Create persistent notification here if it does not exist
        Log.d("Service", "Creating service");
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (mNotification == null) {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("You are currently in a study group")
                    .setContentText("Tap to re-open app")
                    .setSmallIcon(R.drawable.ic_logobunches_solid)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(NOTIFY_FOREGROUND, mNotification);
        }

        // Get instance of database
        mDatabase = DatabaseHandler.getInstance();

        // Get user
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);

        // Register receiver for group expiry
        registerReceiver(timeExpiredReceiver, new IntentFilter("alarm_receiver"));

        // Register time warning receiver
        registerReceiver(timeWarningReceiver, new IntentFilter("time_warning"));

        // Register yes button for time warning
        registerReceiver(doExtendReceiver, new IntentFilter("do_extend"));

        // Register no button for time warning
        registerReceiver(noExtendReceiver, new IntentFilter("no_extend"));
    }

    @Override
    public void onUserRetrieved(User user) {
        synchronized (lock) {
            mUser = user;
        }
    }

    private void removeExpiredGroup() {
        synchronized (lock) {
            if (mCourse != null && mGroup != null) {
                // If is the leader, delete the group
                if (mGroup.getLeader().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    mDatabase.removeGroupFromCourse(mCourseName, mGroup);
                }
            }
        }
    }

    @TargetApi(16)
    private void showNotification(String title, String text, int id) {
        Intent notificationIntent = new Intent(this, SignInActivity.class);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // We want the notification to reopen the app
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyMgr.notify(id, new Notification.Builder(this)
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
        synchronized (lock) {
            // Get shared preferences for notifications
            SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREF_FILE, 0);
            boolean allNotifications = settings.getBoolean(SettingsActivity.ALL_SETTINGS_PREF, true);
            boolean joinNotifications = settings.getBoolean(SettingsActivity.JOIN_SETTINGS_PREF, true);
            boolean leaveNotifications = settings.getBoolean(SettingsActivity.LEAVE_SETTINGS_PREF, true);
            boolean infoNotifications = settings.getBoolean(SettingsActivity.INFO_SETTINGS_PREF, true);

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
                if (allNotifications) {
                    showNotification("Your study group has been disbanded",
                            "Tap to create or join another group", NOTIFY_DISBANDED);
                }

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

            // Get previous maxMembers and description
            int prevCapacity = mGroup != null ? mGroup.getMaxMembers() : 0;
            String prevDescription = mGroup != null ? mGroup.getDescription() : null;

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
                    Log.d("Service", "Cancelling current alarm");
                    mAlarmIntent.cancel();
                    manager.cancel(mAlarmIntent);
                    mAlarmIntent = null;
                }

                Log.d("Service", "Registering intent with alarm manager");
                mAlarmIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent("alarm_receiver"), 0);
                manager.set(AlarmManager.RTC_WAKEUP, mEndDate.getTime(), mAlarmIntent);

                // Cancel warning alarm if it exists
                if (mWarningIntent != null) {
                    mWarningIntent.cancel();
                    manager.cancel(mWarningIntent);
                    mWarningIntent = null;

                    // Also cancel notification
                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.cancel(NOTIFY_TIME_WARNING);
                }

                Log.d("Service", "Registering warning intent with alarm manager");
                mWarningIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent("time_warning"), 0);
                manager.set(AlarmManager.RTC_WAKEUP, parseFiveMinBefore(mEndDate).getTime(),
                        mWarningIntent);
            }

            // Show notification if ownership changed
            if (mGroup.getLeader().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                    !prevLeader.equals(mGroup.getLeader()) && !prevLeader.equals("")) {

                if (allNotifications) {
                    showNotification("You are now the leader of your study group",
                            "Tap to open group management", NOTIFY_NEW_LEADER);
                }
            }

            // If you aren't the leader, show messages that group info is updated
            if (!mGroup.getLeader().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                    infoNotifications) {
                // Check for different end time
                if (previousDate != null && !mEndDate.equals(previousDate)) {
                    String timeString = new SimpleDateFormat("h:mm aa", Locale.US).format(mEndDate);
                    showNotification("Your group's end time has been updated to " + timeString,
                            "Tap to view group", NOTIFY_NEW_ENDTIME);
                }

                // Check for different maxSize
                if (prevCapacity != 0 && prevCapacity != mGroup.getMaxMembers()) {
                    showNotification("Your group's member capacity has " +
                                    ((prevCapacity > mGroup.getMaxMembers()) ? "decreased" : "increased") +
                                    " to " + Integer.toString(mGroup.getMaxMembers()), "Tap to view group",
                            NOTIFY_NEW_CAPACITY);
                }

                // Check for different description
                if (prevDescription != null && !prevDescription.equals(mGroup.getDescription())) {
                    showNotification("Your group's description has been updated", "Tap to view",
                            NOTIFY_DESCRIPTION_CHANGE);
                }
            }

            // Get the previous size of the members arraylist
            int prevSize = mMembers != null ? mMembers.size() : 0;
            ArrayList<String> prevList = mMembers;

            // Get members arraylist
            mMembers = mGroup.getGroupMembers();

            // Create copy of new list and store it
            ArrayList<String> newList = new ArrayList<>(mMembers);

            // Get the new size of the members arraylist
            int newSize = mMembers != null ? mMembers.size() : 0;

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
                if (allNotifications) {
                    showNotification("You have been kicked from the study group",
                            "Tap to join to create a new group", NOTIFY_KICKED);
                }

                // Kill activity and service if fragment not visible
                if (!fragmentIsActive) {
                    sendBroadcast(new Intent("shutdown"));
                    stopSelf();
                }
                return;
            }

            // If the difference is negative, notify the user that someone has joined
            if ((prevSize - newSize) < 0 && prevList != null) {
                // Get the difference between the lists of members
                newList.removeAll(prevList);

                // Only show the notification if you weren't the person who joined
                if (!newList.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                        joinNotifications) {
                    showNotification("A banana has joined your study bunch", "Tap to view group",
                            NOTIFY_MEMBER_JOIN);
                }
            } else if ((prevSize - newSize) > 0 && prevList != null) {
                prevList.removeAll(newList);

                // Only show the notification if you weren't the person who joined
                if (!prevList.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                        leaveNotifications) {
                    showNotification("A banana has left your study bunch", "Tap to view group",
                            NOTIFY_MEMBER_LEAVE);
                }
            }
        }
    }

    private Date parseFiveMinBefore(Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.MINUTE, -5);
        return calendar.getTime();
    }

    private Date extendByThirtyMinutes(Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.MINUTE, 30);
        return calendar.getTime();
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
        synchronized (lock) {
            mPresenter = presenter;
            mCourseName = presenter.getCourseName();
            mGroupID = presenter.getGroupID();
            // Add listener to this course specifically for this service
            if (mCourseName != null) {
                mDatabase.addServiceValueEventListener(mCourseName, this);
            }
        }
    }

    @Override
    public void onDestroy() {
        synchronized (lock) {
            super.onDestroy();
            mDatabase.removeServiceValueEventListener(mCourseName);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Unregister receiver and remove any existing alarm manager intent
            unregisterReceiver(timeExpiredReceiver);
            if (mAlarmIntent != null) {
                Log.d("Service", "Removing alarm intent");
                mAlarmIntent.cancel();
                manager.cancel(mAlarmIntent);
                mAlarmIntent = null;
            }

            // Unregister receiver and remove any existing alarm manager intent
            unregisterReceiver(timeWarningReceiver);
            if (mWarningIntent != null) {
                Log.d("Service", "Removing warning intent");
                mWarningIntent.cancel();
                manager.cancel(mWarningIntent);
                mWarningIntent = null;

                // Cancel any existing notifications
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(NOTIFY_TIME_WARNING);
            }

            // Unregister yes and no receivers
            unregisterReceiver(doExtendReceiver);
            unregisterReceiver(noExtendReceiver);

            Log.d("Service", "Destroying service");
        }
    }
}