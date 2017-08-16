package com.anaiglobal.valetroid.support;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.R;
import com.anaiglobal.valetroid.ValetApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddDamage extends Activity
{
    private static final int CAMERA_REQUEST = 1888;
    private static final String TAG = "AddDamage";

    private Bundle mDamagesBundle;

    private Button mNewDamageButton;
    private Button mDamagePhotoButton;
//    private ListView mDamagesListView;
//    private TextView mNoDamagesTextView;
    private EditText mDamage;
    private EditText mDamageLocation;
    private ImageView mDamageImageView;

    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String, String>> mDamagesArrayList;
    private File mImgFile;

    private ValetApp mApp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_damage);

        mApp = (ValetApp)getApplication();
        mImgFile = null;

        mNewDamageButton = (Button)findViewById(R.id.new_damage_button);
//        mDamagesListView = (ListView)findViewById(R.id.damages_list_view);
//        mNoDamagesTextView = (TextView)findViewById(R.id.no_damages_text_view);
        mDamage = (EditText)findViewById(R.id.damage_edit_text);
        mDamageLocation = (EditText)findViewById(R.id.damage_location_edit_text);
        mDamagePhotoButton = (Button)findViewById(R.id.damage_photo_button);
        mDamageImageView = (ImageView)findViewById(R.id.damage_photo);

        mDamagesArrayList = new ArrayList<HashMap<String, String>>();
        mDamagesBundle = getIntent().getExtras();
        addExistingDamages();

//        if (mDamagesArrayList.isEmpty())
//            mDamagesListView.setVisibility(View.GONE);
//        else
//            mNoDamagesTextView.setVisibility(View.GONE);

//        mSimpleAdapter = new SimpleAdapter(AddDamage.this,
//                mDamagesArrayList,
//                R.layout.damage_list_layout,
//                new String[] {DAMAGE_KEY, LOCATION_KEY, PHOTO_KEY},
//                new int[] {R.id.list_damage_textview, R.id.list_damage_location_textview, R.id.list_damage_photo_imageview});
//
//        mSimpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Object data, String textRepresentation) {
//                if(view.getId() == R.id.list_damage_photo_imageview) {
//                    ImageView imageView = (ImageView) view;
////UC                    Drawable drawable = (Drawable) data;
////UC                    imageView.setImageDrawable(drawable);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        mDamagesListView.setAdapter(mSimpleAdapter);

        mDamagePhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImgFile = new File(mApp.getImagesFolder(), (new Date().getTime() / 1000) + ".jpg");
                try
                {
                    mImgFile.createNewFile();
                }
                catch (IOException e) {
                    Log.e(TAG, "Could not create file.", e);
                }
                Uri fileUri = Uri.fromFile(mImgFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        mNewDamageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addNewDamage();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        if(mImgFile != null)
            savedInstanceState.putString("imgFile", mImgFile.getAbsolutePath());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String prevImage = savedInstanceState.getString("imgFile");
        if(prevImage != null)
            mImgFile = new File(prevImage);
    }

    private void addExistingDamages()
    {
        if(mDamagesBundle == null)
            return;

        int nDamages = mDamagesBundle.getInt("NumDamages", 0);
        // unwrap 1D to 2D
        for(int i=0; i < nDamages; i++)
        {
            String damage  = mDamagesBundle.getString(CloudConstants.DAMAGE_KEY + i);
            String location  = mDamagesBundle.getString(CloudConstants.LOCATION_KEY + i);
            String imagePath = mDamagesBundle.getString(CloudConstants.PHOTO_KEY + i);

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put(CloudConstants.DAMAGE_KEY, damage);
            hashMap.put(CloudConstants.LOCATION_KEY, location);
            hashMap.put(CloudConstants.PHOTO_KEY, imagePath);
            mDamagesArrayList.add(hashMap);

            mDamage.setText(damage);
            mDamageLocation.setText(location);
        }
    }

    private void addNewDamage()
    {
        String damage = mDamage.getText().toString();
        String location = mDamageLocation.getText().toString();

//        Drawable photo = null;
//        if (mDamageImageView.getDrawable() != null) {
//            photo = mDamageImageView.getDrawable();
//        }

        if(!damage.isEmpty() || !location.isEmpty() || mImgFile!= null)
        {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put(CloudConstants.DAMAGE_KEY, damage);
            hashMap.put(CloudConstants.LOCATION_KEY, location);
            if(mImgFile != null)
                hashMap.put(CloudConstants.PHOTO_KEY, mImgFile.getAbsolutePath());
            mDamagesArrayList.add(hashMap);

//            mSimpleAdapter.notifyDataSetChanged();
        }
        mImgFile = null;

//        resetDamageInput();
    }

//    private void resetDamageInput()
//    {
//        mDamage.setText("");
//        mDamageLocation.setText("");
//        mDamagePhotoButton.setText(getResources().getString(R.string.add_damage_photo));
//    }

    // process photo capture
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) // && resultCode == RESULT_OK
        {
            mDamagePhotoButton.setText(getResources().getString(R.string.change_damage_photo));

//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            mDamageImageView.setImageBitmap(photo);

//            mImgFile = storeBitmap(photo);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("NumDamages", mDamagesArrayList.size());
        // wrap 2D to 1D
        for(int i=0; i < mDamagesArrayList.size(); i++)
        {
            HashMap<String, String> map = mDamagesArrayList.get(i);
            intent.putExtra(CloudConstants.DAMAGE_KEY + i, map.get(CloudConstants.DAMAGE_KEY));
            intent.putExtra(CloudConstants.LOCATION_KEY + i, map.get(CloudConstants.LOCATION_KEY));
            intent.putExtra(CloudConstants.PHOTO_KEY + i, map.get(CloudConstants.PHOTO_KEY));
        }
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}