package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.support.annotation.NonNull;

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
        mDatabase = new DatabaseHandler();

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

        mGroupInteractionView.updateUI();
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

        mGroupInteractionView.showHomeView(mActivityCallback);
    }

    @Override
    public void onUserRetrieved(User user) {
        mUser = user;
    }
}