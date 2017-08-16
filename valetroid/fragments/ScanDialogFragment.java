package com.anaiglobal.valetroid.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.anaiglobal.valetroid.ValetCheckOut;

//this fragment creates a dialog to display scan information
public class ScanDialogFragment extends DialogFragment{

    public static final String TAG = "ScanDialogFragment";
    private static final String BARCODE_ARG = "BarcodeResult";
    private static final String ID_ARG = "Id";
    Activity mActivity;

    /**
     * Create a new instance of ScanDialogFragment, providing "barcode"
     * as an argument. This allows reconstruction of the frag properly
     */
    public static ScanDialogFragment newInstance(String barcode, long receiptId) {
        ScanDialogFragment fragment = new ScanDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BARCODE_ARG, barcode);
        bundle.putLong(ID_ARG, receiptId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Scan Result: " + getArguments().getString(BARCODE_ARG) + "\n\nRequest check out for this Vehicle?")
                .setTitle("Scan Successful");
        // Add the buttons
        builder.setPositiveButton("Check Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //get lpn from bar-code
                String lpn = getArguments().getString(BARCODE_ARG);
                long receiptId = getArguments().getLong(ID_ARG);
                Log.d(TAG, "Checking out from scanner with LPN: " + lpn);

                ((ValetCheckOut)mActivity).deleteValetWithLPN(lpn, receiptId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "Cancel");
            }
        });
        return builder.create();
    }

}
