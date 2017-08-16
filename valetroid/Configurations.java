package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.support.Constants;

import java.io.File;

public class Configurations extends Activity
{
    private final static String TAG = "Configurations";

    private Button mCloudButton;
    private Button mImportButton;
    private Button mPrinterButton;

    private static AlertDialog mDialog;
    private static boolean orientationChangeWithDialog = false;

    private String mAgentId = "";
    private String mDomainName = "";
    private String mPortNumber = "0";
    private String mConnectionInterval = "0";
    private String[] mFileList;

    private ValetApp mApp;
    private Cloud mCloud;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations);

        // Handle screen orientation when dialog was open
        if (orientationChangeWithDialog) {
            mDialog.show();
        }
        orientationChangeWithDialog = false;

        mCloudButton = (Button) findViewById(R.id.CloudButton);
        mImportButton = (Button) findViewById(R.id.ImportButton);
        mPrinterButton = (Button) findViewById(R.id.PrinterButton);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        mCloudButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Configurations.this, CloudInfo.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_CLOUDINFO);
            }
        });

        mImportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final File folder = mApp.getConfigurationFolder();
                mFileList = folder.list();

                // Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Configurations.this);
                builder.setTitle("Import From File");
                if (mFileList == null || mFileList.length == 0)
                    builder.setMessage("No files found.");
                builder.setItems(mFileList, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int verdict = mCloud.importSettingsFromFile(folder.getAbsolutePath() + "/" + mFileList[which]);
                        String msg;
                        String title = "Error";
                        switch(verdict)
                        {
                            case CloudConstants.SUCCESS:
                                msg = "New settings detected. Communicating with cloud.";
                                title = "Success";
                                mApp.setPatrollerIdPref(CloudConstants.NO_VALUE);
                                mCloud.start();
                                break;
                            case CloudConstants.SAME_SERVER_SETTINGS:
                                msg = "The cloud information has not changed from previously entered information.";
                                break;
                            case CloudConstants.FILE_WRONG_FORMAT:
                                msg = "File not readable to Valetroid.";
                                break;
                            case CloudConstants.FAILED_READING_FROM_FILE:
                                msg = "Failed at reading from the file.";
                                break;
                            default:
                                msg = "Internal error - Unknown return from importSettingsFromFile";
                                break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(Configurations.this)
                                .setTitle(title).setMessage(msg).setPositiveButton("OK", null);
                        builder.create().show();
                    }
                });
                builder.setPositiveButton("Cancel", null);
                mDialog = builder.create();
                mDialog.show();
            }
        });

        mPrinterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Configurations.this, PrinterMenu.class);
                startActivityForResult(intent, Constants.REQUEST_PRINT_SETUP);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                orientationChangeWithDialog = true;
                mDialog.dismiss();
            }
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_CODE_CLOUDINFO) {
            if (resultCode == RESULT_OK) {
                mApp.setPatrollerIdPref(CloudConstants.NO_VALUE);
                mDomainName = data.getStringExtra("DomainName");
                mConnectionInterval = data.getStringExtra("ConnectionInterval");
                mPortNumber = data.getStringExtra("PortNumber");
                mAgentId = data.getStringExtra("FirstHash") + "-" + data.getStringExtra("SecondHash") +
                        "-" + data.getStringExtra("ThirdHash") + "-" + data.getStringExtra("FourthHash");

                Log.d(TAG, "New agent Id: " + mAgentId);

                if (!mAgentId.equals("") && !mDomainName.equals("") && !mPortNumber.equals("0")) {
                    mCloud.setCloudInfo(mAgentId, mDomainName, Integer.parseInt(mPortNumber));
                    mCloud.setCloudConnectionInterval(Integer.parseInt(mConnectionInterval ));
                    mCloud.start();

                    mApp.startValetArrivalsTimer();

                    Toast.makeText(getApplicationContext(), "New settings detected. Communicating with cloud.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}