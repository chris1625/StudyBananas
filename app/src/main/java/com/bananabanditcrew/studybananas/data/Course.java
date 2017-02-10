package com.bananabanditcrew.studybananas.data;

/**
 * Created by bubblesli96 on 2/9/17.
 */
import java.util.ArrayList;

public class Course {
    private String mCourseName;
    private ArrayList<Group> mStudyGroups;

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

    public String getCourseName() {
        return mCourseName;
    }

}
