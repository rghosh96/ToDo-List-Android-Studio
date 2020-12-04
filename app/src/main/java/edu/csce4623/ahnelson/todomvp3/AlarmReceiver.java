package edu.csce4623.ahnelson.todomvp3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.todolistactivity.ToDoListActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("IN RECEIVER:", String.valueOf(intent));
        Log.d("IN RECEIVER:", String.valueOf(intent.getExtras()));
        // get intent data
        Bundle bundle = intent.getBundleExtra("ToDoItemBundle");
        ToDoItem item = (ToDoItem) bundle.getSerializable("ToDoItem");
        int id = (int) bundle.getLong("Id");
        String title = item.getTitle();
        Log.d("in receiver:", title);

        Intent mainIntent = new Intent(context, ToDoListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, mainIntent, 0);

        // if running
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notifManger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManger.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("To Do Item due!!!")
                    .setContentText(title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);
            notifManger.notify(id, builder.build());
        } else {
            NotificationManager notifManger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("To Do Item due!")
                    .setContentText(title)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);

            notifManger.notify(id, builder.build());
        }
    }
}
