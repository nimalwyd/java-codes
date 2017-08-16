package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;

public class CloudInfo extends Activity
{
    private static final String TAG = "CloudInfo";
    private final int HASH_LENGTH = 10;

    private EditText mDomainName;
    private EditText mPortNumber;
    private EditText mConnectionInterval;
    private EditText mFirstHashEditText;
    private EditText mSecondHashEditText;
    private EditText mThirdHashEditText;
    private EditText mFourthHashEditText;
    private Button mSaveButton;

    private Cloud mCloud;
    private String cDomainName;
    private String cPortNumber;
    private String cConnectionInterval;
    private String mAgentId;

    private static AlertDialog mDialog;
    private static boolean orientationChangeWithDialog = false;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    private ValetApp mApp;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_info);

        mDomainName = (EditText) findViewById(R.id.DomainName);
        mPortNumber = (EditText) findViewById(R.id.PortNumber);
        mConnectionInterval = (EditText) findViewById(R.id.ConnectionInterval);
        mFirstHashEditText = (EditText) findViewById(R.id.FirstHash);
        mSecondHashEditText = (EditText) findViewById(R.id.SecondHash);
        mThirdHashEditText = (EditText) findViewById(R.id.ThirdHash);
        mFourthHashEditText = (EditText) findViewById(R.id.FourthHash);
        mSaveButton = (Button) findViewById(R.id.SaveButton);

        mApp = (ValetApp)getApplication();

        // Handle orientation change when dialog was open
        if (orientationChangeWithDialog) {
            mDialog.show();
        }
        orientationChangeWithDialog = false;

        mCloud = mApp.getCloud();
        cDomainName = mCloud.getCloudDomain();
        cPortNumber = Integer.toString(mCloud.getCloudPortNumber());
        cConnectionInterval = Integer.toString(mCloud.getCloudConnectionInterval());
        mAgentId = mCloud.getActiveAgentId();

        // Fill with existing values if it previously has been entered one
        if (!cDomainName.equals(CloudConstants.NO_VALUE) && !cPortNumber.isEmpty() && !cConnectionInterval.isEmpty()) {
            mDomainName.setText(cDomainName);
            mPortNumber.setText("" + cPortNumber);
            mConnectionInterval.setText("" + cConnectionInterval);
        }

        // Fill with existing values if it previously has been entered one
        if (!mAgentId.equals(CloudConstants.NO_VALUE)) {
            String [] hasStrings = mAgentId.split("-");
            mFirstHashEditText.setText(hasStrings[0]);
            mSecondHashEditText.setText(hasStrings[1]);
            mThirdHashEditText.setText(hasStrings[2]);
            mFourthHashEditText.setText(hasStrings[3]);
        }

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if something has been entered
                if (!mDomainName.getText().toString().isEmpty() &&
                    !mPortNumber.getText().toString().isEmpty() &&
                    !mConnectionInterval.getText().toString().isEmpty() &&
                    mFirstHashEditText.getText().toString().length() == HASH_LENGTH &&
                    mSecondHashEditText.getText().toString().length() == HASH_LENGTH &&
                    mThirdHashEditText.getText().toString().length() == HASH_LENGTH &&
                    mFourthHashEditText.getText().toString().length() == HASH_LENGTH)
                {
                    Log.d(TAG, "New DomainName: " + mDomainName.getText().toString() + ", Old DomainName: " + cDomainName);
                    Log.d(TAG, "New PortNumber: " + mPortNumber.getText().toString() + ", Old PortNumber: " + cPortNumber);
                    Log.d(TAG, "New ConnectionInterval: " + mConnectionInterval.getText().toString() + ", Old PortNumber: " + cConnectionInterval);

                    // Check if the values entered has changed
                    if (!cDomainName.equals(mDomainName.getText().toString()) ||
                        !cPortNumber.equals(mPortNumber.getText().toString()) ||
                        !cConnectionInterval.equals(mConnectionInterval.getText().toString()) ||
                        !mAgentId.equals(mFirstHashEditText.getText().toString() + "-" +
                                mSecondHashEditText.getText().toString() + "-" +
                                mThirdHashEditText.getText().toString() + "-" +
                                mFourthHashEditText.getText().toString())) {
                        Intent intent = new Intent();
                        intent.putExtra("DomainName", mDomainName.getText().toString());
                        intent.putExtra("PortNumber", mPortNumber.getText().toString());
                        intent.putExtra("ConnectionInterval", mConnectionInterval.getText().toString());
                        intent.putExtra("FirstHash", mFirstHashEditText.getText().toString());
                        intent.putExtra("SecondHash", mSecondHashEditText.getText().toString());
                        intent.putExtra("ThirdHash", mThirdHashEditText.getText().toString());
                        intent.putExtra("FourthHash", mFourthHashEditText.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CloudInfo.this)
                                .setTitle("Error").setMessage("The cloud information has not changed from previously entered information.").setPositiveButton("OK", null);
                        builder.create().show();
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CloudInfo.this)
                            .setTitle("Error").setMessage("Missing or incomplete information.").setPositiveButton("OK", null);
                    builder.create().show();
                }
            }
        });
    }
}