package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupContract;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Ryan on 2/24/2017.
 */

public class CreateGroupPresenter implements DatabaseCallback, CreateGroupContract.Presenter {
    private final CreateGroupContract.View mCreateGroupView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;

    public CreateGroupPresenter(@NonNull CreateGroupContract.View mCreateGroupView, ArrayAdapter<String> courseList) {
        this.mCreateGroupView = mCreateGroupView;
        this.mDatabase = new DatabaseHandler();
        this.mCourseList=courseList;
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
        int sHour = mCreateGroupView.getStartHour();
        int sMin = mCreateGroupView.getStartMinute();
        int eHour = mCreateGroupView.getEndHour();
        int eMin = mCreateGroupView.getEndMinute();
        int numMem = mCreateGroupView.getMaxNum();
        String courseName = mCreateGroupView.getCourseName();
        String loc = mCreateGroupView.getLocation();
        String address = mCreateGroupView.getAddress();
        Group grp = new Group(FirebaseAuth.getInstance().getCurrentUser().getEmail(), address, loc, numMem,
                sHour, sMin, eHour, eMin, Long.toString(System.currentTimeMillis()));
        mDatabase.addGroupToCourse(courseName, grp);
        mCreateGroupView.showGroupInteractionView();
    }
}
