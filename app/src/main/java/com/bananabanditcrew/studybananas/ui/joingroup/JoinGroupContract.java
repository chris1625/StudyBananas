package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by chris on 2/10/17.
 */

public interface JoinGroupContract {

    interface View extends BaseView<Presenter> {

        Activity getActivity();

        void showProgressView(String title, String body);

        void hideProgressView();

        void setupAutoComplete(ArrayList<String> courses);

    }

    interface Presenter extends BasePresenter {

        void writeCoursesToDatabase();

        void addCoursesToAutoComplete();
    }
}
