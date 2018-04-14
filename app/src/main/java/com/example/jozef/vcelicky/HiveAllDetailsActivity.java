package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.jozef.vcelicky.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HiveAllDetailsActivity extends BaseActivity {

    final String TAG = "HiveAllDetailsActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    SQLiteHandler db;
    SessionManager session;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Intent intent = getIntent();
        final String hiveId = intent.getExtras().getString("hiveId");
        final String hiveName = intent.getExtras().getString("hiveName");
        final String hiveLocation = intent.getExtras().getString("hiveLocation");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Včelí úľ " + hiveName);
        setSupportActionBar(toolbar);

        db = new SQLiteHandler(getApplicationContext());
        final String token =  db.getUserDetails().get("token");
        final int userId = Integer.parseInt(db.getUserDetails().get("id"));

        session = new SessionManager(getApplicationContext());

        setupGUI(hiveId);

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
        }
        else {
            update(hiveId, hiveName, hiveLocation, userId, token);
        }

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
                            update(hiveId, hiveName, hiveLocation, userId, token);
                        }
                    }
                }
        );
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_hive_all_details;
    }

    private void update(String hiveId, String hiveName, String hiveLocation, int userId, String token){
        String from = dateFormat.format(new Date(0));
        String to = dateFormat.format(new Date().getTime());
        if(session.isFirstTime(hiveId)) {
            Log.i(TAG, "Getting data for the first time");
            session.setFirstTime(hiveId, false);
        }
        else{
            Log.i(TAG, "Most recent time stamp for hive " + hiveName + " is " + db.getMostRecentTimeStamp(hiveId));
            from = dateFormat.format(new Date(db.getMostRecentTimeStamp(hiveId) + 1000)); //1000 is one second on millis
        }
        loadHiveDetailInfoServerReq(hiveId, hiveName, hiveLocation, userId, token, from, to);
    }

    public void loadHiveDetailInfoServerReq(final String hiveId, final String hiveName, final String hiveLocation, int userId, String token, String from, String to){

        Log.i(TAG, "Load Hive Details Info method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();

        Log.d(TAG, ": " + hiveName);
        Log.d(TAG, ": " + token);
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("device_id", hiveId);
            jsonBody.put("token", token);
            jsonBody.put("from", from);
            jsonBody.put("to", to);
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
                            timeStampGregCal = parseDateFromVcelickaApi(true, timeStamp);
                            // parse date from tomo API time format (day.month.year.hour.minute)
                        }
                        Log.i(TAG, "I will add new record to list with timestamp: " + timeStampGregCal.get(Calendar.HOUR_OF_DAY) + ":" + timeStampGregCal.get(Calendar.MINUTE));
                        Log.i(TAG, "Long value of timestamp: " + timeStampGregCal.getTime().getTime());
                        db.addMeasurement(timeStampGregCal.getTimeInMillis(), it, ot, ih, oh, w, p, b, hiveId);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    setupGUI(hiveId);
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

    private void setupGUI(String hiveId){
        ListView menuListView;
        hiveList.clear();
        hiveList = db.getAllMeasurements(hiveId);
        if(hiveList.size() > 0){
            for (HiveBaseInfo hive : hiveList) {
                String time = dateFormat.format(new Date(hive.getTime()));
                hive.setTimeStamp(parseDateFromVcelickaApi(false, time));
            }
            ArrayAdapter<HiveBaseInfo> allAdapter;
            allAdapter = new AdapterHiveDetails(this, hiveList);
            menuListView = findViewById(R.id.hiveAllDetailsListView);
            menuListView.setAdapter(allAdapter);
        }
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getExtras().getString("hiveName") + ", " + intent.getExtras().getString("hiveLocation"));
    }
}
