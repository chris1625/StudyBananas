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

        void showNoCoursePickedError();

        void showNoStartTimePickedError();

        void showNoEndTimePickedError();

        void showNoLocationPickedError();

        void showNoNaxPeoplePickedError();

        void showTimePicker();

        void showNumberPicker();

        void showSuccessIndicator();

        String getDescription();

        int getStartHour();

        int getStartMinute();

        int getEndHour();

        int getEndMinute();

        int getMaxNum();

        String  getCourseName();

        String getLocation();

    }

    interface Presenter extends BasePresenter{
        ArrayAdapter<String> getCoursesAdapter();

        void attemptCreateGroup();

    }
}
