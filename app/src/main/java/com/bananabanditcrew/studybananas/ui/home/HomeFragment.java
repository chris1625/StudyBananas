package com.bananabanditcrew.studybananas.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupFragment;
import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.creategroup.CreateGroupFragment;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupPresenter;
import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter mPresenter;
    private JoinGroupPresenter mJoinGroupPresenter;
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
        Button createGroupButton = (Button) view.findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupView();
            }
        });

        // Create listener for joinGroup button
        Button joinGroupButton = (Button) view.findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinGroupView();
            }
        });

        return view;
    }

    public void showCreateGroupView() {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, createGroupFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void showJoinGroupView() {
        JoinGroupFragment joinGroupFragment = new JoinGroupFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, joinGroupFragment);
        transaction.addToBackStack(null);

        // Create presenter and link it to new fragment
        mJoinGroupPresenter = new JoinGroupPresenter(joinGroupFragment, mPresenter.getCoursesAdapter());

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void showSignInView() {
        Intent myIntent = new Intent(getActivity(), SignInActivity.class);
        startActivity(myIntent);
        getActivity().finish();
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