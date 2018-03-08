package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.jozef.vcelicky.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HiveAllDetailsActivity extends MainActivity {

    final String TAG = "HiveAllDetailsActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ArrayAdapter<HiveBaseInfo> allAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive_all_details);

        Intent intent = getIntent();
        String hiveId = intent.getExtras().getString("hiveId");
        String hiveName = intent.getExtras().getString("hiveName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Včelí úľ " + hiveName);
        setSupportActionBar(toolbar);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String token =  db.getUserDetails().get("token");
        int userId = Integer.parseInt(db.getUserDetails().get("id"));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        allAdapter = new AdapterHiveDetails(this, hiveList);

        loadHiveDetailInfoServerReq(hiveId, hiveName, userId, token);
    }

    public void loadHiveDetailInfoServerReq(final String hiveId, final String hiveName, int userId, String token){

        Log.i(TAG, "Load Hive Details Info method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();

        Log.d(TAG, ": " + hiveName);
        Log.d(TAG, ": " + token);
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("device_id", hiveId);
            jsonBody.put("token", token);
            jsonBody.put("from", 0);    //hardcoded from and to because API can't return records based on timestamp
            jsonBody.put("to", 50);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_GET_HIVE_INFO_DETAILS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Load Hive Base Info From Server Response: " + response.toString());
                GregorianCalendar timeStampGregCal = null;
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONArray jsonArray2 = jsonArray.getJSONArray(i); //proccess additional []
                        int it = 0, ot = 0, ih = 0, oh = 0, w = 0, b = 0;
                        boolean p = true;
                        for(int j = 0; j < jsonArray2.length(); j++){
                            JSONObject jo= jsonArray2.getJSONObject(j);
                            try {
                                String type = jo.getString("typ");
                                if (type.equals("IT")) {
                                    Log.d(TAG, "found IT : ");
                                    it = jo.getInt("hodnota");
                                }
                                if (type.equals("OT")) {
                                    Log.d(TAG, "found OT : ");
                                    ot = jo.getInt("hodnota");
                                }
                                if (type.equals("IH")) {
                                    Log.d(TAG, "found IH : ");
                                    ih = jo.getInt("hodnota");
                                }
                                if (type.equals("OH")) {
                                    Log.d(TAG, "found OH : ");
                                    oh = jo.getInt("hodnota");
                                }
                                if (type.equals("P")) {
                                    Log.d(TAG, "found P : ");
                                    p = jo.getBoolean("hodnota");
                                }
                                if (type.equals("W")) {
                                    Log.d(TAG, "found W : ");
                                    w = jo.getInt("hodnota");
                                }
                                if (type.equals("B")) {
                                    Log.d(TAG, "found B : ");
                                    b = jo.getInt("hodnota");
                                }
                            }catch(Exception e){
                                Log.i(TAG, "Unable to read value in JSON, setting 0");
                            }
                            String timeStamp = jo.getString("cas");
                            timeStampGregCal = parseDateFromVcelickaApi(timeStamp);
                            // parse date from tomo API time format (day.month.year.hour.minute)
                        }
                        Log.i(TAG, "I will add new record to list with timestamp: " + timeStampGregCal.get(Calendar.HOUR_OF_DAY) + ":" + timeStampGregCal.get(Calendar.MINUTE));
                        Log.i(TAG, "Float value of timestamp: " + timeStampGregCal.getTime().getTime());
                        hiveList.add(new HiveBaseInfo(hiveId, hiveName, ot, it, oh, ih, w, p, b, timeStampGregCal));
                        ListView menuListView;
                        menuListView = findViewById(R.id.hiveAllDetailsListView);
                        menuListView.setAdapter(allAdapter);
                    }
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, "Login Error: " + e.getMessage());
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    // parse date from tomo API time format (day.month.year.hour.minute)
    public GregorianCalendar parseDateFromVcelickaApi(String timeStamp){
        String[] timeStampParts = timeStamp.split(" ", -1);
        String[] dateParts = timeStampParts[0].split("-", -1);
        String[] timeParts = timeStampParts[1].split(":", -1);
        int year=0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
        for (int s = 0; s < dateParts.length; s++) {
            if (s == 0) {
                year = Integer.parseInt(dateParts[s]);
            }
            if (s == 1) {
                month = Integer.parseInt(dateParts[s]);
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
        getMenuInflater().inflate(R.menu.menu, menu);
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
}
