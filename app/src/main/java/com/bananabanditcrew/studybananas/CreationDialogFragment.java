package com.bananabanditcrew.studybananas;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreationDialogFragment extends DialogFragment {

    private EditText mEditText;
    private Button mButton;

    public CreationDialogFragment() {
        // Required empty public constructor
    }

    public static CreationDialogFragment newInstance(String title) {
        CreationDialogFragment frag = new CreationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_creation_dialog, container, false);

        mEditText = (EditText)view.findViewById(R.id.edit_name);
        mButton = (Button)view.findViewById(R.id.add_name_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.setName(mEditText.getText().toString());
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.edit_name);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter full name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
