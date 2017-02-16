package com.bananabanditcrew.studybananas.data;

import java.util.ArrayList;

/**
 * Created by bubblesli96 on 2/9/17.
 */

public class Course {
    private String mCourseName;
    private ArrayList<Group> mStudyGroups;

    public Course() {
        mStudyGroups = new ArrayList<>();
    }

    public Course(String courseName){
        mCourseName = courseName;
        mStudyGroups = new ArrayList<>();
    }

    public void addStudyGroup(Group group) {
        mStudyGroups.add(group);
    }

    public void removeStudyGroup(Group group) {
        mStudyGroups.remove(group);
    }

    public void setStudyGroups(ArrayList<Group> studyGroups) {
        mStudyGroups = studyGroups;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public Group getGroupByIndex(int index) {
        return mStudyGroups.get(index);
    }

    public ArrayList<Group> getStudyGroups() {
        return mStudyGroups;
    }

    public void setcourseName(String courseName) {
        mCourseName = courseName;
    }

    @Override
    public boolean equals(Object course) {
        return mCourseName.equals(((Course) course).getCourseName());
    }
}
