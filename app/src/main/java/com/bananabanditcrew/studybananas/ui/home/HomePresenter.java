package com.bananabanditcrew.studybananas.ui.home;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public class HomePresenter implements HomeContract.Presenter, DatabaseCallback {

    // Reference to view
    private final HomeContract.View mHomeView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;

    HomePresenter(@NonNull HomeContract.View homeView) {
        mHomeView = homeView;
        mHomeView.setPresenter(this);
        mDatabase = new DatabaseHandler(this);
    }

    @Override
    public void start() {
        // TODO fill in homepresenter's start method
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        mHomeView.showSignInView();
    }

    @Override
    public void notifyOnCoursesRetrieved() {
        mHomeView.hideProgressView();
        ArrayList<String> courses = mDatabase.getCourseArrayList();

        // Set up array adapter
        mCourseList = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_dropdown_item_1line,
                courses);
    }

    @Override
    public void addCoursesToAutoComplete() {
        if (mCourseList == null) {
            mHomeView.showProgressView("Courses", "Fetching list of courses...");
            mDatabase.getClassesArray();
        }
    }

    @Override
    public Activity getActivity() {
        return mHomeView.getActivity();
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }
}