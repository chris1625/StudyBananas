package com.bananabanditcrew.studybananas.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public class HomePresenter implements HomeContract.Presenter, DatabaseCallback.CoursesCallback,
                                                              DatabaseCallback.ClassUpdateCallback {

    // Reference to view
    private HomeContract.View mHomeView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;
    private Activity mActivity;

    HomePresenter(@NonNull HomeContract.View homeView, Activity activity) {
        mHomeView = homeView;
        mHomeView.setPresenter(this);
        mDatabase = new DatabaseHandler();
        mActivity = activity;
    }

    @Override
    public void setView(HomeContract.View view) {
        mHomeView = view;
    }
    @Override
    public void start() {
        // TODO fill in homepresenter's start method
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        showSignInView();
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
            mDatabase.getClassesArray(this);
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

    @Override
    public void updateClasses() {
        mDatabase.updateClasses(this);
    }

    public void showSignInView() {
        Intent myIntent = new Intent(mActivity, SignInActivity.class);
        mActivity.startActivity(myIntent);
        mActivity.finish();
    }

    @Override
    public HomeContract.HomeActivityCallback getActivityCallback() {
        return (HomeContract.HomeActivityCallback)mActivity;
    }
}