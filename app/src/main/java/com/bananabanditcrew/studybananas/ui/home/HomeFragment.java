package com.bananabanditcrew.studybananas.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.creategroup.CreateGroupFragment;
import com.bananabanditcrew.studybananas.ui.creategroup.CreateGroupPresenter;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupFragment;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupPresenter;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter mPresenter;
    private JoinGroupPresenter mJoinGroupPresenter;
    private CreateGroupPresenter mCreateGroupPresenter;
    private ProgressDialog mProgressView;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Get courses and put them in an arrayadapter
        mPresenter.addCoursesToAutoComplete();

        // Create listener for createGroup button
        ImageButton createGroupButton = (ImageButton) view.findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupView();
            }
        });

        // Create listener for joinGroup button
        ImageButton joinGroupButton = (ImageButton) view.findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinGroupView();
            }
        });

        // Set the action bar title for the home fragment
        Log.d("Title", "Setting title for home fragment");
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.main_activity_title));

        return view;
    }

    public void showCreateGroupView() {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, createGroupFragment);
        transaction.addToBackStack(null);
        // Create presenter and link it to new fragment
        mCreateGroupPresenter = new CreateGroupPresenter(createGroupFragment,
                mPresenter.getCoursesAdapter(),
                this, mPresenter.getActivityCallback());
        createGroupFragment.setPresenter(mCreateGroupPresenter);
        // Commit the transaction
        transaction.commit();
    }

    public void showJoinGroupView() {
        JoinGroupFragment joinGroupFragment = new JoinGroupFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, joinGroupFragment,
                getResources().getString(R.string.join_group_fragment_tag));
        transaction.addToBackStack(null);

        // Create presenter and link it to new fragment
        mJoinGroupPresenter = new JoinGroupPresenter(joinGroupFragment,
                mPresenter.getCoursesAdapter(), this,
                mPresenter.getActivityCallback());

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void showProgressView(String title, String body) {
        mProgressView = ProgressDialog.show(getContext(), title, body);
    }

    @Override
    public void hideProgressView() {
        mProgressView.dismiss();
    }
}