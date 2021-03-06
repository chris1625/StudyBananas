package com.bananabanditcrew.studybananas.ui.groupinteraction;


import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.services.GroupListenerService;
import com.bananabanditcrew.studybananas.services.GroupListenerServiceCallbacks;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInteractionFragment extends Fragment implements GroupInteractionContract.View,
        GroupListenerServiceCallbacks {

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
    private ListView mMemberListView;

    // Service which will run in background
    private GroupListenerService mListenerService;
    private boolean mIsBound;

    // Adapter for the members of a group
    private MemberAdapter mMemberAdapter;

    // Callbacks for service binding
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GroupListenerService.LocalBinder binder = (GroupListenerService.LocalBinder) service;
            mListenerService = binder.getService();
            mIsBound = true;
            mListenerService.setCallbacks(GroupInteractionFragment.this);

            if (mPresenter != null) {
                Log.d("Service", "Setting presenter");
                mListenerService.setPresenter(mPresenter);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    public GroupInteractionFragment() {
        // Required empty public constructor
    }

    @Override
    public GroupInteractionContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to background service
        Intent intent = new Intent(getContext(), GroupListenerService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mPresenter.onResume();
        mPresenter.addGroupListener();
        mPresenter.updateGroupInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.removeGroupListener();

        // Unbind from service
        if (mIsBound) {
            mListenerService.setCallbacks(null);
            getActivity().unbindService(mServiceConnection);
            mIsBound = false;
        }
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
        mMemberListView = (ListView) view.findViewById(R.id.group_interaction_member_list);

        // Listener for location button
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGoogleMaps();
            }
        });

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

    private void showGoogleMaps() {
        String intentString = getResources().getString(R.string.google_maps_base_url,
                mPresenter.getLocationName(), mPresenter.getAddress());
        Uri gmmIntentUri = Uri.parse(intentString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(mapIntent);
        }
    }

    @Override
    public void setActionBarTitle(String courseName) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(courseName);
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

        // Close the keyboard if it is open
        ((HomeActivity) getActivity()).closeKeyboard();

        // Stop the background service
        if (mListenerService != null) {
            mListenerService.stopSelf();
        }
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
        timePicker.setTitle(getResources().getString(R.string.select_end_time));
        timePicker.show();
    }

    @Override
    public String getMemberCountEdited() {
        return mGroupMemberEdit.getText().toString();
    }

    @Override
    public String getDescriptionEdited() {
        return mEditDescription.getText().toString();
    }

    @Override
    public void createAdapter(ArrayList<String> members) {
        mMemberAdapter = new MemberAdapter(getContext(), members);
        mMemberListView.setAdapter(mMemberAdapter);
    }

    @Override
    public void notifyAdapter() {
        if (mMemberAdapter != null) {
            mMemberAdapter.notifyDataSetChanged();
        }
    }

    private void showLeadershipTransferDialog(final String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.change_leadership, user))
                .setTitle(R.string.group_management);
        // Add ok button
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked yes button
                dialog.dismiss();
                mPresenter.transferLeadership(user);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked no button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Custom array adapter to show the current members in the group to the group leader
    public class MemberAdapter extends ArrayAdapter<String> {
        private MemberAdapter(Context context, ArrayList<String> members) {
            super(context, 0, members);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the member data for this position
            final String member = getItem(position);

            // Check if view is being reused, otherwise inflate new view
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(getContext()).inflate(R.layout.list_item_member, parent, false);
            }

            // Get text and button fields
            TextView memberText = (TextView) convertView.findViewById(R.id.member_list_item_text);
            Button kickButton = (Button) convertView.findViewById(R.id.kick_member_button);
            ImageButton ownershipButton = (ImageButton) convertView
                    .findViewById(R.id.transfer_ownership_button);

            // Update the text field with the user email
            memberText.setText(member);

            // Hide or show kick button based on whether this is the current user
            kickButton.setVisibility(mPresenter.isCurrentUser(member) ? View.INVISIBLE : View.VISIBLE);

            // Change source of image button based on leader status of member
            if (mPresenter.getGroupLeader().equals(member)) {
                ownershipButton.setImageResource(R.drawable.ic_crown_gold);
                ownershipButton.getBackground().setAlpha(0);
            } else {
                ownershipButton.setImageResource(R.drawable.ic_crown_white);
                ownershipButton.getBackground().setAlpha(100);
            }

            // Add onclick listeners for both buttons
            kickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Only kick if the user is not yourself (duh)
                    if (!mPresenter.getGroupLeader().equals(member)) {
                        mPresenter.kickUser(member);
                    }
                }
            });

            ownershipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // You can't make yourself the owner when you already are!
                    if (!mPresenter.getGroupLeader().equals(member)) {
                        showLeadershipTransferDialog(member);
                    }
                }
            });
            return convertView;
        }
    }
}
