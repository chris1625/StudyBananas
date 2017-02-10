package com.bananabanditcrew.studybananas.data;

import java.util.ArrayList;

/**
 * Created by bubblesli96 on 2/9/17.
 */

public class User {
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private ArrayList<Course> mCourses;
    private Group mGroup;

    public User (String first, String last, String email) {
        mFirstName = first;
        mLastName = last;
        mEmail = email;
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

    public void setGroup(Group group) {
        mGroup = group;
    }

    public void addCourse(Course course) {
        mCourses.add(course);
    }

    public void removeCourse(Course course) {
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
    public Group getGroup() {
        return mGroup;
    }

    @Override
    public String toString() {
        return (mFirstName + " " + mLastName);
    }
}
