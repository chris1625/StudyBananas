package com.bananabanditcrew.studybananas.data;

/**
 * Created by bubblesli96 on 2/9/17.
 */

import java.util.ArrayList;

public class Group {

    public Course course;
    public User groupLeader;
    public ArrayList<User> groupMembers;
    public String location;
    public int startTimeHour;
    public int startTimeMinute;
    public int endTimeHour;
    public int endTimeMinute;


    public Group(Course course, User groupLeader, String location, int startTimeHour,
                 int startTimeMinute, int endTimeHour, int endTimeMinute) {
        this.course = course;
        this.groupLeader = groupLeader;
        this.location = location;

        //make a new calendar
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;

        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;

    }

    public void addGroupMember(User groupMember) {
        this.groupMembers.add(groupMember);
    }


}
