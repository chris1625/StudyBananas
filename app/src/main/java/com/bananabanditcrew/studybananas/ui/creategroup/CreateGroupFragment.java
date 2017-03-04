package com.bananabanditcrew.studybananas.ui.creategroup;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.services.GroupListenerService;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionContract;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionPresenter;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;
import com.bananabanditcrew.studybananas.ui.joingroup.JoinGroupContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateGroupFragment extends Fragment implements CreateGroupContract.View, GoogleApiClient.OnConnectionFailedListener {

    private String DEFAULT_BUTTON_TEXT="Select";
    private CreateGroupContract.Presenter mPresenter;
    private AutoCompleteTextView mCoursesSelect;
    private ArrayList<Course> mCourseArrayList;
    private Button mStartTimeButton;
    private Button mEndTimeButton;
    private Button mMaxGroupButton;
    private Button mCreateGroupButton;
    private EditText mDescritionText;
    private Button mLocationButton;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int maxNum;
    private String location;
    private String address;
    private String course;
    private boolean course_selected;
    private boolean time_compatibility_error;

    private GroupInteractionContract.Presenter mGroupInteractionPresenter;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance() { return new CreateGroupFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        course_selected=false;
        time_compatibility_error=false;
        final Calendar c = Calendar.getInstance();
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);
        endHour = c.get(Calendar.HOUR_OF_DAY);
        endMinute = c.get(Calendar.MINUTE);
        View root = inflater.inflate(R.layout.fragment_create_group, container, false);
        mCoursesSelect = (AutoCompleteTextView) root.findViewById(R.id.pick_class);
        setupCoursesSelectView();
        mStartTimeButton= (Button)root.findViewById(R.id.start_time_button);
        mEndTimeButton= (Button)root.findViewById(R.id.end_time_button);
        mMaxGroupButton= (Button)root.findViewById(R.id.max_people_button);
        mDescritionText= (EditText)root.findViewById(R.id.description_text);
        mLocationButton=(Button)root.findViewById(R.id.location_button);
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimePicker();
                mStartTimeButton.setError(null);
                if(time_compatibility_error){
                    mEndTimeButton.setError(null);
                    time_compatibility_error=false;
                }
            }
        });
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndTimePicker();
                mEndTimeButton.setError(null);
                if(time_compatibility_error){
                    mStartTimeButton.setError(null);
                    time_compatibility_error=false;
                }
            }
        });
        mMaxGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
                mMaxGroupButton.setError(null);
            }
        });
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationPicker();
                mLocationButton.setError(null);
            }
        });
        mCreateGroupButton=(Button)root.findViewById(R.id.new_group_button);
        mCreateGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetErrors();
                if(doValidations()) {
                    mPresenter.attemptCreateGroup();
                    showSuccessIndicator();
                }
            }
        });
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

    @Override
    public void showIncorrectTimeError() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("The group study time must be less than 12 hours");
        alertDialog.show();
        mStartTimeButton.setError("Group study period needs to be under 12 hours");
        mEndTimeButton.setError("Group study period needs to be under 12 hours");
    }

    @Override
    public void showNoCoursePickedError() {
        mCoursesSelect.setError("Please select a course");
    }

    @Override
    public void showNoStartTimePickedError() {
        mStartTimeButton.setError("Please select a start time");
    }

    @Override
    public void showNoEndTimePickedError() {
        mEndTimeButton.setError("Please select a start time");
    }

    @Override
    public void showNoLocationPickedError() {
        mLocationButton.setError("Please select a location");
    }

    @Override
    public void showNoMaxPeoplePickedError() {
        mMaxGroupButton.setError("Please select the maximum group occupancy");
    }

    @Override
    public void showStartTimePicker() {
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startHour = hourOfDay;
                startMinute = minute;
                String min=""+minute;
                int hour=hourOfDay;
                String stub=" a.m.";
                if(hour==0){
                    hour=12;
                }
                else if(hour>12) {
                    hour = hour - 12;
                    stub = " p.m.";
                }
                else if(hour==12){
                    stub=" p.m.";
                }
                if(minute<10){
                    min="0"+min;
                }
                mStartTimeButton.setText(hour + ":" + min+stub);
            }
        }, startHour, startMinute, DateFormat.is24HourFormat(getActivity()));
        tpd.show();
    }

    @Override
    public void showEndTimePicker() {
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endHour = hourOfDay;
                endMinute = minute;
                String min=""+minute;
                int hour=hourOfDay;
                String stub=" a.m.";
                if(hour==0){
                    hour=12;
                }
                else if(hour>12){
                    hour=hour-12;
                    stub= " p.m.";
                }
                else if(hour==12){
                    stub=" p.m.";
                }
                if(minute<10){
                    min="0"+min;
                }
                mEndTimeButton.setText(hour + ":" + min+stub);
            }
        }, endHour, endMinute, DateFormat.is24HourFormat(getActivity()));
        tpd.show();
    }

    @Override
    public void showNumberPicker() {
        RelativeLayout linearLayout = new RelativeLayout(getActivity());
        final NumberPicker aNumberPicker = new NumberPicker(getActivity());
        aNumberPicker.setMaxValue(20);
        aNumberPicker.setMinValue(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Set Group Member Limit");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                maxNum=aNumberPicker.getValue();
                                mMaxGroupButton.setText(""+maxNum);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    public void resetErrors() {
        mMaxGroupButton.setError(null);
        mCoursesSelect.setError(null);
        mStartTimeButton.setError(null);
        mEndTimeButton.setError(null);
        mLocationButton.setError(null);
    }

    @Override
    public void showGroupInteractionView(String course, String groupID) {
        // Setup groupInteraction fragment and presenter
        GroupInteractionFragment groupInteractionFragment = new GroupInteractionFragment();

        getFragmentManager().beginTransaction().remove(this).commit();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager().beginTransaction().remove(mPresenter.getHomeFragment()).commit();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, groupInteractionFragment,
                "group_interaction").commit();

        mGroupInteractionPresenter = new GroupInteractionPresenter(groupInteractionFragment,
                course, groupID,
                mPresenter.getHomeActivityCallback());

        // Start background service
        Intent intent = new Intent(getContext(), GroupListenerService.class);
        getActivity().startService(intent);
    }

    @Override
    public void showSuccessIndicator() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Creating Group");
        alertDialog.setMessage("Success");
        alertDialog.show();
    }

    @Override
    public String getDescription() {
        return mDescritionText.getText().toString();
    }

    @Override
    public int getStartHour() {
        return startHour;
    }

    @Override
    public int getStartMinute() {
        return startMinute;
    }

    @Override
    public int getEndHour() {
        return endHour;
    }

    @Override
    public int getEndMinute() {
        return endMinute;
    }

    @Override
    public int getMaxNum() {
        return maxNum;
    }

    @Override
    public String getCourseName() {
        return course;
    }

    @Override
    public void showLocationPicker() {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            Place place = PlaceAutocomplete.getPlace(getActivity(),intent);
            //System.out.println(place.getName());
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getAddress() { return address;}

    @Override
    public boolean doValidations() {
        boolean correct_info_given=true;
        boolean start_time_picked=true;
        boolean end_time_picked=true;
        if(mLocationButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showNoLocationPickedError();
            correct_info_given=false;
        }
        if(mStartTimeButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showNoStartTimePickedError();
            correct_info_given=false;
            start_time_picked=false;
        }
        if(mEndTimeButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showNoEndTimePickedError();
            correct_info_given=false;
            end_time_picked=false;
        }
        if(mMaxGroupButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showNoMaxPeoplePickedError();
            correct_info_given=false;
        }
        if(!course_selected){
            showNoCoursePickedError();
            correct_info_given=false;
        }
        if(start_time_picked && end_time_picked){
            int numHours= endHour-startHour;
            if(numHours >12 || (numHours > -12 &&numHours<0)){
                time_compatibility_error=true;
                showIncorrectTimeError();
                correct_info_given=false;
            }
        }
        return correct_info_given;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                location = place.getName().toString();
                address = place.getAddress().toString();
                mLocationButton.setText(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                /*Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());*/

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    public void setupCoursesSelectView() {
        mCoursesSelect.setAdapter(mPresenter.getCoursesAdapter());

        // Close window once option is selected
        mCoursesSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                course=mCoursesSelect.getText().toString();
                mCoursesSelect.setError(null);
                course_selected=true;
            }
        });
    }
}
