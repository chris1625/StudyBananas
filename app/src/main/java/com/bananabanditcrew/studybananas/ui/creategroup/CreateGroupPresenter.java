package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupContract;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Ryan on 2/24/2017.
 */

public class CreateGroupPresenter implements DatabaseCallback.GetUserCallback,
                                             DatabaseCallback.GetCourseCallback,
                                             CreateGroupContract.Presenter {

    private final CreateGroupContract.View mCreateGroupView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;
    private HomeFragment mHomeFragment;
    private HomeContract.HomeActivityCallback mHomeActivityCallback;
    private String mCourseName;
    private Course mCourse;
    private Group mGroup;
    private String mGroupID;

    public CreateGroupPresenter(@NonNull CreateGroupContract.View createGroupView,
                                ArrayAdapter<String> courseList, HomeFragment homeFragment,
                                HomeContract.HomeActivityCallback homeActivityCallback) {
        mCreateGroupView = createGroupView;
        mDatabase = DatabaseHandler.getInstance();
        mCourseList = courseList;
        mHomeFragment = homeFragment;
        mHomeActivityCallback = homeActivityCallback;
    }

    @Override
    public void start() {
        //// TODO: 2/24/2017
    }

    public Activity getActivity() {
        return mCreateGroupView.getActivity();
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }

    @Override
    public void attemptCreateGroup() {
        boolean cancel;
        int mStartHour = mCreateGroupView.getStartHour();
        int mStartMinute = mCreateGroupView.getStartMinute();
        int mEndHour = mCreateGroupView.getEndHour();
        int mEndMinute = mCreateGroupView.getEndMinute();
        int mMembers = mCreateGroupView.getMaxNum();
        mCourseName = mCreateGroupView.getCourseName();
        String mLocationName = mCreateGroupView.getLocation();
        String mAddress = mCreateGroupView.getAddress();
        String mDescription = mCreateGroupView.getDescription();
        mGroupID = Long.toString(System.currentTimeMillis());
        mGroup = new Group(FirebaseAuth.getInstance().getCurrentUser().getEmail(), mAddress,
                mLocationName, mMembers, mStartHour, mStartMinute, mEndHour, mEndMinute, mDescription,
                mGroupID);
        mDatabase.getCourse(mCourseName, this);
    }

    @Override
    public void onCourseRetrieved(Course course, boolean isUIActive) {
        mCourse = course;
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);
    }

    @Override
    public void onUserRetrieved(User user) {
        // Update user fields
        user.setGroupCourse(mCourseName);
        user.setGroupID(mGroupID);

        mGroup.addGroupMember(user.getEmail());
        mGroup.setLeader(user.getEmail());
        mCourse.addStudyGroup(mGroup);

        // Update the user and course through the database
        mDatabase.updateUser(user);
        mDatabase.updateCourse(mCourse);

        mCreateGroupView.showGroupInteractionView(mCourseName, mGroupID);
    }

    @Override
    public HomeFragment getHomeFragment() {
        return mHomeFragment;
    }

    @Override
    public HomeContract.HomeActivityCallback getHomeActivityCallback() {
        return mHomeActivityCallback;
    }
}
