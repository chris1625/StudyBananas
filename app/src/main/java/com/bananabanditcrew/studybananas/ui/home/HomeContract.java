package com.bananabanditcrew.studybananas.ui.home;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupContract;

import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        Activity getActivity();

        void showProgressView(String title, String body);

        void hideProgressView();

    }

    interface Presenter extends BasePresenter {

        void setView(HomeContract.View view);

        void signOut();

        ArrayAdapter<String> getCoursesAdapter();

        void addCoursesToAutoComplete();

        void updateClasses();

        void showSignInView();

        HomeContract.HomeActivityCallback getActivityCallback();
    }

    interface HomeActivityCallback {

        HomeFragment createHomeFragment();

        void showEditActionButton();

        void showSaveActionButton();

        void hideActionButtons();

    }
}