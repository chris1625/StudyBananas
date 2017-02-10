package com.bananabanditcrew.studybananas.data;

/**
 * Created by bubblesli96 on 2/9/17.
 */

import android.location.Location;

import java.util.ArrayList;

public class Group {

    private Course mCourse;
    private User mGroupLeader;
    private ArrayList<User> mGroupMembers;
    private Location mLocation;
    private int mStartHour;
    private int mStartMinute;
    private int mEndHour;
    private int mEndMinute;


    public Group(Course course, User groupLeader, Location location, int startTimeHour,
                 int startTimeMinute, int endTimeHour, int endTimeMinute) {
        mCourse = course;
        mGroupLeader = groupLeader;
        mLocation = location;

        //make a new calendar
        mStartHour = startTimeHour;
        mStartMinute = startTimeMinute;

        mEndHour = endTimeHour;
        mEndMinute = endTimeMinute;
    }

    public void addGroupMember(User groupMember) {
        if (mGroupMembers == null) {
            mGroupMembers = new ArrayList<>();
        }

        mGroupMembers.add(groupMember);
    }

    public void removeGroupMember(User groupMember) {
        if (mGroupMembers == null) {
            return;
        }

        mGroupMembers.remove(groupMember);
    }

    public Course getCourse() {
        return mCourse;
    }

    public void setCourse(Course course) {
        mCourse = course;
    }

    public User getLeader() {
        return mGroupLeader;
    }

    public void setLeader(User user) {
        mGroupLeader = user;
    }

    public ArrayList<User> getGroupMembers() {
        return mGroupMembers;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public int getStartHour() {
        return mStartHour;
    }

    public void setStartHour(int startHour) {
        mStartHour = startHour;
    }

    public int getStartMinute() {
        return mStartMinute;
    }

    public void setStartMinute(int startMinute) {
        mStartMinute = startMinute;
    }

    public int getEndHour() {
        return mEndHour;
    }

    public void setEndHour(int endHour) {
        mEndHour = endHour;
    }

    public int getEndMinute() {
        return mEndMinute;
    }

    public void setEndMinute(int endMinute) {
        mEndMinute = endMinute;
    }
}
