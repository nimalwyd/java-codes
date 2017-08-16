package com.anaiglobal.valetroid;

import android.app.*;
import android.content.*;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.cloud.support.CloudUtils;
import com.anaiglobal.cloud.support.FragmentCallbackInterface;
import com.anaiglobal.valetroid.adapters.ColorSpinnerAdapter;
import com.anaiglobal.valetroid.fragments.PrintPreviewDialogFragment;
import com.anaiglobal.cloud.support.BluetoohService;
import com.anaiglobal.valetroid.support.AddDamage;
import com.anaiglobal.valetroid.support.AddPerson;
import com.anaiglobal.valetroid.support.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ValetRegister extends Activity implements FragmentCallbackInterface
{
    private final String TAG = "ValetRegister";

    private Button mSetReturnTimeButton;
    private Button mPersonButton;
    private Button mDamageButton;
    private Button mVASButton;
    private Button mRegButton;
    private Button mCancelButton;

    private EditText mLPNEditText;
    private EditText mModelEditText;
    private EditText mAmountEditText;
    private EditText mReturnTimeEditText;

    private Spinner mColorSpinner;
    private ColorSpinnerAdapter mColorSpinnerAdapter;

    private Spinner mValetCodeSpinner;
    private Spinner mMakeSpinner;

    private static Dialog mDialog;
    private static boolean orientationChangeWithDialog = false;

    private SimpleAdapter mValetCodeAdapter;
    private ArrayList<HashMap<String, String>> mValetCodeList;

    private ArrayList<String> mMakeList;

    private MatrixCursor mFreqUserCursor;

    private Button enterButton;
    private Button cancelButton;
    private TimePicker timePicker;
    private DatePicker datePicker;

    private GregorianCalendar mDate;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private String mStreet;
    private String mCity;
    private String mPostal;
    private String mProvince;
    private String mCountry;

    private String mAmount;
    private String mMake;
    private String mModel;
    private String mColor;
    private String mLPN;
    private String mReturnTime;
    private String mValetCode;

    private Bundle mDamagesBundle;
    private Bundle mVASBundle;

    ValetApp mApp;
    Cloud mCloud;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Valet Reg created");
        setContentView(R.layout.activity_valet_register);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        registerReceiver(printFailReceiver, new IntentFilter(BluetoohService.BROADCAST_ACTION));

        mSetReturnTimeButton = (Button) findViewById(R.id.SetReturnTimeButton);
        mPersonButton = (Button) findViewById(R.id.PersonButton);
        mDamageButton = (Button) findViewById(R.id.DamageButton);
        mVASButton = (Button) findViewById(R.id.VASButton);
        mCancelButton = (Button) findViewById(R.id.cancelBtn);
        mRegButton = (Button) findViewById(R.id.regValButton);
        mVASButton = (Button) findViewById(R.id.VASButton);
        mLPNEditText = (EditText) findViewById(R.id.LPNEditText);
        mModelEditText = (EditText) findViewById(R.id.ModelEditText);
        mColorSpinner = (Spinner) findViewById(R.id.ColorSpinner);
        mAmountEditText = (EditText) findViewById(R.id.AmountEditText);
        mReturnTimeEditText = (EditText) findViewById(R.id.ReturnTimeEditText);
        mValetCodeSpinner = (Spinner) findViewById(R.id.ValetCodeSpinner);
        mMakeSpinner = (Spinner) findViewById(R.id.MakeSpinner);

        // HANDLE SCREEN ORIENTATION AND DIALOG OPEN SCENARIO
        if (orientationChangeWithDialog) {
            mDialog.show();
        }
        orientationChangeWithDialog = false;

        /* VALET CODE SPINNER */
        MatrixCursor mValetCodeCursor = mCloud.getValetCodes(mApp.getPropertyIdPref());
        // Initialize
        mValetCodeList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < mValetCodeCursor.getCount(); i++) {
            mValetCodeCursor.moveToPosition(i);
            HashMap<String, String> valetCodeMaps = new HashMap<String, String>();
            valetCodeMaps.put("ValetCode", columnValue(mValetCodeCursor, "ValetCode"));
            valetCodeMaps.put("ValetCodeDescription", columnValue(mValetCodeCursor, "ValetCodeDescription"));
            valetCodeMaps.put("Amount", "$" + columnValue(mValetCodeCursor, "Amount"));
            mValetCodeList.add(valetCodeMaps);
        }
        mValetCodeCursor.close();

        mFreqUserCursor = mCloud.getFrequentUsers(null);

        // Populate the spinner with the newly generated patroller map
        mValetCodeAdapter = new SimpleAdapter(ValetRegister.this,
                mValetCodeList,
                R.layout.spinner_item,
                new String[] {"ValetCodeDescription", "Amount"},
                new int[] {R.id.SpinnerTextLeft, R.id.SpinnerTextRight});

        mValetCodeSpinner.setAdapter(mValetCodeAdapter);
        mValetCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                mValetCode = mValetCodeList.get(i).get("ValetCode");
                mAmountEditText.setText( mValetCodeList.get(i).get("Amount") );
            }
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        /* LPN EDIT TEXT */
        mLPNEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                String newLPN = mLPNEditText.getText().toString().toUpperCase();
                if( !hasFocus && !newLPN.equals(mLPN) )
                {
                    mLPN = newLPN;

                    // Init personal and vehicle information
                    mModelEditText.setText("");
                    mColorSpinner.setSelection(0, true);
                    mMakeSpinner.setSelection(0, true);
                    mFirstName = null;
                    mLastName = null;
                    mPhoneNumber = null;
                    mStreet = null;
                    mCity = null;
                    mPostal = null;
                    mProvince = null;
                    mCountry = null;
                    mPersonButton.setText("Add Personal Information");

                    // Find Frequent User
                    int pos = 0;
                    while( mFreqUserCursor.moveToPosition(pos++) )
                    {
                        if(mLPN.equalsIgnoreCase(columnValue(mFreqUserCursor, "LPN")))
                        {
                            mModelEditText.setText( columnValue(mFreqUserCursor, "Model"));
                            int idx = mColorSpinnerAdapter.indexOf(columnValue(mFreqUserCursor, "Color"));
                            mColorSpinner.setSelection(Math.max(idx, 0), true);
                            idx = mMakeList.indexOf(columnValue(mFreqUserCursor, "Make"));
                            mMakeSpinner.setSelection(idx, true);
                            mFirstName = personDataFromCloud(columnValue(mFreqUserCursor, "First"));
                            mLastName = personDataFromCloud(columnValue(mFreqUserCursor, "Last"));
                            mPhoneNumber = personDataFromCloud(columnValue(mFreqUserCursor, "Phone"));
                            mStreet = personDataFromCloud(columnValue(mFreqUserCursor, "Street"));
                            mCity = personDataFromCloud(columnValue(mFreqUserCursor, "City"));
                            mPostal = personDataFromCloud(columnValue(mFreqUserCursor, "Postal"));
                            mProvince = CloudConstants.NO_VALUE; //TODO personDataFromCloud(columnValue(mFreqUserCursor, "Province"));
                            mCountry = personDataFromCloud(columnValue(mFreqUserCursor, "Country"));
                            mPersonButton.setText("Update Personal Information");
                            break;
                        }
                    }
                }
            }
        });

        /* MAKE SPINNER */
        mMakeList = new ArrayList<String>();
        MatrixCursor mMakeCursor = mCloud.getMakes();
        for(int i=0; mMakeCursor.moveToPosition(i); i++)
        {
            mMakeList.add(columnValue(mMakeCursor, "Make"));
        }
        mMakeCursor.close();
        ArrayAdapter<String> mMakeAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mMakeList);
        mMakeSpinner.setAdapter(mMakeAdapter);
        mMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mMake = (String)parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /* COLOR SPINNER*/
        mColorSpinnerAdapter = new ColorSpinnerAdapter(this);
        mColorSpinner.setAdapter(mColorSpinnerAdapter);
        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mColor = (String)parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /* AMOUNT EDIT TEXT */
        mAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    String amount = mAmountEditText.getText().toString().trim();
                    if(amount.startsWith("$"))
                        amount = amount.substring(1);
                    mAmountEditText.setText(String.format("$%.2f", Double.parseDouble(amount)));
                }
            }
        });

        mSetReturnTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mDialog = new Dialog(ValetRegister.this);
                mDialog.setTitle("Estimated Return Time and Date");
                mDialog.setContentView(R.layout.dialog_choose_datetime);

                enterButton = (Button) mDialog.findViewById(R.id.EnterButton);
                cancelButton = (Button) mDialog.findViewById(R.id.CancelButton);
                timePicker = (TimePicker) mDialog.findViewById(R.id.timePicker);
                datePicker = (DatePicker) mDialog.findViewById(R.id.datePicker);

                if(mDate == null)
                    mDate = new GregorianCalendar();

                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(mDate.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(mDate.get(Calendar.MINUTE));

                // don't allow to go back
                datePicker.setMinDate(new GregorianCalendar().getTime().getTime() - 1000);//local current minus 1 second
                datePicker.init(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH), null);

                enterButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        mSetReturnTimeButton.setText("Edit");

                        // get local time
                        mDate = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                        String msg = CloudUtils.dateToLocal(mDate.getTime());
                        mReturnTimeEditText.setText(msg);
                        mDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        mDialog.dismiss();
                    }
                });

                mDialog.show();
            }
        });

        mPersonButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mLPNEditText.getText().toString().length() > 0)
                {
                    Intent intent = new Intent(ValetRegister.this, AddPerson.class);
                    intent.putExtra("First", mFirstName);
                    intent.putExtra("Last", mLastName);
                    intent.putExtra("PhoneNumber", mPhoneNumber);
                    intent.putExtra("Street", mStreet);
                    intent.putExtra("City", mCity);
                    intent.putExtra("Postal", mPostal);
                    intent.putExtra("Province", mProvince);
                    intent.putExtra("Country", mCountry);
                    startActivityForResult(intent, Constants.REQUEST_ADD_PERSON_TO_VALET);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "A license plate must be entered before personal information can be added.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mDamagesBundle = new Bundle();
        mDamagesBundle.putInt("NumDamages", 0);

        mDamageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ValetRegister.this, AddDamage.class);
                intent.putExtras(mDamagesBundle);
                startActivityForResult(intent, Constants.REQUEST_ADD_DAMAGE_TO_VALET);
            }
        });

        mVASBundle = new Bundle();
        mVASBundle.putInt("NumEntries", 0);
        mVASButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Service not implemented"); //TODO
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exiting Valet Register Activity, cancel clicked");
                finish(); //go back to previous activity
            }
         });

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Registerting Valet...");

                mAmount = mAmountEditText.getText().toString().replace("$", "");
                mModel = mModelEditText.getText().toString();
                mLPN = CloudUtils.formatLPN(mLPNEditText.getText().toString());
                mReturnTime = CloudUtils.dateToUTC(mReturnTimeEditText.getText().toString());

                // Check if vehicle is registered already
                MatrixCursor cur  = mCloud.getAllValetTickets(String.format("LPN='%s'", mLPN));
                if( cur.getCount() > 0 )
                {
                    Log.w(TAG, "License plate duplicated");
                    AlertDialog.Builder builder = new AlertDialog.Builder(ValetRegister.this);
                    builder.setTitle("Error");
                    builder.setMessage("Vehicle with this license plate is already registered.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id) { }
                    });
                    builder.create().show();
                    return;
                }

                sendDataToCloud();

                DialogFragment printPreview = PrintPreviewDialogFragment.newInstance(ValetRegister.this, mLPNEditText.getText().toString(), mApp.getPrinterAddressPref());
                printPreview.show(getFragmentManager(), "PrintPreviewDialogFragment");

                mApp.restartIdlingTimer();
            }
        });
    }

    private String columnValue(MatrixCursor mCursor, String column)
    {
        int idx = mCursor.getColumnIndex(column);
        return (idx > -1 ? mCursor.getString(idx) : null);
    }

    // replaces magic "None" by empty string
    private String personDataFromCloud(String arg)
    {
//UC        return (arg != null && arg.equals(CloudConstants.NO_VALUE) ? null : arg);
        return arg;
    }

    // replaces empty string by magic "None"
    private String personDataForCloud(String arg)
    {
//UC        return (arg == null || arg.isEmpty() ? CloudConstants.NO_VALUE : arg);
        return (arg == null || arg.isEmpty() ? null : arg);
    }

    private void sendDataToCloud()
    {
        Calendar currTime = Calendar.getInstance();
        String createTime = CloudUtils.dateToUTC(currTime.getTime());

        String companyId = CloudConstants.NO_VALUE;// TODO set
        String patrollerId = mApp.getPatrollerIdPref();
        String propertyId = mApp.getPropertyIdPref() + "";
        String lotId = mApp.getLotIdPref() + "";
        double[] coords = mApp.getCurrentCoordinates();

        // Reference is unique ID used to link Valet, Persona and Damage data
        // It is comprised of ANDROID_ID and current time UTC in seconds from epoch
        String androidId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
        String reference = String.format("%16s%06x", androidId, Calendar.getInstance().getTimeInMillis() / 1000);

        // Send data to Cloud service (see DatabaseHelper.insertValetTicket for column names)
        Bundle bundle = new Bundle();
        bundle.putString("Reference", reference);
        bundle.putString("CreatedUTC", createTime);
        bundle.putString("Status", "CheckIn");
        bundle.putString("PropertyId", propertyId);
        bundle.putString("LotId", lotId);
        bundle.putString("Lat", Double.isNaN(coords[0]) ? CloudConstants.NO_VALUE : Double.toString(coords[0]));
        bundle.putString("Lon", Double.isNaN(coords[1]) ? CloudConstants.NO_VALUE : Double.toString(coords[1]));
        bundle.putString("Code", "1");//UC
        bundle.putString("Description", "Test");
        bundle.putString("Amount", personDataForCloud(mAmount));
        bundle.putString("LPN", mLPN);
        bundle.putString("CheckoutUTC", mReturnTime);
        bundle.putString("Make", personDataForCloud(mMake));
        bundle.putString("Model", personDataForCloud(mModel));
        bundle.putString("Color", personDataForCloud(mColor));
        bundle.putString("CompanyId", personDataForCloud(companyId));//UC
        bundle.putString("ValetCode", personDataForCloud(mValetCode));
        bundle.putString("PatrollerId", patrollerId);
        bundle.putString("FName", personDataForCloud(mFirstName));
        bundle.putString("LName", personDataForCloud(mLastName));
        bundle.putString("Phone", personDataForCloud(mPhoneNumber));
        bundle.putString("Street", personDataForCloud(mStreet));
        bundle.putString("City", personDataForCloud(mCity));
        bundle.putString("Postal", personDataForCloud(mPostal));
        bundle.putString("Province", personDataForCloud(mProvince));
        bundle.putString("Country", personDataForCloud(mCountry));
        Log.d(TAG," -- Valet --");
        for(String key : bundle.keySet())
            Log.d(TAG, key + " : " + bundle.get(key));

        mCloud.enableRequests();//start transaction

        mCloud.insertValetTicket(bundle);
        // Schedule server notification
        mCloud.sendValetCheckIn();

        int nDamages = mDamagesBundle.getInt("NumDamages", 0);
        if(nDamages > 0)
        {
            for(int i=0; i < nDamages; i++)
            {
                String place = mDamagesBundle.getString(CloudConstants.LOCATION_KEY + i);
                String damage = mDamagesBundle.getString(CloudConstants.DAMAGE_KEY + i);
                String imgPath = mDamagesBundle.getString(CloudConstants.PHOTO_KEY + i);
                mCloud.insertValetDamage(mLPN, place, damage, imgPath);
            }
            // Schedule server notification
            mCloud.sendValetDamages(mLPN);
        }

        int nServices = mVASBundle.getInt("NumEntries");
        if(nServices > 0)
        {
            for(int i=0; i < nServices; i++)
            {
                String offer = mVASBundle.getString(CloudConstants.OFFER_KEY + i);
                String choice = mVASBundle.getString(CloudConstants.CHOICE_KEY + i);
                String amount = mVASBundle.getString(CloudConstants.AMOUNT_KEY + i);
                mCloud.insertServiceOffer(mLPN, offer, choice, amount);
            }
            // Schedule server notification
            mCloud.sendValetVAS(mLPN);
        }

        mCloud.sendRequests();//end transaction
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unregisterReceiver(printFailReceiver);

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                orientationChangeWithDialog = true;
                mDialog.dismiss();
            }
        }
    }

    private BroadcastReceiver printFailReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Print failed, please check your printer settings", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_ADD_PERSON_TO_VALET)
        {
            if (data != null && data.hasExtra("First"))
            {
                mFirstName = data.getStringExtra("First");
                mLastName = data.getStringExtra("Last");
                mPhoneNumber = data.getStringExtra("PhoneNumber");
                mStreet = data.getStringExtra("Street");
                mCity = data.getStringExtra("City");
                mPostal = data.getStringExtra("Postal");
                mProvince = data.getStringExtra("Province");
                mCountry = data.getStringExtra("Country");

                mPersonButton.setText("Update Personal Information");
            }
        }
        else if (requestCode == Constants.REQUEST_ADD_DAMAGE_TO_VALET && resultCode == RESULT_OK)
        {
            mDamagesBundle = data.getExtras();
        }

        if (resultCode == Constants.RESULT_LOGOUT)
        {
            finish();
        }
    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, Bundle data)
    {
        if (requestCode == Constants.REQUEST_PREVIEW_VALET)
        {
            finish(); //close AFTER print preview
        }
    }
}