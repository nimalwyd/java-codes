package com.anaiglobal.valetroid.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.events.CloudEvents;
import com.anaiglobal.cloud.events.CloudIsCommandDoneEventClass;
import com.anaiglobal.cloud.events.CloudIsCommandDoneListener;
import com.anaiglobal.cloud.events.CloudIsOperationalEventClass;
import com.anaiglobal.cloud.events.CloudIsOperationalListener;
import com.anaiglobal.valetroid.R;

public class AddPerson extends Activity
{
    private Button mCancelButton;
    private Button mUpdateInfoButton;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mStreetEditText;
    private EditText mCityEditText;
    private EditText mPostalEditText;
    private EditText mProvinceEditText;
    private EditText mCountryEditText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mCancelButton = (Button) findViewById(R.id.CancelButton);
        mUpdateInfoButton = (Button) findViewById(R.id.UpdateInfoButton);
        mFirstNameEditText = (EditText) findViewById(R.id.FirstName);
        mLastNameEditText = (EditText) findViewById(R.id.LastName);
        mPhoneNumberEditText = (EditText) findViewById(R.id.PhoneNumber);
        mStreetEditText = (EditText) findViewById(R.id.Street);
        mCityEditText = (EditText) findViewById(R.id.City);
        mPostalEditText = (EditText) findViewById(R.id.Postal);
        mProvinceEditText = (EditText) findViewById(R.id.Province);
        mCountryEditText = (EditText) findViewById(R.id.Country);

        Bundle bundle = getIntent().getExtras();
        mFirstNameEditText.setText(bundle.getString("First"));
        mLastNameEditText.setText(bundle.getString("Last"));
        mPhoneNumberEditText.setText(bundle.getString("PhoneNumber"));
        mStreetEditText.setText(bundle.getString("Street"));
        mCityEditText.setText(bundle.getString("City"));
        mPostalEditText.setText(bundle.getString("Postal"));
        mProvinceEditText.setText(bundle.getString("Province"));
        mCountryEditText.setText(bundle.getString("Country"));

        mUpdateInfoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setResult(RESULT_OK, createReturnIntent());
                finish();

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED, createReturnIntent());
                finish();
            }
        });
    }

    private Intent createReturnIntent()
    {
        Intent intent = new Intent();
        intent.putExtra("First", mFirstNameEditText.getText().toString());
        intent.putExtra("Last", mLastNameEditText.getText().toString());
        intent.putExtra("PhoneNumber", mPhoneNumberEditText.getText().toString());
        intent.putExtra("Street", mStreetEditText.getText().toString());
        intent.putExtra("City", mCityEditText.getText().toString());
        intent.putExtra("Postal", mPostalEditText.getText().toString());
        intent.putExtra("Province", mProvinceEditText.getText().toString());
        intent.putExtra("Country", mCountryEditText.getText().toString());
        return intent;
    }
}