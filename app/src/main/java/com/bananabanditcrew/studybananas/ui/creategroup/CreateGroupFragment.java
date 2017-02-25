package com.bananabanditcrew.studybananas.ui.creategroup;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment implements CreateGroupContract.View{

    private CreateGroupContract.Presenter mPresenter;
    private AutoCompleteTextView mCoursesSelect;
    private ArrayList<Course> mCourseArrayList;
    private Button mStartTimeButton;
    private Button mEndTimeButton;
    private Button mMaxGroupButton;
    private Button mCreateGroupButton;
    private EditText mDescritionText;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int maxNum;
    private String location;
    private String course;

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

        //mCourseArrayList=
        mCoursesSelect = (AutoCompleteTextView) root.findViewById(R.id.pick_class);
        mStartTimeButton= (Button)root.findViewById(R.id.start_time_button);
        mEndTimeButton= (Button)root.findViewById(R.id.end_time_button);
        mMaxGroupButton= (Button)root.findViewById(R.id.max_people_button);
        mDescritionText= (EditText)root.findViewById(R.id.description_text);
        mCreateGroupButton=(Button)root.findViewById(R.id.new_group_button);
        mCreateGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.attemptCreateGroup();
            }
        });
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimePicker();
            }
        });
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndTimePicker();
            }
        });
        mMaxGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
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
        mStartTimeButton.setError("Start time must be before end time");
        mEndTimeButton.setError("End time must be after start time");
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
        //TODO
    }

    @Override
    public void showNoNaxPeoplePickedError() {
        mMaxGroupButton.setError("Please select the maximum group occupancy");
    }

    @Override
    public void showStartTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
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
        }, hour, minute, DateFormat.is24HourFormat(getActivity()));
        tpd.show();
    }

    @Override
    public void showEndTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
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
        }, hour, minute, DateFormat.is24HourFormat(getActivity()));
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_DARK);
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
    }

    @Override
    public void showGroupManagementView() {
        //uncoment code when GroupManagement fragment is ready
        /*GroupManagementFragment createGroupFragment = new GroupManagemtFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, createGroupFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/
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
        return mCoursesSelect.getText().toString();
    }

    @Override
    public String getLocation() {
        return location;
    }
}
