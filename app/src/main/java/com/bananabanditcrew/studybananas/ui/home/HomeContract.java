package com.bananabanditcrew.studybananas.ui.home;

import com.bananabanditcrew.studybananas.ui.BasePresenter;
import com.bananabanditcrew.studybananas.ui.BaseView;

/**
 * Created by chris on 2/9/17.
 */

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void showSignInView();

    }

    interface Presenter extends BasePresenter {

        void signOut();

    }
}
