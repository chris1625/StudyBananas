package com.bananabanditcrew.studybananas.ui.signup;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.bananabanditcrew.studybananas.R;

public class SignUpFragment extends Fragment implements SignUpContract.View {

    private SignUpContract.Presenter mPresenter;
    private EditText mFirstName;
    private EditText mLastName;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private ProgressDialog mProgressView;
    private Button mEmailSignUpButton;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull SignUpContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Set title of toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.action_create_account));

        // Set up fields in fragment
        mFirstName = (EditText) root.findViewById(R.id.first_name);
        mLastName = (EditText) root.findViewById(R.id.last_name);
        mEmailView = (AutoCompleteTextView) root.findViewById(R.id.create_email);
        mPasswordView = (EditText) root.findViewById(R.id.create_password);
        mConfirmPasswordView = (EditText) root.findViewById(R.id.confirm_password);
        mEmailSignUpButton = (Button) root.findViewById(R.id.email_create_account_button);

        // Set up onclick listener for create account button
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.attemptCreateAccount();
            }
        });

        // Create the presenter
        mPresenter = new SignUpPresenter(this);

        return root;
    }

    @Override
    public void startProgressIndicator(String title, String body) {
        mProgressView = ProgressDialog.show(getContext(), title, body);
    }

    @Override
    public void stopProgressIndicator() {
        if (mProgressView != null)
            mProgressView.dismiss();
    }

    @Override
    public void resetErrors() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
    }

    @Override
    public void showFirstEmptyError() {
        mFirstName.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showLastEmptyError() {
        mLastName.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showEmailEmptyError() {
        mEmailView.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showAccountExistsError() {
        mEmailView.setError(getString(R.string.error_account_exists));
    }

    @Override
    public void showPasswordEmptyError() {
        mPasswordView.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showPasswordTooShortError() {
        mPasswordView.setError(getString(R.string.error_short_password));
    }

    @Override
    public void showInvalidEmailError() {
        mEmailView.setError(getString(R.string.error_invalid_email));
    }

    @Override
    public void showPasswordNoMatchError() {
        mPasswordView.setError(getString(R.string.error_mismatched_passwords));
        mConfirmPasswordView.setError(getString(R.string.error_mismatched_passwords));
    }

    @Override
    public void setFirstFocus() {
        mFirstName.requestFocus();
    }

    @Override
    public void setLastFocus() {
        mLastName.requestFocus();
    }

    @Override
    public void setEmailFocus() {
        mEmailView.requestFocus();
    }

    @Override
    public void setPasswordFocus() {
        mPasswordView.requestFocus();
    }

    @Override
    public void setPasswordConfirmFocus() {
        mConfirmPasswordView.requestFocus();
    }

    @Override
    public String getFirst() {
        return mFirstName.getText().toString();
    }

    @Override
    public String getLast() {
        return mLastName.getText().toString();
    }

    @Override
    public String getEmail() {
        return mEmailView.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    @Override
    public String getConfirmPassword() {
        return mConfirmPasswordView.getText().toString();
    }

    @Override
    public void showEmailVerifyDialog() {

        // Show verification alert to user
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.email_verification)
                .setTitle(R.string.email_verification_short);

        // Add close button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // User clicked ok button
                showSignInView();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showSignInView() {
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.addFirebaseAuthStateListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.removeFirebaseAuthStateListener();
        }
    }
}