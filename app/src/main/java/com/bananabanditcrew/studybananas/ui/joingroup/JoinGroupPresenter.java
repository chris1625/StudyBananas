package com.bananabanditcrew.studybananas.ui.joingroup;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements JoinGroupContract.Presenter {

    private final JoinGroupContract.View mJoinGroupView;
    private ArrayAdapter<String> mCourseList;

    public JoinGroupPresenter(@NonNull JoinGroupContract.View joinGroupView,
                              ArrayAdapter<String> courseList) {
        mJoinGroupView = joinGroupView;
        mJoinGroupView.setPresenter(this);
        mCourseList = courseList;
    }

    @Override
    public void start() {
        // TODO fill in JoinGroup presenter's start method
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }
}