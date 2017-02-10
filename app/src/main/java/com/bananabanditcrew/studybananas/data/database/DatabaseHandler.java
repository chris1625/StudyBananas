package com.bananabanditcrew.studybananas.data.database;

import android.util.Log;

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
import java.util.ArrayList;

/**
 * Created by chris on 2/9/17.
 */

public class DatabaseHandler {

    private DatabaseReference mDatabase;
    private DatabaseCallback mCallback;
    private ArrayList<String> mCourses;

    public DatabaseHandler(DatabaseCallback callback) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCallback = callback;
    }

    public void writeNewUser(String first, String last, String email) {
        User user = new User(first, last, email);
        mDatabase.child("users").child(email.substring(0,email.indexOf('@'))).setValue(user);
    }

    public void updateClasses() {
        String json = null;
        try {
            InputStream is = mCallback.getActivity().getAssets().open("classes.json");
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
            for (int i = 0; i < m_jArry.length(); i++) {
                String courseString = m_jArry.getString(i);
                int shortNameIndex = ordinalIndexOf(courseString, " ", 2);
                String shortName = courseString;
                if (shortNameIndex != -1) {
                    shortName = courseString.substring(0, shortNameIndex).replace(".","");
                }
                Course course = new Course(shortName);
                mDatabase.child("courses").push().setValue(course);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public void getClassesArray() {
        mDatabase.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCourses = new ArrayList<>();
                Log.d("Database", "Count = " + dataSnapshot.getChildrenCount());
                for (DataSnapshot coursesSnapshot: dataSnapshot.getChildren()) {
                    String course = coursesSnapshot.getValue(Course.class).getCourseName();
                    mCourses.add(course);
                }
                Log.d("Database", "Got " + mCourses.size() + " items");
                mCallback.notifyOnCoursesRetrieved();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<String> getCourseArrayList() {
        return mCourses;
    }
}
