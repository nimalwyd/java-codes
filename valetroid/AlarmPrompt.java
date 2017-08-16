package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import com.anaiglobal.valetroid.support.Constants;
import com.anaiglobal.cloud.support.PowerManagerHelper;

/**
 * Created by sasha on 3/17/14.
 * Former IdleTimeoutDialogFragment
 */
public class AlarmPrompt extends Activity
{
    private ValetApp mApp;
    private AlertDialog mDialog;
    private PowerManagerHelper mPmh;

    // No onCreate with setContentView !

    @Override
    protected void onStart()
    {
        super.onStart();

        mApp = (ValetApp)getApplication();

        mPmh = new PowerManagerHelper(this);
        mPmh.acquireFullLock(60000);

        displayAlert();
    }

    @Override
    protected void onStop()
    {
        //No mPmh.releaseFullLock();

        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();

        super.onStop();
    }

    private void displayAlert()
    {
        Bundle params = getIntent().getExtras();
        String title = params.getString("Title");
        String message = params.getString("Message");
        final boolean idlingTimeout = params.getBoolean("IdlingTimeout");
        String yesText = params.getString("YesText");
        if(yesText == null)
            yesText = "Yes";

        if( idlingTimeout )
        {
            mApp.onIdleTooLong();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Dialog)); //Theme_Holo_Light_Dialog Theme_DeviceDefault_Light_Dialog
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                runCallback(idlingTimeout);
                finish();
            }
        });
        if( !idlingTimeout )
        {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    runCallback(true);
                    finish();
                }
            });
        }
        mDialog = builder.create();
        mDialog.show();
    }

    private void runCallback(boolean logout)
    {
        if (logout)
            mApp.logOut();
        else
            mApp.restartSessionTimer(Constants.HOUR);
    }
}