package com.bananabanditcrew.studybananas.ui.groupinteraction;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInteractionFragment extends Fragment implements GroupInteractionContract.View {

    private GroupInteractionContract.Presenter mPresenter;

    private Button mLocationButton;
    private ViewSwitcher mGroupMemberCountSwitcher;
    private TextView mGroupMemberCount;
    private EditText mGroupMemberEdit;
    private TextView mStartTimeView;
    private ViewSwitcher mEndTimeSwitcher;
    private TextView mEndTimeView;
    private Button mEndTimeButton;
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
        mStartTimeView = (TextView) view.findViewById(R.id.group_interaction_start_time);
        mEndTimeSwitcher = (ViewSwitcher) view.findViewById(R.id.group_interaction_end_time_switcher);
        mEndTimeView = (TextView) view.findViewById(R.id.group_interaction_end_time);
        mEndTimeButton = (Button) view.findViewById(R.id.group_interaction_end_time_button);
        mDescriptionSwitcher = (ViewSwitcher) view.findViewById(R.id.group_interaction_description_switcher);
        mDescription = (TextView) view.findViewById(R.id.group_interaction_description);
        mEditDescription = (EditText) view.findViewById(R.id.group_interaction_description_edit);
        mMemberListViewLayout = (LinearLayout) view.findViewById(R.id.group_interaction_member_layout);
        mLeaveGroupButton = (Button) view.findViewById(R.id.leave_group_button);

        // Listener for set end time button
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Listener for leave group button
        mLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.leaveGroup();
            }
        });

        // Allow scrolling through the description
        mDescription.setMovementMethod(new ScrollingMovementMethod());

        mPresenter.getGroupFromDatabase();
        mPresenter.addGroupListener();

        // Set the title to be the group name
        mPresenter.setActionBarTitle();

        return view;
    }

    @Override
    public void setActionBarTitle(String courseName) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(courseName);
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
    public void setStartTime(String startTime) {
        mStartTimeView.setText(startTime);
    }

    @Override
    public void setEndTime(String endTime) {
        mEndTimeView.setText(endTime);
    }

    @Override
    public void setEndTimeButtonText(String endTimeButtonText) {
        mEndTimeButton.setText(endTimeButtonText);
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
        mEndTimeSwitcher.showNext();

        if (editable) {
            mPresenter.updateEditFields();
        } else {
            Log.d("Interaction update", "Calling presenter database update method");
            mPresenter.updateGroupInDataBase();
        }
    }

    @Override
    public void setLeaveButtonText(String text) {
        mLeaveGroupButton.setText(text);
    }

    public String getStringByID(int string) {
        return getString(string);
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mPresenter.updateEndTime(hourOfDay, minute);
            }
        }, mPresenter.getEditedEndHour(), mPresenter.getEditedEndMinute(), false);
        timePicker.setTitle("Select end time");
        timePicker.show();
    }

    @Override
    public String getMemberCountEdited() {
        return mGroupMemberEdit.getText().toString();
    }

    @Override
    public String getEndTimeEdited() {
        return mEndTimeButton.getText().toString();
    }

    @Override
    public String getDescriptionEdited() {
        return mEditDescription.getText().toString();
    }
}
