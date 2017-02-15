package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements DatabaseCallback.UserCoursesCallback, JoinGroupContract.Presenter {

    private final JoinGroupContract.View mJoinGroupView;
    private ArrayAdapter<String> mCourseList;
    private ArrayList<String> mUserCourseList;
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
    public void notifyOnUserCoursesRetrieved(ArrayList<String> userCoursesList) {
        mUserCourseList = userCoursesList;
    }

    @Override
    public ArrayList<String> getUserCoursesList() {
        return mUserCourseList;
    }

    public Activity getActivity() {
        return mJoinGroupView.getActivity();
    }
}