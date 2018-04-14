package com.example.jozef.vcelicky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jozef.vcelicky.app.AppConfig;
import com.example.jozef.vcelicky.app.AppController;
import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *  This activity is not used at the moment. It is here for future use.
 */

public class LimitValuesChooseHiveActivity extends BaseActivity {

    private static String TAG = "LimitValuesChooseHiveActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    ListView menuListView;
    ArrayList<HiveBaseInfo> hiveIDs =  new ArrayList<>();
    ArrayAdapter<HiveBaseInfo> allAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityActivateToolbarAndSideBar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hraničné hodnoty");

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String token =  db.getUserDetails(session.getLoggedUser()).get("token");
        int userId = Integer.parseInt(db.getUserDetails(session.getLoggedUser()).get("id"));
        Log.i(TAG, "Token: " + token);
        Log.i(TAG, "UserID: " + userId);

        allAdapter = new AdapterLimitValues(this, hiveList);
        menuListView = findViewById(R.id.hiveListView);
        menuListView.setAdapter(allAdapter);
        hiveClicked();

        hiveList.add(new HiveBaseInfo("0", "Všetky úle","jjj", 55 , 45, 70, 80, 69,  true,99));
        hiveList.add(new HiveBaseInfo("1234", "Alfa","jjj", 55 , 45, 70, 80, 69,  true,99));
        hiveList.add(new HiveBaseInfo("1235", "Beta","jjj", 40 , 43, 68, 85,50,true,99));
        hiveList.add(new HiveBaseInfo("1236", "Gama","jjj", 30 , 42, 68, 82,60,false,99));
        hiveList.add(new HiveBaseInfo("1237", "Delta","jjj", 40 , 45, 50, 81,53,true,99));
        hiveList.add(new HiveBaseInfo("1238", "Pomaranč","jjj", 35 , 43, 68, 75,56,true,99));
        hiveList.add(new HiveBaseInfo("1239", "Žehlička","jjj", 32 , 49, 61, 70,89,true,99));
        hiveList.add(new HiveBaseInfo("1240", "Imro", "jjj",36 , 45, 68, 60,66,true,99));
        hiveList.add(new HiveBaseInfo("1241", "Kýbeľ", "jjj",36 , 45, 68, 75,66,true,99));
        hiveList.add(new HiveBaseInfo("1242", "Stolička","jjj", 36 , 45, 68, 78,66,true,99));
        hiveList.add(new HiveBaseInfo("1243", "Slniečko","jjj", 36 , 45, 68, 80,66,true,99));

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public void hiveClicked(){
        menuListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        HiveBaseInfo device = (HiveBaseInfo) parent.getAdapter().getItem(position);
                        Intent i = new Intent(getApplicationContext(), LimitValuesSettingsActivity.class);
                        Log.i(TAG, "SPARTA: hiveId: " + device.getHiveId());
                        Log.i(TAG, "SPARTA: hiveName " + device.getHiveName());
                        i.putExtra("hiveId", device.getHiveId());
                        i.putExtra("hiveName", device.getHiveName());
                        startActivity(i);
                    }
                }
        );

    }

}
