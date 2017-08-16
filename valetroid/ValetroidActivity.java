package com.anaiglobal.valetroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.anaiglobal.valetroid.support.Constants;

/**
 * Created by sasha on 3/14/14.
 */
public class ValetroidActivity extends Activity
{
    private final static String TAG = "VALETROID";

    @Override
    public void onStart()
    {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mSessionEndReceiver, new IntentFilter(Constants.BROADCAST_SESSION_CLOSED));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mSessionEndReceiver);
    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
////        partialWakeLock.acquire();
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(fullWakeLock.isHeld()){
//            fullWakeLock.release();
//        }
//        if(partialWakeLock.isHeld()){
//            partialWakeLock.release();
//        }
//    }

    protected boolean onSessionEnd()
    {
        return true;
    }

    final private BroadcastReceiver mSessionEndReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "SessionEnd received");
            if( onSessionEnd() )
            {
                finish();
            }
        }
    };
}
