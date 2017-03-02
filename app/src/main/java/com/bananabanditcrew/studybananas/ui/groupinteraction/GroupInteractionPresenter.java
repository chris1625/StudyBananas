package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.support.annotation.NonNull;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomePresenter;
import com.google.firebase.auth.FirebaseAuth;

import java.security.acl.Group;

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
    public void getGroupFromDatabase() {
        mDatabase.getCourse(mCourseName, this);
    }

    @Override
    public void onCourseRetrieved(Course course) {
        mCourse = course;
        com.bananabanditcrew.studybananas.data.Group group =
                new com.bananabanditcrew.studybananas.data.Group(mGroupID);
        int groupIndex = course.getStudyGroups().indexOf(group);
        mGroup = course.getGroupByIndex(groupIndex);

        updateGroupInfo();
    }

    @Override
    public void leaveGroup() {
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

    @Override
    public void onUserRetrieved(User user) {
        mUser = user;
        updateLeaderFunctions();
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
        mGroupInteractionView.setTimeRange(parseTimeRange());
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
        return Integer.toString(mGroup.getGroupMembers().size()) + "/" +
                Integer.toString(mGroup.getMaxMembers());
    }

    private String parseTimeRange() {

        int startHour = mGroup.getStartHour();
        int endHour = mGroup.getEndHour();
        int startMinute = mGroup.getStartMinute();
        int endMinute = mGroup.getEndMinute();

        // Boolean for AM vs PM
        boolean startIsPostMeridian = (startHour >= 12);
        boolean endIsPostMeridian = (endHour >= 12);

        int start12Hour = ((startIsPostMeridian) ? ((startHour == 12) ? 12 : (startHour - 12)) :
                ((startHour == 0) ? 12 : startHour));
        int end12Hour = ((endIsPostMeridian) ? ((endHour == 12) ? 12 : (endHour - 12)) :
                ((endHour == 0) ? 12 : endHour));

        // Time string
        return Integer.toString(start12Hour) + ":" +
                (startMinute == 0 ? "00" : Integer.toString(startMinute)) +
                (startIsPostMeridian ? " PM" : " AM") + " - " +
                Integer.toString(end12Hour) + ":" +
                (endMinute == 0 ? "00" : Integer.toString(endMinute)) +
                (endIsPostMeridian ? " PM" : " AM");
    }

    @Override
    public void updateEditFields() {
        if (mGroup != null) {
            mGroupInteractionView.setMemberCountEdit(Integer.toString(mGroup.getMaxMembers()));
            mGroupInteractionView.setDescriptionEdit((mGroup.getDescription() != null) ?
                    mGroup.getDescription() : "");
        }
    }
}