package com.example.jozef.vcelicky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class NotificationsActivity extends BaseActivity implements Observer {

    private static String TAG = "NotificatonsActivity";


    static private ArrayList<NotificationInfo> notificationInfoList=new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<NotificationInfo> allAdapter;
    private Observable mUserDataRepositoryObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notifikácie");

//        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
//        String token =  db.getUserDetails().get("token");
//        int userId = Integer.parseInt(db.getUserDetails().get("id"));
//        Log.i(TAG, "Token: " + token);
//        Log.i(TAG, "UserID: " + userId);

        try {
            loadNotificationInfoListFromSharedPreferencies();
        }catch (Exception ex){
            Log.d(TAG, "Cant load from shared preferencies");
        }

        allAdapter = new AdapterNotifications(this,notificationInfoList );
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        mUserDataRepositoryObservable = NotificationObservable.getInstance();
        mUserDataRepositoryObservable.addObserver(this);
        hiveClicked();
 //     createTestData();

    }
    public void loadNotificationInfoListFromSharedPreferencies(){
        notificationInfoList.clear();
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationArchive",getApplicationContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            notificationInfoList.clear();
        } else {
            Type type = new TypeToken<List<NotificationInfo>>() {
            }.getType();
            notificationInfoList = gson.fromJson(json, type);
        }
    }
    public void saveNotificationInfoListFromSharedPreferencies(){
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationArchive", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationInfoList);
        prefsEditor.putString("myJson", json);
        prefsEditor.commit();
    }


    @Override
    public void update( final Observable observable, Object o) {
        Log.d(TAG, "Update run");

        if (observable instanceof NotificationObservable) {
            Log.d(TAG, "Update run NotificationObservable type");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationObservable notificationObservable = (NotificationObservable)observable;
                    notificationInfoList.add(0, notificationObservable.getNotificationInfo());
                    if(notificationInfoList.size() > 10)
                        notificationInfoList.remove(notificationInfoList.size()-1);
                    saveNotificationInfoListFromSharedPreferencies();
                    refreshListView();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationObservable.getInstance().deleteObserver(this);
    }

    public void refreshListView(){
        allAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void hiveClicked(){
        menuListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        NotificationInfo device = (NotificationInfo) parent.getAdapter().getItem(position);
                        Intent i = new Intent(getApplicationContext(), HiveDetailsActivity.class);
                        i.putExtra("hiveId", device.getHiveId());
                        i.putExtra("hiveName", device.getHiveName());
                        startActivity(i);
                    }
                }
        );
    }

}
