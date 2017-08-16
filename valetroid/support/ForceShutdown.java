package com.anaiglobal.valetroid.support;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by toreatle on 13-05-17.
 */
public class ForceShutdown extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle.getString("Reason").equals(Constants.LOCATION_PROVIDER_CHANGED))
        {
            Toast.makeText(getApplicationContext(),
                    mBundle.getString("Provider") + " was disabled. Closing Valetroid.",
                    Toast.LENGTH_LONG).show();
        }

        finish();
    }
}