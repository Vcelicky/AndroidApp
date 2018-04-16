package com.example.jozef.vcelicky;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class LimitValuesSettingsActivity extends AppCompatActivity  {

    private static String TAG = "LimitValuesSettingsActivity";
    int temperature_in_up_limit;
    int temperature_in_down_limit;
    int weight_limit;
    int temperature_out_up_limit;
    int temperature_out_down_limit;
    int humidity_in_up_limit;
    int humidity_in_down_limit;
    int humidity_out_up_limit;
    int humidity_out_down_limit;
    int batery_limit;
    String hiveId;
    String hiveName;
    String token;
    int userId;

    EditText edit_text_temperature_in_up_limit;
    EditText edit_text_temperature_in_down_limit;
    EditText edit_text_weight_limit;
    EditText edit_text_temperature_out_up_limit;
    EditText edit_text_temperature_out_down_limit;
    EditText edit_text_humidity_in_up_limit;
    EditText edit_text_humidity_in_down_limit;
    EditText edit_text_humidity_out_up_limit;
    EditText edit_text_humidity_out_down_limit;
    EditText edit_text_batery_limit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_values_settings);

        Intent intent = getIntent();
        hiveId =  intent.getExtras().getString("hiveId");
        hiveName = intent.getExtras().getString("hiveName");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hiveName);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        token =  db.getUserDetails().get("token");
        userId = Integer.parseInt(db.getUserDetails().get("id"));

        loadLimitValues(hiveId, userId, token);

        //Arrow back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadLimitValues (final String hiveId, int userId, String token){

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
            return;
        }



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
                                temperature_in_up_limit = json.getInt("temperature_in_up_limit");
                                temperature_in_down_limit = json.getInt("temperature_in_down_limit");
                                weight_limit = json.getInt("weight_limit");
                                temperature_out_up_limit = json.getInt("temperature_out_up_limit");
                                temperature_out_down_limit =json.getInt("temperature_out_down_limit");
                                humidity_in_up_limit = json.getInt("humidity_in_up_limit");
                                humidity_in_down_limit = json.getInt("humidity_in_down_limit");
                                humidity_out_up_limit = json.getInt("humidity_out_up_limit");
                                humidity_out_down_limit =json.getInt("humidity_out_down_limit");
                                batery_limit = json.getInt("batery_limit");
                            }catch(Exception e){
                                Log.i(TAG, "NULL value loaded, saving variable with 0");
                            }

                        setLoadedLimitValuesToGuiEditBoxes();
                    } catch (Exception e) {
                        // JSON error
                        e.printStackTrace();
                        Log.e(TAG, " Error: " + e.getMessage());
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
              //      Log.e(TAG, " ErrorBBB: " + error.getMessage());
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

    public void setLimitValues (final String hiveId, int userId, String token){

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Log.i(TAG, "PUT on servers limit values method");
        String tag_json_obj = "json_obj_req";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("device_id", hiveId);
            jsonBody.put("token", token);
            jsonBody.put("it_u", temperature_in_up_limit);
            jsonBody.put("it_d", temperature_in_down_limit);
            jsonBody.put("ot_u",temperature_out_up_limit );
            jsonBody.put( "ot_d", temperature_out_down_limit);
            jsonBody.put("ih_u",humidity_in_up_limit );
            jsonBody.put("ih_d", humidity_in_down_limit);
            jsonBody.put("oh_u", humidity_out_up_limit);
            jsonBody.put("oh_d", humidity_out_down_limit);
            jsonBody.put("w", weight_limit);
            jsonBody.put( "b",batery_limit );
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
               AppConfig.URL_PUT_SET_LIMIT_VALUES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response" + response.toString());
                try {
                    String result = response.getString("error");
                    if (result.equals("false")){
                        Log.i(TAG, "Save was successfull");
                        Toast.makeText(getApplicationContext(),
                                "Uloženie bolo úspešné", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Zmeny sa nepodarilo uložiť: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void resetLimitValues (final String hiveId, final int userId, final String token){

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),
                    R.string.no_service, Toast.LENGTH_LONG)
                    .show();
            return;
        }


        Log.i(TAG, "PUT on servers limit values method");
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
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                AppConfig.URL_PUT_RESET_LIMIT_VALUES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Response" + response.toString());
                try {
                    String result = response.getString("error");
                    if (result.equals("false")){
                        Log.i(TAG, "Reset was successfull");
                        Toast.makeText(getApplicationContext(),
                                "Predvolené hodnoty boli nastavené", Toast.LENGTH_LONG).show();
                        loadLimitValues(hiveId, userId, token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public void send(View view) {
        Log.i("TAG", "SEND function ");
        if (!isSomethingNew()){
            Toast.makeText(getApplicationContext(),
                    "Nebola vykonaná žiadna zmena", Toast.LENGTH_LONG).show();
            return;
        }
        if (minIsBiggerThanMax()){
            return;
        }

        loadNewValuesFromGui();
        setLimitValues(hiveId, userId, token);
    }

    public boolean isSomethingNew(){
        edit_text_temperature_in_up_limit = findViewById(R.id.editTextTempInMax);
        edit_text_temperature_in_down_limit = findViewById(R.id.editTextTempInMin);
        edit_text_weight_limit  = findViewById(R.id.editTextWeiMax);
        edit_text_temperature_out_up_limit  = findViewById(R.id.editTextTempOutMax);
        edit_text_temperature_out_down_limit =  findViewById(R.id.editTextTempOutMin);
        edit_text_humidity_in_up_limit =  findViewById(R.id.editTextHumInMax);
        edit_text_humidity_in_down_limit =  findViewById(R.id.editTextHumInMin);
        edit_text_humidity_out_up_limit =  findViewById(R.id.editTextHumOutMax);
        edit_text_humidity_out_down_limit =  findViewById(R.id.editTextHumOutMin);
        edit_text_batery_limit = findViewById(R.id.editTextBatMin);

        if ( temperature_in_up_limit != Integer.parseInt(edit_text_temperature_in_up_limit.getText().toString())){return true;}
        if ( temperature_in_down_limit != Integer.parseInt(edit_text_temperature_in_down_limit.getText().toString())){return true;}
        if (  weight_limit != Integer.parseInt(edit_text_weight_limit.getText().toString())){return true;}
        if ( temperature_out_up_limit != Integer.parseInt(edit_text_temperature_out_up_limit.getText().toString())){return true;}
        if ( temperature_out_down_limit != Integer.parseInt(edit_text_temperature_out_down_limit.getText().toString())){return true;}
        if (  humidity_in_up_limit != Integer.parseInt(edit_text_humidity_in_up_limit.getText().toString())){return true;}
        if (  humidity_in_down_limit != Integer.parseInt(edit_text_humidity_in_down_limit.getText().toString())){return true;}
        if (  humidity_out_up_limit != Integer.parseInt(edit_text_humidity_out_up_limit.getText().toString())){return true;}
        if (  humidity_out_down_limit != Integer.parseInt(edit_text_humidity_out_down_limit.getText().toString())){return true;}
        if (  batery_limit != Integer.parseInt(edit_text_batery_limit.getText().toString())){return true;}
        return false;
    }

    public boolean minIsBiggerThanMax(){
        edit_text_temperature_in_up_limit = findViewById(R.id.editTextTempInMax);
        edit_text_temperature_in_down_limit = findViewById(R.id.editTextTempInMin);
        edit_text_weight_limit  = findViewById(R.id.editTextWeiMax);
        edit_text_temperature_out_up_limit  = findViewById(R.id.editTextTempOutMax);
        edit_text_temperature_out_down_limit =  findViewById(R.id.editTextTempOutMin);
        edit_text_humidity_in_up_limit =  findViewById(R.id.editTextHumInMax);
        edit_text_humidity_in_down_limit =  findViewById(R.id.editTextHumInMin);
        edit_text_humidity_out_up_limit =  findViewById(R.id.editTextHumOutMax);
        edit_text_humidity_out_down_limit =  findViewById(R.id.editTextHumOutMin);
        edit_text_batery_limit = findViewById(R.id.editTextBatMin);

        if ( Integer.parseInt(edit_text_temperature_in_up_limit.getText().toString())<= Integer.parseInt(edit_text_temperature_in_down_limit.getText().toString())){
            Toast.makeText(getApplicationContext(), "Maximálna hodnota musí byť väčšia ako minimálna", Toast.LENGTH_LONG).show();
            return true;
        }
        if ( Integer.parseInt(edit_text_temperature_out_up_limit.getText().toString())<= Integer.parseInt(edit_text_temperature_out_down_limit.getText().toString())){
            Toast.makeText(getApplicationContext(), "MAX must be bigger than MIN", Toast.LENGTH_LONG).show();
            return true;
        }

        if ( Integer.parseInt(edit_text_humidity_in_up_limit.getText().toString())<= Integer.parseInt(edit_text_humidity_in_down_limit.getText().toString())){
            Toast.makeText(getApplicationContext(), "MAX must be bigger than MIN", Toast.LENGTH_LONG).show();
            return true;
        }

        if ( Integer.parseInt(edit_text_humidity_out_up_limit.getText().toString())<= Integer.parseInt(edit_text_humidity_out_down_limit.getText().toString())){
            Toast.makeText(getApplicationContext(), "MAX must be bigger than MIN", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void reset(View view) {
        Log.i("MainActivity", "RESET function ");
        resetLimitValues(hiveId, userId, token);
    }

    public void loadNewValuesFromGui(){
        Log.i("TAG", "loadNewValuesFromGui method ");

        edit_text_temperature_in_up_limit = findViewById(R.id.editTextTempInMax);
        edit_text_temperature_in_down_limit = findViewById(R.id.editTextTempInMin);
        edit_text_weight_limit  = findViewById(R.id.editTextWeiMax);
        edit_text_temperature_out_up_limit  = findViewById(R.id.editTextTempOutMax);
        edit_text_temperature_out_down_limit =  findViewById(R.id.editTextTempOutMin);
        edit_text_humidity_in_up_limit =  findViewById(R.id.editTextHumInMax);
        edit_text_humidity_in_down_limit =  findViewById(R.id.editTextHumInMin);
        edit_text_humidity_out_up_limit =  findViewById(R.id.editTextHumOutMax);
        edit_text_humidity_out_down_limit =  findViewById(R.id.editTextHumOutMin);
        edit_text_batery_limit = findViewById(R.id.editTextBatMin);

        temperature_in_up_limit = Integer.parseInt(edit_text_temperature_in_up_limit.getText().toString());
        temperature_in_down_limit = Integer.parseInt(edit_text_temperature_in_down_limit.getText().toString());
        weight_limit = Integer.parseInt(edit_text_weight_limit.getText().toString());
        temperature_out_up_limit = Integer.parseInt(edit_text_temperature_out_up_limit.getText().toString());
        temperature_out_down_limit = Integer.parseInt(edit_text_temperature_out_down_limit.getText().toString());
        humidity_in_up_limit = Integer.parseInt(edit_text_humidity_in_up_limit.getText().toString());
        humidity_in_down_limit = Integer.parseInt(edit_text_humidity_in_down_limit.getText().toString());
        humidity_out_up_limit = Integer.parseInt(edit_text_humidity_out_up_limit.getText().toString());
        humidity_out_down_limit = Integer.parseInt(edit_text_humidity_out_down_limit.getText().toString());
        batery_limit = Integer.parseInt(edit_text_batery_limit.getText().toString());
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

    public void setLoadedLimitValuesToGuiEditBoxes(){
        Log.i(TAG, "setLoadedLimitValuesToGuiEditBoxes method  ");
        edit_text_temperature_in_up_limit = findViewById(R.id.editTextTempInMax);
        edit_text_temperature_in_down_limit = findViewById(R.id.editTextTempInMin);
        edit_text_weight_limit  = findViewById(R.id.editTextWeiMax);
        edit_text_temperature_out_up_limit  = findViewById(R.id.editTextTempOutMax);
        edit_text_temperature_out_down_limit =  findViewById(R.id.editTextTempOutMin);
        edit_text_humidity_in_up_limit =  findViewById(R.id.editTextHumInMax);
        edit_text_humidity_in_down_limit =  findViewById(R.id.editTextHumInMin);
        edit_text_humidity_out_up_limit =  findViewById(R.id.editTextHumOutMax);
        edit_text_humidity_out_down_limit =  findViewById(R.id.editTextHumOutMin);
        edit_text_batery_limit = findViewById(R.id.editTextBatMin);

        edit_text_temperature_in_up_limit.setText(Integer.toString(temperature_in_up_limit));
        edit_text_temperature_in_down_limit.setText(Integer.toString(temperature_in_down_limit));
        edit_text_weight_limit.setText(Integer.toString(weight_limit));
        edit_text_temperature_out_up_limit.setText(Integer.toString(temperature_out_up_limit));
        edit_text_temperature_out_down_limit.setText(Integer.toString(temperature_out_down_limit));
        edit_text_humidity_in_up_limit.setText(Integer.toString(humidity_in_up_limit));
        edit_text_humidity_in_down_limit.setText(Integer.toString(humidity_in_down_limit));
        edit_text_humidity_out_up_limit.setText(Integer.toString(humidity_out_up_limit));
        edit_text_humidity_out_down_limit.setText(Integer.toString(humidity_out_down_limit));
        edit_text_batery_limit.setText(Integer.toString(batery_limit));
    }
}
