package com.bananabanditcrew.studybananas.ui.creategroup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.bananabanditcrew.studybananas.data.Course;
import com.bananabanditcrew.studybananas.data.Group;
import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.bananabanditcrew.studybananas.ui.home.HomeContract;
import com.bananabanditcrew.studybananas.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class CreateGroupPresenter implements DatabaseCallback.GetUserCallback,
        DatabaseCallback.GetCourseCallback,
        CreateGroupContract.Presenter {

    private final CreateGroupContract.View mCreateGroupView;
    private ArrayAdapter<String> mCourseList;
    private DatabaseHandler mDatabase;
    private HomeFragment mHomeFragment;
    private HomeContract.HomeActivityCallback mHomeActivityCallback;
    private String mCourseName;
    private Course mCourse;
    private Group mGroup;
    private String mGroupID;

    public CreateGroupPresenter(@NonNull CreateGroupContract.View createGroupView,
                                ArrayAdapter<String> courseList, HomeFragment homeFragment,
                                HomeContract.HomeActivityCallback homeActivityCallback) {
        mCreateGroupView = createGroupView;
        mDatabase = DatabaseHandler.getInstance();
        mCourseList = courseList;
        mHomeFragment = homeFragment;
        mHomeActivityCallback = homeActivityCallback;
    }

    @Override
    public void start() {
    }

    public Activity getActivity() {
        return mCreateGroupView.getActivity();
    }

    @Override
    public ArrayAdapter<String> getCoursesAdapter() {
        return mCourseList;
    }

    @Override
    public void attemptCreateGroup() {
        int mStartHour = mCreateGroupView.getStartHour();
        int mStartMinute = mCreateGroupView.getStartMinute();
        int mEndHour = mCreateGroupView.getEndHour();
        int mEndMinute = mCreateGroupView.getEndMinute();
        int mMembers = mCreateGroupView.getMaxNum();
        mCourseName = mCreateGroupView.getCourseName();
        String mLocationName = mCreateGroupView.getLocation();
        String mAddress = mCreateGroupView.getAddress();
        String mDescription = mCreateGroupView.getDescription();
        mGroupID = Long.toString(System.currentTimeMillis());
        mGroup = new Group(FirebaseAuth.getInstance().getCurrentUser().getEmail(), mAddress,
                mLocationName, mMembers, mStartHour, mStartMinute, mEndHour, mEndMinute, mDescription,
                mGroupID);
        mDatabase.getCourse(mCourseName, this);
    }

    @Override
    public void onCourseRetrieved(Course course) {
        mCourse = course;
        mDatabase.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), this);
    }

    @Override
    public void onUserRetrieved(User user) {
        // Update user fields
        user.setGroupCourse(mCourseName);
        user.setGroupID(mGroupID);

        mGroup.addGroupMember(user.getEmail());
        mGroup.setLeader(user.getEmail());
        mCourse.addStudyGroup(mGroup);

        // Update the user and course through the database
        mDatabase.updateUser(user);
        mDatabase.updateCourse(mCourse);

        mCreateGroupView.showGroupInteractionView(mCourseName, mGroupID);
    }

    @Override
    public HomeFragment getHomeFragment() {
        return mHomeFragment;
    }

    @Override
    public HomeContract.HomeActivityCallback getHomeActivityCallback() {
        return mHomeActivityCallback;
    }

    @Override
    public boolean doValidations() {
        boolean correct_info_given = true;
        if (!mCreateGroupView.isLocation_picked()) {
            mCreateGroupView.showNoLocationPickedError();
            correct_info_given = false;
        }
        if (!mCreateGroupView.isStart_time_picked()) {
            mCreateGroupView.showNoStartTimePickedError();
            correct_info_given = false;
        }
        if (!mCreateGroupView.isEnd_time_picked()) {
            mCreateGroupView.showNoEndTimePickedError();
            correct_info_given = false;
        }
        if (!mCreateGroupView.isMax_members_picked()) {
            mCreateGroupView.showNoMaxPeoplePickedError();
            correct_info_given = false;
        }
        if (!mCreateGroupView.isCourse_selected()) {
            mCreateGroupView.showNoCoursePickedError();
            correct_info_given = false;
        }
        if (mCreateGroupView.isStart_time_picked() && mCreateGroupView.isEnd_time_picked()) {
            int startHour = mCreateGroupView.getStartHour();
            int endHour = mCreateGroupView.getEndHour();
            int startMinute = mCreateGroupView.getStartMinute();
            int endMinute = mCreateGroupView.getEndMinute();

            int timeDiff = endHour - startHour;
            timeDiff = (timeDiff < 0) ? 24 + timeDiff : timeDiff;

            if (timeDiff > 16 || ((startHour == endHour) && (endMinute <= startMinute))) {
                mCreateGroupView.setTime_compatibility_error(true);
                mCreateGroupView.showIncorrectTimeError();
                correct_info_given = false;
            }
        }
        return correct_info_given;
    }
}
