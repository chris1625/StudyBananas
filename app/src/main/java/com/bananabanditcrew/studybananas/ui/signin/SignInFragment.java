package com.bananabanditcrew.studybananas.ui.signin;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements SignInContract.View {

    private SignInContract.Presenter mPresenter;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressDialog mProgressView;
    private Button mEmailSignInButton;
    private TextView mCreateAccountText;
    private TextView mForgotPasswordText;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
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
    public void setPresenter(@NonNull SignInContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Set up fields in fragment
        mEmailView = (AutoCompleteTextView) root.findViewById(R.id.email);
        mPasswordView = (EditText) root.findViewById(R.id.password);
        mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.attemptLogin();
            }
        });

        mCreateAccountText = (TextView) root.findViewById(R.id.create_account);
        mCreateAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpView();
            }
        });

        mForgotPasswordText = (TextView) root.findViewById(R.id.forgot_password);
        mForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordPrompt();
            }
        });

        // Create the presenter
        mPresenter = new SignInPresenter(this);

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
    }

    @Override
    public void showEmailEmptyError() {
        mEmailView.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showPasswordEmptyError() {
        mPasswordView.setError(getString(R.string.error_field_required));
    }

    @Override
    public void showInvalidEmailError() {
        mEmailView.setError(getString(R.string.error_invalid_email));
    }

    @Override
    public void showEmailNotFoundError() {
        mEmailView.setError(getString(R.string.error_account_no_exist));
    }

    @Override
    public void showInvalidPasswordError() {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
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
    public String getEmail() {
        return mEmailView.getText().toString();
    }

    @Override
    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    @Override
    public void showResetPasswordPrompt() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setMessage(R.string.password_reset_email).setTitle(R.string.password_reset_title);
        final EditText emailInput = new EditText(getContext());
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint(R.string.prompt_email);
        alertBuilder.setView(emailInput, 40, 0, 40, 0);

        // Add close button
        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked cancel button
                dialog.dismiss();
            }
        });

        // Add send button
        alertBuilder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked send button
                mPresenter.resetPassword(emailInput.getText().toString(), dialog);
                dialog.dismiss();
            }
        });

        AlertDialog emailDialog = alertBuilder.create();
        emailDialog.show();
    }

    @Override
    public void showResetPasswordFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.password_reset_failure).setTitle(R.string.password_reset_title);
        // Add ok button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked ok button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showResetPasswordSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.password_reset_success).setTitle(R.string.password_reset_title);
        // Add ok button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked ok button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showEmailNotVerified() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.email_verification_failure)
                .setTitle(R.string.email_verification_short);
        // Add close/ok button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked ok button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showSignUpView() {
        // TODO Add signup fragment creation here
    }

    @Override
    public void showHomeView() {
        Intent myIntent = new Intent(getActivity(), HomeActivity.class);
        startActivity(myIntent);
        getActivity().finish();
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