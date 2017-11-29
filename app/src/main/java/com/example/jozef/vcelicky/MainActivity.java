package com.example.jozef.vcelicky;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

// jakub dev branch

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Prehľad úľov");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
/////////////////
        createTestData();
        ArrayAdapter<HiveBaseInfo> allAdapter = new AdapterHive(this, hiveList);
        ListView menuListView = (ListView) findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);


        menuListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        HiveBaseInfo device = (HiveBaseInfo) parent.getAdapter().getItem(position);
                        Intent i = new Intent(getApplicationContext(), HiveDetailsActivity.class);
                        i.putExtra("hiveId",device.getHiveId());
                        startActivity(i);
                    }
                }
        );

    }


    public void createTestData(){
        hiveList.add(new HiveBaseInfo(1234, "Včelí úľ Alfa", 55 , 45, 70, 69));
        hiveList.add(new HiveBaseInfo(1235, "Včelí úľ Beta", 40 , 43, 68, 50));
        hiveList.add(new HiveBaseInfo(1236, "Včelí úľ Gama", 30 , 42, 68, 60));
        hiveList.add(new HiveBaseInfo(1237, "Včelí úľ Delta", 40 , 45, 50, 53));
        hiveList.add(new HiveBaseInfo(1238, "Včelí úľ Pomaranč", 35 , 43, 68, 56));
        hiveList.add(new HiveBaseInfo(1239, "Včelí úľ Žehlička", 32 , 49, 61, 89));
        hiveList.add(new HiveBaseInfo(1240, "Včelí úľ Imro", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1241, "Včelí úľ Kýbeľ", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1242, "Včelí úľ Stolička", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1243, "Včelí úľ Slniečko", 36 , 45, 68, 66));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_project) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
