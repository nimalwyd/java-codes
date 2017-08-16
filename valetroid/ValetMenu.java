package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;

//TODO: depending on the patroller, need to disable certain buttons (page 12 of manual)
public class ValetMenu extends Activity
{
    public static final String TAG = "ValetMenu";

    private Button mCheckInButton;
    private Button mCheckOutButton;
    private Button mReviewButton;
    private Button mValetSearchButton;
    private Button mLogOutButton;
    private Button mPrinterSettingsButton;

    private ValetApp mApp;
    private Cloud mCloud;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_menu);

        mCheckInButton = (Button) findViewById(R.id.ValetCheckInButton);
        mCheckOutButton = (Button) findViewById(R.id.ValetCheckOutButton);
        mReviewButton = (Button) findViewById(R.id.ValetReviewButton);
        mValetSearchButton = (Button) findViewById(R.id.ValetSearchButton);
        mPrinterSettingsButton = (Button) findViewById(R.id.PrintSetButton);
        mLogOutButton = (Button) findViewById(R.id.LogOutButton);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), ValetRegister.class));
            }
        });

        mCheckOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), ValetCheckOut.class));
            }
        });

        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocalValetActivity.class);
                startActivity(intent.putExtra("local", true));
            }
        });

        mValetSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ValetSearch.class));
            }
        });

        mPrinterSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PrinterMenu.class));
            }
        });

        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ValetMenu.this);
                builder.setTitle("Log Out");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.d(TAG, "Logging out");
                        mCloud.sendPatrollerIndicator(mApp.getPatrollerIdPref(), Constants.LOGOUT);
                        mApp.setPatrollerIdPref(CloudConstants.NO_VALUE);
                        mApp.setUsernamePref(CloudConstants.NO_VALUE);
                        ValetMenu.super.onBackPressed();
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Cannot go back from here, need to logout instead");
    }
}