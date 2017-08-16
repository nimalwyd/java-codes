package com.anaiglobal.valetroid;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.cloud.support.CloudConstants;
import com.anaiglobal.valetroid.adapters.LocalValetAdapter;
import com.anaiglobal.valetroid.fragments.ValetOptDialogFragment;

/**
 * Locally Stored Valets
 */
public class LocalValetActivity extends Activity
{

    public static final String TAG = "LocalValetActivity";
    ValetApp mApp;
    Cloud mCloud;

    // List view
    private ListView listView;

    // Search EditText
    EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_valets);

        mApp = (ValetApp)getApplication();
        mCloud = mApp.getCloud();

        boolean local = getIntent().getBooleanExtra("local", true);

        final LocalValetAdapter adapter = new LocalValetAdapter(getApplicationContext(), mCloud, local);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //TODO: Reduce search results according to search text

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String ticketId = ((TextView) (view.findViewById(android.R.id.text1))).getText().toString().replaceFirst("LPN: ", "");
                DialogFragment valetOptionDialogFragment = ValetOptDialogFragment.newInstance(ticketId, id);
                valetOptionDialogFragment.show(getFragmentManager(), "ValetOptDialogFragment");
            }
        });

        //filter function
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //no implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //filter adapter based on seq provided in search
                //TODO implement LPN filter to valet adapter class
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.updateFilter(inputSearch.getText().toString());
            }
        });
    }

    public void deleteValetWithId(long id)
    {
        Log.d(TAG, "Removing local valet with ticket id " + id);
        mCloud.removeValetTicket(id);
        refreshList();
    }

    public void checkOutValetWithId(String lpn)
    {
        Log.d(TAG, "Removing local valet with LPD " + lpn);
        mCloud.enableRequests();
        mCloud.sendValetCheckOut(lpn);
        mCloud.sendRequests();
        refreshList();
    }

    private void refreshList()
    {
        LocalValetAdapter adapter = (LocalValetAdapter)listView.getAdapter();
        adapter.updateData();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "Req valets from cloud");
        getApplicationContext().registerReceiver(mValetListChanged, new IntentFilter(CloudConstants.BROADCAST_VALETS_CHANGED));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getApplicationContext().unregisterReceiver(mValetListChanged);
    }

    private BroadcastReceiver mValetListChanged = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Valet List changed");
            // boolean changed = intent.getBooleanExtra("CheckOut", false);
            refreshList();
        }
    };
}
