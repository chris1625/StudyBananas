package com.bananabanditcrew.studybananas.data.database;

import android.util.Log;
import android.util.SparseArray;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
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
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by chris on 2/9/17.
 */

public class DatabaseHandler {

    private DatabaseReference mDatabase;
    private ArrayList<String> mCourses;
    private ArrayList<Course> mUserCourses;
    private ArrayList<String> mUserCourseStrings;

    // Map of hashcodes to listener objects
    private SparseArray<ValueEventListener> mCourseListeners;

    public DatabaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void createNewUser(String first, String last, final String email,
                              final DatabaseCallback.UserCreationCallback callback) {
        final User user = new User(first, last, email);
        mDatabase.child("users").child(uidFromEmail(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.d("Database", "Updating user " + email);
                    updateUser(user);
                    callback.finishAccountCreation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        mUserCourseStrings = new ArrayList<>();
                        Log.d("Database", "Count = " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot coursesSnapshot: dataSnapshot.getChildren()) {
                            mUserCourseStrings.add((String)coursesSnapshot.getValue());
                        }
                        Log.d("Database", "Got " + mUserCourseStrings.size() + " items");
                        mUserCourses = new ArrayList<>();
                        stringsToCourses(mUserCourseStrings, callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getCourseByString(String course, final boolean isLast,
                                   final DatabaseCallback.UserCoursesCallback callback) {
        DatabaseReference databaseReference = mDatabase.child("courses")
                .child(Integer.toString(course.hashCode()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUserCourses.add(dataSnapshot.getValue(Course.class));
                        if (isLast)
                            callback.notifyOnUserCoursesRetrieved(mUserCourses);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // Add listener to this course
        addValueEventListener(databaseReference, course.hashCode(), callback);
    }

    private void addValueEventListener(DatabaseReference databaseReference, int hashCode,
                                       final DatabaseCallback.UserCoursesCallback callback) {
        // Create a listener for this course
        mCourseListeners.append(hashCode, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.notifyOnCourseUpdated(dataSnapshot.getValue(Course.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add the listener
        databaseReference.addValueEventListener(mCourseListeners.get(hashCode));
        Log.d("EventListeners", "Added event listener for " + Integer.toString(hashCode));
    }

    private void removeValueEventListener(DatabaseReference databaseReference, int hashCode) {
        databaseReference.removeEventListener(mCourseListeners.get(hashCode));
        Log.d("EventListeners", "Removed event listener for " + Integer.toString(hashCode));
    }

    private void stringsToCourses(ArrayList<String> courseStrings,
                                  DatabaseCallback.UserCoursesCallback callback) {

        int counter = 0;

        // Initialize map of listener objects
        mCourseListeners = new SparseArray<>();

        for (String courseName: courseStrings) {
            counter++;
            getCourseByString(courseName, (counter == courseStrings.size()), callback);
        }

        if (counter == 0) {
            callback.notifyOnUserCoursesRetrieved(mUserCourses);
        }

    }

    private void updateUserClasses(String email, ArrayList<String> courses) {
        mDatabase.child("users").child(uidFromEmail(email)).child("courses").setValue(courses);
    }

    public void removeUserClass(String email, String course,
                                final DatabaseCallback.UserCoursesCallback callback) {
        mUserCourseStrings.remove(course);
        updateUserClasses(email, mUserCourseStrings);

        DatabaseReference databaseReference = mDatabase.child("courses")
                .child(Integer.toString(course.hashCode()));

        // Find course and remove it from adapter
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.notifyOnUserCourseRetrievedToRemove(dataSnapshot.getValue(Course.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // Remove listener for course
        removeValueEventListener(databaseReference, course.hashCode());
    }

    public void addUserClass(String email, String course,
                             final DatabaseCallback.UserCoursesCallback callback) {
        if (!mUserCourseStrings.contains(course)) {
            mUserCourseStrings.add(course);

            DatabaseReference databaseReference = mDatabase.child("courses")
                    .child(Integer.toString(course.hashCode()));

            // Find course and add it to adapter
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    callback.notifyOnUserCourseRetrievedToAdd(dataSnapshot.getValue(Course.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Add a data change listener
            addValueEventListener(databaseReference, course.hashCode(), callback);
        }
        updateUserClasses(email, mUserCourseStrings);
    }

    public void addGroupToCourse(String course, final Group group) {

        // Get course first
        final DatabaseReference databaseReference = mDatabase.child("courses")
                .child(Integer.toString(course.hashCode()));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course refCourse = dataSnapshot.getValue(Course.class);
                refCourse.addStudyGroup(group);
                updateCourse(databaseReference, refCourse);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeGroupFromCourse(String course, final Group group) {

        // Get course first
        final DatabaseReference databaseReference = mDatabase.child("courses")
                .child(Integer.toString(course.hashCode()));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course refCourse = dataSnapshot.getValue(Course.class);
                refCourse.removeStudyGroup(group);
                updateCourse(databaseReference, refCourse);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCourse(DatabaseReference databaseReference, Course course) {
        Log.d("Database", "Updating course from handler");
        databaseReference.setValue(course);
    }
}