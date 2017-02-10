package com.bananabanditcrew.studybananas.data.database;

import android.util.Log;
import android.widget.ArrayAdapter;

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

/**
 * Created by chris on 2/9/17.
 */

public class DatabaseHandler {

    private DatabaseReference mDatabase;
    private DatabaseCallback mCallback;

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
                Course course = new Course(courseString);
                mDatabase.child("courses").push().setValue(course);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void autocompleteClasses(final ArrayAdapter<String> autocomplete) {
        mDatabase.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                    String suggestion = suggestionSnapshot.child("courseName").getValue(String.class);
                    autocomplete.add(suggestion);
                    Log.d("Autocomplete", suggestion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
