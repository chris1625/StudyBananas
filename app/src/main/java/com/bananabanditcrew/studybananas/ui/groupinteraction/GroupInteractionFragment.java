package com.bananabanditcrew.studybananas.ui.groupinteraction;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private ViewSwitcher mGroupMemberCountSwitcher;
    private TextView mGroupMemberCount;
    private EditText mGroupMemberEdit;
    private TextView mTimeView;
    private ViewSwitcher mDescriptionSwitcher;
    private TextView mDescription;
    private EditText mEditDescription;
    private Button mLeaveGroupButton;
    private LinearLayout mMemberListViewLayout;

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
        mGroupMemberCountSwitcher = (ViewSwitcher) view.findViewById(R.id.group_interaction_members_switcher);
        mGroupMemberCount = (TextView) view.findViewById(R.id.group_interaction_members);
        mGroupMemberEdit = (EditText) view.findViewById(R.id.group_interaction_members_edit);
        mTimeView = (TextView) view.findViewById(R.id.group_interaction_time);
        mDescriptionSwitcher = (ViewSwitcher) view.findViewById(R.id.group_interaction_description_switcher);
        mDescription = (TextView) view.findViewById(R.id.group_interaction_description);
        mEditDescription = (EditText) view.findViewById(R.id.group_interaction_description_edit);
        mMemberListViewLayout = (LinearLayout) view.findViewById(R.id.group_interaction_member_layout);
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
    public void setMemberCountEdit(String memberCount) {
        mGroupMemberEdit.setText(memberCount);
    }

    @Override
    public void setTimeRange(String timeRange) {
        mTimeView.setText(timeRange);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void setDescriptionEdit(String description) {
        mEditDescription.setText(description);
    }

    @Override
    public void setMemberListViewVisibility(boolean isVisible) {
        mMemberListViewLayout.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setFieldsEditable(boolean editable) {
        mGroupMemberCountSwitcher.showNext();
        mDescriptionSwitcher.showNext();

        mPresenter.updateEditFields();
    }

    @Override
    public void setLeaveButtonText(String text) {
        mLeaveGroupButton.setText(text);
    }

    public String getStringByID(int string) {
        return getString(string);
    }
}
