package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.graphics.Paint;
import android.widget.ArrayAdapter;

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

        void attachAdapter(JoinGroupFragment.CoursesAdapter adapter);

    }

    interface Presenter extends BasePresenter {

        ArrayAdapter<String> getCoursesAdapter();

        JoinGroupFragment.CoursesAdapter getUserCoursesAdapter();

        void getUserSavedCourses();

        void addUserCourse(String course);

        void removeUserCourse(String course);

    }
}