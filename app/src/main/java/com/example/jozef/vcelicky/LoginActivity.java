package com.example.jozef.vcelicky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.jozef.vcelicky.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String BOUNDARY = "VcelickovyBoundary";
    private EditText mail, pass;
    private ConstraintLayout main, error, reg;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private EditText editName;
    private EditText editMail;
    private EditText editPass;
    private EditText editPassAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i("LoginAct", "Startin' activity");
        mail = findViewById(R.id.editMail);
        pass = findViewById(R.id.editPass);
        main = findViewById(R.id.mainLayout);
        error = findViewById(R.id.errorLayout);
        reg = findViewById(R.id.registerLayout);
        editName = findViewById(R.id.editName);
        editMail = findViewById(R.id.editRegMail);
        editPass = findViewById(R.id.editRegPass);
        editPassAgain = findViewById(R.id.editPassAgain);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());


// Temp - bypass login
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        Log.i("LoginAct", "Prihlasujem bez overenia...");
  //

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            if(!isOnline()){
                Toast.makeText(getApplicationContext(),
                        "Žiadne pripojenie k intenetu, aktualizácia dát bola neúspešná", Toast.LENGTH_LONG)
                        .show();
            }
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.i("LoginAct", "Prihlasujem bez overenia...");
        }
    }

    @Override
    public void onBackPressed() {
        if (reg.getVisibility() == View.VISIBLE){
            reg.setVisibility(View.INVISIBLE);
            main.setAlpha(1);
            editName.setText("");
            editMail.setText("");
            editPass.setText("");
            editPassAgain.setText("");
            editName.setError(null);
            editMail.setError(null);
            editPass.setError(null);
            editPassAgain.setError(null);
        }
        else{
            finish();
        }
    }

    public void login (View view){
        String email = mail.getText().toString().trim();
        String password = pass.getText().toString().trim();

        // Check for empty data in the form
        if(!email.isEmpty() && !password.isEmpty()){
            if(!FieldChecker.isEmailValid(email)){
                main.setAlpha((float)0.5);
                error.setVisibility(View.VISIBLE);
                return;
            }
            // login user
            if(isOnline()) {
                checkLogin(email, password);
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "Žiadne pripojenie k internetu", Toast.LENGTH_LONG)
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

        pDialog.setMessage("Prebieha prihlasovanie ...");
        showDialog();

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
                        int user_id = Integer.parseInt(response.getString("id"));
                        int role = Integer.parseInt(response.getString("role_id"));
                        String token = response.getString("token");

                        JSONObject user = response.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");

                        // Inserting row in users table
                        db.addUser(user_id, name, email, role, token);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        //intent.putExtra("token", token);
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
                        "Nastala chyba počas prihlasovania", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "multipart/form-data;boundary=" + BOUNDARY;
            }

            @Override
            public byte[] getBody() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                final String requestBody = createPostBody(params);
                return requestBody.getBytes();
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private String createPostBody(Map<String, String> params) {
        StringBuilder sbPost = new StringBuilder();
        for (String key : params.keySet()) {
            if (params.get(key) != null) {
                sbPost.append("\r\n" + "--" + BOUNDARY + "\r\n");
                sbPost.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n");
                sbPost.append(params.get(key));
            }
        }
        return sbPost.toString();
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
        editName.setError(null);
        editMail.setError(null);
        editPass.setError(null);
        editPassAgain.setError(null);

        final String name = editName.getText().toString().trim();
        final String email = editMail.getText().toString().trim();
        final String pass = editPass.getText().toString().trim();
        String passAgain = editPassAgain.getText().toString().trim();

        Log.i("LoginAct", "heslo: " + pass);
        Log.i("LoginAct", "znova: " + passAgain);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passAgain)){
            editPassAgain.setError(getString(R.string.error_field_required));
            focusView = editPassAgain;
            cancel = true;
        } else if(!FieldChecker.isPasswordValid(passAgain)) {
            editPassAgain.setError(getString(R.string.error_invalid_password));
            focusView = editPassAgain;
            cancel = true;
        }

        // Check for a valid password again.
        if (TextUtils.isEmpty(pass)){
            editPass.setError(getString(R.string.error_field_required));
            focusView = editPass;
            cancel = true;
        } else if(!FieldChecker.isPasswordValid(pass)) {
            editPass.setError(getString(R.string.error_invalid_password));
            focusView = editPass;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editMail.setError(getString(R.string.error_field_required));
            focusView = editMail;
            cancel = true;
        } else if (!FieldChecker.isEmailValid(email)) {
            editMail.setError(getString(R.string.error_invalid_email));
            focusView = editMail;
            cancel = true;
        }

        // Check for a valid name and surname entered.
        if(TextUtils.isEmpty(name)){
            editName.setError(getString(R.string.error_field_required));
            focusView = editName;
            cancel = true;
        } else if(!FieldChecker.isNameValid(name)){
            editName.setError(getString(R.string.error_invalid_name));
            focusView = editName;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }
        else {
            if (pass.equals(passAgain)) {
                String tag_json_obj = "json_obj_req";

                pDialog.setMessage("Registrácia sa spracováva...");
                showDialog();

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        AppConfig.URL_REGISTER, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
                            boolean er = response.getBoolean("error");

                            // Check for error node in json
                            if (!er) {
                                Toast.makeText(getApplicationContext(), "Registrácia bola úspešná, môžete sa prihlásiť", Toast.LENGTH_LONG).show();
                                reg.setVisibility(View.INVISIBLE);
                                main.setAlpha(1);
                                editName.setText("");
                                editMail.setText("");
                                editPass.setText("");
                                editPassAgain.setText("");
                            } else {
                                String errMsg = response.getString("error_msg");
                                Toast.makeText(getApplicationContext(), "Nepodarilo sa registrovať, používateľ s daným e-mailom už existuje. Skúste znova", Toast.LENGTH_LONG).show();
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
                        return "multipart/form-data;boundary=" + BOUNDARY;
                    }

                    @Override
                    public byte[] getBody() {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", name);
                        params.put("email", email);
                        params.put("password", pass);
                        final String requestBody = createPostBody(params);
                        return requestBody.getBytes();
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            } else {
                editPassAgain.setError(getString(R.string.error_not_equal_password));
                focusView = editPassAgain;
                editPass.setError(getString(R.string.error_not_equal_password));
                focusView = editPass;
                focusView.requestFocus();
            }
        }
    }
}
