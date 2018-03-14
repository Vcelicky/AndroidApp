package com.example.jozef.vcelicky;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.jozef.vcelicky.helper.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HiveDetailsActivity extends BaseActivity {

    final String TAG = "HiveDetailsActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    SQLiteHandler db;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        Intent intent = getIntent();
        final String hiveId =  intent.getExtras().getString("hiveId");
        final String hiveName = intent.getExtras().getString("hiveName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Včelí úľ " + hiveName);

        db = new SQLiteHandler(getApplicationContext());
        final String token =  db.getUserDetails().get("token");
        final int userId = Integer.parseInt(db.getUserDetails().get("id"));

        session = new SessionManager(getApplicationContext());

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

        setupGUI(hiveId);

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
        }
        else {
            update(hiveId, hiveName, userId, token);
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
                            update(hiveId, hiveName, userId, token);
                        }
                    }
                }
        );
    }

    private void update(String hiveId, String hiveName, int userId, String token){
        String from = dateFormat.format(new Date(0));
        String to = dateFormat.format(new Date().getTime());
        if(session.isFirstTime()) {
            Log.i(TAG, "Getting data for the first time");
            session.setFirstTime(false);
        }
        else{
            Log.i(TAG, "Most recent time stamp for hive " + hiveName + " is " + db.getMostRecentTimeStamp(hiveId));
            from = dateFormat.format(new Date(db.getMostRecentTimeStamp(hiveId) + 1000)); //1000 is one second on millis
        }
        loadHiveDetailInfoServerReq(hiveId, hiveName, userId, token, from, to);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_hive_details;
    }

    public void loadHiveDetailInfoServerReq(final String hiveId, final String hiveName, int userId, String token, String from, String to){

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
                            timeStampGregCal = parseDateFromVcelickaApi(timeStamp);
                            }
                        Log.i(TAG, "I will add new record to database with timestamp: " + dateFormat.format(new Date(timeStampGregCal.getTimeInMillis())));
                        Log.i(TAG, "Long value of timestamp: " + timeStampGregCal.getTimeInMillis());
                        db.addMeasurement(timeStampGregCal.getTimeInMillis(), it, ot, ih, oh, w, p, b, hiveName, hiveId);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    setupGUI(hiveId);
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, "Reading data error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Loading data error response: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        R.string.error_loading_data, Toast.LENGTH_LONG).show();
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

    private void setupGUI(String hiveId) {
        ListView menuListView;
        LineChart chart;
        List<Entry> entries;

        //Get entries from database
        hiveList.clear();
        hiveList = db.getAllMeasurements(hiveId);
        for(HiveBaseInfo hive : hiveList){
            String time = dateFormat.format(new Date(hive.getTime()));
            hive.setTimeStamp(parseDateFromVcelickaApi(time));
        }

        //Temperature tab
        ArrayAdapter<HiveBaseInfo> temperatureAdapter;
        temperatureAdapter = new AdapterHiveTemperatureDetails(this, hiveList);
        menuListView = findViewById(R.id.temperatureListView);
        menuListView.setAdapter(temperatureAdapter);
        chart = findViewById(R.id.temperatureChart);
        ArrayList<ILineDataSet> datasets = new ArrayList<>();
        //Outside temperature
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getOutsideTemperature()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Vonkajšia teplota");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        datasets.add(dataSet);
        //Inside temperature
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getInsideTemperature()));
        }
        dataSet = new LineDataSet(entries, "Vnútorná teplota");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        datasets.add(dataSet);
        //Mapping data on chart
        LineData lineData = new LineData(datasets);
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
        datasets = new ArrayList<>();
        //Outside humidity
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getOutsideHumidity()));
        }
        dataSet = new LineDataSet(entries, "Vonkajšia vlhkosť");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        datasets.add(dataSet);
        //Inside humidity
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getInsideHumidity()));
        }
        dataSet = new LineDataSet(entries, "Vnútorná vlhkosť");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        datasets.add(dataSet);
        //Mapping data on chart
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
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getWeight()));
        }
        dataSet = new LineDataSet(entries, "Hmotnosť");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        //Mapping data on chart
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
        entries = new ArrayList<>();
        for (int i = hiveList.size() - 1; i >= 0; i--) {
            entries.add(new Entry(hiveList.get(i).getTime(), hiveList.get(i).getBattery()));
        }
        dataSet = new LineDataSet(entries, "Batéria");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        //Mapping data on chart
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
}
