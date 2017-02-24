package com.bananabanditcrew.studybananas.ui.creategroup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment implements CreateGroupContract.View{

    private CreateGroupContract.Presenter mPresenter;
    private AutoCompleteTextView mCoursesSelect;
    private ArrayList<Course> mCourseArrayList;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance() { return new CreateGroupFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_group, container, false);

        mCoursesSelect = (AutoCompleteTextView) root.findViewById(R.id.pick_class);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(CreateGroupContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
