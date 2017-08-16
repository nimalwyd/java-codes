package com.anaiglobal.valetroid.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.anaiglobal.valetroid.R;

/**
 * Created by sasha on 6/25/14.
 */
public class MyProgressDialog extends DialogFragment
{
    private String mTitle;
    private String mText;
    private TextView mTextBox;

    public static DialogFragment newInstance(String title, String text)
    {
        MyProgressDialog fragment = new MyProgressDialog(title, text);
        return fragment;
    }

    public MyProgressDialog()
    {
    }

    private MyProgressDialog(String title, String text)
    {
        mTitle = title;
        mText = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_progress, container, false);

        if(savedInstanceState != null && mText == null)
        {
            mTitle = savedInstanceState.getString("title");
            mText = savedInstanceState.getString("text");
        }

        getDialog().setTitle(mTitle);
        getDialog().setCancelable(false);

        mTextBox = (TextView) view.findViewById(R.id.text);
        mTextBox.setText(mText);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("title", mTitle);
        outState.putString("text", mText);
        super.onSaveInstanceState(outState);
    }
}