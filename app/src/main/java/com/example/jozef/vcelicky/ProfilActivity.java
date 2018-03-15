package com.example.jozef.vcelicky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.jozef.vcelicky.helper.SQLiteHandler;

import java.util.HashMap;

public class ProfilActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        Toolbar toolbar = findViewById(R.id.toolbar6);
        toolbar.setTitle("Profil včelára");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        TextView nameBeekeeper = findViewById(R.id.nameBeekeeper);
        nameBeekeeper.setText(user.get("name"));
        TextView emailBeekeeper = findViewById(R.id.emailBeekeeper);
        emailBeekeeper.setText(user.get("email"));
        TextView deviceBeekeper = findViewById(R.id.deviceBeekeeper);
        deviceBeekeper.setText(db.getUserDevicesCount());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
