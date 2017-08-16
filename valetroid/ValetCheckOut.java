package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.valetroid.fragments.ScanDialogFragment;

public class ValetCheckOut extends Activity
{

    public static final String TAG = "ValetCheckOut";
    public static final int CAM_RESULT = 0;
    public Button btnScan, btnLocalValet;
    ValetApp mApp;
    Cloud mCloud;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_checkout);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        //scan button
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start scan intent
                Log.d(TAG, "Starting QR scan intent");
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                startActivityForResult(intent,CAM_RESULT);
            }
        });

        //select local valets
        btnLocalValet = (Button) findViewById(R.id.btnSelValet);
        btnLocalValet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocalValetActivity.class);
                startActivity(intent.putExtra("local", false));
            }
        });
    }

    public void deleteValetWithLPN(String lpn, long id) {
        Log.d(TAG, "Removing local valet with ticket LPN " + lpn);
        if (mCloud.isLPNInLocalDataBase(lpn)) {
            //TODO see LocalValetActivity.checkOutValetWithId()
            mCloud.enableRequests();
            mCloud.sendValetCheckOut(lpn);
            mCloud.sendRequests();
            Toast toast = Toast.makeText(this, "Deleted valet with LPN " + lpn, Toast.LENGTH_SHORT);
            toast.show();
            mApp.restartIdlingTimer();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage("Valet with LPN " + lpn + " is not in the database.").setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    //callback from scan dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Activity result called");
        if (requestCode == CAM_RESULT) {
            if (resultCode == RESULT_OK) {
                //String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.d(TAG, "Successful scan: barcode " + contents );
                // Verify that contents is valid
                Bundle receipt = mCloud.getValetReceiptForLPN(contents);
                long id = (receipt.isEmpty() ? -1 : Long.parseLong(receipt.getString("Id")));
                DialogFragment scanSucc = ScanDialogFragment.newInstance(contents, id);
                scanSucc.show(getFragmentManager(), "ScanSuccess");
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.d(TAG, "Scan cancelled");
            }
        }

    }
}
