package com.anaiglobal.valetroid.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.anaiglobal.valetroid.R;

import java.util.Arrays;

/**
 * Created by sasha on 3/24/14.
 */
public class ColorSpinnerAdapter extends BaseAdapter implements SpinnerAdapter
{
    private final static String[] COLOR_NAMES ={"unknown", "red", "green", "yellow", "blue", "pink", "white", "black", "brown", "silver"};
    private final static int[] COLORS ={Color.DKGRAY, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, 0xFFFFCBCB, Color.WHITE, Color.BLACK, 0xFF964B00, Color.LTGRAY};

    private Context mContext;

    public ColorSpinnerAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount()
    {
        return COLOR_NAMES.length;
    }

    @Override
    public Object getItem(int i)
    {
        return COLOR_NAMES[i];
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //layout inflater used to automaitcally fill in GUI
            view = inflater.inflate(R.layout.color_spinner_item, null);
        }

        TextView tv = (TextView) view.findViewById(android.R.id.text1); //R.id.SpinnerTextLeft
        tv.setText(COLOR_NAMES[i]);

        tv.setTextColor(COLORS[i]);
        if(i != 0) // make text "invisible"
            tv.setBackgroundColor(COLORS[i]);

        return view;
    }

    public int indexOf(String color)
    {
        return Arrays.asList(COLOR_NAMES).indexOf(color.toLowerCase());
    }
}
