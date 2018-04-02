package com.example.jozef.vcelicky;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";

    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<HiveBaseInfo> allAdapter;
    int flag = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Prehľad úľov");

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String token = db.getUserDetails().get("token");
        int userId = Integer.parseInt(db.getUserDetails().get("id"));
        Log.i(TAG, "Token: " + token);
        Log.i(TAG, "UserID: " + userId);

        allAdapter = new AdapterHive(this, hiveList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);
        hiveClicked();
        loadHiveNames(userId, token);

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("hives");
        FirebaseMessaging.getInstance().subscribeToTopic(db.getUserDetails().get("id"));
        Log.d("firebase", "Firebase Token: " + firebaseToken);

        // Just fake data for testing
        //createTestData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void loadHiveBaseInfo(int userId, String token){
        Log.d(TAG, "Loading hives");
        try {
            for (HiveBaseInfo hive : hiveIDs) {
                Log.i(TAG, "Loading data for : " + hive.getHiveId());
                loadHiveBaseInfoServerReq(hive.getHiveId(), hive.getHiveName(), userId, token, hive.getHiveLocation());
            }
        }  catch (Exception e) {
            Log.e(TAG, " Error: loadHiveBaseInfoServerReq: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error loading data from server" + e.getMessage(), Toast.LENGTH_LONG).show();
         return;
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

                try {
                    int it = 0;
                    int ot = 0;
                    int oh = 0, ih = 0, b = 0, w = 0;
                    boolean p = false;
                    long time = 0;

                    //Temporary variable because of wrong returning JSON from server array in array
                    JSONArray tempJsonArray = response.getJSONArray("data");
                    JSONArray jsonArray =  tempJsonArray.getJSONArray(0);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject json = jsonArray.getJSONObject(i);

                        try {
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
                                time = parseDateFromVcelickaApi(timestamp).getTimeInMillis();
                                Log.i(TAG, "Timestamp from record is: " + timestamp);
                                Log.i(TAG, "Timestamp from record is: " + time);
                            }
                        }catch(Exception e){
                            Log.i(TAG, "NULL value loaded, saving variable with 0");
                        }
                    }
                    HiveBaseInfo hive = new HiveBaseInfo(hiveId, hiveName,hiveLocation, ot , it, oh, ih, w, p, b);
     //               loadLimitValues (hiveId, userId, token,hive);

                    Log.i(TAG, "Hivelist lenght : " + hiveList.size());

                    hiveList.add(hive);
                    menuListView = findViewById(R.id.hiveListView);
                    menuListView.setAdapter(allAdapter);

                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                    db.addMeasurement(time, it, ot, ih, oh, w, p, b, hiveId);
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, " ErrorCCC: " + e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " ErrorBBB: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "ErrorBBB: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
            // Tag used to cancel the request

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
        //                String hiveName = json.getString("location");
                        String hiveName = json.getString("uf_name");
                     //   String hiveLocation = json.getString("location");
                        String hiveLocation =json.getString("location");
                        String hiveId = json.getString("device_id");
                        Log.i(TAG, "Loaded Hive: " + json.toString());
                        hiveIDs.add(new HiveBaseInfo(hiveId, hiveName,hiveLocation));
                    }
                    loadHiveBaseInfo(userId, token);
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Hotfix2 Error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "ErrorAAA: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                        startActivity(i);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_limit_values);
        item.setVisible(false);
        return true;
    }

    public void loadLimitValues (final String hiveId, int userId, String token, final HiveBaseInfo hive){

        Log.i(TAG, "Load limit values method");
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
                AppConfig.URL_GET_HIVE_LIMIT_VALUES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Load Hive Limit values " + response.toString());

                try {
                    JSONObject json = response.getJSONObject("data");

                    try {
                       hive.setHumidity_in_up_limit(json.getInt("temperature_in_up_limit"));
                       hive.setTemperature_in_down_limit(json.getInt("temperature_in_down_limit"));
                       hive.setWeight_limit(json.getInt("weight_limit"));
                       hive.setTemperature_out_up_limit(json.getInt("temperature_out_up_limit"));
                       hive.setTemperature_out_down_limit(json.getInt("temperature_out_down_limit"));
                       hive.setHumidity_in_up_limit( json.getInt("humidity_in_up_limit"));
                       hive.setHumidity_in_down_limit(json.getInt("humidity_in_down_limit"));
                       hive.setHumidity_out_up_limit(json.getInt("humidity_out_up_limit"));
                       hive.setHumidity_out_down_limit(json.getInt("humidity_out_down_limit"));
                       hive.setBatery_limit(json.getInt("batery_limit"));

                    }catch(Exception e){
                        Log.i(TAG, "NULL value loaded, saving variable with 0");
                    }
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, " ErrorCCC: " + e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " ErrorBBB: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "ErrorBBB: " + error.getMessage(), Toast.LENGTH_LONG).show();
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


//        hiveList.add(new HiveBaseInfo("1234", "Alfa", 55 , 45, 70, 80, 69,  true,99));
//        hiveList.add(new HiveBaseInfo("1235", "Beta", 40 , 43, 68, 85,50,true,99));
//        hiveList.add(new HiveBaseInfo("1236", "Gama", 30 , 42, 68, 82,60,false,99));
//        hiveList.add(new HiveBaseInfo("1237", "Delta", 40 , 45, 50, 81,53,true,99));
//        hiveList.add(new HiveBaseInfo("1238", "Pomaranč", 35 , 43, 68, 75,56,true,99));
//        hiveList.add(new HiveBaseInfo("1239", "Žehlička", 32 , 49, 61, 70,89,true,99));
//        hiveList.add(new HiveBaseInfo("1240", "Imro", 36 , 45, 68, 60,66,true,99));
//        hiveList.add(new HiveBaseInfo("1241", "Kýbeľ", 36 , 45, 68, 75,66,true,99));
//        hiveList.add(new HiveBaseInfo("1242", "Stolička", 36 , 45, 68, 78,66,true,99));
//        hiveList.add(new HiveBaseInfo("1243", "Slniečko", 36 , 45, 68, 80,66,true,99));

}
