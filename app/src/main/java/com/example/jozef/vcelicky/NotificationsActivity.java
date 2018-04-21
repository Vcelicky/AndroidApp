package com.example.jozef.vcelicky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.jozef.vcelicky.helper.SQLiteHandler;
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
//    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<NotificationInfo> allAdapter;
    private Observable mUserDataRepositoryObservable;
    SQLiteHandler db;
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

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setCustomView(R.layout.switch_layout);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
//
//

        try {
            loadNotificationInfoListFromSharedPreferencies();
        }catch (Exception ex){
            Log.d(TAG, "Cant load from shared preferencies");
        }

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setEnabled(false);

        allAdapter = new AdapterNotifications(this,notificationInfoList );
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        mUserDataRepositoryObservable = NotificationObservable.getInstance();
        mUserDataRepositoryObservable.addObserver(this);
        hiveClicked();
 //     createTestData();

    }
    public void loadNotificationInfoListFromSharedPreferencies(){
        Log.d("fcmMessagingService", "Notification activity list first size " + notificationInfoList.size());
        notificationInfoList.clear();
        db = new SQLiteHandler(getApplicationContext());
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(db.getUserDetails(session.getLoggedUser()).get("id"),getApplicationContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        Log.d("fcmMessagingService", "Notification activity loading prefs " + mPrefs.getString("myJson", ""));
        if (json.isEmpty()) {
            notificationInfoList.clear();
        } else {
            Type type = new TypeToken<List<NotificationInfo>>() {
            }.getType();
            notificationInfoList = gson.fromJson(json, type);
        }
        if (notificationInfoList.size() == 0 ){
            Toast.makeText(getApplicationContext(),
                    R.string.no_notification, Toast.LENGTH_LONG)
                    .show();
        }
  //      refreshListView();

    }

    @Override
    public void update( final Observable observable, Object o) {
        Log.d(TAG, "Update run");

        if (observable instanceof NotificationObservable) {
            Log.d(TAG, "Update run NotificationObservable type");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadNotificationInfoListFromSharedPreferencies();
                    allAdapter = new AdapterNotifications(getApplicationContext(),notificationInfoList );
                    menuListView = findViewById(R.id.hiveListView);
                    menuListView.setAdapter(allAdapter);

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationObservable.getInstance().deleteObserver(this);
    }

//    public void refreshListView(){
//        allAdapter.notifyDataSetChanged();
//    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.switch_menu, menu);

        SwitchCompat button = (SwitchCompat) menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switchForActionBar);

        try {
            db = new SQLiteHandler(getApplicationContext());
            SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(db.getUserDetails(session.getLoggedUser()).get("id"),getApplicationContext().MODE_PRIVATE);
            boolean sw = mPrefs.getBoolean("notificationSwitch", true);
            button.setChecked(sw);
        }catch (Exception e){
            button.setChecked(true);
        }

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db = new SQLiteHandler(getApplicationContext());
                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(db.getUserDetails(session.getLoggedUser()).get("id"), getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                if (isChecked) {

//                   Toast.makeText(getApplicationContext(),
//                           "Notifications are On", Toast.LENGTH_LONG)
//                           .show();

                   prefsEditor.putBoolean("notificationSwitch",true );
                   prefsEditor.commit();

               }else{
//                   Toast.makeText(getApplicationContext(),
//                           "Notifications Off", Toast.LENGTH_LONG)
//                           .show();
                    prefsEditor.putBoolean("notificationSwitch",false );
                    prefsEditor.commit();
               }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_limit_values) {
            Intent intent = new Intent(NotificationsActivity.this, LimitValuesSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void myMethod(){

    }


}
