package com.bananabanditcrew.studybananas.ui.groupinteraction;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananabanditcrew.studybananas.R;
import com.bananabanditcrew.studybananas.ui.signup.SignUpContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInteractionFragment extends Fragment implements GroupInteractionContract.View {

    private GroupInteractionContract.Presenter mPresenter;

    public GroupInteractionFragment() {
        // Required empty public constructor
    }

    @Override
    public void setPresenter(@NonNull GroupInteractionContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_interaction, container, false);
    }

}
