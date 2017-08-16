package com.anaiglobal.valetroid.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.FragmentCallbackInterface;
import com.anaiglobal.valetroid.R;
import com.anaiglobal.valetroid.ValetApp;
import com.anaiglobal.valetroid.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;

//this fragment creates a dialog to display scan information
public class PropertiesDialogFragment extends DialogFragment
{
    public static final String TAG = "PropertiesDialogFragment";

    private ValetApp mApp;
    private Cloud mCloud;

    private Button mContinueButton;
    private Button mPropertyInfoButton;
    private Spinner mPropertySpinner;
    private Spinner mLotSpinner;

    private String mPropertyId;
    private String mPropertyAlias;

    private String mLotId;
    private String mLotAlias;

    private String mUsername;
    private String mPatroller;
    private ArrayList<HashMap<String,String>> mLotList;
    private ArrayList<HashMap<String,String>> mPropertyList;
    private int mResult;

    private FragmentCallbackInterface mCaller;

    public static DialogFragment newInstance(FragmentCallbackInterface caller, String user, String userId)
    {
        PropertiesDialogFragment fragment = new PropertiesDialogFragment(caller, user, userId);
        return fragment;
    }

    private PropertiesDialogFragment(FragmentCallbackInterface caller, String user, String userId)
    {
        mCaller = caller;
        mUsername = user;
        mPatroller = userId;
        mResult = Activity.RESULT_CANCELED;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_choose_property, container, false);

        mApp = (ValetApp)(getActivity().getApplication());
        mCloud = mApp.getCloud();

        getDialog().setTitle("User \"" + mUsername + "\" Verified");

        mContinueButton = (Button) view.findViewById(R.id.ContinueButton);
        mPropertyInfoButton = (Button) view.findViewById(R.id.PropertyInfoButton);
        mPropertySpinner = (Spinner) view.findViewById(R.id.PropertySpinner);
        mLotSpinner = (Spinner) view.findViewById(R.id.LotSpinner);

        MatrixCursor mCur = mCloud.getProperties(null);
        if (mCur == null || mCur.getCount() == 0)
        {
            Toast.makeText(mApp, "No properties available for user " + mUsername, Toast.LENGTH_LONG).show();
            return null; //TODO is it right?
        }

        mPropertyList = getIdAliasArrayListFromMatrixCursor(mCur);
        mPropertyId = mPropertyList.get(0).get("id");

        SimpleAdapter propertySpinnerAdapter = getIdAliasSimpleAdapter(mPropertyList);

        mPropertySpinner.setAdapter(propertySpinnerAdapter);
        mPropertySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long row) {
                onPropertySelected(row);
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        mLotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long row) {
                onLotSelected(row);
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        // Implement info button
        mPropertyInfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onInfo();
            }
        });

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mResult = Activity.RESULT_OK;
                setData();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mCaller != null)
            mCaller.onFragmentActivityResult(Constants.REQUEST_SHOW_PROPERTIES, mResult, null);
    }

    private void setData()
    {
        mApp.setPropertyIdPref(mPropertyId);
        mApp.setPropertyAliasPref(mPropertyAlias);
        mApp.setLotIdPref(mLotId);
        mApp.setLotAliasPref(mLotAlias);

        mApp.startTimers();
    }

    private ArrayList<HashMap<String, String>> getIdAliasArrayListFromMatrixCursor (MatrixCursor mCur)
    {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        int idIndex = mCur.getColumnIndex("id");
        int aliasIndex = mCur.getColumnIndex("Alias");

        for (int i = 0; i < mCur.getCount(); i++)
        {
            mCur.moveToPosition(i);

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("id", mCur.getString(idIndex));
            hashMap.put("Alias", mCur.getString(aliasIndex));

            list.add(hashMap);
        }

        return list;
    }

    private SimpleAdapter getIdAliasSimpleAdapter(ArrayList<HashMap<String, String>> list)
    {
        return new SimpleAdapter(getActivity(),
                list,
                R.layout.spinner_item,
                new String[] {"Alias", "id"},
                new int[] {R.id.SpinnerTextLeft});
    }

    private void onPropertySelected(long row)
    {
        //arielf: listeners within listerns causes memory leakage issues, but ok for now
        mPropertyId = mPropertyList.get((int) row).get("id");
        mPropertyAlias = mPropertyList.get((int) row).get("Alias");

        MatrixCursor mCur = mCloud.getLots(mPropertyId);
        mLotList = getIdAliasArrayListFromMatrixCursor(mCur);
        mLotId = mLotList.get(0).get("id");

        SimpleAdapter lotSpinnerAdapter = getIdAliasSimpleAdapter(mLotList);

        mLotSpinner.setAdapter(lotSpinnerAdapter);
    }

    private void onLotSelected(long row)
    {
        mLotId = mLotList.get((int)row).get("id");
        mLotAlias = mLotList.get((int)row).get("Alias");
    }

    private void onInfo()
    {
//                PropertyInfoDialog mPropertyInfoDialog = new PropertyInfoDialog();
//                AlertDialog.Builder builder = mPropertyInfoDialog.create(PatrollerLogin.this, mChosenPropertyId, mChosenLotId);
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i)
//                    {
//                        mPropertyInfoAlertDialog.dismiss();
//                        mDialog.show();
//                    }
//                });
//                mPropertyInfoAlertDialog = builder.create();
//                mPropertyInfoAlertDialog.show();
    }
}