package com.anaiglobal.valetroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;

public class SplashScreen extends Activity
{
    private Handler mHandler;

    private Cloud mCloud;

    private ValetApp mApp;

    //first runs on start up
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mApp = (ValetApp)getApplication();
        mApp.setPatrollerIdPref(CloudConstants.NO_VALUE);

        // Set the cloud information to what was previously entered and trigger start
        mCloud = mApp.getCloud();
        if(mCloud.start() == CloudConstants.MISSING_SERVER_INFO)
        {
            startActivity( new Intent(getApplicationContext(), Configurations.class) );
        }
	}

    @Override
    protected void onStart()
    {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mOperationalReceiver, new IntentFilter(Constants.BROADCAST_CLOUD_OPERATIONAL));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOperationalReceiver);
    }

    private BroadcastReceiver mOperationalReceiver= new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isOperational = intent.getBooleanExtra("mIsOperational", false);
            if (isOperational)
            {
                // Start the main activity and close/finish the splash screen
                startActivity( new Intent(getApplicationContext(), Valetroid.class) );
                finish();
            }
            else
            {
                String verdict = intent.getStringExtra("mVerdict");
                String msg;
                if (verdict.equals(CloudConstants.SOCKET_CONNECT_FAIL))
                    msg = "Failed at connecting to the Cloud. Double check the settings and please try again.";
                else
                    msg = "The new cloud settings are not valid.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    };
}
