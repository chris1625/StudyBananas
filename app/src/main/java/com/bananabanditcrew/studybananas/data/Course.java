package com.bananabanditcrew.studybananas.data;

/**
 * Created by bubblesli96 on 2/9/17.
 */
import java.util.ArrayList;

public class Course {
    public String courseName;
    public ArrayList<Group> studyGroups;

    public void addStudyGroup(Group group) {
        studyGroups.add(group);
    }

}
