package com.bananabanditcrew.studybananas.ui.joingroup;


import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.creategroup.CreateGroupFragment;

import java.util.ArrayList;

public class JoinGroupFragment extends Fragment implements JoinGroupContract.View {

    private JoinGroupContract.Presenter mPresenter;
    private LinearLayout mDummyLayout;
    private AutoCompleteTextView mCoursesSelect;
    private ArrayAdapter<String> mCourseList;
    private ProgressDialog mProgressView;

    public JoinGroupFragment() {
        // Required empty public constructor
    }

    public static JoinGroupFragment newInstance() {
        return new JoinGroupFragment();
    }

    @Override
    public void setPresenter(@NonNull JoinGroupContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_join_group, container, false);

        mCoursesSelect = (AutoCompleteTextView) root.findViewById(R.id.course_select);
        mCoursesSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoursesSelect.showDropDown();
            }
        });

        mDummyLayout = (LinearLayout) root.findViewById(R.id.dummy_layout);

        mCoursesSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDummyLayout.requestFocus();
                closeKeyboard(getActivity(), mDummyLayout.getWindowToken());
            }
        });

        mPresenter.addCoursesToAutoComplete();

        return root;
    }

    @Override
    public void showProgressView(String title, String body) {
        mProgressView = ProgressDialog.show(getContext(), title, body);
    }

    @Override
    public void hideProgressView() {
        mProgressView.dismiss();
    }

    @Override
    public void setupAutoComplete(ArrayList<String> courses) {
        mCourseList = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_dropdown_item_1line,
                courses);
        mCoursesSelect.setAdapter(mCourseList);
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}