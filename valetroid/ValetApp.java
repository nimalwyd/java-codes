package com.anaiglobal.valetroid;

import android.app.Application;
import android.content.*;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import android.widget.Toast;
import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.events.CloudEvents;
import com.anaiglobal.cloud.events.CloudIsCommandDoneEventClass;
import com.anaiglobal.cloud.events.CloudIsCommandDoneListener;
import com.anaiglobal.cloud.events.CloudIsOperationalEventClass;
import com.anaiglobal.cloud.events.CloudIsOperationalListener;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;
import com.anaiglobal.valetroid.support.ValetroidSessionAlarm;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Application class, holds database access object and cloud access to classes
 */
public class ValetApp extends Application {

    public static final String TAG = "ValetApp";
    private SharedPreferences mPrefs;

    private Timer valetArrivalsTimer;
    private ValetroidSessionAlarm alarm;

    //arielf: moved cloud to here to create singleton
    private Cloud mCloud;
    private CloudEvents mCloudEvents;

    @Override
    public void onCreate() {
        super.onCreate();

        //single instance of cloud objects declared here
        Log.d(TAG, "Creating cloud object and enabling listeners");
        mCloud = new Cloud(getApplicationContext());
        mCloud.init(CloudConstants.PRODUCT_VALETROID, false);
        mCloudEvents = new CloudEvents();
        mCloudEvents.addCloudIsOperationalListener(mOperationalListener);
        mCloudEvents.addCloudIsCommandDoneListener(mCommandListener);
        mCloud.registerCloudEventListeners(mCloudEvents);

        //create shared pref manager
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Create required folders for "import from file" and damage images
        File filepath = new File(Environment.getExternalStorageDirectory() + "/Valetroid/Import/");
        filepath.mkdirs();
        filepath = new File(Environment.getExternalStorageDirectory() + "/Valetroid/Images/");
        filepath.mkdirs();

        alarm = new ValetroidSessionAlarm();
        alarm.register(this);

        Log.i(TAG, "Application started, database initilized");
    }

    @Override
    public void onTerminate()
    {
        //stop service when application is destroyed
        mCloud.stopService();

        alarm.unregister(this);

        super.onTerminate();
    }

    // callback
    public void logOut()
    {
        Log.d(TAG, "Logging out");
        Toast.makeText(getApplicationContext(), "Restarting application.", Toast.LENGTH_LONG).show();

        setPatrollerIdPref(CloudConstants.NO_VALUE);
        setUsernamePref(CloudConstants.NO_VALUE);
        alarm.cancelAlarm(this, Constants.IDLE_TOO_LONG);
        alarm.cancelAlarm(this, Constants.SESSION_TOO_LONG);

        sendLocalBroadcast(new Intent(Constants.BROADCAST_SESSION_CLOSED));

        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
        startActivity( intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) );
    }

    // callbacks
    public void onIdleTooLong()
    {
        Log.d(TAG, "Sending Patroller Location to Server");
        mCloud.sendPatrollerIndicator(getPatrollerIdPref(), Constants.IDLE);
    }

    public void startTimers()
    {
        restartIdlingTimer();
        restartSessionTimer(null);
        startValetArrivalsTimer();
    }

    public void restartIdlingTimer()
    {
        Log.d(TAG, "Restarting Idling timer");
        alarm.cancelAlarm(this, Constants.IDLE_TOO_LONG);
        String patrollerID = getPatrollerIdPref();
        long interval = mCloud.getIdleTimeMinutes(patrollerID) * Constants.MINUTE;
        if(interval > 0)
            alarm.setAlarm(this, interval, Constants.IDLE_TOO_LONG);
    }

    public void restartSessionTimer(Long interval)
    {
        if(interval == null)
            interval = mCloud.getSessionTimeMinutes(getPatrollerIdPref()) * Constants.MINUTE;
        else
            Toast.makeText(getApplicationContext(), "Your session has been extended.", Toast.LENGTH_LONG).show();
        alarm.cancelAlarm(this, Constants.SESSION_TOO_LONG);
        if(interval > 0)
            alarm.setAlarm(this, interval, Constants.SESSION_TOO_LONG);
    }

    public void startValetArrivalsTimer()
    {
        Log.d(TAG, "Restarting arrivals task");
        if(valetArrivalsTimer != null)
        {
            valetArrivalsTimer.cancel();
            valetArrivalsTimer.purge();
            valetArrivalsTimer = null;
        }
        long period = getConnectionInterval() * Constants.SECOND;
        if(period > 0)
        {
            valetArrivalsTimer = new Timer("Arrivals");
            valetArrivalsTimer.schedule(new ValetArrivalsTask(), period, period);
        }
    }

    public double[] getCurrentCoordinates()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        Location loc = locationManager.getLastKnownLocation(provider);
        if(loc != null)
            return new double[] {loc.getLatitude(), loc.getLongitude()};
        else
            return new double[]{Double.NaN, Double.NaN};
    }

    public File getConfigurationFolder()
    {
        return new File(Environment.getExternalStorageDirectory() + "/Valetroid/Import/");
    }

    public File getImagesFolder()
    {
        return new File(Environment.getExternalStorageDirectory() + "/Valetroid/Images/");
    }

    //todo: for test only
//    public void printTest(){
//        try{
//            CPCLHelper btPrinter = new CPCLHelper();
//            Log.d(TAG, "Test print");
//            btPrinter.multiLineTest(1);
//        }catch(Exception e)
//        {
//            Log.d(TAG, "Printer exeption thrown, " + e);
//        }
//    }
//
//    public void printLPN(String lpn){
//        if(mBluetoothPort.isConnected()){
//            try{
//                CPCLHelper btPrinter = new CPCLHelper();
//                Log.d(TAG, "Printing LPN " + lpn);
//                btPrinter.printLPN(lpn);
//            }catch(Exception e){
//                Log.d(TAG, "Printer exeption thrown, " + e);
//            }
//        }
//        else{
//            Log.d(TAG, "BT is not connected");
//        }
//    }

    public Cloud getCloud(){ return mCloud; }

    //cloud listeners to android local broadcasts
    private CloudIsOperationalListener mOperationalListener = new CloudIsOperationalListener()
    {
        @Override
        public void cloudIsOperationalEvent(CloudIsOperationalEventClass arg0)
        {
            Intent intent = new Intent(Constants.BROADCAST_CLOUD_OPERATIONAL);
            intent.putExtra("mIsOperational", arg0.isOperational());
            intent.putExtra("mVerdict", arg0.getVerdict());
            sendLocalBroadcast(intent);
        }
    };

    private CloudIsCommandDoneListener mCommandListener = new CloudIsCommandDoneListener()
    {
        @Override
        public void cloudIsCommandDoneEvent(CloudIsCommandDoneEventClass arg0)
        {
            Log.d(TAG, "Cloud CMD broadcast");
            //broadcast
            Intent intent = new Intent(Constants.BROADCAST_CLOUD_CMD_DONE);
            intent.putExtra("mIsCommandDone", arg0.isCommandDone());
            intent.putExtra("mVerdict", arg0.getVerdict());
            sendLocalBroadcast(intent);
        }
    };

    public void sendLocalBroadcast(Intent intent){
        boolean ret = LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Log.d(TAG, "Local broadcast: " + intent.toString() + " , sent = " + ret);
    }

    // This one doesn't need to run in deep sleep (so it's not alarm)
    private class ValetArrivalsTask extends TimerTask
    {
        @Override
        public void run()
        {
            Log.d(TAG, "Requesting new arrivals");
            mCloud.enableRequests();
            mCloud.requestValetArrivals();
            mCloud.sendRequests();
        }
    }

    //
    // Persistent data
    //

    public void setPatrollerIdPref(String patrollerID){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("PatrollerId", patrollerID);
        editor.commit();
    }

    public String getPatrollerIdPref(){
        String patrollerID = mPrefs.getString("PatrollerId", CloudConstants.NO_VALUE);
        return patrollerID;
    }

    public void setPropertyIdPref(String propertyId){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("PropertyId", propertyId);
        editor.commit();
    }

    public String getPropertyIdPref(){
        return mPrefs.getString("PropertyId", CloudConstants.NO_VALUE);
    }

    public void setPropertyAliasPref(String propertyAlias) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("PropertyAlias", propertyAlias);
        editor.commit();
    }

    public String getPropertyAliasPref() {
        String propertyAlias = mPrefs.getString("PropertyAlias", CloudConstants.NO_VALUE);
        return propertyAlias;
    }

    public void setLotAliasPref(String lotAlias){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("LotAlias", lotAlias);
        editor.commit();
    }

    public String getLotAliasPref() {
        return mPrefs.getString("LotAlias", CloudConstants.NO_VALUE);
    }

    public void setLotIdPref(String lotId){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("LotId", lotId);
        editor.commit();
    }

    public String getLotIdPref() {
        return mPrefs.getString("LotId", CloudConstants.NO_VALUE);
    }

    public void setPrinterAddressPref(String printerAddress){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("PrinterAddress", printerAddress);
        editor.commit();
    }

    public String getPrinterAddressPref() {
        return mPrefs.getString("PrinterAddress", "00:00:00:00:00:00");
    }

    public void setUsernamePref(String username){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("Username", username);
        editor.commit();
    }

    public int getConnectionInterval()
    {
        return mPrefs.getInt("ConnectionInterval", 300);
    }

    public String getUsernamePref(){
        String username = mPrefs.getString("Username", CloudConstants.NO_VALUE);
        return username;
    }

    //clear all preferences (on logouts)
    public void clearPrefs(){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }
}
