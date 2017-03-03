package com.bananabanditcrew.studybananas.ui.groupinteraction;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;

/**
 * Created by chris on 2/25/17.
 */

public interface GroupInteractionContract {

    interface View extends BaseView<Presenter> {

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

    }

    interface Presenter extends BasePresenter {

        void getGroupFromDatabase();

        void leaveGroup();

        void addGroupListener();

        void removeGroupListener();

        void openMapView();

        void updateGroupInfo();

        void updateEditFields();

        void updateEndTime(int endHour, int endMinute);

        void updateGroupInDataBase();

        int getEditedEndHour();

        int getEditedEndMinute();

    }
}
