package com.example.jozef.vcelicky;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class NotificationsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "NotificatonsActivity";

    ArrayList<NotificationInfo> notificationInfoList = new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<NotificationInfo> allAdapter;

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

        notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "umbakarna", "Úlik pri jazierku"));
        notificationInfoList.add(new NotificationInfo("Úlik pri malej dolinke","This si text", "umbakarna2", "Úlik pri malej dolinke"));
        notificationInfoList.add(new NotificationInfo("Úlik pri jazierku","This si text", "umbakarna3", "Úlik pri jazierku"));
        notificationInfoList.add(new NotificationInfo("This is title text 4","This si text", "umbakarna4", "25"));
        allAdapter = new AdapterNotifications(this, notificationInfoList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);
 //     hiveClicked();
 //     createTestData();
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
