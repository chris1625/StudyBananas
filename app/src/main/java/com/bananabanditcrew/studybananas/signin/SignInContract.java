package com.bananabanditcrew.studybananas.signin;

import com.bananabanditcrew.studybananas.BasePresenter;
import com.bananabanditcrew.studybananas.BaseView;

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
    }

    interface Presenter extends BasePresenter {

        void attemptLogin();

        void resetPassword(String email);

        void sendPasswordResetEmail(String email);

        void firebaseSignIn(String email, String password);

        void startFirebaseAuthListener();
    }
}