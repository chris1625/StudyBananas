package com.bananabanditcrew.studybananas.data.database;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;

import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public interface DatabaseCallback {

    interface CoursesCallback {

        void notifyOnCoursesRetrieved();

    }

    interface UserCoursesCallback {

        void notifyOnUserCoursesRetrieved(ArrayList<Course> userCoursesList);

        void notifyOnUserCourseRetrievedToRemove(Course course);

        void notifyOnUserCourseRetrievedToAdd(Course course);

        void notifyOnCourseUpdated(Course course);

    }

    interface ClassUpdateCallback {

        Activity getActivity();

    }

    interface UserCreationCallback {

        void finishAccountCreation();

    }

}
