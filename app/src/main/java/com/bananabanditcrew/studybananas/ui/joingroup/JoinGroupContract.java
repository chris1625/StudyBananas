package com.bananabanditcrew.studybananas.ui.joingroup;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionPresenter;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;

/**
 * Created by chris on 2/10/17.
 */

public interface JoinGroupContract {

    interface View extends BaseView<Presenter> {

        Activity getActivity();

        void attachAdapter(JoinGroupFragment.CoursesAdapter adapter);

        void showGroupInteractionView(String course, String groupID);

    }

    interface Presenter extends BasePresenter {

        ArrayAdapter<String> getCoursesAdapter();

        void getUserSavedCourses();

        void addUserCourse(String course);

        void removeUserCourse(String course);

        void addGroupToCourse(String course, Group group);

        void removeGroupFromCourse(String course, Group group);

        HomeFragment getHomeFragment();

        void addUserToGroup(String course, String groupID);

        HomeContract.HomeActivityCallback getActivityCallback();

        void removeCourseListeners();

        void addCourseListeners();
    }
}