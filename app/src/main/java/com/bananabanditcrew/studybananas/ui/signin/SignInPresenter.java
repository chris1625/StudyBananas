package com.bananabanditcrew.studybananas.ui.signin;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.bananabanditcrew.studybananas.data.database.DatabaseCallback;
import com.bananabanditcrew.studybananas.data.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chris on 2/8/17.
 */

public class SignInPresenter implements SignInContract.Presenter {

    // Firebase auth listener stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Reference to our View
    private final SignInContract.View mSignInView;

    SignInPresenter(@NonNull SignInContract.View signInView) {
        mSignInView = signInView;
        mSignInView.setPresenter(this);
        startFirebaseAuthListener();
    }

    @Override
    public void start() {
        // TODO fill in SignInPresenter's start method
    }

    @Override
    public void attemptLogin() {

        // Clear error messages
        mSignInView.resetErrors();

        // Get values from fields
        String email = mSignInView.getEmail();
        String password = mSignInView.getPassword();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mSignInView.showPasswordEmptyError();
            mSignInView.setPasswordFocus();
            cancel = true;
        }

        // check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mSignInView.showEmailEmptyError();
            mSignInView.setEmailFocus();
            cancel = true;
        } else if (!isEmailValid(email)) {
            mSignInView.showInvalidEmailError();
            mSignInView.setEmailFocus();
            cancel = true;
        }

        if (!cancel) {
            mSignInView.startProgressIndicator("Login", "Authenticating...");
            firebaseSignIn(email, password);
        }
    }

    /**
     * Helper method to parse emails and ensure they contain the UCSD domain.
     * @param email Email string to be parsed
     * @return      Whether email is valid
     */
    private boolean isEmailValid(String email) {
       return (email.endsWith("@ucsd.edu") && !email.equals("@ucsd.edu"));
    }

    @Override
    public void resetPassword(String email, DialogInterface dialog) {
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            Log.d("Password reset", "Email " + email + " is not valid.");
            dialog.dismiss();
            return;
        }

        // Dismiss dialog
        dialog.dismiss();

        mSignInView.startProgressIndicator("Reset Password", "Sending email...");

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Send email succeeded
                mSignInView.stopProgressIndicator();
                mSignInView.showResetPasswordSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Send email failed
                mSignInView.stopProgressIndicator();
                mSignInView.showResetPasswordFailed();
            }
        });
    }

    private void firebaseSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mSignInView.getFragmentActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Accounts", "signInWithEmail:onComplete:" + task.isSuccessful());
                        mSignInView.stopProgressIndicator();
                        // Task unsuccessful
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidUserException e) {
                                mSignInView.showEmailNotFoundError();
                                mSignInView.setEmailFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mSignInView.showInvalidPasswordError();
                                mSignInView.setPasswordFocus();
                            } catch(Exception e) {
                                Log.e("Accounts", e.getMessage());
                            }
                            return;
                        }

                        // Login was successful
                        // First check for email verification
                        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                            // Send the user a notification
                            mSignInView.showEmailNotVerified();
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            mSignInView.showHomeView();
                        }
                    }
                });
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

    @Override
    public boolean isUserLoggedIn() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }
}