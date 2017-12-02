package com.example.jozef.vcelicky;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// jakub dev branch

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ListView menuListView;
    int userId = 3;
    ArrayList<String> hiveNames =  new ArrayList<>();
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMifQ.NWOjv_uDNmgUU4sYWN3-wkCCjo4d-berGfRWC3FQ-9g";
    final String TAG = "MainActivity";
    ArrayAdapter<HiveBaseInfo> allAdapter;

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

        allAdapter = new AdapterHive(this, hiveList);
        menuListView = (ListView) findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);
        hiveClicked();

        loadHiveNames();

        createTestData();

 //       createTestData();

    //    createTestData();

    }

    public void loadHiveBaseInfo(){
        Log.d(TAG, "Loading hives");
        for (String hive : hiveNames) {
            Log.d(TAG, "Loading data for : " + hive);
            loadHiveBaseInfoServerReq(hive);
        }
    }

    public void loadHiveBaseInfoServerReq(final String hiveName){

        Log.d(TAG, "Load Hive BASE Info method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("device_name", hiveName);
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_GET_HIVE_INFO, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Load Hive Base Info From Server Response: " + response.toString());

                try {
                    ///////////////////////
                    int it = 0;
                    int ot = 0;
                    int h = 0;
                    int p = 0;

                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject json = jsonArray.getJSONObject(i);

                        String type = json.getString("typ");
                        if (type.equals("IT")) {
                            Log.d(TAG, "found IT : ");
                            it = json.getInt("hodnota");
                        }
                        if (type.equals("OT")) {
                            Log.d(TAG, "found OT : ");
                            ot = json.getInt("hodnota");
                        }
                        if (type.equals("H")) {
                            Log.d(TAG, "found H : ");
                            h = json.getInt("hodnota");
                        }
                        if (type.equals("P")) {
                            Log.d(TAG, "found P : ");
                           //TODO P (proximity is not in this model) need HOTFIX // Weight is mising
                        }

                    }
                    hiveList.add(new HiveBaseInfo(0, hiveName, ot , it, h, 0));
                    menuListView = (ListView) findViewById(R.id.hiveListView);
                    menuListView.setAdapter(allAdapter);


                    Log.d(TAG, "Hivelist lenght : "+hiveList.size());
////////////////////////////
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee){
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }


    public String loadHiveNames(){
            // Tag used to cancel the request

        Log.d(TAG, "Load Hive method");
             String tag_json_obj = "json_obj_req";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("user_id", userId);
                jsonBody.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            final String requestBody = jsonBody.toString();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_GETHIVES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Load Hive Server Response: " + response.toString());

                try {
                    ///////////////////////

                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject json = jsonArray.getJSONObject(i);
                        String name=json.getString("name");
                        Log.d(TAG, "Hive : " + name);
                        hiveNames.add(name);
                    }
                    loadHiveBaseInfo();



////////////////////////////
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee){
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return null;
    }

    public void hiveClicked(){
        menuListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        HiveBaseInfo device = (HiveBaseInfo) parent.getAdapter().getItem(position);
                        Intent i = new Intent(getApplicationContext(), HiveDetailsActivity.class);
                        i.putExtra("hiveId",device.getHiveId());
                        i.putExtra("hiveName",device.getHiveName());
                        i.putExtra("token",token);
                        startActivity(i);
                    }
                }
        );


    }

    public void createTestData(){
        hiveList.add(new HiveBaseInfo(1234, "Alfa", 55 , 45, 70, 69));
        hiveList.add(new HiveBaseInfo(1235, "Beta", 40 , 43, 68, 50));
        hiveList.add(new HiveBaseInfo(1236, "Gama", 30 , 42, 68, 60));
        hiveList.add(new HiveBaseInfo(1237, "Delta", 40 , 45, 50, 53));
        hiveList.add(new HiveBaseInfo(1238, "Pomaranč", 35 , 43, 68, 56));
        hiveList.add(new HiveBaseInfo(1239, "Žehlička", 32 , 49, 61, 89));
        hiveList.add(new HiveBaseInfo(1240, "Imro", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1241, "Kýbeľ", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1242, "Stolička", 36 , 45, 68, 66));
        hiveList.add(new HiveBaseInfo(1243, "Slniečko", 36 , 45, 68, 66));
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
