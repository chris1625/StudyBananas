package com.bananabanditcrew.studybananas.data;

import java.util.ArrayList;

/**
 * Created by bubblesli96 on 2/9/17.
 */

public class Group {

    private String mGroupLeader;
    private ArrayList<String> mGroupMembers;
    private GroupLocation mLocation;
    private int mStartHour;
    private int mStartMinute;
    private int mEndHour;
    private int mEndMinute;

    public Group() {}

    public Group(String groupLeader, GroupLocation location, int startTimeHour,
                 int startTimeMinute, int endTimeHour, int endTimeMinute) {
        mGroupLeader = groupLeader;
        mLocation = location;

        //make a new calendar
        mStartHour = startTimeHour;
        mStartMinute = startTimeMinute;

        mEndHour = endTimeHour;
        mEndMinute = endTimeMinute;
    }

    public void addGroupMember(String groupMember) {
        if (mGroupMembers == null) {
            mGroupMembers = new ArrayList<>();
        }

        mGroupMembers.add(groupMember);
    }

    public void removeGroupMember(String groupMember) {
        if (mGroupMembers == null) {
            return;
        }

        mGroupMembers.remove(groupMember);
    }

    public String getLeader() {
        return mGroupLeader;
    }

    public void setLeader (String user) {
        mGroupLeader = user;
    }

    public ArrayList<String> getGroupMembers() {
        return mGroupMembers;
    }

    public GroupLocation getLocation() {
        return mLocation;
    }

    public void setLocation(GroupLocation location) {
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
