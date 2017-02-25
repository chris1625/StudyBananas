package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

/**
 * Created by Ryan on 2/24/2017.
 */

public interface CreateGroupContract {
    interface View extends BaseView<Presenter> {
        Activity getActivity();

        void showIncorrectTimeError();

        void showTImePicker();

        void showNumberPicker();

        void showSuccessIndicator();

        String getDescription();

        int getStartHour();

        int getStartMinute();

        int getEndHour();

        int getMinute();

        int getMaxNum();

        String  getCourseName();

    }

    interface Presenter extends BasePresenter{
        ArrayAdapter<String> getCoursesAdapter();

        void attemptCreateGroup();

    }
}
