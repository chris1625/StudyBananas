package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements DatabaseCallback.UserCoursesCallback, JoinGroupContract.Presenter {

    private final JoinGroupContract.View mJoinGroupView;
    private ArrayAdapter<String> mCourseList;
    private JoinGroupFragment.CoursesAdapter mUserCoursesAdapter;
    private DatabaseHandler mDatabase;

    public JoinGroupPresenter(@NonNull JoinGroupContract.View joinGroupView,
                              ArrayAdapter<String> courseList) {
        mJoinGroupView = joinGroupView;
        mJoinGroupView.setPresenter(this);
        mCourseList = courseList;
        mDatabase = new DatabaseHandler();
    }

    @Override
    public void start() {
        // TODO fill in JoinGroup presenter's start method
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }

    @Override
    public void notifyOnUserCoursesRetrieved(ArrayList<Course> userCoursesList) {

        mUserCoursesAdapter = new JoinGroupFragment.CoursesAdapter(mJoinGroupView.getActivity(),
                                                                   userCoursesList, this);
        mJoinGroupView.attachAdapter(mUserCoursesAdapter);
    }

    @Override
    public void getUserSavedCourses() {
        mDatabase.getUserClassesArray(getUserEmail(), this);
    }

    private String getUserEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public void addUserCourse(String course) {
        mDatabase.addUserClass(getUserEmail(), course);
        getUserSavedCourses();
    }

    @Override
    public void removeUserCourse(String course) {
        mDatabase.removeUserClass(getUserEmail(), course);
        getUserSavedCourses();
    }

    @Override
    public JoinGroupFragment.CoursesAdapter getUserCoursesAdapter() {
        return mUserCoursesAdapter;
    }

    public Activity getActivity() {
        return mJoinGroupView.getActivity();
    }
}