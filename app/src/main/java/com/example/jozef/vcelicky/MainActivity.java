package com.example.jozef.vcelicky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";

    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<HiveBaseInfo> allAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Prehľad úľov");

        db = new SQLiteHandler(getApplicationContext());
        final String token =  db.getUserDetails(session.getLoggedUser()).get("token");
        final int userId = Integer.parseInt(db.getUserDetails(session.getLoggedUser()).get("id"));

        Log.i(TAG, "Token: " + token);
        Log.i(TAG, "UserID: " + userId);

        hiveList = db.getActualMeasurement(userId);
        allAdapter = new AdapterHive(this, hiveList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        hiveClicked();
        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
        }
        else {
            hiveIDs.clear();
            hiveList.clear();
            progressDialog.setMessage("Prebieha sťahovanie dát zo servera ...");
            showDialog();
            loadHiveNames(userId, token);
            hideDialog();
        }

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("hives");
        FirebaseMessaging.getInstance().subscribeToTopic(db.getUserDetails(session.getLoggedUser()).get("id"));
        Log.d("firebase", "Firebase Token: " + firebaseToken);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!isOnline()){
                            Toast.makeText(getApplicationContext(),
                                    R.string.no_service, Toast.LENGTH_LONG)
                                    .show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else {
                            hiveIDs.clear();
                            hiveList.clear();
                            loadHiveNames(userId, token);
                        }
                    }
                }
        );

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void loadHiveBaseInfo(int userId, String token){
        Log.d(TAG, "Loading hives");
        try {
            if(hiveIDs.size() == 0){
                hideDialog();
                showMessageAlertDialog(getString(R.string.no_hives_available));
            }
            else {
                for (HiveBaseInfo hive : hiveIDs) {
                    Log.i(TAG, "Loading data for : " + hive.getHiveId());
                    loadHiveBaseInfoServerReq(hive.getHiveId(), hive.getHiveName(), userId, token, hive.getHiveLocation());
                }
            }
        }  catch(Exception e) {
            Log.e(TAG, " Error: loadHiveBaseInfoServerReq: " + e.getMessage());
            Toast.makeText(getApplicationContext(), R.string.error_loading_data, Toast.LENGTH_LONG).show();
        }
    }

    public void loadHiveBaseInfoServerReq(final String hiveId, final String hiveName, final int userId,final String token, final String hiveLocation) {

        Log.i(TAG, "Load Hive BASE Info method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("device_id", hiveId);
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
                Log.i(TAG, "Load Hive Base Info From Server Response: " + response.toString());
                int it = 0;
                int ot = 0;
                int oh = 0, ih = 0, b = 0, w = 0;
                boolean p = false;
                long time = 0;
                int Temperature_in_up_limit =0, Temperature_in_down_limit=0, Weight_limit=0, Temperature_out_up_limit=0, Temperature_out_down_limit=0,
                        Humidity_in_up_limit=0, Humidity_in_down_limit=0,Humidity_out_up_limit=0, Humidity_out_down_limit=0,Batery_limit=0;
                try {
                    //Temporary variable because of wrong returning JSON from server array in array
                    JSONArray tempJsonArray = response.getJSONArray("data");
                    JSONArray jsonArray =  tempJsonArray.getJSONArray(0);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject json = jsonArray.getJSONObject(i);


                        try {

                            if (i == jsonArray.length()-1){
                                HiveBaseInfo hive = new HiveBaseInfo();
                                Temperature_in_up_limit = json.getInt("temperature_in_up_limit");
                                Temperature_in_down_limit = json.getInt("temperature_in_down_limit");
                                Weight_limit = json.getInt("weight_limit");
                                Temperature_out_up_limit = json.getInt("temperature_out_up_limit");
                                Temperature_out_down_limit = json.getInt("temperature_out_down_limit");
                                Humidity_in_up_limit =  json.getInt("humidity_in_up_limit");
                                Humidity_in_down_limit = json.getInt("humidity_in_down_limit");
                                Humidity_out_up_limit = json.getInt("humidity_out_up_limit");
                                Humidity_out_down_limit = json.getInt("humidity_out_down_limit");
                                Batery_limit = json.getInt("batery_limit");
                                Log.i(TAG, "Loaded Battery limit: "+Batery_limit);
                                break;
                            }


                            String type = json.getString("typ");
                            if (type.equals("IT")) {
                                Log.i(TAG, "found IT : ");
                                it = json.getInt("hodnota");
                            }
                            if (type.equals("OT")) {
                                Log.i(TAG, "found OT : ");
                                ot = json.getInt("hodnota");
                            }
                            if (type.equals("OH")) {
                                Log.i(TAG, "found OH : ");
                                oh = json.getInt("hodnota");
                            }
                            if (type.equals("IH")) {
                                Log.i(TAG, "found IH : ");
                                ih = json.getInt("hodnota");
                            }
                            if (type.equals("P")) {
                                Log.i(TAG, "found P : ");
                                p = json.getBoolean("hodnota");
                            }
                            if (type.equals("W")) {
                                Log.i(TAG, "found W : ");
                                w = json.getInt("hodnota");
                            }
                            if (type.equals("B")) {
                                Log.i(TAG, "found B : ");
                                b = json.getInt("hodnota");
                            }
                            if(time == 0){
                                String timestamp = json.getString("cas");
                                time = parseDateFromVcelickaApi(true, timestamp).getTimeInMillis();
                                Log.i(TAG, "Timestamp from record is: " + timestamp);
                                Log.i(TAG, "Timestamp from record is: " + time);
                            }
                        }catch(Exception e){
                            Log.i(TAG, "NULL value loaded, saving variable with 0");
                        }
                    }
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, " Error: " + e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                HiveBaseInfo hive = new HiveBaseInfo(hiveId, hiveName,hiveLocation, ot , it, oh, ih, w, p, b);
                hive.setTemperature_in_up_limit(Temperature_in_up_limit);
                hive.setTemperature_in_down_limit(Temperature_in_down_limit);
                hive.setWeight_limit(Weight_limit);
                hive.setTemperature_out_up_limit(Temperature_out_up_limit);
                hive.setTemperature_out_down_limit(Temperature_out_down_limit);
                hive.setHumidity_in_up_limit( Humidity_in_up_limit);
                hive.setHumidity_in_down_limit(Humidity_in_down_limit);
                hive.setHumidity_out_up_limit(Humidity_out_up_limit);
                hive.setHumidity_out_down_limit(Humidity_out_down_limit);
                hive.setBatery_limit(Batery_limit);

                hiveList.add(hive);
                Log.i(TAG, "Hivelist lenght : " + hiveList.size());
                allAdapter = new AdapterHive(MainActivity.this, hiveList);
                menuListView = findViewById(R.id.hiveListView);
                menuListView.setAdapter(allAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
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

    public void loadHiveNames(final int userId, final String token){
        Log.i(TAG, "Load Hive method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_GET_HIVES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Load Hive Server Response: " + response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject json = jsonArray.getJSONObject(i);
                        String hiveName = json.getString("uf_name");
                        String hiveLocation =json.getString("location");
                        String hiveId = json.getString("device_id");
                        Log.i(TAG, "Loaded Hive: " + json.toString());
                        hiveIDs.add(new HiveBaseInfo(hiveId, hiveName, hiveLocation));
                        db.addDevice(hiveId, hiveName, hiveLocation, userId);
                    }
                    loadHiveBaseInfo(userId, token);
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.i(TAG, "Error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
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

    public void hiveClicked(){
        menuListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        HiveBaseInfo device = (HiveBaseInfo) parent.getAdapter().getItem(position);
                        Intent i = new Intent(getApplicationContext(), HiveDetailsActivity.class);
                        Log.i(TAG, "SPARTA: hiveId: " + device.getHiveId());
                        Log.i(TAG, "SPARTA: hiveName " + device.getHiveName());
                        i.putExtra("hiveId", device.getHiveId());
                        i.putExtra("hiveName", device.getHiveName());
                        i.putExtra("hiveLocation", device.getHiveLocation());
                        startActivity(i);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
