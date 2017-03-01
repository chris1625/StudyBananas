package com.bananabanditcrew.studybananas.ui.groupinteraction;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;

/**
 * Created by chris on 2/25/17.
 */

public interface GroupInteractionContract {

    interface View extends BaseView<Presenter> {

        void updateUI();

        void showHomeView(HomeContract.HomeActivityCallback callback);

    }

    interface Presenter extends BasePresenter {

        void getGroupFromDatabase();

        void leaveGroup();

    }
}
