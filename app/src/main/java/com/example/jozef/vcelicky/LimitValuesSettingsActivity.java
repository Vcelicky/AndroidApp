package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class LimitValuesSettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();
        Intent intent = getIntent();
        String hiveId =  intent.getExtras().getString("hiveId");
        String hiveName = intent.getExtras().getString("hiveName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hiveName);
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

}
