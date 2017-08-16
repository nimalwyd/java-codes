package com.anaiglobal.valetroid.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudUtils;
import com.anaiglobal.valetroid.R;
import com.anaiglobal.valetroid.ValetApp;
import com.anaiglobal.valetroid.support.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by olshansky on 1/26/2014.
 */
public class QuickInfoFragment extends DialogFragment
{
    private final static String TAG = "QUICK_INFO";

    private ValetApp mApp;
    private Cloud mCloud;
    private Activity mActivity;
    private ListView listView;
    private ListAdapter mListAdapter;

    static QuickInfoFragment newInstance(Bundle args)
    {
        QuickInfoFragment qif = new QuickInfoFragment();
        qif.setArguments(args);
        return qif;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.quick_info_layout, container, false);

        mApp = (ValetApp)(getActivity().getApplication());
        mCloud = mApp.getCloud();

        String lpn = getArguments().getString("LPN");

        getDialog().setTitle("Info : " + lpn);

        listView = (ListView)view.findViewById(R.id.list_view);
        mListAdapter = createAdapter( mCloud.getValetReceiptForLPN(lpn), mCloud.getNumberOfDamages(lpn) );
        listView.setAdapter(mListAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    // See http://stackoverflow.com/questions/2432951/how-to-display-a-two-column-listview-in-android
    private ListAdapter createAdapter(Bundle bundle, int damages)
    {
        final String[] matrix  = { "_id", "name", "value" };
        final String[] columns = { "name", "value" };
        final int[]    layouts = { android.R.id.text1, android.R.id.text2 };
        MatrixCursor cursor = new MatrixCursor(matrix);
        String[] fields = {"Make","Model","Color","Stall", "FName","LName","Phone"};

        //TODO Might need to translate some like LotId, CheckoutUTC
        int id = 0;
        for(String key : fields)
            cursor.addRow(new Object[] {id++, key, CloudUtils.convertFromServer(bundle.getString(key))});
        cursor.addRow(new Object[] {id++, "Damages", Integer.toString(damages)});
        cursor.addRow(new Object[] {id++, "Checkout", CloudUtils.dateToLocal( bundle.getString("CheckoutUTC"))});
        cursor.addRow(new Object[] {id++, "Checkin", CloudUtils.dateToLocal(bundle.getString("CreatedUTC"))});

        return new SimpleCursorAdapter(mActivity, R.layout.valet_base_adapter, cursor, columns, layouts);
    }
}