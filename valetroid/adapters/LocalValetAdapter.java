package com.anaiglobal.valetroid.adapters;

import android.content.Context;
import android.database.MatrixCursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anaiglobal.cloud.Cloud;
import com.anaiglobal.valetroid.R;

/**
 * Created by arik on 12/25/13.
 */
public class LocalValetAdapter extends BaseAdapter {

    private Context context;

    private String filter = "";
    private Cloud mCloud;
    private MatrixCursor valets;
    private boolean mLocal;

    public LocalValetAdapter(Context context, Cloud cloud, boolean local)
    {
        this.context = context;
        mCloud = cloud;
        mLocal = local;
        updateData();
    }

    @Override
    public int getCount()
    {
        return valets.getCount();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        valets.moveToPosition(i);
        try {
            return valets.getLong(valets.getColumnIndex("id"));
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //layout inflater used to automaitcally fill in GUI
        View valetView = ( view != null ? view : inflater.inflate(R.layout.valet_base_adapter, null) );//check if view does not exist

        TextView lpnTextView = (TextView) valetView.findViewById(android.R.id.text1);
        valets.moveToPosition(pos);
        String lpn = valets.getString(valets.getColumnIndex("LPN"));
        lpnTextView.setText("LPN: " + lpn);

        return valetView;
    }

    public void updateData()
    {
        String filterOut = (filter == null || filter.isEmpty() ? null : "LPN LIKE '%" + filter + "%'");
        if(mLocal)
            valets = mCloud.getLocalValetTickets(filterOut);
        else
            valets = mCloud.getActiveValetTickets(filterOut);
        notifyDataSetChanged();
    }

    public void updateFilter(String filter)
    {
        this.filter = filter;
        updateData();
    }
}
