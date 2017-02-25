package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;

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

    }
}
