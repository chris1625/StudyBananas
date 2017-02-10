package com.bananabanditcrew.studybananas.ui.home;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.bananabanditcrew.studybananas.ui.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by chris on 2/9/17.
 */

public class HomePresenter implements HomeContract.Presenter {

    // Reference to view
    private final HomeContract.View mHomeView;

    HomePresenter(@NonNull HomeContract.View homeView) {
        mHomeView = homeView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void start() {
        // TODO fill in homepresenter's start method
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        mHomeView.showSignInView();
    }
}