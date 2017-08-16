package com.anaiglobal.valetroid.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertView 
{
	public static AlertDialog.Builder create (Context ctxt, String title, String message)
	{
		AlertDialog.Builder myDialog = new AlertDialog.Builder(ctxt);
		myDialog.setTitle(title);
		TextView textView = new TextView(ctxt);
		
		textView.setText (message);
		
		LayoutParams textViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textViewLayoutParams);
        textView.setPadding(10, 10, 10, 10);
        
        LinearLayout layout = new LinearLayout(ctxt);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);
        
        myDialog.setView(layout);
        
        return myDialog;
	}

	// FROM CITIZEN
	
	public static void showError(String message, Context ctxt)
	{
		showDialog("Error", message, ctxt);
	}
	
	public static void showAlert(String message, Context ctxt)
	{
		showDialog("Alert", message, ctxt);
	}
	
	public static void showWarning(String message, Context ctxt)
	{
		showDialog("Warning", message, ctxt);
	}
	
	public static void showAttention (String message, Context ctxt)
	{
		showDialog("Attention", message, ctxt);
	}
	
	private static void showDialog(String title, String message, Context ctxt)
	{
		//Create a builder	
		AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
		builder.setTitle(title);
		builder.setMessage(message);
		//add buttons and listener
		PositiveListener pl = new PositiveListener();
		builder.setPositiveButton("OK", pl);
		//Create the dialog
		AlertDialog ad = builder.create();
		//show
		ad.show();	
	}

    static class PositiveListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
        }
    }
}
