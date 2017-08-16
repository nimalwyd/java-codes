package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;

public class PrinterMenu extends Activity
{
    private RadioButton mCitizenCMP30RadioButton;
    private Button mContinueButton;

    private BluetoothAdapter mBtAdapter;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_menu);

        mCitizenCMP30RadioButton = (RadioButton) findViewById(R.id.CitizenCMP30RadioButton);
        mContinueButton = (Button) findViewById(R.id.ContinueButton);

        mContinueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBtAdapter != null)
                {
                    if (mCitizenCMP30RadioButton.isChecked())
                    {
                        Intent intent = new Intent(PrinterMenu.this, CitizenBluetooth.class);
                        intent.putExtra(CloudConstants.PRINTER_IP, mPrefs.getString("PrinterAddress", "00:00:00:00:00:00"));// see getPrinterAddressPref()
                        startActivityForResult(intent, Constants.REQUEST_CONNECT_PRINTER);
                    }
//					else if () // Other printer types
//					{
//						
//					}
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Your device does not support bluetooth",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
//        if (requestCode == Constants.REQUEST_CONNECT_PRINTER)
//        {
//            if (resultCode == Constants.RESULT_PRINTER_CONNECTED)
//            {
//                setResult(Constants.RESULT_PRINTER_CONNECTED);
//            }
//            else if (resultCode == Constants.RESULT_LOGOUT)
//            {
//                setResult(Constants.RESULT_LOGOUT);
//            }
//        }
        finish();
    }
}
