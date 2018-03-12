package com.example.jozef.vcelicky;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "fcmMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationObservable notificationObservable;

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if the message contains data
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

            String title_text = remoteMessage.getData().get("title_text");
            String text = remoteMessage.getData().get("text");
            String hive_id = remoteMessage.getData().get("hive_id");
            String hive_name = remoteMessage.getData().get("hive_name");
            sendNotification(title_text,text);  // delete later///////////////////////////////////////////////////////////////////////////////////
            notificationObservable = NotificationObservable.getInstance();
            notificationObservable.setNotificationInfo(new NotificationInfo(title_text,text, hive_name, hive_id));
            notificationObservable.myNotifyObservers();
        }
    }

    private void sendNotification(String title, String text) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);
        notifiBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of notification*/, notifiBuilder.build());
    }

}
