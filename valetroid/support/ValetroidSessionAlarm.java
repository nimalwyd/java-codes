package com.anaiglobal.valetroid.support;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.anaiglobal.valetroid.AlarmPrompt;
import com.anaiglobal.valetroid.R;

/**
 * Created by sasha on 3/14/14.
 */
public class ValetroidSessionAlarm extends BroadcastReceiver
{
    private static final String TAG = "SESSION_ALARM";
    private static final String BROADCAST_ALARM_SESSION = "com.anaiglobal.valetroid.AlarmSession";
    private static final String BROADCAST_ALARM_IDLE = "com.anaiglobal.valetroid.AlarmIdle";
    private static int mNotificationId;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "== Received message. context=" + context.getClass().getName() );

        Intent promptIntent = new Intent(context, AlarmPrompt.class);
        promptIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent.getAction().equals(BROADCAST_ALARM_IDLE))
        {
            promptIntent.putExtra("Title", "Logging Out");
            promptIntent.putExtra("Message", "System has been IDLE to long, please login again.");
            promptIntent.putExtra("YesText", "Restart");
            promptIntent.putExtra("IdlingTimeout", true);
        }
        else
        {
            promptIntent.putExtra("Title", "Attention");
            promptIntent.putExtra("Message", "Your session time has expired.\n"
                    + "Do you want to extend it for an hour?");
            promptIntent.putExtra("IdlingTimeout", false);
        }

        sendNotification(promptIntent, context);

        context.startActivity(promptIntent);
    }

    public void setAlarm(Context context, long interval, int type)
    {
        String msgType = (type == Constants.IDLE_TOO_LONG ? BROADCAST_ALARM_IDLE : BROADCAST_ALARM_SESSION);
        Log.d(TAG, "Setting alarm to " + interval + " msec for " + msgType);
        Intent intent = new Intent(msgType);
        intent.putExtra("AlarmType", type);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, sender);
    }

    public void cancelAlarm(Context context, int type)
    {
        String msgType = (type == Constants.IDLE_TOO_LONG ? BROADCAST_ALARM_IDLE : BROADCAST_ALARM_SESSION);
        Intent intent = new Intent(msgType);
        intent.putExtra("AlarmType", type);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void register(Context context)
    {
        IntentFilter inf = new IntentFilter();
        inf.addAction(BROADCAST_ALARM_SESSION);
        inf.addAction(BROADCAST_ALARM_IDLE);
        context.registerReceiver(this, inf);
    }

    public void unregister(Context context)
    {
        try {
            context.unregisterReceiver(this);
        } catch (IllegalArgumentException iae) {
            // do nothing - receiver might not be registered
        }
    }

    private void sendNotification(Intent intent, Context context)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Session Expired")
                        .setContentText("Extend Valetroid Session!");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AlarmPrompt.class); // intent.resolveActivity(context.getPackageManager())

        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mNotificationId allows you to update the notification later on.
        mNotificationManager.notify(++mNotificationId, mBuilder.build());    }
}
