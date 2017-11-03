package com.example.jozef.vcelicky;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.invoke.ConstantCallSite;

public class LoginActivity extends AppCompatActivity {

    EditText mail, pass;
    ConstraintLayout main, error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v("LoginAct", "Startin' activity");
        mail = findViewById(R.id.editMail);
        pass = findViewById(R.id.editPass);
        main = findViewById(R.id.mainLayout);
        error = findViewById(R.id.errorLayout);
    }

    public void login (View view){
        if(mail.getText().toString().equals("")){
            main.setAlpha((float)0.5);
            error.setVisibility(View.VISIBLE);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void close(View view){
        error.setVisibility(View.INVISIBLE);
        main.setAlpha(1);
    }

    public void hideKeyboard(View view){
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
