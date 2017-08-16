package com.anaiglobal.valetroid.fragments;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;
import com.anaiglobal.valetroid.LocalValetActivity;
import com.anaiglobal.valetroid.ValetApp;

//this fragment creates a dialog to display scan information
public class ValetOptDialogFragment extends DialogFragment
{
    private static final String TAG = "ValetOptDialogFragment";
    private static final String[] OPTS = {"Quick Info","Permanent Check Out","Remove Local Copy","Enter Stall"};
    Activity mActivity;

    /**
     * Append LPN to dialog as param
     */
    public static ValetOptDialogFragment newInstance(String lpn, long id) {
        ValetOptDialogFragment fragment = new ValetOptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("LPN", lpn);
        bundle.putLong("Id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ValetApp app = (ValetApp)getActivity().getApplication();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String lpn = getArguments().getString("LPN");
        final long id = getArguments().getLong("Id");
        builder.setTitle(lpn)
           .setItems(OPTS, new DialogInterface.OnClickListener()
           {
               public void onClick(DialogInterface dialog, int which)
               {
                   Log.d(TAG, "Opt selected: " + which);
                   switch(which)
                   {
                       case 0:
                           showQuickInfo(getArguments());
                           break;
                       case 1:
                           permanentCheckOut(lpn, id);
                           break;
                       case 2:
                           removeLocalCopy(lpn, id);
                           break;
                       case 3:
                           relocateVehicle(getArguments());
                           break;
                       default:
                           //should not happen
                   }
               }
           });
        return builder.create();
    }

    private void permanentCheckOut(final String lpn, final long receiptId)
    {
        new AlertDialog.Builder(mActivity)
            .setMessage("Are you sure you want to check out ticket with LPN " + lpn + "?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((LocalValetActivity)mActivity).checkOutValetWithId(lpn);
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void removeLocalCopy(final String lpn, final long receiptId)
    {
        new AlertDialog.Builder(mActivity)
            .setMessage("Are you sure you want remove information for the ticket with LPN " + lpn + "?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((LocalValetActivity)mActivity).deleteValetWithId(receiptId);
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void showQuickInfo(Bundle args)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);

        DialogFragment qif = QuickInfoFragment.newInstance(args);
        qif.show(ft, "dialog");
    }


    private void relocateVehicle(Bundle args)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);

        RelocateDialogFragment rdf = RelocateDialogFragment.newInstance(args);
        rdf.show(ft, "dialog");
    }
}
