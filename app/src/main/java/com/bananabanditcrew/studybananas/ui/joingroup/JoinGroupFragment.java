package com.bananabanditcrew.studybananas.ui.joingroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class JoinGroupFragment extends Fragment implements JoinGroupContract.View {

    private JoinGroupContract.Presenter mPresenter;
    private LinearLayout mDummyLayout;
    private AutoCompleteTextView mCoursesSelect;
    private ExpandableListView mUserCourseList;
    private TextView mEmptyView;
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
        mEmptyView = (TextView) root.findViewById(R.id.courses_empty);
        mUserCourseList.setEmptyView(mEmptyView);
        mPresenter.getUserSavedCourses();

        // Set title of app to "join group"
        ((AppCompatActivity) getActivity()).getSupportActionBar()
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
                mPresenter.addUserCourse((String) mCoursesSelect.getAdapter().getItem(position));
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
                getResources().getString(R.string.group_interaction_fragment_tag))
                .commit();

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

        // Click time recorder to prevent double clicks
        private long mLastClickTime = 0;

        public CoursesAdapter(Context context, List<Course> courses, JoinGroupPresenter presenter,
                              JoinGroupContract.View view) {
            mContext = context;
            mCourses = courses;
            mPresenter = presenter;
            mView = view;
        }

        @Override
        public Group getChild(int listPosition, int expandedListPosition) {
            // Get the indices which are hidden
            ArrayList<Group> studyGroups = mCourses.get(listPosition).getStudyGroups();
            for (int i = 0; i < studyGroups.size(); i++) {
                Group group = studyGroups.get(i);
                if (group.getMaxMembers() == group.getGroupMembers().size() &&
                        i <= expandedListPosition) {
                    expandedListPosition++;
                }
            }
            return (expandedListPosition < studyGroups.size()) ?
                    studyGroups.get(expandedListPosition) : null;
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(final int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final Group groupRef = getChild(listPosition, expandedListPosition);

            // If the group is null, we know there are no groups, so show empty layout
            if (groupRef == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.empty_course, null);
                return convertView;
            }

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
                    (startMinute < 10 ? "0" + Integer.toString(startMinute) : Integer.toString(startMinute)) +
                    (startIsPostMeridian ? " PM" : " AM") + " - " +
                    Integer.toString(end12Hour) + ":" +
                    (endMinute < 10 ? "0" + Integer.toString(endMinute) : Integer.toString(endMinute)) +
                    (endIsPostMeridian ? " PM" : " AM");

            final String members = Integer.toString(groupRef.getGroupMembers().size()) +
                    "/" + Integer.toString(groupRef.getMaxMembers());

            if (convertView == null || convertView.findViewById(R.id.location_button) == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item_group, null);
            }

            Button locationButton = (Button) convertView.findViewById(R.id.location_button);
            locationButton.setText(locationName);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGoogleMaps(groupRef);

                }
            });

            TextView timeView = (TextView) convertView.findViewById(R.id.group_time_view);
            timeView.setText(timeFrame);

            TextView memberCount = (TextView) convertView.findViewById(R.id.member_count);
            memberCount.setText(members);

            Button joinGroupButton = (Button) convertView.findViewById(R.id.group_view_join_button);
            joinGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mPresenter.addUserToGroup(getGroup(listPosition).getCourseName(),
                            groupRef.getGroupID());
                }
            });

            return convertView;
        }

        private void showGoogleMaps(Group group) {
            String intentString = mContext.getResources().getString(R.string.google_maps_base_url,
                    group.getLocationName(), group.getAddressLine());
            Uri gmmIntentUri = Uri.parse(intentString);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(mapIntent);
            }
        }

        @Override
        public int getChildrenCount(int listPosition) {
            int visibleCount = 0;
            for (Group group : mCourses.get(listPosition).getStudyGroups()) {
                if (group.getMaxMembers() != group.getGroupMembers().size()) {
                    visibleCount++;
                }
            }
            return visibleCount == 0 ? 1 : visibleCount;
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