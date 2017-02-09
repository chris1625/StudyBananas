package com.bananabanditcrew.studybananas.signin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.bananabanditcrew.studybananas.R;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if (findViewById(R.id.fragment_container_signin) != null) {
            if (savedInstanceState != null) {
                return;
            }

            SignInFragment signInFragment = new SignInFragment();
            signInFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_signin,
                    signInFragment).commit();
        }
    }

//    public void signIn(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("Accounts", "signInWithEmail:onComplete:" + task.isSuccessful());
//                        mProgressView.dismiss();
//                        // Task unsuccessful
//                        if (!task.isSuccessful()) {
//                            try {
//                                throw task.getException();
//                            } catch(FirebaseAuthInvalidUserException e) {
//                                mEmailView.setError(getString(R.string.error_account_no_exist));
//                                mEmailView.requestFocus();
//                            } catch(FirebaseAuthInvalidCredentialsException e) {
//                                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                                mPasswordView.requestFocus();
//                            } catch(Exception e) {
//                                Log.e("Accounts", e.getMessage());
//                            }
//                            return;
//                        }
//
//                        // Login was successful
//                        // First check for email verification
//                        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
//                            // Give the user a popup
//                            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
//                            builder.setMessage(R.string.email_verification_failure)
//                                   .setTitle(R.string.email_verification_short);
//                            // Add close/ok button
//                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // User clicked ok button
//                                    dialog.dismiss();
//                                }
//                            });
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//                            FirebaseAuth.getInstance().signOut();
//                        } else {
//                            Intent myIntent = new Intent(SignInActivity.this, HomeActivity.class);
//                            SignInActivity.this.startActivity(myIntent);
//                            finish();
//                        }
//                    }
//                });
//    }

//    public void resetPassword() {
//
//        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignInActivity.this);
//        alertBuilder.setMessage(R.string.password_reset_email)
//                .setTitle(R.string.password_reset_title);
//        final EditText emailInput = new EditText(this);
//        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        emailInput.setHint(R.string.prompt_email);
//        alertBuilder.setView(emailInput, 40, 0, 40, 0);
//
//        // Add close button
//        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // User clicked cancel button
//                dialog.dismiss();
//            }
//        });
//
//        alertBuilder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // User clicked send button
//                final String email = emailInput.getText().toString();
//
//                // Perform data verification
//                if (TextUtils.isEmpty(email)) {
//                    emailInput.setError(getString(R.string.error_field_required));
//                    return;
//                } else if (!(email.endsWith("@ucsd.edu")) || email.equals("@ucsd.edu")) {
//                    emailInput.setError(getString(R.string.error_invalid_email));
//                    return;
//                }
//                // Dismiss dialog
//                dialog.dismiss();
//
//                // Start progress dialog
//                mProgressView = ProgressDialog.show(SignInActivity.this, "Reset Password", "Sending email...");
//
//                // Holy mother of nesting (so so sorry)
//                mAuth.sendPasswordResetEmail(email)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // send email succeeded
//                                mProgressView.dismiss();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
//                                builder.setMessage(R.string.password_reset_success)
//                                        .setTitle(R.string.password_reset_title);
//                                // Add ok button
//                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // User clicked ok button
//                                        dialog.dismiss();
//                                    }
//                                });
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //  Send email failed
//                        mProgressView.dismiss();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
//                        builder.setMessage(R.string.password_reset_failure)
//                                .setTitle(R.string.password_reset_title);
//                        // Add ok button
//                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // User clicked ok button
//                                dialog.dismiss();
//                            }
//                        });
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//                    }
//                });
//            }
//        });
//
//        AlertDialog emailDialog = alertBuilder.create();
//        emailDialog.show();
//
//
//    }
}