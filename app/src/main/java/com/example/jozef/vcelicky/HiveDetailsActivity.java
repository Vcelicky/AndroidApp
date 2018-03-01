package com.example.jozef.vcelicky;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HiveDetailsActivity extends MainActivity{

    ArrayList<HiveBaseInfo> hiveList;
    int hiveID;
    String token;
    final String TAG = "HiveDetailsActivity";
    String hiveName;
    ListView menuListView;
    LineChart chart;
    List<Entry> entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive_details);

        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        hiveID =  intent.getIntExtra("hiveId",0);
        hiveName = intent.getExtras().getString("hiveName");
        token =  intent.getExtras().getString("token");
        toolbar.setTitle("Včelí úľ " + hiveName);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Just fake data for testing
        //createTestData();

        //Tab 1 setup (Temperature)
        TabHost.TabSpec spec = host.newTabSpec("temperature");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString((R.string.temperature)));
        host.addTab(spec);

        //Tab 2 setup (Humidity)
        spec = host.newTabSpec("humidity");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.humidity));
        host.addTab(spec);

        //Tab 3 setup (Weight)
        spec = host.newTabSpec("weight");
        spec.setContent(R.id.tab3);
        spec.setIndicator(getString(R.string.weight));
        host.addTab(spec);

        //Tab 4 setup (Battery)
        spec = host.newTabSpec("battery");
        spec.setContent(R.id.tab4);
        spec.setIndicator(getString(R.string.battery));
        host.addTab(spec);

        //Tab 5 setup (Position)
        spec = host.newTabSpec("accelerometer");
        spec.setContent(R.id.tab5);
        spec.setIndicator(getString(R.string.accelerometer));
        host.addTab(spec);

        //Temperature tab
        ArrayAdapter<HiveBaseInfo> temperatureAdapter;
        temperatureAdapter = new AdapterHiveTemperatureDetails(this, hiveList);
        menuListView = findViewById(R.id.temperatureListView);
        menuListView.setAdapter(temperatureAdapter);
        chart = findViewById(R.id.temperatureChart);
        entries = new ArrayList<Entry>();
        float xIndex = 0;
        for(HiveBaseInfo value : hiveList){
            entries.add(new Entry(xIndex, value.getInsideTemperature()));
            xIndex++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Vnútorná teplota");
        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);
//        xIndex = 0;
//        for(HiveBaseInfo value : hiveList){
//            entries.add(new Entry(xIndex, value.getOutsideTemperature()));
//            xIndex++;
//        }
//        dataSet = new LineDataSet(entries, "Vonkajšia teplota");
//        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getAxisRight().setEnabled(false);

        //Humidity tab
        ArrayAdapter<HiveBaseInfo> humidityAdapter;
        humidityAdapter = new AdapterHiveHumidityDetails(this, hiveList);
        menuListView = findViewById(R.id.humidityListView);
        menuListView.setAdapter(humidityAdapter);
        chart = findViewById(R.id.humidityChart);
        entries = new ArrayList<Entry>();
        xIndex = 0;
        for(HiveBaseInfo value : hiveList){
            entries.add(new Entry(xIndex, value.getInsideHumidity()));
            xIndex++;
        }
        dataSet = new LineDataSet(entries, "Vlhkosť");
        lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getAxisRight().setEnabled(false);

        //Weight tab
        ArrayAdapter<HiveBaseInfo> weightAdapter;
        weightAdapter = new AdapterHiveWeightDetails(this, hiveList);
        menuListView = findViewById(R.id.weightListView);
        menuListView.setAdapter(weightAdapter);
        chart = findViewById(R.id.weightChart);
        entries = new ArrayList<Entry>();
        xIndex = 0;
        for(HiveBaseInfo value : hiveList){
            entries.add(new Entry(xIndex, value.getWeight()));
            xIndex++;
        }
        dataSet = new LineDataSet(entries, "Hmotnosť");
        lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getAxisRight().setEnabled(false);

        //Battery tab
        ArrayAdapter<HiveBaseInfo> batteryAdapter;
        batteryAdapter = new AdapterHiveBatteryDetails(this, hiveList);
        menuListView = findViewById(R.id.batteryListView);
        menuListView.setAdapter(batteryAdapter);
        chart = findViewById(R.id.batteryChart);
        entries = new ArrayList<Entry>();
        xIndex = 0;
        for(HiveBaseInfo value : hiveList){
            entries.add(new Entry(xIndex, value.getBattery()));
            xIndex++;
        }
        dataSet = new LineDataSet(entries, "Batéria");
        lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getAxisRight().setEnabled(false);

        //No need to load real data if there aren't any :)
        //loadHiveBaseInfoServerReq(hiveName);
    }

    public void createTestData(){
        hiveList = new ArrayList<>();
        Calendar ts =  new GregorianCalendar(1995, 2, 29, 11, 22);
        ts.set(1995, 2, 29, 11, 22) ;
        hiveList.add(new HiveBaseInfo("1234", "Včelí úľ Alfa", 55 , 45, 70, 80, 69, new GregorianCalendar(1995, 2, 29, 11, 20),true,99));
        hiveList.add(new HiveBaseInfo("1235", "Včelí úľ Alfa", 40 , 43, 68, 78,50, new GregorianCalendar(1995, 2, 29, 11, 30),true,99));
        hiveList.add(new HiveBaseInfo("1236", "Včelí úľ Alfa", 30 , 42, 68, 76,60, new GregorianCalendar(1995, 2, 29, 11, 40),true,99));
        hiveList.add(new HiveBaseInfo("1237", "Včelí úľ Alfa", 40 , 45, 50, 74,53, new GregorianCalendar(1995, 2, 29, 11, 50),true,99));
        hiveList.add(new HiveBaseInfo("1238", "Včelí úľ Alfa", 35 , 43, 68, 72,56, new GregorianCalendar(1995, 2, 29, 12, 00),true,99));
        hiveList.add(new HiveBaseInfo("1239", "Včelí úľ Alfa", 32 , 49, 61, 75,89, new GregorianCalendar(1995, 2, 29, 12, 10),true,99));
        hiveList.add(new HiveBaseInfo("1240", "Včelí úľ Alfa", 36 , 45, 68, 80,66, new GregorianCalendar(1995, 2, 29, 12, 20),true,99));
        hiveList.add(new HiveBaseInfo("1241", "Včelí úľ Alfa", 36 , 45, 68, 85,66, new GregorianCalendar(1995, 2, 29, 12, 30),true,99));
        hiveList.add(new HiveBaseInfo("1242", "Včelí úľ Alfa", 36 , 45, 68, 72,66, new GregorianCalendar(1995, 2, 29, 12, 40),true,99));
        hiveList.add(new HiveBaseInfo("1243", "Včelí úľ Alfa", 36 , 45, 68, 75,66, new GregorianCalendar(1995, 2, 29, 12, 50),true,98));

    }

//    public void loadHiveBaseInfoServerReq( String hiveName){
//
//        Log.d(TAG, "Load Hive Details Info method");
//        String tag_json_obj = "json_obj_req";
//        JSONObject jsonBody = new JSONObject();
//
//        Log.d(TAG, ": " + hiveName);
//        Log.d(TAG, ": " + token);
//        try {
//            jsonBody.put("device_name", hiveName);
//            jsonBody.put("token", token);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }
//        final String requestBody = jsonBody.toString();
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                AppConfig.URL_GET_HIVE_INFO_DETAILS, null, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "Load Hive Base Info From Server Response: " + response.toString());
//
//                try {
//                    int it = 0;
//                    int ot = 0;
//                    int h = 0,w = 0, b = 0;
//                    boolean p = true;
//
//                    JSONArray jsonArray = response.getJSONArray("data");
//                    for(int i=0;i<jsonArray.length();i++){
//                        JSONArray jsonArray2 = jsonArray.getJSONArray(i); // proccess additional []
//                        int recordValue = 0;
//                        for(int j=0;j<jsonArray2.length();j++){
//                            JSONObject jo= jsonArray2.getJSONObject(j);
//                            String type = jo.getString("typ");
//
//                            int valueTypesCount = 6; // make this constant later or change this code
//
//                            if (type.equals("IT")) {
//                                Log.d(TAG, "found IT : ");
//                                it = jo.getInt("hodnota");
//                            }
//                            if (type.equals("OT")) {
//                                Log.d(TAG, "found OT : ");
//                                ot = jo.getInt("hodnota");
//                            }
//                            if (type.equals("H")) {
//                                Log.d(TAG, "found H : ");
//                                h = jo.getInt("hodnota");
//                            }
//                            if (type.equals("P")) {
//                                Log.d(TAG, "found P : ");
//                                p = jo.getBoolean("hodnota");
//                            }
//                            if (type.equals("W")) {
//                                Log.d(TAG, "found W : ");
//                                w = jo.getInt("hodnota");
//                            }
//                            if (type.equals("B")) {
//                                Log.d(TAG, "found B : ");
//                                b = jo.getInt("hodnota");
//                            }
//                            String timeStamp = jo.getString("cas");
//                            GregorianCalendar timeStampGregCal = parseDateFromVcelickaApi(timeStamp);
//                            // parse date from tomo API time format (day.month.year.hour.minute)
//
//                            Log.d(TAG, "Cas: "+timeStamp);
//
//                            recordValue++;
//                            if (recordValue == valueTypesCount) {                     // every record have 4 values after that new record is processed
//                                Log.d(TAG, "I will add new record to list: ");
//                                hiveList.add(new HiveBaseInfo(0, "hiveNameIsNotUsedHere", ot, it, h, w,timeStampGregCal,p,b));
//                                menuListView = (ListView) findViewById(R.id.hiveDetailsListView);
//                                menuListView.setAdapter(allAdapter);
//                                recordValue = 0;
//                            }
//                        }
//
//                    }
//
//                } catch (Exception e) {
//                    // JSON error
//                    e.printStackTrace();
//                    Log.e(TAG, "Login Error: " + e.getMessage());
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee){
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                    return null;
//                }
//            }
//
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }

    // parse date from tomo API time format (day.month.year.hour.minute)
    public GregorianCalendar parseDateFromVcelickaApi(String timeStamp){
        String[] timeStampParts = timeStamp.split("\\.", -1);
        int year=0, month = 0, day = 0, hour = 0, minute = 0;
        for (int s=0; s<timeStampParts.length;s++){
            Log.d(TAG, "P: "+timeStampParts[s]);
            if (s == 0){
                day = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 1){
                month = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 2){
                year = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 3){
                hour = Integer.parseInt(timeStampParts[s]);
            }
            if (s == 4){
                minute = Integer.parseInt(timeStampParts[s]);
            }
        }
        return new GregorianCalendar(year, month, day, hour, minute);

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
}
