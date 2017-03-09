package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.app.Activity;

import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;

import java.util.ArrayList;

/**
 * Created by chris on 2/25/17.
 */

public interface GroupInteractionContract {

    interface View extends BaseView<Presenter> {

        Activity getActivity();

        void setActionBarTitle(String courseName);

        void showHomeView(HomeContract.HomeActivityCallback callback);

        void setLocation(String locationName);

        void setMemberCount(String memberCount);

        void setMemberCountEdit(String memberCount);

        void setStartTime(String startTime);

        void setEndTime(String endTime);

        void setEndTimeButtonText(String endTimeButtonText);

        void setDescription(String description);

        void setDescriptionEdit(String description);

        void setMemberListViewVisibility(boolean isVisible);

        void setLeaveButtonText(String text);

        void setFieldsEditable(boolean editable);

        String getStringByID(int string);

        String getMemberCountEdited();

        String getEndTimeEdited();

        String getDescriptionEdited();

        void createAdapter(ArrayList<String> members);

        void notifyAdapter();

        void showKickedMessage();

        void showGroupDisbandedMessage();

        void showNewLeaderDialog();

    }

    interface Presenter extends BasePresenter {

        void setActionBarTitle();

        void getGroupFromDatabase();

        void leaveGroup();

        void addGroupListener();

        void removeGroupListener();

        void updateGroupInfo();

        void updateEditFields();

        void updateEndTime(int endHour, int endMinute);

        void updateGroupInDataBase();

        int getEditedEndHour();

        int getEditedEndMinute();

        String getCourseName();

        boolean isCurrentUser(String user);

        String getGroupLeader();

        void kickUser(String user);

        void transferLeadership(String user);

        void onResume();

        String getGroupID();

        User getUser();

        String getAddress();

        String getLocationName();
    }
}
