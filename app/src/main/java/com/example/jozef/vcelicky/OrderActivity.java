package com.example.jozef.vcelicky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;
import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = OrderActivity.class.getSimpleName();
    private EditText editName;
    private EditText editLocation;
    private CheckBox checkSms, checkEmail;
    private EditText editNotes;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        editName = findViewById(R.id.editName);
        editLocation = findViewById(R.id.editMail);
        checkSms = findViewById(R.id.sms);
        checkEmail = findViewById(R.id.email);
        editNotes = findViewById(R.id.editNotes);

        //Arrow back
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());
    }

    public void send(View view){
        editName.setError(null);
        editLocation.setError(null);
        checkSms.setChecked(false);
        checkEmail.setChecked(false);
        editNotes.setError(null);

        boolean cancel = false;
        View focusView = null;

        String name = editName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        boolean sms = checkSms.isChecked();
        boolean email = checkEmail.isChecked();
        String notes = editNotes.getText().toString().trim();

        String tag_json_obj = "json_obj_req";

        // Check for empty e-mail address
        if (TextUtils.isEmpty(location)){
            editLocation.setError(getString(R.string.error_field_required));
            focusView = editLocation;
            cancel = true;
        }

        // Check for empty name and surname
        if (TextUtils.isEmpty(name)){
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {

            SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails(session.getLoggedUser());

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id", Integer.parseInt(user.get("id")));
                jsonBody.put("hive_name", name);
                jsonBody.put("hive_address", location);
                jsonBody.put("SMS", sms);
                jsonBody.put("E-mail", email);
                jsonBody.put("notes", notes);
                jsonBody.put("token", user.get("token"));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            final String requestBody = jsonBody.toString();
            Log.i(TAG, requestBody);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    AppConfig.URL_ORDER, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Objednávka bola úspešne vytvorená", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error response: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error response: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    int mStatusCode = response.statusCode;
                    Log.i(TAG, "Status code is " + mStatusCode);
                    return super.parseNetworkResponse(response);
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
