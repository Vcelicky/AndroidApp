package com.example.jozef.vcelicky;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class NotificationsActivity extends BaseActivity implements Observer {

    private static String TAG = "NotificatonsActivity";


//    private Observable mUserDataRepositoryObservable;
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

 //       notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "Bratislava", "36B7B7"));
          NotificationArchive.getInstance().getNotificationInfoList().add(new NotificationInfo("Úlik pri malej dolinke","This si text", "Trnava", "36B7B0"));
 //       notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "Sliač", "Úlik pri jazierku"));
 //       notificationInfoList.add(new NotificationInfo("This is title text 4","This si text", "Handlová", "25"));

        allAdapter = new AdapterNotifications(this,  NotificationArchive.getInstance().getNotificationInfoList());
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        mUserDataRepositoryObservable = UserDataRepository.getInstance();
        mUserDataRepositoryObservable.addObserver(this);
        hiveClicked();
 //     createTestData();
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d(TAG, "Update run");
        if (observable instanceof NotificationArchive) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshListView();
                }
            });
            Log.d(TAG, "Update run Notification type");
        }

        if (observable instanceof UserDataRepository) {
            Log.d(TAG, "Update run UserDataRepository type");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshListView();
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationArchive.getInstance().deleteObserver(this);
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
            NotificationArchive.getInstance().getNotificationInfoList().add(new NotificationInfo("Úlik pri malej dolinke","This si text", "ProfileNotification", "36B7B0"));
            NotificationArchive.getInstance().myNotifyObservers();

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
