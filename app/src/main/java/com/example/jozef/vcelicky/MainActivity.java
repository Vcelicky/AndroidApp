package com.example.jozef.vcelicky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";

    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs = new ArrayList<>();
    ArrayAdapter<HiveBaseInfo> allAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Prehľad úľov");


        ArrayList<HiveBaseInfo> tmpHiveList = new ArrayList<>();
        allAdapter = new AdapterHive(this, tmpHiveList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

        if (!isOnline()) {
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
        } else {
            hiveIDs.clear();
            hiveList.clear();
            progressDialog.setMessage("Prebieha sťahovanie dát zo servera ...");
            showDialog();
            loadHiveNames(1007, "bleh");
            hideDialog();
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (!isOnline()) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.no_service, Toast.LENGTH_LONG)
                                    .show();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            hiveIDs.clear();
                            hiveList.clear();
                            progressDialog.setMessage("Prebieha sťahovanie dát zo servera ...");
                            //showDialog();
                            loadHiveNames(1007, "bleh"); //TODO
                           // hideDialog();
                        }
                    }
                }
        );
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void loadHiveNames(final int userId, final String token) {
        Log.i(TAG, "Load Hive method");
        String hiveName = "FIIT STU";
        String hiveLocation = "Bratislava";
        String hiveId = "12345";
        hiveIDs.add(new HiveBaseInfo(hiveId, hiveName, hiveLocation));
        loadHiveBaseInfo(userId, token);
    }

    public void loadHiveBaseInfo(int userId, String token) {
        Log.d(TAG, "Loading hives");
        try {
            if (hiveIDs.size() == 0) {
                showMessageAlertDialog(getString(R.string.no_hives_available));
            } else {
                for (HiveBaseInfo hive : hiveIDs) {
                    Log.i(TAG, "Loading data for : " + hive.getHiveId());
                    loadHiveBaseInfoServerReq(hive.getHiveId(), hive.getHiveName(), userId, token, hive.getHiveLocation());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " Error: loadHiveBaseInfoServerReq: " + e.getMessage());
            Toast.makeText(getApplicationContext(), R.string.error_loading_data, Toast.LENGTH_LONG).show();
        }
    }

    public void loadHiveBaseInfoServerReq(final String hiveId, final String hiveName, final int userId, final String token, final String hiveLocation) {

        Log.i(TAG, "Load Hive BASE Info method");
        String tag_json_obj = "json_obj_req";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_GET_HIVE_INFO, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Load Hive Base Info From Server Response: " + response.toString());
                int it = 0;
                int ot = 0;
                int oh = 0, ih = 0, b = 0, w = 0;
                boolean p = false;
                long time = 0;
                int Temperature_in_up_limit = 100, Temperature_in_down_limit = -20, Weight_limit = 200, Temperature_out_up_limit = 100, Temperature_out_down_limit = -20,
                        Humidity_in_up_limit = 120, Humidity_in_down_limit = -1, Humidity_out_up_limit = 120, Humidity_out_down_limit = -1, Batery_limit = -1;
                double latitude = 0, longitude = 0;
                String dataInHexstring="defaultdatavalue";
                try {
                    //Temporary variable because of wrong returning JSON from server array in array
                    JSONArray tempJsonArray = response.getJSONArray("result");
                    JSONObject json = tempJsonArray.getJSONObject(0);
                    Log.i(TAG, "fist parse: " + json.toString());
                    JSONObject a = json.getJSONObject("data");
                    dataInHexstring = a.getString("value");
                    Log.i(TAG, "second parse: " + dataInHexstring);

                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, " Error: " + e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Log.i(TAG, "second parse: " + dataInHexstring);
                Map parsed_data = parseData(dataInHexstring);
                int aaaa = (int) parsed_data.get("hmotnost");
                Log.i(TAG, "jjjj parse: " + aaaa);


//                "poloha", poloha);
//                "hmotnost", hmotnost);
//                "vnutorna_teplota", vnutorna_teplota);
//                "vonkajsia_teplota", vonkajsia_teplota);
//                "vonkajsia_vlhkost", vonkajsia_vlhkost);
//                "vnutorna_vlhkost", vnutorna_vlhkost);
//                "stav_baterie", stav_baterie);
                Boolean prevrateny_ul = false;
                if ((int) parsed_data.get("poloha")==1)
                    prevrateny_ul = true;
                Boolean nabija_sa = false;
                if ((int) parsed_data.get("nabijanie")==1)
                    nabija_sa = true;

                        //(int) parsed_data.get("hmotnost");

                HiveBaseInfo hive = new HiveBaseInfo(hiveId, hiveName, hiveLocation, (int) parsed_data.get("vonkajsia_teplota"),
                        (int) parsed_data.get("vnutorna_teplota"),
                        (int) parsed_data.get("vonkajsia_vlhkost"),
                        (int) parsed_data.get("vnutorna_vlhkost"),
                        (int) parsed_data.get("hmotnost"), prevrateny_ul, (int) parsed_data.get("stav_baterie"));
                //hive.setBattery(51);
                hive.setCharging(nabija_sa);
                hive.setTemperature_in_up_limit(Temperature_in_up_limit);
                hive.setTemperature_in_down_limit(Temperature_in_down_limit);
                hive.setWeight_limit(Weight_limit);
                hive.setTemperature_out_up_limit(Temperature_out_up_limit);
                hive.setTemperature_out_down_limit(Temperature_out_down_limit);
                hive.setHumidity_in_up_limit(Humidity_in_up_limit);
                hive.setHumidity_in_down_limit(Humidity_in_down_limit);
                hive.setHumidity_out_up_limit(Humidity_out_up_limit);
                hive.setHumidity_out_down_limit(Humidity_out_down_limit);
                hive.setBatery_limit(Batery_limit);
                hive.setLatitude(1);
                hive.setLongitude(2);
                hiveList.add(hive);
                saveHiveListToSharedPreferencies();
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
        })
        {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Carriots.apiKey", "582155c4a8a15467d4fbede176862673f4c5a5137b911e8a7cbf5034ff7c38ce");
                headers.put("Device", "DeviceBratislava@fiittp20.fiittp20");
                return headers;
            }
        };

//        {
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    public static Map parseData(String data){
        System.out.println(data.length());
        for (int i = 0; i < 12-data.length(); i++) {
            data = "0"+ data;
        }
        int hmotnost = ((Integer.parseInt(data.substring(1, 3),16))>> 1);
        int poloha = ((Integer.parseInt(data.substring(0, 1),16)))%2;
        int vonkajsia_teplota,vnutorna_teplota;
        if (Integer.parseInt(data.substring(10, 12),16)/128==1){
            vonkajsia_teplota  = (Integer.parseInt(data.substring(10, 12),16)% 128)*-1;
        }else{
            vonkajsia_teplota  = (Integer.parseInt(data.substring(10, 12),16)% 128);
        }
        if (Integer.parseInt(data.substring(8, 10),16)/128==1){
            vnutorna_teplota  = (Integer.parseInt(data.substring(8, 10),16)% 128)*-1;
        }else{
            vnutorna_teplota  = (Integer.parseInt(data.substring(8, 10),16)% 128);
        }
        int vonkajsia_vlhkost = (Integer.parseInt(data.substring(6, 8),16)% 128);
        int vnutorna_vlhkost =((Integer.parseInt(data.substring(4, 7),16))>> 3)% 128;
        int stav_baterie = ((Integer.parseInt(data.substring(2, 5),16))>> 2)% 128;
        int nabijanie = (((Integer.parseInt(data.substring(0, 1),16)))>>1)%2;

        Map parsed_values = new HashMap();
        parsed_values.put("poloha", poloha);
        parsed_values.put("hmotnost", hmotnost);
        parsed_values.put("vnutorna_teplota", vnutorna_teplota);
        parsed_values.put("vonkajsia_teplota", vonkajsia_teplota);
        parsed_values.put("vonkajsia_vlhkost", vonkajsia_vlhkost);
        parsed_values.put("vnutorna_vlhkost", vnutorna_vlhkost);
        parsed_values.put("stav_baterie", stav_baterie);
        parsed_values.put("nabijanie", nabijanie);
        System.out.println(parsed_values);
        return parsed_values;
    }

    public void tempLoadHiveBaseInfoServerReq(final String hiveId, final String hiveName, final int userId, final String token, final String hiveLocation) {


        int it = 0;
        int ot = 0;
        int oh = 0, ih = 0, b = 0, w = 0;
        boolean p = false;
        long time = 0;
        int Temperature_in_up_limit = 0, Temperature_in_down_limit = 0, Weight_limit = 0, Temperature_out_up_limit = 0, Temperature_out_down_limit = 0,
                Humidity_in_up_limit = 0, Humidity_in_down_limit = 0, Humidity_out_up_limit = 0, Humidity_out_down_limit = 0, Batery_limit = 0;

        HiveBaseInfo hive = new HiveBaseInfo(hiveId, hiveName, hiveLocation, ot, it, oh, ih, w, p, b);
        hive.setTemperature_in_up_limit(Temperature_in_up_limit);
        hive.setTemperature_in_down_limit(Temperature_in_down_limit);
        hive.setWeight_limit(Weight_limit);
        hive.setTemperature_out_up_limit(Temperature_out_up_limit);
        hive.setTemperature_out_down_limit(Temperature_out_down_limit);
        hive.setHumidity_in_up_limit(Humidity_in_up_limit);
        hive.setHumidity_in_down_limit(Humidity_in_down_limit);
        hive.setHumidity_out_up_limit(Humidity_out_up_limit);
        hive.setHumidity_out_down_limit(Humidity_out_down_limit);
        hive.setBatery_limit(Batery_limit);
        hive.setLatitude(1);
        hive.setLongitude(2);
        hiveList.add(hive);
        saveHiveListToSharedPreferencies();
        Log.i(TAG, "Hivelist lenght : " + hiveList.size());
        allAdapter = new AdapterHive(MainActivity.this, hiveList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

    }

    public void hiveClicked() {
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

    public void saveHiveListToSharedPreferencies() {
        db = new SQLiteHandler(getApplicationContext());
        //TODO:
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(db.getUserDetails(session.getLoggedUser()).get("id"), getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hiveList);
        Log.d(TAG, "saving list json " + json);
        prefsEditor.putString("hiveList", json);
        Log.d(TAG, "savingPreferencies " + mPrefs.getString("hiveList", ""));
        //     prefsEditor.commit();
        prefsEditor.commit();
        Log.d(TAG, "savingPreferencies " + mPrefs.getString("hiveList", ""));
    }
}