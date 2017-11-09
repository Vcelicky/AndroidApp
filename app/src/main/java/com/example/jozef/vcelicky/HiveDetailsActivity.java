package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class HiveDetailsActivity extends AppCompatActivity {

    int hiveID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        // hiveId
        hiveID =  intent.getIntExtra("hiveId",0);
        toolbar.setTitle("Včelí úľ ID:"+Integer.toString(hiveID));
        setSupportActionBar(toolbar);

    }
}
