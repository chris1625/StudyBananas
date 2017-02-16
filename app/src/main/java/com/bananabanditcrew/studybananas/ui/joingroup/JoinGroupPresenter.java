package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements DatabaseCallback.UserCoursesCallback, JoinGroupContract.Presenter {

    private final JoinGroupContract.View mJoinGroupView;
    private ArrayAdapter<String> mCourseList;
    private ArrayList<Course> mUserCoursesList;
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
        mUserCoursesList = userCoursesList;
        mUserCoursesAdapter = new JoinGroupFragment.CoursesAdapter(mJoinGroupView.getActivity(),
                                                                   mUserCoursesList, this);
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
        mDatabase.addUserClass(getUserEmail(), course, this);
    }

    @Override
    public void removeUserCourse(String course) {
        mDatabase.removeUserClass(getUserEmail(), course, this);
    }

    @Override
    public void notifyOnUserCourseRetrievedToRemove(Course course) {
        mUserCoursesList.remove(course);
        mUserCoursesAdapter.notifyDataSetChanged();
        Log.d("Database", Integer.toString(mUserCoursesList.size()));
    }

    @Override
    public void notifyOnUserCourseRetrievedToAdd(Course course) {
        mUserCoursesList.add(course);
        mUserCoursesAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyOnCourseUpdated(Course course) {
        Log.d("Database", "Course updated: " + course.getCourseName());
        if (mUserCoursesAdapter != null) {
            mUserCoursesList.set(mUserCoursesList.indexOf(course), course);
            mUserCoursesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addGroupToCourse(String course, Group group) {
        mDatabase.addGroupToCourse(course, group);
    }

    @Override
    public void removeGroupFromCourse(String course, Group group) {
        mDatabase.removeGroupFromCourse(course, group);
    }

    public Activity getActivity() {
        return mJoinGroupView.getActivity();
    }
}