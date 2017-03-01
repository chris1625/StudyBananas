package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ryan on 2/24/2017.
 */

public class CreateGroupPresenter implements DatabaseCallback, CreateGroupContract.Presenter {
    private final CreateGroupContract.View mCreateGroupView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;

    public CreateGroupPresenter(@NonNull CreateGroupContract.View mCreateGroupView,
                                ArrayAdapter<String> courseList) {
        this.mCreateGroupView = mCreateGroupView;
        this.mCourseList = courseList;
        this.mDatabase = new DatabaseHandler();
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
        return null;
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
