package org.zakvdm.rememberme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;
import android.os.Bundle;

public class RememberMeReceiver extends BroadcastReceiver {

    public final static String REMEMBER_ME_TO_TEXT = "remember_me_to_text";

    private static final int REMEMBER_ME_NOTIFICATION_ID = 0x1001;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString(REMEMBER_ME_TO_TEXT);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            
            sendNotification(context, message);
        } catch (Exception e) {
            //Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void sendNotification(Context context, String msg) {
        sendNotification(context, CreateRememberMeActivity.class, msg, context.getString(R.string.remember_me_to) + " " + msg, 1, true, true);
    }
    

    private void sendNotification(Context caller, Class<?> activityToLaunch, String title, String msg, int numberOfEvents, boolean flashLed, boolean vibrate) {
        NotificationManager notifier = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notify = new Notification(R.drawable.icon, "", System.currentTimeMillis());

        notify.icon = R.drawable.icon;
        notify.tickerText = msg;
        notify.when = System.currentTimeMillis();
        notify.number = numberOfEvents;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;

        if (flashLed) {
        // add lights
            notify.flags |= Notification.FLAG_SHOW_LIGHTS;
            notify.ledARGB = Color.CYAN;
            notify.ledOnMS = 500;
            notify.ledOffMS = 500;
        }

        if (vibrate) {
            notify.vibrate = new long[] {100, 200, 200, 200, 200, 200, 1000, 200, 200, 200, 1000, 200};
        }

        Intent toLaunch = new Intent(caller, activityToLaunch);
        PendingIntent intentBack = PendingIntent.getActivity(caller, 0, toLaunch, 0);

        notify.setLatestEventInfo(caller, title, msg, intentBack);
        notifier.notify(REMEMBER_ME_NOTIFICATION_ID, notify);
    }
    


}