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
import com.example.jozef.vcelicky.helper.HourAxisValueFormatter;
import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HiveDetailsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "HiveDetailsActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        Intent intent = getIntent();
        String hiveId =  intent.getExtras().getString("hiveId");
        String hiveName = intent.getExtras().getString("hiveName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Včelí úľ " + hiveName);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String token =  db.getUserDetails().get("token");
        int userId = Integer.parseInt(db.getUserDetails().get("id"));

        loadHiveDetailInfoServerReq(hiveId, hiveName, userId, token);

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
    }

//    public void createTestData(){
//        notificationInfoList = new ArrayList<>();
//        Calendar ts =  new GregorianCalendar(1995, 2, 29, 11, 22);
//        ts.set(1995, 2, 29, 11, 22) ;
//        notificationInfoList.add(new HiveBaseInfo("1234", "Včelí úľ Alfa", 55 , 45, 70, 80, 69, new GregorianCalendar(1995, 2, 29, 11, 20),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1235", "Včelí úľ Alfa", 40 , 43, 68, 78,50, new GregorianCalendar(1995, 2, 29, 11, 30),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1236", "Včelí úľ Alfa", 30 , 42, 68, 76,60, new GregorianCalendar(1995, 2, 29, 11, 40),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1237", "Včelí úľ Alfa", 40 , 45, 50, 74,53, new GregorianCalendar(1995, 2, 29, 11, 50),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1238", "Včelí úľ Alfa", 35 , 43, 68, 72,56, new GregorianCalendar(1995, 2, 29, 12, 00),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1239", "Včelí úľ Alfa", 32 , 49, 61, 75,89, new GregorianCalendar(1995, 2, 29, 12, 10),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1240", "Včelí úľ Alfa", 36 , 45, 68, 80,66, new GregorianCalendar(1995, 2, 29, 12, 20),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1241", "Včelí úľ Alfa", 36 , 45, 68, 85,66, new GregorianCalendar(1995, 2, 29, 12, 30),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1242", "Včelí úľ Alfa", 36 , 45, 68, 72,66, new GregorianCalendar(1995, 2, 29, 12, 40),true,99));
//        notificationInfoList.add(new HiveBaseInfo("1243", "Včelí úľ Alfa", 36 , 45, 68, 75,66, new GregorianCalendar(1995, 2, 29, 12, 50),true,98));
//    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_hive_details;
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
                        }
                    setupGUI();
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

    private void setupGUI() {
        ListView menuListView;
        LineChart chart;
        List<Entry> entries;

        //Temperature tab
        ArrayAdapter<HiveBaseInfo> temperatureAdapter;
        temperatureAdapter = new AdapterHiveTemperatureDetails(this, hiveList);
        menuListView = findViewById(R.id.temperatureListView);
        menuListView.setAdapter(temperatureAdapter);
        chart = findViewById(R.id.temperatureChart);
        entries = new ArrayList<Entry>();
        for(int i = hiveList.size() - 1; i >= 0; i--){
            entries.add(new Entry(hiveList.get(i).getTimeStamp().getTime().getTime(), hiveList.get(i).getOutsideTemperature()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Vnútorná teplota");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);
//        xIndex = 0;
//        for(HiveBaseInfo value : notificationInfoList){
//            entries.add(new Entry(xIndex, value.getOutsideTemperature()));
//            xIndex++;
//        }
//        dataSet = new LineDataSet(entries, "Vonkajšia teplota");
//        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setValueFormatter(new HourAxisValueFormatter(null));
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        //Humidity tab
        ArrayAdapter<HiveBaseInfo> humidityAdapter;
        humidityAdapter = new AdapterHiveHumidityDetails(this, hiveList);
        menuListView = findViewById(R.id.humidityListView);
        menuListView.setAdapter(humidityAdapter);
        chart = findViewById(R.id.humidityChart);
        ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
        //Outside humidity
        entries = new ArrayList<Entry>();
        for(int i = hiveList.size() - 1; i >= 0; i--){
            entries.add(new Entry(hiveList.get(i).getTimeStamp().getTime().getTime(), hiveList.get(i).getOutsideHumidity()));
        }
        dataSet = new LineDataSet(entries, "Vonkajšia vlhkosť");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        datasets.add(dataSet);
        //Inside humidity
        entries = new ArrayList<Entry>();
        for(int i = hiveList.size() - 1; i >= 0; i--){
            entries.add(new Entry(hiveList.get(i).getTimeStamp().getTime().getTime(), hiveList.get(i).getInsideHumidity()));
        }
        dataSet = new LineDataSet(entries, "Vnútorná vlhkosť");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        datasets.add(dataSet);
        lineData = new LineData(datasets);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setValueFormatter(new HourAxisValueFormatter(null));
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        //Weight tab
        ArrayAdapter<HiveBaseInfo> weightAdapter;
        weightAdapter = new AdapterHiveWeightDetails(this, hiveList);
        menuListView = findViewById(R.id.weightListView);
        menuListView.setAdapter(weightAdapter);
        chart = findViewById(R.id.weightChart);
        entries = new ArrayList<Entry>();
        for(int i = hiveList.size() - 1; i >= 0; i--){
            entries.add(new Entry(hiveList.get(i).getTimeStamp().getTime().getTime(), hiveList.get(i).getWeight()));
        }
        dataSet = new LineDataSet(entries, "Hmotnosť");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setValueFormatter(new HourAxisValueFormatter(null));
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        //Battery tab
        ArrayAdapter<HiveBaseInfo> batteryAdapter;
        batteryAdapter = new AdapterHiveBatteryDetails(this, hiveList);
        menuListView = findViewById(R.id.batteryListView);
        menuListView.setAdapter(batteryAdapter);
        chart = findViewById(R.id.batteryChart);
        entries = new ArrayList<Entry>();
        for(int i = hiveList.size() - 1; i >= 0; i--){
            entries.add(new Entry(hiveList.get(i).getTimeStamp().getTime().getTime(), hiveList.get(i).getBattery()));
        }
        dataSet = new LineDataSet(entries, "Batéria");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setValueFormatter(new HourAxisValueFormatter(null));
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        //Position tab
        ArrayAdapter<HiveBaseInfo> positionAdapter;
        positionAdapter = new AdapterHivePositionDetails(this, hiveList);
        menuListView = findViewById(R.id.accelerometerListView);
        menuListView.setAdapter(positionAdapter);
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





}
