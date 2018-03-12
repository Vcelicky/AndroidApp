package com.example.jozef.vcelicky;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;

import java.util.ArrayList;

/**
 * Created by MSI on 8. 3. 2018.
 */

public abstract class BaseActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        Log.d("BasicActivity", "OnCreate");

    }

 //   protected abstract int getLayoutResourceId();

    public void baseActivityActivateToolbarAndSideBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("BaseActivity");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    protected abstract int getLayoutResourceId();

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            Intent intent = new Intent(BaseActivity.this, OpisActivity.class);
            Log.d("BasicActivity", "AboutProject");
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Log.d("BasicActivity", "Profile");
        } else if (id == R.id.nav_notifications) {
            Intent intent = new Intent(BaseActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            showLogoutAlertDialog();
            Log.d("BasicActivity", "LogOut");
        } else if (id == R.id.nav_order){
            Intent intent = new Intent(BaseActivity.this, OrderActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void showLogoutAlertDialog(){
        AlertDialog.Builder logoutAlert = new AlertDialog.Builder(BaseActivity.this)
                .setMessage(R.string.proceed_with_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SessionManager session = new SessionManager(getApplicationContext());
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        if (session.isLoggedIn()) {
                            session.setLogin(false);
                            db.deleteUsers();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        logoutAlert.show();
    }

}
