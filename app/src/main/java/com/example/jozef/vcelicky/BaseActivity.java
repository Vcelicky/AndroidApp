package com.example.jozef.vcelicky;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    protected static final long CHARTSCALE = 86400000;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    ProgressDialog progressDialog;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        Log.d("BasicActivity", "OnCreate");

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_project) {
            Intent intent = new Intent(BaseActivity.this, OpisActivity.class);
            Log.d("BasicActivity", "AboutProject");
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(BaseActivity.this, ProfilActivity.class);
            startActivity(intent);
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
        } else if (id == R.id.nav_mainActivity){
            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            if (isOnline()){
                Intent intent = new Intent(BaseActivity.this, MapsActivity.class);
                intent.putExtra("mode", "allHives");
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),
                        R.string.need_internet, Toast.LENGTH_LONG)
                        .show();
            }
        }

//        else if (id == R.id.nav_limit_values){
//            Intent intent = new Intent(BaseActivity.this, LimitValuesChooseHiveActivity.class);
//            startActivity(intent);
//        }

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
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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

    public void showMessageAlertDialog(String message){
        final AlertDialog.Builder messageAlert = new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        messageAlert.show();
    }

    // parse date from tomo API time format (day.month.year.hour.minute)
    public GregorianCalendar parseDateFromVcelickaApi(boolean fromServer, String timeStamp){ //flag fromServer because of stupidity of GregorianCalendar class
        String[] timeStampParts = timeStamp.split(" ", -1);
        String[] dateParts = timeStampParts[0].split("-", -1);
        String[] timeParts = timeStampParts[1].split(":", -1);
        int year=0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
        for (int s = 0; s < dateParts.length; s++) {
            if (s == 0) {
                year = Integer.parseInt(dateParts[s]);
            }
            if (s == 1) {
                if(fromServer){
                    month = Integer.parseInt(dateParts[s]) - 1;
                }
                else{
                    month = Integer.parseInt(dateParts[s]);
                }
            }
            if (s == 2) {
                day = Integer.parseInt(dateParts[s]);
            }
        }
        for(int s = 0; s < timeParts.length; s++){
            if (s == 0){
                hour = Integer.parseInt(timeParts[s]);
            }
            if (s == 1){
                minute = Integer.parseInt(timeParts[s]);
            }
            if (s == 2){
                second = Integer.parseInt(timeParts[s]);
            }
        }
        return new GregorianCalendar(year, month, day, hour, minute, second);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
