package com.anaiglobal.valetroid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.cloud.support.FragmentCallbackInterface;
import com.anaiglobal.valetroid.R;
import com.anaiglobal.cloud.support.BluetoohService;
import com.anaiglobal.valetroid.support.Constants;

//this fragment creates a dialog to display scan information
public class PrintPreviewDialogFragment extends DialogFragment
{
    private static final String TAG = "PrintPreviewDialogFragment";
    private static final String ARG_LPN = "LPN";

    private FragmentCallbackInterface mCaller;

    public static PrintPreviewDialogFragment newInstance(FragmentCallbackInterface caller, String lpn, String printer) {
        PrintPreviewDialogFragment fragment = new PrintPreviewDialogFragment(caller);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_LPN, lpn);
        bundle.putString(CloudConstants.PRINTER_IP, printer);
        fragment.setArguments(bundle);
        return fragment;
    }

    private PrintPreviewDialogFragment(FragmentCallbackInterface caller)
    {
        mCaller = caller;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Print Preview");
        builder.setView(inflater.inflate(R.layout.dialog_print_preview, null));
        builder.setPositiveButton("Print", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d(TAG, "Starting BT and Print Service");
                Intent btIntent = new Intent(getActivity(), BluetoohService.class);
                btIntent.putExtra(ARG_LPN, getArguments().getString(ARG_LPN));
                btIntent.putExtra(CloudConstants.PRINTER_IP, getArguments().getString(CloudConstants.PRINTER_IP));
                getActivity().startService(btIntent);
            }
        });
        builder.setNegativeButton("Not Now", null);

        return builder.create();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mCaller.onFragmentActivityResult(Constants.REQUEST_PREVIEW_VALET, Activity.RESULT_OK, null);
    }
}
