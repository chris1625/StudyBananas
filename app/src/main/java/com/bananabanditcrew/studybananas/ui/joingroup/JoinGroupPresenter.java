package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements JoinGroupContract.Presenter, DatabaseCallback{

    private final JoinGroupContract.View mJoinGroupView;
    private DatabaseHandler mDatabase;
    private ArrayList<String> mCourseList;

    public JoinGroupPresenter(@NonNull JoinGroupContract.View joinGroupView) {
        mJoinGroupView = joinGroupView;
        mJoinGroupView.setPresenter(this);
        mDatabase = new DatabaseHandler(this);
    }

    @Override
    public void start() {
        // TODO fill in JoinGroup presenter's
    }

    @Override
    public Activity getActivity() {
        return mJoinGroupView.getActivity();
    }

    @Override
    public void writeCoursesToDatabase() {
        mDatabase.updateClasses();
    }

    @Override
    public void notifyOnCoursesRetrieved() {
        mJoinGroupView.hideProgressView();
        mCourseList = mDatabase.getCourseArrayList();

        // Create array adapter in view
        mJoinGroupView.setupAutoComplete(mCourseList);
    }

    @Override
    public void addCoursesToAutoComplete() {
        if (mCourseList == null) {
            mJoinGroupView.showProgressView("Courses", "Fetching list of courses...");
            mDatabase.getClassesArray();
        }
    }
}