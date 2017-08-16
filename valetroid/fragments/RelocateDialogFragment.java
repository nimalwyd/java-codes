package com.anaiglobal.valetroid.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.valetroid.R;
import com.anaiglobal.valetroid.ValetApp;

/**
 * Created by sasha on 3/20/14.
 */
public class RelocateDialogFragment extends DialogFragment
{
    private ValetApp mApp;
    private Cloud mCloud;
    private String mLPN;
    private Activity mActivity;
    private EditText stallEdit;

    static RelocateDialogFragment newInstance(Bundle args)
    {
        RelocateDialogFragment rdf = new RelocateDialogFragment();
        rdf.setArguments(args);
        return rdf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.relocate_layout, container, false);

        mApp = (ValetApp)(getActivity().getApplication());
        mCloud = mApp.getCloud();
        mLPN = getArguments().getString("LPN");

        getDialog().setTitle("Enter stall for " + mLPN);

        stallEdit = (EditText) view.findViewById(R.id.stallBox);

        Button okBttn = (Button) view.findViewById(R.id.okButton);
        okBttn.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String stall = stallEdit.getText().toString();
                if(!stall.isEmpty())
                {
                    mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mCloud.enableRequests();
                    mCloud.sendValetRelocate(mLPN, stall);
                    mCloud.sendRequests();
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
}
