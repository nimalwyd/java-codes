package com.anaiglobal.valetroid;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.anaiglobal.cloud.Cloud;

/**
 * Created by olshansky on 1/5/2014.
 */
public class ValetSearch extends Activity
{
    private ValetApp mApp;
    private Cloud mCloud;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        //TODO put real list as in Local Activity
//        Cursor cur = mCloud.getValetArrivals();
//        Log.d("VAL_ARR", "-- Valet Arrivals --");
//        int pos = 0;
//        while(cur.moveToPosition(pos++))
//        {
//            StringBuffer vals = new StringBuffer();
//            for(int i=0; i < cur.getColumnCount(); i++)
//                vals.append(cur.getColumnName(i)).append(" = ").append(cur.getString(i)).append("; ");
//            Log.d("VAL_ARR", pos + " : " + vals.toString());
//        }
    }
}