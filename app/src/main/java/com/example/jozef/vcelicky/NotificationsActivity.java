package com.example.jozef.vcelicky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

 //       notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "Bratislava", "36B7B7"));
        notificationInfoList.add(new NotificationInfo("Úlik pri malej dolinke","This si text", "Trnava", "36B7B0"));
 //       notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "Sliač", "Úlik pri jazierku"));
 //       notificationInfoList.add(new NotificationInfo("This is title text 4","This si text", "Handlová", "25"));

        allAdapter = new AdapterNotifications(this,notificationInfoList );
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        mUserDataRepositoryObservable = UserDataRepository.getInstance();
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

        if (observable instanceof UserDataRepository) {
            Log.d(TAG, "Update run UserDataRepository type");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UserDataRepository userDataRepository = (UserDataRepository)observable;
                    notificationInfoList.add(0,userDataRepository.getNotificationInfo());
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
        UserDataRepository.getInstance().deleteObserver(this);
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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_project) {
            Intent intent = new Intent(NotificationsActivity.this, OpisActivity.class);
            Log.d("BasicActivity", "AboutProject");
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Log.d("BasicActivity", "Profile");
            notificationInfoList.clear();
            notificationInfoList.add(new NotificationInfo("Úlik pri malej dolinke2","This si text", "Trnava2", "36B7B0"));
            saveNotificationInfoListFromSharedPreferencies();
            refreshListView();


        } else if (id == R.id.nav_notifications) {
            Intent intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            showLogoutAlertDialog();
            Log.d("BasicActivity", "LogOut");
        } else if (id == R.id.nav_order){
            Intent intent = new Intent(NotificationsActivity.this, OrderActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
