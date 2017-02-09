package com.bananabanditcrew.studybananas.ui.signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.home.HomeActivity;

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

            SignInPresenter mPresenter = new SignInPresenter(signInFragment);

            if (mPresenter.isUserLoggedIn()) {
                Intent myIntent = new Intent(SignInActivity.this, HomeActivity.class);
                SignInActivity.this.startActivity(myIntent);
                finish();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_signin,
                        signInFragment).commit();
            }
        }
    }
}