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

        void showNoMaxPeoplePickedError();

        void showEndTimePicker();

        void showStartTimePicker();

        void showNumberPicker();

        void resetErrors();

        void showGroupInteractionView();

        void showSuccessIndicator();

        String getDescription();

        int getStartHour();

        int getStartMinute();

        int getEndHour();

        int getEndMinute();

        int getMaxNum();

        String  getCourseName();

        void showLocationPicker();

        String getLocation();

        String getAddress();

        boolean doValidations();
    }

    interface Presenter extends BasePresenter{
        ArrayAdapter<String> getCoursesAdapter();

        void attemptCreateGroup();

    }
}
