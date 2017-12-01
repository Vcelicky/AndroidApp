package com.example.jozef.vcelicky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }

    public void send(View view){
        EditText editName = findViewById(R.id.editName);
        EditText editMail = findViewById(R.id.editMail);
        EditText editPhone = findViewById(R.id.editPhone);
        EditText editDevCount = findViewById(R.id.editDevCount);
        EditText editNotes = findViewById(R.id.editNotes);

        String name = editName.getText().toString().trim();
        String mail = editMail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        int devCount = Integer.parseInt(editDevCount.getText().toString());
        String notes = editNotes.getText().toString().trim();

        String tag_json_obj = "json_obj_req";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", mail);
            jsonBody.put("phone", phone);
            jsonBody.put("device_count", devCount);
            jsonBody.put("notes", notes);
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
                } catch (UnsupportedEncodingException uee){
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
