package com.bananabanditcrew.studybananas.ui.home;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void showSignInView();

        Activity getActivity();

        void showProgressView(String title, String body);

        void hideProgressView();

    }

    interface Presenter extends BasePresenter {

        void signOut();

        ArrayAdapter<String> getCoursesAdapter();

        void addCoursesToAutoComplete();

    }
}