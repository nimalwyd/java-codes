package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anaiglobal.valetroid.support.Constants;


public class Valetroid extends Activity
{
    public static final String TAG = "Valetroid";

    private Button mLoginButton;
    private TextView mStatusOperationalText;
    private TextView mStatusPatrollerText;
    private TextView mStatusPrinterText;

    private int mExitCounter;

    private AlertDialog mAlertDialog;

    ValetApp mApp;
    LocationManager mLocationManager;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valetroid);

        mApp = (ValetApp)getApplication();

        mExitCounter = 1;

        mLoginButton = (Button) findViewById(R.id.LogButton);
        mStatusOperationalText = (TextView) findViewById(R.id.StatusOperational);
        mStatusPatrollerText = (TextView) findViewById(R.id.StatusPatroller);
        mStatusPrinterText = (TextView) findViewById(R.id.StatusPrinter);

        //TODO register local listners
//        registerReceiver(mBtLostConnection, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
//        registerReceiver(mBtSwitchedOffDetection, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        // Make sure GPS is enabled.
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
         && !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            noLocationAlert();
        }

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Valetroid.this, PatrollerLogin.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_PATROLLERLOG);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mStatusPatrollerText.setText("Not logged in");
        mStatusOperationalText.setText("Status: Operational");
        boolean isPrinterConnected = !mApp.getPrinterAddressPref().equals("00:00:00:00:00:00");
        mStatusPrinterText.setText(isPrinterConnected ? "Printer connected" : "Printer not connected");
    }

    @Override
    protected void onDestroy()
    {
//        unregisterReceiver(mBtLostConnection);
//        unregisterReceiver(mBtSwitchedOffDetection);

        if (mAlertDialog != null && mAlertDialog.isShowing())
        {
            mAlertDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (mExitCounter-- > 0)
        {
            Toast.makeText(getApplicationContext(), "Press back once more to close Valetroid.", Toast.LENGTH_LONG).show();
        }
        else
        {
            mApp.logOut();
            finish();
        }
    }

    //
    // Local ASYNC Broadcast events handled here
    //

//    private BroadcastReceiver mBtLostConnection = new BroadcastReceiver()
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//            String mPrinterHWAddress = mApp.getPrinterAddressPref();
//
//            if (device.getAddress().equals(mPrinterHWAddress))
//            {
//                Toast.makeText(getApplicationContext(),
//                        "Printer: " + device.getName() + " [" + device.getAddress() + "] disconnected",
//                        Toast.LENGTH_SHORT).show();
//                mStatusPrinterText.setText("Printer not connected");
//
//                mApp.setPrinterConnectedPref("No");
//                mApp.setPrinterAddressPref("None");
//            }
//        }
//    };
//
//    private BroadcastReceiver mBtSwitchedOffDetection = new BroadcastReceiver()
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF)
//            {
//                mStatusPrinterText.setText("Printer not connected");
//
//                mApp.setPrinterConnectedPref("No");
//                mApp.setPrinterAddressPref("None");
//            }
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_valetroid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent intent = new Intent(Valetroid.this, Configurations.class);
                startActivity(intent);
                break;
            //case R.id.status:
            //    break;
        }

        return true;
    }

    private void noLocationAlert()
    {
        //todo handle this in a nicer manner
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention")
            .setCancelable(false)
            .setMessage("GPS and Network Location services must be enabled.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, Constants.REQUEST_LOCATION_SETTINGS);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    finish();
                }
            });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_CODE_PATROLLERLOG)
        {
            if (resultCode == Constants.RESULT_NOT_OK)
            {
                // AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Valetroid.this);
                builder.setTitle("Attention")
                    .setMessage("Your personal settings has been configured incorrectly. Contact your administrator.")
                    .setPositiveButton("OK", null);
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        }
        else if (requestCode == Constants.REQUEST_LOCATION_SETTINGS)
        {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                Log.d(TAG, "GPS settings still not set, please try again.");
                noLocationAlert();
            }
        }
    }
}