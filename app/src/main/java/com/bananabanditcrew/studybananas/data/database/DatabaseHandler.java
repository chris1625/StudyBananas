package com.bananabanditcrew.studybananas.data.database;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 2/9/17.
 */

public class DatabaseHandler {

    private DatabaseReference mDatabase;
    private ArrayList<String> mCourses;

    public DatabaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void updateUser(String first, String last, String email) {
        User user = new User(first, last, email);
        updateUser(user);
    }

    private void updateUser(User user) {
        Log.d("Database", user.getEmail());
        String email = user.getEmail();
        mDatabase.child("users").child(uidFromEmail(user.getEmail())).setValue(user);
    }

    private String uidFromEmail(String email) {
        return email.substring(0,email.indexOf('@'));
    }

    public void updateClasses(DatabaseCallback.ClassUpdateCallback callback) {
        String json = null;
        try {
            InputStream is = callback.getActivity().getAssets().open("classes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray m_jArry = obj.getJSONArray("classes");
            HashMap<String, Course> courseMap = new HashMap<>();
            for (int i = 0; i < m_jArry.length(); i++) {
                String courseString = m_jArry.getString(i);
                int shortNameIndex = ordinalIndexOf(courseString, " ", 2);
                String shortName = courseString;
                if (shortNameIndex != -1) {
                    shortName = courseString.substring(0, shortNameIndex).replace(".","");
                }
                Course course = new Course(shortName);
                courseMap.put(Integer.toString(shortName.hashCode()), course);
            }
            mDatabase.child("courses").setValue(courseMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public void getClassesArray(final DatabaseCallback.CoursesCallback callback) {
        mDatabase.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCourses = new ArrayList<>();
                for (DataSnapshot coursesSnapshot: dataSnapshot.getChildren()) {
                    String course = coursesSnapshot.getValue(Course.class).getCourseName();
                    mCourses.add(course);
                }
                Collections.sort(mCourses);
                callback.notifyOnCoursesRetrieved();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<String> getCourseArrayList() {
        return mCourses;
    }

    public void getUserClassesArray(String email, final DatabaseCallback.UserCoursesCallback callback) {
        mDatabase.child("users").child(uidFromEmail(email)).child("courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> courseList = new ArrayList<>();
                        Log.d("Database", "Count = " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot coursesSnapshot: dataSnapshot.getChildren()) {
                            courseList.add((String)coursesSnapshot.getValue());
                        }
                        Log.d("Database", "Got " + courseList.size() + " items");
                        callback.notifyOnUserCoursesRetrieved(courseList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void updateUserClasses(String email, ArrayList<String> courses) {
        mDatabase.child("users").child(uidFromEmail(email)).child("courses").setValue(courses);
    }
}