package com.bananabanditcrew.studybananas.ui.signup;

import com.bananabanditcrew.studybananas.BasePresenter;
import com.bananabanditcrew.studybananas.BaseView;
import com.bananabanditcrew.studybananas.ui.signin.SignInContract;

/**
 * Created by chris on 2/9/17.
 */

public interface SignUpContract {

    interface View extends BaseView<SignInContract.Presenter> {


    }

    interface Presenter extends BasePresenter {

    }
}
