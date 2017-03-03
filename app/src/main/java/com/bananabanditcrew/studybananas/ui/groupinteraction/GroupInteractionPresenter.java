package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomePresenter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

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
            mCourse = course;
            com.bananabanditcrew.studybananas.data.Group group =
                    new com.bananabanditcrew.studybananas.data.Group(mGroupID);
            int groupIndex = course.getStudyGroups().indexOf(group);
            mGroup = course.getGroupByIndex(groupIndex);

            Log.d("Interaction", "Course retrieved, updating info...");
            updateGroupInfo();
        }
    }

    @Override
    public void leaveGroup() {
        synchronized (lock) {
            int groupIndex = mCourse.getStudyGroups().indexOf(mGroup);
            mCourse.getStudyGroups().get(groupIndex).removeGroupMember(mUser.getEmail());

            // Update this course's info and push it to the database
            mDatabase.updateCourse(mCourse);

            // Update user info and push to database
            mUser.setGroupID(null);
            mUser.setGroupCourse(null);
            mDatabase.updateUser(mUser);

            // Remove the group listener
            mDatabase.removeGroupValueEventListener(mCourse.getCourseName());

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
    public void openMapView() {

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
        mGroupInteractionView.setLocation(mGroup.getLocationName());
        mGroupInteractionView.setMemberCount(parseMemberCount());
        mGroupInteractionView.setStartTime(parseStartTime(mGroup.getStartHour(), mGroup.getStartMinute()));
        mGroupInteractionView.setEndTime(parseEndTime(mGroup.getEndHour(), mGroup.getEndMinute()));
        mGroupInteractionView.setDescription((mGroup.getDescription() != null) ?
                mGroup.getDescription() : "");
        updateLeaderFunctions();
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
                (startMinute == 0 ? "00" : Integer.toString(startMinute)) +
                (startIsPostMeridian ? " PM" : " AM");
    }

    private String parseEndTime(int endHour, int endMinute) {

        // Boolean for AM vs PM
        boolean endIsPostMeridian = (endHour >= 12);

        int end12Hour = ((endIsPostMeridian) ? ((endHour == 12) ? 12 : (endHour - 12)) :
                ((endHour == 0) ? 12 : endHour));

        // Time string
        return Integer.toString(end12Hour) + ":" +
                (endMinute == 0 ? "00" : Integer.toString(endMinute)) +
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
            int memberCount = Integer.parseInt(mGroupInteractionView.getMemberCountEdited());
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
}