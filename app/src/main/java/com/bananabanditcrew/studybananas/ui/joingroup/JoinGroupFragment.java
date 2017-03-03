package com.bananabanditcrew.studybananas.ui.joingroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.services.GroupListenerService;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionFragment;
import com.bananabanditcrew.studybananas.ui.groupinteraction.GroupInteractionPresenter;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JoinGroupFragment extends Fragment implements JoinGroupContract.View {

    private JoinGroupContract.Presenter mPresenter;
    private LinearLayout mDummyLayout;
    private AutoCompleteTextView mCoursesSelect;
    private ExpandableListView mUserCourseList;
    private ArrayList<Course> mCourseArrayList;
    private GroupInteractionPresenter mGroupInteractionPresenter;

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

        // Re-add listeners if they do not exist
        if (mPresenter != null)
            mPresenter.addCourseListeners();
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

        mDummyLayout = (LinearLayout) root.findViewById(R.id.dummy_layout);
        mCoursesSelect = (AutoCompleteTextView) root.findViewById(R.id.course_select);
        setupCoursesSelectView();
        mUserCourseList = (ExpandableListView) root.findViewById(R.id.courses_list);

        mPresenter.getUserSavedCourses();

//         PURELY EXPERIMENTAL SECTION, TO BE REMOVED
        ArrayList<Address> addresses = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String addressLines = "";
        String addressName = "";
        try {
            addresses = (ArrayList<Address>) geocoder.getFromLocationName("Canyon Vista La Jolla", 1);
            Address address = addresses.get(0);
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                addressLines += (" " + address.getAddressLine(i));
            addressName = address.getFeatureName();

        } catch (Exception e) {
            Log.e("Location services", "IO Exception");
        }

        String description = "The quick brown fox jumped over the lazy dogs";

        Group testGroup = new Group("crh013@ucsd.edu", addressLines, addressName, 6, 13, 0,
                15, 30, description, Long.toString(System.currentTimeMillis()));
        mPresenter.addGroupToCourse("CSE 110", testGroup);
        // END EXPERIMENTAL SECTION

        // Set title of app to "join group"
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.join_group));
        return root;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void attachAdapter(CoursesAdapter adapter) {
        mUserCourseList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setupCoursesSelectView() {

        mCoursesSelect.setAdapter(mPresenter.getCoursesAdapter());
        mCoursesSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoursesSelect.showDropDown();
            }
        });

        // Close window once option is selected
        mCoursesSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDummyLayout.requestFocus();
                closeKeyboard(getActivity(), mDummyLayout.getWindowToken());
                mPresenter.addUserCourse((String)mCoursesSelect.getAdapter().getItem(position));
                mCoursesSelect.setText("");
            }
        });

        // Setup the function of the clear button in the right-hand side
        mCoursesSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = mCoursesSelect.getRight()
                            - mCoursesSelect.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        mCoursesSelect.setText("");
                        mCoursesSelect.dismissDropDown();
                        return true;
                    }

                }
                return false;
            }
        });

        // Prevent copy/paste in mCoursesSelect
        mCoursesSelect.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

    }

    @Override
    public void showGroupInteractionView(String course, String groupID) {

        // Add the user to the group
        mPresenter.addUserToGroup(course, groupID);

        // Setup groupInteraction fragment and presenter
        GroupInteractionFragment groupInteractionFragment = new GroupInteractionFragment();

        getFragmentManager().beginTransaction().remove(this).commit();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager().beginTransaction().remove(mPresenter.getHomeFragment()).commit();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.replace(R.id.fragment_container, groupInteractionFragment).commit();

        mGroupInteractionPresenter = new GroupInteractionPresenter(groupInteractionFragment,
                                                                   course, groupID,
                                                                   mPresenter.getActivityCallback());

        // Start background service
        Intent intent = new Intent(getContext(), GroupListenerService.class);
        getActivity().startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove event listeners for user courses
        mPresenter.removeCourseListeners();
    }

    public static class CoursesAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private List<Course> mCourses;
        private JoinGroupPresenter mPresenter;
        private final JoinGroupContract.View mView;

        public CoursesAdapter(Context context, List<Course> courses, JoinGroupPresenter presenter,
                              JoinGroupContract.View view) {
            mContext = context;
            mCourses = courses;
            mPresenter = presenter;
            mView = view;

        }

        @Override
        public Group getChild(int listPosition, int expandedListPosition) {
            return mCourses.get(listPosition).getGroupByIndex(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(final int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final Group groupRef = getChild(listPosition, expandedListPosition);
            final String locationName = groupRef.getLocationName();

            // Get time string
            int startHour = groupRef.getStartHour();
            int startMinute = groupRef.getStartMinute();
            int endHour = groupRef.getEndHour();
            int endMinute = groupRef.getEndMinute();

            // Boolean for AM vs PM
            boolean startIsPostMeridian = (startHour >= 12);
            boolean endIsPostMeridian = (endHour >= 12);

            int start12Hour = ((startIsPostMeridian) ? ((startHour == 12) ? 12 : (startHour - 12)) :
                    ((startHour == 0) ? 12 : startHour));
            int end12Hour = ((endIsPostMeridian) ? ((endHour == 12) ? 12 : (endHour - 12)) :
                    ((endHour == 0) ? 12 : endHour));

            // Time string
            final String timeFrame = Integer.toString(start12Hour) + ":" +
                    (startMinute == 0 ? "00" : Integer.toString(startMinute)) +
                    (startIsPostMeridian ? " PM" : " AM") + " - " +
                    Integer.toString(end12Hour) + ":" +
                    (endMinute == 0 ? "00" : Integer.toString(endMinute)) +
                    (endIsPostMeridian ? " PM" : " AM");

            final String members = Integer.toString(groupRef.getGroupMembers().size()) +
                    "/" + Integer.toString(groupRef.getMaxMembers());

            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item_group, null);
            }

            Button locationButton = (Button) convertView.findViewById(R.id.location_button);
            locationButton.setText(locationName);

            TextView timeView = (TextView) convertView.findViewById(R.id.group_time_view);
            timeView.setText(timeFrame);

            TextView memberCount = (TextView) convertView.findViewById(R.id.member_count);
            memberCount.setText(members);

            Button joinGroupButton = (Button) convertView.findViewById(R.id.group_view_join_button);
            joinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mView.showGroupInteractionView(getGroup(listPosition).getCourseName(),
                            groupRef.getGroupID());
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return mCourses.get(listPosition).getStudyGroups().size();
        }

        @Override
        public Course getGroup(int listPosition) {
            return mCourses.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return mCourses.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(final int listPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            final String listTitle = getGroup(listPosition).getCourseName();
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group_course, null);
            }

            final TextView listTitleTextView = (TextView)
                    convertView.findViewById(R.id.list_group_course);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);

            Button removeClassButton = (Button) convertView.findViewById(R.id.delete_class_button);
            removeClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.removeUserCourse(getGroup(listPosition).getCourseName());
                }
            });

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }
}