package com.anaiglobal.valetroid;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.*;
import com.anaiglobal.valetroid.fragments.MyProgressDialog;
import com.anaiglobal.valetroid.fragments.PropertiesDialogFragment;
import com.anaiglobal.valetroid.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sasha on 5/2/14.
 */
public class PatrollerLogin extends RestorableActivity implements FragmentCallbackInterface
{
    private static String TAG = "PATROLLER_LOG";

    private Spinner mPatrollerSpinner;
    private Button mLogButton;
    @Restorable
    private EditText mPasswordText;

    private SimpleAdapter mPatrollerAdapter;
    private ArrayList<HashMap<String, String>> mPatrollersList;

    private String mChosenPatroller = CloudConstants.NO_VALUE;
    private String mChosenUsername = CloudConstants.NO_VALUE;
    private String mChosenPassword = CloudConstants.NO_VALUE;

    private DialogFragment mDialog;
    private static boolean showProgressDialog = false;

    private ValetApp mApp;
    private Cloud mCloud;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // ASSUMING AT LEAST ONE PATROLLER IS AVAILABLE IF THE USER GETS TO THIS POINT

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patroller_log);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        mPatrollerSpinner = (Spinner) findViewById(R.id.PatrollerSpinner);
        mLogButton = (Button) findViewById(R.id.LogButton);
        mPasswordText = (EditText) findViewById(R.id.Password);

        if (showProgressDialog) {
            showProgressDialog();
        }

        // Populate the patroller map with values from cloud
        MatrixCursor patrollerCursor = mCloud.getPatrollers(null);
        mPatrollersList = new ArrayList<HashMap<String, String>>();

        if (patrollerCursor != null && patrollerCursor.getCount() > 0)
        {
            patrollerCursor.moveToFirst();
            // Initialize
            Log.d("TAG", "Row count: " + patrollerCursor.getCount());
            mChosenPatroller = patrollerCursor.getString(patrollerCursor.getColumnIndex("Id"));
            mChosenUsername = patrollerCursor.getString(patrollerCursor.getColumnIndex("Username"));
            mChosenPassword = patrollerCursor.getString(patrollerCursor.getColumnIndex("Password"));

            for (int i = 0; i < patrollerCursor.getCount(); i++) {
                patrollerCursor.moveToPosition(i);

                HashMap<String, String> patrollerMaps = new HashMap<String, String>();
                patrollerMaps.put("Id", patrollerCursor.getString(patrollerCursor.getColumnIndex("Id")));
                patrollerMaps.put("Username", patrollerCursor.getString(patrollerCursor.getColumnIndex("Username")));
                patrollerMaps.put("Password", patrollerCursor.getString(patrollerCursor.getColumnIndex("Password")));
                Log.d(TAG, "Adding user to map: " + patrollerMaps.toString());
                mPatrollersList.add(patrollerMaps); //arielf: list of hash maps, not sure if this is ideal but will leave it...
            }

            patrollerCursor.close();
        } else {
            //TODO: Handle this case by showing a textView saying there are no users to display
            Log.e(TAG, "No patrollers to display");
        }

        // Populate the spinner with the newly generated patroller map
        mPatrollerAdapter = new SimpleAdapter(PatrollerLogin.this,
                mPatrollersList,
                R.layout.spinner_item,
                new String[] {"Username"},
                new int[] {R.id.SpinnerTextLeft});

        mPatrollerSpinner.setAdapter(mPatrollerAdapter);
        mPatrollerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                //arielf: looks like the password is being compared to locally, is this secure?
                mChosenPatroller = mPatrollersList.get(arg2).get("Id");
                mChosenUsername = mPatrollersList.get(arg2).get("Username");
                mChosenPassword = mPatrollersList.get(arg2).get("Password");
//TODO                mPasswordText.setText("");
                mPasswordText.setText(mChosenPassword);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        mLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //arielf: logging in...
                Log.d(TAG, "Logging in");
                if (mPasswordText.getText().toString().equals(mChosenPassword))  {
                    NetworkState networkState = new NetworkState();
                    if (networkState.hasConnection(getApplicationContext())) {
                        Log.d(TAG, "Passwords match, making cloud requests");

                        //request required info
                        mCloud.enableRequests();
                        mCloud.requestProperties();
                        mCloud.requestValetVASList();
                        mCloud.requestFrequentUsers();//TODO
                        mCloud.requestMakes();//TODO
                        mCloud.requestValetCodes();//TODO
                        mCloud.requestValetPrintInformation();//TODO
                        mCloud.requestValetArrivals();//TODO
                        mCloud.sendRequests();

                        Log.d(TAG, "Sending username to the cloud");
                        mCloud.sendPatrollerIndicator(mChosenPatroller, Constants.LOGIN);
                        double[] coords = mApp.getCurrentCoordinates();
                        mCloud.setPatrollerLocation(mChosenPatroller, coords[0], coords[1]);
                        mApp.setPatrollerIdPref(mChosenPatroller);

                        showProgressDialog();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Seek cellular/wifi coverage. Must be connected to the Internet in order to log in",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid password for user: " + mChosenPatroller, Toast.LENGTH_LONG).show();
                }
            }
        });

Log.d(TAG, "- Registering listner. REMOVE LATER");
        LocalBroadcastManager.getInstance(this).registerReceiver(mCloudCmdDone, new IntentFilter(Constants.BROADCAST_CLOUD_CMD_DONE));
    }

    @Override
    protected void onDestroy()
    {
Log.d(TAG, "- Unreg onDestroy. REMOVE LATER");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCloudCmdDone);

        super.onDestroy();
    }

    private void showProgressDialog()
    {
        showProgressDialog = true;
        mDialog = MyProgressDialog.newInstance("Logging in", "Downloading settings.\nPlease stand by ...");
        showDialog();
    }

    private void showDialog()
    {
Log.d(TAG, "- Showing dialog " + mDialog.getClass().getSimpleName() + ". REMOVE LATER");
        mDialog.show(getFragmentManager(), "dialog");
    }

    private void dismissDialog()
    {
Log.d(TAG, "- Dimissing dialog. REMOVE LATER");

        if(mDialog != null)
            mDialog.dismiss();
        mDialog= null;
        showProgressDialog = false;
    }

    @Override
    protected void onPause()
    {
        boolean hasProgress = showProgressDialog;
        dismissDialog();
        showProgressDialog = hasProgress;

        super.onPause();
    }

    //broadcast handler for cloud operational events
    private BroadcastReceiver mCloudCmdDone = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            dismissDialog();

            if(intent.getStringExtra("mVerdict").equals(CloudConstants.MISSING_VALUES))
            {
                Log.e(TAG, "MISSING VALUES");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Id", mChosenPatroller);
                resultIntent.putExtra("Username", mChosenUsername);
                PatrollerLogin.this.setResult(Constants.RESULT_NOT_OK, resultIntent);
                PatrollerLogin.this.finish();
            }
            else
            {
                mApp.setPatrollerIdPref(mChosenPatroller);
                mApp.setUsernamePref(mChosenUsername);

                mDialog = PropertiesDialogFragment.newInstance(PatrollerLogin.this, mChosenUsername, mChosenPatroller);
                showDialog();
            }
        }
    };

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, Bundle data)
    {
        if(requestCode == Constants.REQUEST_SHOW_PROPERTIES && resultCode == RESULT_OK)
        {
             startActivity(new Intent(getApplicationContext(), ValetMenu.class)) ;
        }
    }
}
