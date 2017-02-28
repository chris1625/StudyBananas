package com.bananabanditcrew.studybananas.ui.groupinteraction;

import android.support.annotation.NonNull;

import com.bananabanditcrew.studybananas.data.User;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;

import java.security.acl.Group;

/**
 * Created by chris on 2/25/17.
 */

public class GroupInteractionPresenter implements GroupInteractionContract.Presenter {

    private final GroupInteractionContract.View mGroupInteractionView;
    private User mUser;
    private com.bananabanditcrew.studybananas.data.Group mGroup;
    private DatabaseHandler mDatabase;

    public GroupInteractionPresenter(@NonNull GroupInteractionContract.View groupInteractionView) {
        mGroupInteractionView = groupInteractionView;
        mGroupInteractionView.setPresenter(this);
        mDatabase = new DatabaseHandler();
    }
    @Override
    public void start() {
        // TODO fill in GroupInteractionPResenter's start method
    }

}
