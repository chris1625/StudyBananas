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

        void setTimeRange(String timeRange);

        void setDescription(String description);

    }

    interface Presenter extends BasePresenter {

        void getGroupFromDatabase();

        void leaveGroup();

        void addGroupListener();

        void removeGroupListener();

        void openMapView();

        void updateGroupInfo();

    }
}
