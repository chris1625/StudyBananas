package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by chris on 2/25/17.
 */

public class GroupInteractionPresenter implements GroupInteractionContract.Presenter,
                                                  DatabaseCallback.GetCourseCallback,
                                                  DatabaseCallback.GetUserCallback {

    private final GroupInteractionContract.View mGroupInteractionView;
    private String mCourseName;
    private Course mCourse;
    private String mGroupID;
    private User mUser;
    private ArrayList<String> mMembers;
    private com.bananabanditcrew.studybananas.data.Group mGroup;
    private DatabaseHandler mDatabase;
    private HomeContract.HomeActivityCallback mActivityCallback;

    // Separate fields for edited end time
    private int mEditedEndHour;
    private int mEditedEndMinute;

    // Object to synchronize on to ensure we don't have race conditions
    private static final Object lock = new Object();

    public GroupInteractionPresenter(@NonNull GroupInteractionContract.View groupInteractionView,
                                     String course, String groupID,
                                     HomeContract.HomeActivityCallback callback) {
        mGroupInteractionView = groupInteractionView;
        mGroupInteractionView.setPresenter(this);
        mDatabase = DatabaseHandler.getInstance();

        // Get the current user from database
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);

        mCourseName = course;
        mGroupID = groupID;

        mActivityCallback = callback;

        Log.d("Presenter", "Creating new interaction presenter");
    }

    @Override
    public void start() {
        // TODO fill in GroupInteractionPResenter's start method
    }

    @Override
    public void setActionBarTitle() {
        Log.d("Title", "Setting title in group interaction page to " + mCourseName);
        mGroupInteractionView.setActionBarTitle(mCourseName);
    }

    @Override
    public void getGroupFromDatabase() {
        mDatabase.getCourse(mCourseName, this);
    }

    @Override
    public void onCourseRetrieved(Course course) {
        synchronized (lock) {

            GroupInteractionFragment fragment = (GroupInteractionFragment)
                    ((HomeActivity)mActivityCallback).
                    getSupportFragmentManager().findFragmentByTag("group_interaction");

            boolean fragmentIsActive = (fragment != null && fragment.isVisible());

            mCourse = course;
            com.bananabanditcrew.studybananas.data.Group group =
                    new com.bananabanditcrew.studybananas.data.Group(mGroupID);
            int groupIndex = course.getStudyGroups().indexOf(group);
            boolean groupExists = (groupIndex != -1);

            // Initial check for a null group. If group is null, leave the group
            if (!groupExists) {
                mDatabase.removeGroupValueEventListener(mCourseName);
                if (fragmentIsActive) {
                    fragment.showHomeView(mActivityCallback);
                    mActivityCallback.hideActionButtons();
                }
                return;
            }

            // Get group
            mGroup = course.getGroupByIndex(groupIndex);

            Log.d("Interaction", "Course retrieved, updating info...");
            updateGroupInfo();

            // Initialize members array list if null, and also create an adapter for it
            if (mMembers == null) {
                mMembers = mGroup.getGroupMembers();
                mGroupInteractionView.createAdapter(mMembers);
            } else {
                mMembers.removeAll(mMembers);
                mMembers.addAll(mGroup.getGroupMembers());
            }

            // If group exists but the current user is no longer part of the member list, leave the
            // group
            if (!mMembers.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail()) &&
                    mUser != null) {
                Log.d("Group management", "User kicked from group");
                mDatabase.removeGroupValueEventListener(mCourseName);
                if (fragmentIsActive) {
                    mActivityCallback.hideActionButtons();
                    fragment.showHomeView(mActivityCallback);
                }
                return;
            }

            // Notify adapter of member change
            mGroupInteractionView.notifyAdapter();
        }
    }

    @Override
    public void leaveGroup() {
        synchronized (lock) {
            Log.d("Presenter", "Leaving group");
            int groupIndex = mCourse.getStudyGroups().indexOf(mGroup);

            // Remove listeners
            mDatabase.removeGroupValueEventListener(mCourse.getCourseName());
            mDatabase.removeServiceValueEventListener(mCourse.getCourseName());

            // Only update the group if it still exists
            if (groupIndex != -1) {

                mCourse.getStudyGroups().get(groupIndex).removeGroupMember(mUser.getEmail());

                // One further check: if we are group leader, this action should delete the group
                if (mGroup.getLeader()
                        .equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    mCourse.removeStudyGroup(mGroup);
                }

                // Update this course's info and push it to the database
                mDatabase.updateCourse(mCourse);
            }

            // Update user info and push to database
            mUser.setGroupID(null);
            mUser.setGroupCourse(null);
            mDatabase.updateUser(mUser);

            mGroupInteractionView.showHomeView(mActivityCallback);
            mActivityCallback.hideActionButtons();
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        synchronized (lock) {
            mUser = user;
            updateLeaderFunctions();
        }
    }

    @Override
    public void addGroupListener() {
        mDatabase.addGroupValueEventListener(mCourseName, this);
    }

    @Override
    public void removeGroupListener() {
        mDatabase.removeGroupValueEventListener(mCourseName);
    }

    @Override
    public void updateGroupInfo() {
        if (mGroup != null) {
            mGroupInteractionView.setLocation(mGroup.getLocationName());
            mGroupInteractionView.setMemberCount(parseMemberCount());
            mGroupInteractionView.setStartTime(parseStartTime(mGroup.getStartHour(), mGroup.getStartMinute()));
            mGroupInteractionView.setEndTime(parseEndTime(mGroup.getEndHour(), mGroup.getEndMinute()));
            mGroupInteractionView.setDescription((mGroup.getDescription() != null) ?
                    mGroup.getDescription() : "");
            updateLeaderFunctions();
        }
    }

    private void updateLeaderFunctions() {
        if (mUser != null && mGroup != null) {
            if (mGroup.getLeader().equals(mUser.getEmail())) {
                mActivityCallback.showEditActionButton();
                mGroupInteractionView.setMemberListViewVisibility(true);
                mGroupInteractionView.setLeaveButtonText
                        (mGroupInteractionView.getStringByID(R.string.disband_group));
            } else {
                mActivityCallback.hideActionButtons();
                mGroupInteractionView.setMemberListViewVisibility(false);
                mGroupInteractionView.setLeaveButtonText
                        (mGroupInteractionView.getStringByID(R.string.leave_group));
            }
        }
    }

    private String parseMemberCount() {
        synchronized (lock) {
            return Integer.toString(mGroup.getGroupMembers().size()) + "/" +
                    Integer.toString(mGroup.getMaxMembers());
        }
    }

    private String parseStartTime(int startHour, int startMinute) {

        // Boolean for AM vs PM
        boolean startIsPostMeridian = (startHour >= 12);


        int start12Hour = ((startIsPostMeridian) ? ((startHour == 12) ? 12 : (startHour - 12)) :
                ((startHour == 0) ? 12 : startHour));

        // Time string
        return Integer.toString(start12Hour) + ":" +
                (startMinute < 10 ? "0" + Integer.toString(startMinute) : Integer.toString(startMinute)) +
                (startIsPostMeridian ? " PM" : " AM");
    }

    private String parseEndTime(int endHour, int endMinute) {

        // Boolean for AM vs PM
        boolean endIsPostMeridian = (endHour >= 12);

        int end12Hour = ((endIsPostMeridian) ? ((endHour == 12) ? 12 : (endHour - 12)) :
                ((endHour == 0) ? 12 : endHour));

        // Time string
        return Integer.toString(end12Hour) + ":" +
                (endMinute < 10 ? "0" + Integer.toString(endMinute) : Integer.toString(endMinute)) +
                (endIsPostMeridian ? " PM" : " AM");
    }

    @Override
    public void updateEndTime(int endHour, int endMinute) {

        synchronized (lock) {
            if (mGroup != null) {

                // First grab the start hour so we can calculate the total time range
                int startHour = mGroup.getStartHour();
                int startMinute = mGroup.getStartMinute();

                int timeDiff = endHour - startHour;
                timeDiff = (timeDiff < 0) ? 24 + timeDiff : timeDiff;

                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);

                // Only update the fields if the user selected a valid time
                if (timeDiff < 16 && !((startHour == endHour) && (endMinute < startMinute))) {
                    mGroupInteractionView.setEndTimeButtonText(parseEndTime(endHour, endMinute));
                    mEditedEndHour = endHour;
                    mEditedEndMinute = endMinute;
                }

                Log.d("Time picker", "Time difference is " + Integer.toString(timeDiff));
            }
        }
    }

    @Override
    public void updateEditFields() {
        synchronized (lock) {
            if (mGroup != null) {
                mGroupInteractionView.setMemberCountEdit(Integer.toString(mGroup.getMaxMembers()));
                mGroupInteractionView
                        .setEndTimeButtonText(parseEndTime(mGroup.getEndHour(), mGroup.getEndMinute()));
                mGroupInteractionView.setDescriptionEdit((mGroup.getDescription() != null) ?
                        mGroup.getDescription() : "");

                // Default setting for end time
                mEditedEndHour = mGroup.getEndHour();
                mEditedEndMinute = mGroup.getEndMinute();
            }
        }
    }

    @Override
    public void updateGroupInDataBase() {
        synchronized (lock) {
            // Grab info from fragment and make sure it is valid
            // Time is stored in instance vars and is already valid, so update the group
            mGroup.setEndHour(mEditedEndHour);
            mGroup.setEndMinute(mEditedEndMinute);

            // Parse the member count and ensure it is valid
            int memberCount;
            try {
                memberCount = Integer.parseInt(mGroupInteractionView.getMemberCountEdited());
            } catch (Exception e) {
                memberCount = 0;
            }
            Log.d("Interaction", "Got membercount: " + Integer.toString(memberCount));
            if (memberCount <= 20 && memberCount >= mGroup.getGroupMembers().size()) {
                mGroup.setMaxMembers(memberCount);
            }

            // Directly update description, as validation is not necessary
            mGroup.setDescription(mGroupInteractionView.getDescriptionEdited());

            // Now push the updated course to the database
            ArrayList<Group> studyGroups = mCourse.getStudyGroups();
            int groupIndex = studyGroups.indexOf(mGroup);
            studyGroups.set(groupIndex, mGroup);
            mCourse.setStudyGroups(studyGroups);
            mDatabase.updateCourse(mCourse);
        }
    }

    @Override
    public int getEditedEndHour() {
        return mEditedEndHour;
    }

    @Override
    public int getEditedEndMinute() {
        return mEditedEndMinute;
    }

    @Override
    public String getCourseName() {
        return mCourseName;
    }

    @Override
    public boolean isCurrentUser(String user) {
        synchronized (lock) {
            return FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(user);
        }
    }

    @Override
    public String getGroupLeader() {
        synchronized (lock) {
            return (mGroup == null) ? "" : mGroup.getLeader();
        }
    }

    @Override
    public void kickUser(String user) {
        synchronized (lock) {
            // Remove the user from the arraylist and update it
            ArrayList<Group> studyGroups = mCourse.getStudyGroups();
            int groupIndex = mCourse.getStudyGroups().indexOf(mGroup);
            ArrayList<String> members = mGroup.getGroupMembers();
            members.remove(user);
            mGroup.setGroupMembers(members);
            studyGroups.set(groupIndex, mGroup);
            mDatabase.updateCourse(mCourse);
        }
    }

    @Override
    public void transferLeadership(String user) {
        synchronized (lock) {
            // Change the leader of the group and update the database
            ArrayList<Group> studyGroups = mCourse.getStudyGroups();
            int groupIndex = mCourse.getStudyGroups().indexOf(mGroup);
            mGroup.setLeader(user);
            studyGroups.set(groupIndex, mGroup);
            mDatabase.updateCourse(mCourse);
        }
    }

    @Override
    public void onResume() {
        // Force a refresh on the data
        getGroupFromDatabase();
    }

    @Override
    public String getGroupID() {
        return mGroupID;
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public String getAddress() {
        return mGroup != null ? mGroup.getAddressLine() : "";
    }
}