package com.example.jozef.vcelicky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.jozef.vcelicky.helper.FieldChecker;
import com.example.jozef.vcelicky.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editMail;
    private EditText editPhone;
    private EditText editDevCount;
    private EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        editName = findViewById(R.id.editName);
        editMail = findViewById(R.id.editMail);
        editPhone = findViewById(R.id.editPhone);
        editDevCount = findViewById(R.id.editDevCount);
        editNotes = findViewById(R.id.editNotes);
    }

    public void send(View view){
        editName.setError(null);
        editMail.setError(null);
        editPhone.setError(null);
        editDevCount.setError(null);
        editNotes.setError(null);

        boolean cancel = false;
        View focusView = null;

        String name = editName.getText().toString().trim();
        String email = editMail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        int devCount = 0;
        if(!editDevCount.getText().toString().isEmpty()) {
            try {
                devCount = Integer.parseInt(editDevCount.getText().toString());
            }catch(Exception e){
                devCount = 0;
            }
        }
        String notes = editNotes.getText().toString().trim();

        String tag_json_obj = "json_obj_req";

        // Check for empty device count
        if (devCount == 0){
            editDevCount.setError(getString(R.string.error_field_required));
            focusView = editDevCount;
            cancel = true;
        }

        // Check for empty phone number
        if (TextUtils.isEmpty(phone)){
            editPhone.setError(getString(R.string.error_field_required));
            focusView = editPhone;
            cancel = true;
        } else if(!FieldChecker.isPhoneNumberValid(phone)){
            editPhone.setError(getString(R.string.error_invalid_phone));
            focusView = editPhone;
            cancel = true;
        }

        // Check for empty e-mail address
        if (TextUtils.isEmpty(email)){
            editMail.setError(getString(R.string.error_field_required));
            focusView = editMail;
            cancel = true;
        } else if (!FieldChecker.isEmailValid(email)) {
            editMail.setError(getString(R.string.error_invalid_email));
            focusView = editMail;
            cancel = true;
        }

        // Check for empty name and surname
        if (TextUtils.isEmpty(name)){
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        } else if(!FieldChecker.isNameValid(name)){
            editName.setError(getString(R.string.error_invalid_name));
            focusView = editName;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {

            SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", name);
                jsonBody.put("email", email);
                jsonBody.put("phone", phone);
                jsonBody.put("device_count", devCount);
                jsonBody.put("notes", notes);
                jsonBody.put("user_id", user.get("id"));
                jsonBody.put("token", user.get("token"));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            final String requestBody = jsonBody.toString();
            Log.i("order", requestBody);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    AppConfig.URL_ORDER, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Objednávka bola úspešne vytvorená", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                Log.e("error", "Error response: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        "Error response: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Objednávka bola úspešne vytvorená", Toast.LENGTH_LONG).show();
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
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            // Adding request to request queue
            //AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }
}
