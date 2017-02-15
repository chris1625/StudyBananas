package com.bananabanditcrew.studybananas.ui.signin;

import android.app.Activity;
import android.content.DialogInterface;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

/**
 * Created by chris on 2/8/17.
 */

public interface SignInContract {

    interface View extends BaseView<Presenter> {

        void startProgressIndicator(String title, String body);

        void stopProgressIndicator();

        void resetErrors();

        void showEmailEmptyError();

        void showPasswordEmptyError();

        void showInvalidEmailError();

        void showEmailNotFoundError();

        void showInvalidPasswordError();

        void setEmailFocus();

        void setPasswordFocus();

        String getEmail();

        String getPassword();

        void showResetPasswordPrompt();

        void showResetPasswordFailed();

        void showResetPasswordSuccess();

        void showEmailNotVerified();

        void showSignUpView();

        void showHomeView();

        Activity getFragmentActivity();
    }

    interface Presenter extends BasePresenter {

        void attemptLogin();

        void resetPassword(String email, DialogInterface dialog);

        void startFirebaseAuthListener();

        void addFirebaseAuthStateListener();

        void removeFirebaseAuthStateListener();

        boolean isUserLoggedIn();

    }
}