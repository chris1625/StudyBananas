package com.bananabanditcrew.studybananas.ui.signup;

import android.app.Activity;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

/**
 * Created by chris on 2/9/17.
 */

public interface SignUpContract {

    interface View extends BaseView<Presenter> {

        void startProgressIndicator(String title, String body);

        void stopProgressIndicator();

        void resetErrors();

        void showFirstEmptyError();

        void showLastEmptyError();

        void showEmailEmptyError();

        void showAccountExistsError();

        void showPasswordEmptyError();

        void showPasswordTooShortError();

        void showInvalidEmailError();

        void showPasswordNoMatchError();

        void setFirstFocus();

        void setLastFocus();

        void setEmailFocus();

        void setPasswordFocus();

        void setPasswordConfirmFocus();

        String getFirst();

        String getLast();

        String getEmail();

        String getPassword();

        String getConfirmPassword();

        void showEmailVerifyDialog();

        void showSignInView();

        Activity getFragmentActivity();
    }

    interface Presenter extends BasePresenter {

        void attemptCreateAccount();

        void startFirebaseAuthListener();

        void addFirebaseAuthStateListener();

        void removeFirebaseAuthStateListener();
    }
}
