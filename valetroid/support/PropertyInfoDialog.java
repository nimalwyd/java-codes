package com.anaiglobal.valetroid.support;

import android.app.AlertDialog;
import android.content.Context;
import android.database.MatrixCursor;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anaiglobal.cloud.Cloud;

import java.util.Calendar;

public class PropertyInfoDialog
{
    private Cloud mCloud;
    private AlertDialog.Builder myDialog;

    public AlertDialog.Builder create (Context ctxt, String PropertyId, String LotId)
    {
        Calendar date = Calendar.getInstance();

        String status;
        String close_time_hour;
        String close_time_min;
        String open_time_hour;
        String open_time_min;
        String overnight_start_hour;
        String overnight_start_min;
        String overnight_stop_hour;
        String overnight_stop_min;
        String Street;
        String City;
        String PropertyText;
        String LotText;

        mCloud = new Cloud(ctxt);
        MatrixCursor curProp = mCloud.getProperties(null);
        MatrixCursor curLot = mCloud.getLots(PropertyId);

        for (int i = 0; i < curProp.getCount(); i++)
        {
            curProp.moveToPosition(i);
            if (curProp.getString(curProp.getColumnIndex("id")).equals(PropertyId))
            {
                break;
            }
        }
        for (int i = 0; i < curLot.getCount(); i++)
        {
            curLot.moveToPosition(i);
            if (curLot.getString(curLot.getColumnIndex("id")).equals(LotId))
            {
                break;
            }
        }


        myDialog = new AlertDialog.Builder(ctxt);
        myDialog.setTitle("Property Details");

        PropertyText = curProp.getString(curProp.getColumnIndex("Alias"));
        LotText = curLot.getString(curLot.getColumnIndex("Alias"));

        open_time_hour = curLot.getString(curLot.getColumnIndex("ValidStart")).substring(0, 2);
        open_time_min = curLot.getString(curLot.getColumnIndex("ValidStart")).substring(3, curLot.getString(curLot.getColumnIndex("ValidStart")).length());
        close_time_hour = curLot.getString(curLot.getColumnIndex("ValidStop")).substring(0, 2);
        close_time_min = curLot.getString(curLot.getColumnIndex("ValidStop")).substring(3, curLot.getString(curLot.getColumnIndex("ValidStop")).length());
        overnight_start_hour = curLot.getString(curLot.getColumnIndex("OvernightStart")).substring(0, 2);
        overnight_start_min = curLot.getString(curLot.getColumnIndex("OvernightStart")).substring(3, curLot.getString(curLot.getColumnIndex("OvernightStart")).length());
        overnight_stop_hour = curLot.getString(curLot.getColumnIndex("OvernightStop")).substring(0, 2);
        overnight_stop_min = curLot.getString(curLot.getColumnIndex("OvernightStop")).substring(3, curLot.getString(curLot.getColumnIndex("OvernightStop")).length());

        Street = curProp.getString(curProp.getColumnIndex("Street"));
        City = curProp.getString(curProp.getColumnIndex("City"));

        TextView textView = new TextView(ctxt);

        if (1 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidSunday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" +
                            open_time_hour + ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Sundays\n");
            }
        }
        else if (2 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidMonday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Mondays\n");
            }
        }
        else if (3 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidTuesday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Tuesdays\n");
            }
        }
        else if (4 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidWednesday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Wednesdays\n");
            }
        }
        else if (5 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidThursday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Thursdays\n");
            }
        }
        else if (6 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidFriday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Fridays\n");
            }
        }
        else if (7 == date.get(Calendar.DAY_OF_WEEK))
        {
            if (curLot.getInt(curLot.getColumnIndex("ValidSaturday")) == 1)
            {
                if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(open_time_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(close_time_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(open_time_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(close_time_min)))
                {
                    status = "Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else if ((date.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(overnight_start_hour) ||
                        date.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(overnight_stop_hour)) &&
                        (date.get(Calendar.MINUTE) >= Integer.parseInt(overnight_start_min) ||
                                date.get(Calendar.MINUTE) <= Integer.parseInt(overnight_stop_min)))
                {
                    status = "Overnight Open";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
                else
                {
                    status = "Close";
                    textView.setText (PropertyText + "\n" + Street + ", " + City + "\n" + LotText + "\n\n" + "Open:\t\t\t\t" + open_time_hour +
                            ":" + open_time_min + " to " + close_time_hour + ":" + close_time_min + "\nOvernight:\t\t" +
                            overnight_start_hour + ":" + overnight_start_min + " to " + overnight_stop_hour + ":" + overnight_stop_min +
                            "\n\nStatus: \t\t\t" + status);
                }
            }
            else
            {
                textView.setText (PropertyText + "\n" + Street + ", " + City +  "\n" + LotText + "\n\n" + "Status:\t\tNot open on Saturdays\n");
            }
        }

        LayoutParams textViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textViewLayoutParams);
        textView.setPadding(10, 10, 10, 10);

        curProp.close();
        curLot.close();

        LinearLayout layout = new LinearLayout(ctxt);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);

        ScrollView main = new ScrollView(ctxt);
        main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        main.addView(layout);

        myDialog.setView(main);

        return myDialog;
    }
}
