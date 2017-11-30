package com.example.jozef.vcelicky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;
import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mail, pass;
    private ConstraintLayout main, error, reg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v("LoginAct", "Startin' activity");
        mail = findViewById(R.id.editMail);
        pass = findViewById(R.id.editPass);
        main = findViewById(R.id.mainLayout);
        error = findViewById(R.id.errorLayout);
        reg = findViewById(R.id.registerLayout);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.i("LoginAct", "Prihlasujem bez overenia...");
        }
    }

    public void login (View view){
        String email = mail.getText().toString().trim();
        String password = pass.getText().toString().trim();

        // Check for empty data in the form
        if(!email.isEmpty() && !password.isEmpty()){
            // login user
            if(isOnline()) {
                checkLogin(email, password);
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "No connection to the Internet", Toast.LENGTH_LONG)
                        .show();
            }
        }
        else{
            main.setAlpha((float)0.5);
            error.setVisibility(View.VISIBLE);
        }
    }

    public void close(View view){
        error.setVisibility(View.INVISIBLE);
        main.setAlpha(1);
    }

    public void hideKeyboard(View view){
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pDialog.setMessage("Logging in ...");
        showDialog();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String requestBody = jsonBody.toString();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    boolean er = response.getBoolean("error");

                    // Check for error node in json
                    if (!er) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String id = response.getString("id");
                        String role = response.getString("role_id");

                        JSONObject user = response.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String token = user.getString("token");

                        // Inserting row in users table
                        db.addUser(name, email, role);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        main.setAlpha((float)0.5);
                        error.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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

    public void showRegisterForm(View view){
        main.setAlpha((float)0.5);
        reg.setVisibility(View.VISIBLE);
    }

    public void register(View view){
        EditText editName = findViewById(R.id.editName);
        EditText editMail = findViewById(R.id.editMail);
        EditText editPass = findViewById(R.id.editPass);
        EditText editPassAgain = findViewById(R.id.editPassAgain);

        String name = editName.getText().toString().trim();
        String mail = editMail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();
        String passAgain = editPassAgain.getText().toString().trim();

        if(pass.equals(passAgain)){
            
        }
        else{
            //nejaka hlaska na error ze sa nezhoduju hesla
        }
    }
}
