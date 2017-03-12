package com.bananabanditcrew.studybananas.ui.signup;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by chris on 2/9/17.
 */

public class SignUpPresenter implements SignUpContract.Presenter,
        DatabaseCallback.UserCreationCallback {

    // Firebase auth listener stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Reference to our view
    private final SignUpContract.View mSignUpView;

    // Database reference
    private DatabaseHandler mDatabase;

    public SignUpPresenter(@NonNull SignUpContract.View signUpView) {
        mSignUpView = signUpView;
        mSignUpView.setPresenter(this);
        startFirebaseAuthListener();
        mDatabase = DatabaseHandler.getInstance();
    }

    @Override
    public void start() {
    }

    @Override
    public void attemptCreateAccount() {

        // Reset errors
        mSignUpView.resetErrors();

        // Get values from fields
        String firstName = mSignUpView.getFirst();
        String lastName = mSignUpView.getLast();
        String email = mSignUpView.getEmail();
        String password = mSignUpView.getPassword();
        String confirmPassword = mSignUpView.getConfirmPassword();

        // Resources for strings
        Resources resources = ((SignUpFragment) (mSignUpView)).getResources();

        boolean cancel = false;

        // Check for a valid password, if the user entered one
        if (TextUtils.isEmpty(password)) {
            mSignUpView.showPasswordEmptyError();
            mSignUpView.setPasswordFocus();
            cancel = true;
        } else if (password.length() < 6) {
            mSignUpView.showPasswordTooShortError();
            mSignUpView.setPasswordFocus();
            cancel = true;
        }

        // Check for confirmed password
        if (!(password.equals(confirmPassword))) {
            mSignUpView.showPasswordNoMatchError();
            mSignUpView.setPasswordFocus();
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mSignUpView.showEmailEmptyError();
            mSignUpView.setEmailFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            mSignUpView.showInvalidEmailError();
            mSignUpView.setEmailFocus();
        }

        // Check to ensure last name is entered
        if (TextUtils.isEmpty(lastName)) {
            mSignUpView.showLastEmptyError();
            mSignUpView.setLastFocus();
        }

        // Check to ensure first name is entered
        if (TextUtils.isEmpty(firstName)) {
            mSignUpView.showFirstEmptyError();
            mSignUpView.setFirstFocus();
        }

        if (!cancel) {
            mSignUpView.startProgressIndicator(resources.getString(R.string.action_create_account),
                    resources.getString(R.string.create_account_progress));
            firebaseCreateAccount(mSignUpView.getFirst(), mSignUpView.getLast(), email, password);
        }
    }

    @Override
    public void startFirebaseAuthListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is logged in
                } else {
                    // User is signed out
                }
            }
        };
    }

    @Override
    public void addFirebaseAuthStateListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void removeFirebaseAuthStateListener() {
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private boolean isEmailValid(String email) {
        String emailSuffix = ((SignUpFragment) mSignUpView).getResources()
                .getString(R.string.email_suffix);
        return (email.endsWith(emailSuffix) && !email.equals(emailSuffix));
    }

    private void firebaseCreateAccount(final String first, final String last, final String email,
                                       final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mSignUpView.getFragmentActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Accounts", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        mSignUpView.stopProgressIndicator();

                        // Task unsuccessful
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mSignUpView.showInvalidEmailError();
                                mSignUpView.setEmailFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                mSignUpView.showAccountExistsError();
                                mSignUpView.setEmailFocus();
                            } catch (Exception e) {
                                Log.e("Accounts", e.getMessage());
                            }
                            return;
                        }

                        // Login successful
                        // Send email verification, account won't be accessible until this is completed
                        firstTimeCreation(first, last, email);

                        // The verification will be completed in the callback method
                    }
                });
    }

    private void firstTimeCreation(String first, String last, String email) {
        mDatabase.createNewUser(first, last, email, this);
    }

    @Override
    public void finishAccountCreation() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
        Log.d("Accounts", "sending email verification");

        // Display popup about the email
        mSignUpView.showEmailVerifyDialog();
    }
}