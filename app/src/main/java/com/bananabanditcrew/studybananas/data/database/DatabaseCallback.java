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

        void notifyOnUserCoursesRetrieved(ArrayList<String> userCoursesList);

    }

    interface ClassUpdateCallback {

        Activity getActivity();

    }

}
