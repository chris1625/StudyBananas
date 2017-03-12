package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public class JoinGroupPresenter implements DatabaseCallback.UserCoursesCallback, JoinGroupContract.Presenter,
        DatabaseCallback.GetUserCallback, DatabaseCallback.GetCourseCallback {

    private final JoinGroupContract.View mJoinGroupView;
    private ArrayAdapter<String> mCourseList;
    private ArrayList<Course> mUserCoursesList;
    private JoinGroupFragment.CoursesAdapter mUserCoursesAdapter;
    private DatabaseHandler mDatabase;
    private HomeFragment mHomeFragment;
    private String mGroupID;
    private Course mCourse;
    private HomeContract.HomeActivityCallback mActivityCallback;

    // Object to synchronize on to ensure we don't have race conditions
    private static final Object lock = new Object();

    public JoinGroupPresenter(@NonNull JoinGroupContract.View joinGroupView,
                              ArrayAdapter<String> courseList, HomeFragment homeFragment,
                              HomeContract.HomeActivityCallback callback) {
        mJoinGroupView = joinGroupView;
        mJoinGroupView.setPresenter(this);
        mCourseList = courseList;
        mDatabase = DatabaseHandler.getInstance();
        mHomeFragment = homeFragment;
        mActivityCallback = callback;
    }

    @Override
    public void start() {
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }

    @Override
    public void notifyOnUserCoursesRetrieved(ArrayList<Course> userCoursesList) {
        synchronized (lock) {
            mUserCoursesList = userCoursesList;
            mUserCoursesAdapter = new JoinGroupFragment.CoursesAdapter(mJoinGroupView.getActivity(),
                    mUserCoursesList, this,
                    mJoinGroupView);
            mJoinGroupView.attachAdapter(mUserCoursesAdapter);
        }
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
        synchronized (lock) {
            mUserCoursesList.remove(course);
            mUserCoursesAdapter.notifyDataSetChanged();
            Log.d("Database", Integer.toString(mUserCoursesList.size()));
        }
    }

    @Override
    public void notifyOnUserCourseRetrievedToAdd(Course course) {
        synchronized (lock) {
            mUserCoursesList.add(course);
            mUserCoursesAdapter.notifyDataSetChanged();
        }
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

    @Override
    public HomeFragment getHomeFragment() {
        return mHomeFragment;
    }

    @Override
    public void addUserToGroup(String course, String groupID) {
        synchronized (lock) {
            // Set the groupID instance variable for retrieval in onUserRetrieved
            mGroupID = groupID;

            // We need to first get the course
            mDatabase.getCourse(course, this);
            // The database will call oncoursesretrieved from here
        }
    }

    @Override
    public void onCourseRetrieved(Course course) {
        synchronized (lock) {
            // We now have the course, and need the user so we can update the user
            mCourse = course;

            // Grab the user
            mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        synchronized (lock) {
            // This method chain was initially started by addUserToGroup, so we finish that here
            user.setGroupCourse(mCourse.getCourseName());
            user.setGroupID(mGroupID);

            ArrayList<Group> groups = mCourse.getStudyGroups();

            int groupIndex = groups.indexOf(new Group(mGroupID));
            Log.d("GroupID", "Group index is " + Integer.toString(groupIndex));
            Group updatedGroup = groups.get(groupIndex);

            Log.d("Users", "Added user " + user.getEmail() + " to group");
            updatedGroup.addGroupMember(user.getEmail());
            groups.set(groupIndex, updatedGroup);
            mCourse.setStudyGroups(groups);

            Log.d("Group", "Number of members in group is " +
                    Integer.toString(mCourse.getStudyGroups().get(groupIndex).getGroupMembers()
                            .size()));
            // Update the user and course through the database
            mDatabase.updateUser(user);
            mDatabase.updateCourse(mCourse);

            mJoinGroupView.showGroupInteractionView(mCourse.getCourseName(), mGroupID);
        }
    }

    public Activity getActivity() {
        return mJoinGroupView.getActivity();
    }

    @Override
    public HomeContract.HomeActivityCallback getActivityCallback() {
        return mActivityCallback;
    }

    @Override
    public void removeCourseListeners() {
        mDatabase.removeCourseListeners();
    }

    @Override
    public void addCourseListeners() {
        mDatabase.addCourseListeners(this);
    }
}