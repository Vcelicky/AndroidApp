package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class LimitValuesSettingsActivity extends AppCompatActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_values_settings);

        Intent intent = getIntent();
        String hiveId =  intent.getExtras().getString("hiveId");
        String hiveName = intent.getExtras().getString("hiveName");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hiveName);

        //Arrow back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
