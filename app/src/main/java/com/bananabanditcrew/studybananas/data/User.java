package com.bananabanditcrew.studybananas.data;

import java.util.ArrayList;

/**
 * Created by bubblesli96 on 2/9/17.
 */

public class User {
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private ArrayList<String> mCourses;
    private String mGroupCourse;
    private long mGroupID;

    public User() {};

    public User (String first, String last, String email) {
        mFirstName = first;
        mLastName = last;
        mEmail = email;
        mCourses = new ArrayList<>();
    }

    public void setFirstName(String first) {
        mFirstName = first;
    }

    public void setLastName(String last) {
        mLastName = last;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setGroupCourse(String groupCourse) {
        mGroupCourse = groupCourse;
    }

    public void setGroupID(long groupID) {
        mGroupID = groupID;
    }

    public void addCourse(String course) {
        mCourses.add(course);
    }

    public void removeCourse(String course) {
        mCourses.remove(course);
    }

    public String getFirstName() {
        return mFirstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public String getEmail() {
        return mEmail;
    }
    public String getGroupCourse() {
        return mGroupCourse;
    }
    public long getGroupID() {
        return mGroupID;
    }
    public ArrayList<String> getCourses() {
        return mCourses;
    }

    @Override
    public String toString() {
        return (mFirstName + " " + mLastName);
    }
}
