package com.bananabanditcrew.studybananas.ui.joingroup;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;

import java.util.ArrayList;
import java.util.List;

public class JoinGroupFragment extends Fragment implements JoinGroupContract.View {

    private JoinGroupContract.Presenter mPresenter;
    private LinearLayout mDummyLayout;
    private AutoCompleteTextView mCoursesSelect;
    private ExpandableListView mUserCourseList;
    private ArrayList<Course> mCourseArrayList;

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

        // TODO THIS IS A PURELY EXPERIMENTAL SECTION, WILL BE REMOVED

        User user1 = new User("Chris", "Harris", "crh013@ucsd.edu");
        User user2 = new User("Bob", "Ross", "rip@gmail.com");
        Location loc1 = new Location("text");
        Location loc2 = new Location("lul");
        Course course1 = new Course("CSE 101");
        Course course2 = new Course("CSE 120");
        Group group1 = new Group(course1, user1, loc1, 0, 0, 2, 20);
        Group group2 = new Group(course2, user2, loc2, 2, 20, 4, 0);
        course1.addStudyGroup(group1);
        course2.addStudyGroup(group2);

        mCourseArrayList = new ArrayList<>();
        mCourseArrayList.add(course1);
        mCourseArrayList.add(course2);

        // Create adapter from list
        CoursesAdapter coursesAdapter = new CoursesAdapter(getActivity(), mCourseArrayList);
        mUserCourseList.setAdapter(coursesAdapter);

        // TODO END EXPERIMENTAL SECTION

        return root;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
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

    private static class CoursesAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private List<Course> mCourses;

        public CoursesAdapter(Context context, List<Course> courses) {
            mContext = context;
            mCourses = courses;
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
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = getChild(listPosition,
                    expandedListPosition).getLeader().getFirstName();
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item_group, null);
            }

            TextView expandedListTextView =
                    (TextView) convertView.findViewById(R.id.list_item_group);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return mCourses.get(listPosition).getGroupCount();
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
        public View getGroupView(int listPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            String listTitle = getGroup(listPosition).getCourseName();
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group_course, null);
            }

            TextView listTitleTextView = (TextView)
                    convertView.findViewById(R.id.list_group_course);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
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