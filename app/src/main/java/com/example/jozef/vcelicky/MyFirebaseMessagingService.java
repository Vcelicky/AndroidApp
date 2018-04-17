package com.example.jozef.vcelicky;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "fcmMessagingService";
    public ArrayList<NotificationInfo> notificationInfoList=new ArrayList<>();
    SQLiteHandler db;
    SessionManager session;
    String receivedUserId = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationObservable notificationObservable;
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        Log.d(TAG, "FROM:" + remoteMessage.getFrom());
        String[] splited = remoteMessage.getFrom().split("/");
        try {
            receivedUserId = splited[2];
        }catch (Exception e){
            Log.e(TAG, "Cant parse received notification ID");
        }

        //Check if the message contains data
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

            String title_text = remoteMessage.getData().get("title_text");
            String text = remoteMessage.getData().get("text");
            String hive_id = remoteMessage.getData().get("hive_id");
            String hive_name = remoteMessage.getData().get("hive_name");

            if (receivedUserId.equals(db.getUserDetails(session.getLoggedUser()).get("id"))) {
                sendNotification(title_text, text);
                Log.d(TAG, "This notification is for me");
            }else {
                Log.d(TAG, "This notification is not for me");
            }

            loadNotificationInfoListFromSharedPreferencies();
            notificationInfoList.add(0,new NotificationInfo(title_text,text, hive_name, hive_id));

            if(notificationInfoList.size() > 100)
                notificationInfoList.remove(notificationInfoList.size()-1);

            Log.d(TAG, "list Print " + notificationInfoList);

            saveNotificationInfoListFromSharedPreferencies();

            if (receivedUserId.equals(db.getUserDetails(session.getLoggedUser()).get("id"))) {
                notificationObservable = NotificationObservable.getInstance();
                notificationObservable.setNotificationInfo(new NotificationInfo(title_text,text, hive_name, hive_id));
                notificationObservable.myNotifyObservers();
            }
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


    public void loadNotificationInfoListFromSharedPreferencies(){
        notificationInfoList.clear();
        db = new SQLiteHandler(getApplicationContext());
        //TODO:
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(receivedUserId,getApplicationContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        Log.d(TAG, "loadedPreferencies " + mPrefs.getString("myJson", ""));
        if (json.isEmpty()) {
            notificationInfoList.clear();
        } else {
            Type type = new TypeToken<List<NotificationInfo>>() {
            }.getType();
            notificationInfoList = gson.fromJson(json, type);
        }
    }
    public void saveNotificationInfoListFromSharedPreferencies(){
        db = new SQLiteHandler(getApplicationContext());
        //TODO:
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(receivedUserId, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationInfoList);
        Log.d(TAG, "saving list Print " + notificationInfoList);
        Log.d(TAG, "saving list json " + json);
        prefsEditor.putString("myJson", json);
        Log.d(TAG, "savingPreferencies " + mPrefs.getString("myJson", ""));
   //     prefsEditor.commit();
        prefsEditor.commit();
        Log.d(TAG, "savingPreferencies " + mPrefs.getString("myJson", ""));
    }

}
