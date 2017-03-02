package com.bananabanditcrew.studybananas.ui.groupinteraction;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInteractionFragment extends Fragment implements GroupInteractionContract.View {

    private GroupInteractionContract.Presenter mPresenter;

    private Button mLocationButton;
    private TextView mGroupMemberCount;
    private TextView mTimeView;
    private TextView mDescription;
    private Button mLeaveGroupButton;

    public GroupInteractionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mPresenter.addGroupListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.removeGroupListener();
    }

    @Override
    public void setPresenter(@NonNull GroupInteractionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_interaction, container, false);

        mLocationButton = (Button) view.findViewById(R.id.group_interaction_location);
        mGroupMemberCount = (TextView) view.findViewById(R.id.group_interaction_members);
        mTimeView = (TextView) view.findViewById(R.id.group_interaction_time);
        mDescription = (TextView) view.findViewById(R.id.group_interaction_description);

        mLeaveGroupButton = (Button) view.findViewById(R.id.leave_group_button);
        mLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.leaveGroup();
            }
        });

        mPresenter.getGroupFromDatabase();
        mPresenter.addGroupListener();
        return view;
    }

    @Override
    public void showHomeView(HomeContract.HomeActivityCallback callback) {
        // Kill this fragment and return to home view
        HomeFragment homeFragment = callback.createHomeFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right,
                R.anim.slide_from_right, R.anim.slide_to_left);
        transaction.replace(R.id.fragment_container, homeFragment).commit();

        getFragmentManager().beginTransaction().remove(this).commit();
        getFragmentManager().popBackStack();
    }

    @Override
    public void setLocation(String location) {
        mLocationButton.setText(location);
    }

    @Override
    public void setMemberCount(String memberCount) {
        mGroupMemberCount.setText(memberCount);
    }

    @Override
    public void setTimeRange(String timeRange) {
        mTimeView.setText(timeRange);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }
}
