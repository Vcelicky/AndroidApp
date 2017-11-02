package com.example.jozef.vcelicky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createTestData();

        ArrayAdapter<HiveBaseInfo> allAdapter = new AdapterHive(this, hiveList);
        ListView menuListView = (ListView) findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);

    }

    public void createTestData(){
        hiveList.add(new HiveBaseInfo(1234, "Vceli ul 1", 55 , 45, 70, 69));
        hiveList.add(new HiveBaseInfo(1235, "Vceli ul 2", 40 , 43, 68, 50));
        hiveList.add(new HiveBaseInfo(1236, "Vceli ul 3", 30 , 42, 68, 60));
        hiveList.add(new HiveBaseInfo(1237, "Vceli ul 4", 40 , 45, 50, 53));
        hiveList.add(new HiveBaseInfo(1238, "Vceli ul 5", 35 , 43, 68, 56));
        hiveList.add(new HiveBaseInfo(1239, "Vceli ul 6", 32 , 49, 61, 89));
        hiveList.add(new HiveBaseInfo(1240, "Vceli ul 7", 36 , 45, 68, 66));
    }

}
