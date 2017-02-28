package com.bananabanditcrew.studybananas.data;

import android.location.Address;

import java.util.ArrayList;

/**
 * Created by bubblesli96 on 2/9/17.
 */

public class Group {

    private String mGroupLeader;
    private ArrayList<String> mGroupMembers;
    private int mMaxMembers;
    private String mAddressLine;
    private String mLocationName;
    private int mStartHour;
    private int mStartMinute;
    private int mEndHour;
    private int mEndMinute;
    private long mGroupID;

    public Group() {
        mGroupMembers = new ArrayList<>();
    }

    public Group(String groupLeader, String addressLine, String locationName, int maxMembers, int startTimeHour,
                 int startTimeMinute, int endTimeHour, int endTimeMinute, long groupID) {
        mGroupLeader = groupLeader;
        mMaxMembers = maxMembers;
        mAddressLine = addressLine;
        mLocationName = locationName;

        //make a new calendar
        mStartHour = startTimeHour;
        mStartMinute = startTimeMinute;

        mEndHour = endTimeHour;
        mEndMinute = endTimeMinute;

        mGroupID = groupID;

        mGroupMembers = new ArrayList<>();
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

    public void setGroupMembers(ArrayList<String> members) {
        mGroupMembers = members;
    }

    public String getAddressLine() {
        return mAddressLine;
    }

    public void setAddressLine(String addressLine) {
        mAddressLine = addressLine;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public int getMaxMembers() {
        return mMaxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        mMaxMembers = maxMembers;
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

    public long getGroupID() {
        return mGroupID;
    }

    public void setGroupID(long groupID) {
        mGroupID = groupID;
    }
}