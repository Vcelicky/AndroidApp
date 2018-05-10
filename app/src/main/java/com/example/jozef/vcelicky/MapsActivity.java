package com.example.jozef.vcelicky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.jozef.vcelicky.R;
import com.example.jozef.vcelicky.helper.SQLiteHandler;
import com.example.jozef.vcelicky.helper.SessionManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "MapsActivity";
    ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
    SQLiteHandler db;
    SessionManager session;
    String mode;
    String hiveId = "";
    String hiveName;
    String hiveLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        mode =  intent.getExtras().getString("mode");
        try {
            hiveId = intent.getExtras().getString("hiveId");
            hiveName = intent.getExtras().getString("hiveName");
        }catch (Exception e){
            Log.i(TAG, "No hive Id from intent");
        }

        Log.i(TAG, "Mode: "+mode);

        session = new SessionManager(getApplicationContext());
        loadNotificationInfoListFromSharedPreferencies();
        Toolbar toolbar = findViewById(R.id.toolbarWithBackArrow);
        if (mode.equals("oneHive")) {
            toolbar.setTitle(hiveName);
        }else{
            toolbar.setTitle("Mapa úľov");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setSupportActionBar(toolbar);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //When Map Loads Successfully
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.hive);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                Bitmap biggerMarker = Bitmap.createScaledBitmap(b, 80, 80, false);

                //Your code where exception occurs goes here...
                List<LatLng> locations = new ArrayList<>();

                for(HiveBaseInfo hive : hiveList) {
                   Log.i(TAG, "longitude:" +hive.getLongitude());
                   Log.i(TAG, "latitude:" +hive.getLatitude());
                   Log.i(TAG, hiveId+" VS " +hive.getHiveId());

                   LatLng latLng = new LatLng(hive.getLatitude(),hive.getLongitude());
                   locations.add(latLng);
                    if (mode.equals("oneHive") && hive.getHiveId().equals(hiveId)){
                        Log.i(TAG, "This is THE ONE HIVE" +hive.getLatitude());
                        Marker m =  mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(hive.getHiveName() + "," + hive.getHiveLocation())
                        );
                          m.setIcon(BitmapDescriptorFactory.fromBitmap(biggerMarker));
                          m.showInfoWindow();

                    }else {
                //        mMap.addMarker(new MarkerOptions().position(latLng).title(hive.getHiveName() + "," + hive.getHiveLocation())).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(hive.getHiveName() + "," + hive.getHiveLocation()+", "+hive.getLatitude()+"° N,"+hive.getLongitude()+"° W")).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    }
                }
                //LatLngBound will cover all your marker on Google Maps
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(locations.get(0)); //Taking Point A (First LatLng)
                builder.include(locations.get(locations.size() - 1)); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                //  mMap.moveCamera(cu);
                //  mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                //  next stuff http://yasirameen.com/2017/09/understading-google-maps-marker/
                mMap.animateCamera(cu);
            }
        });
    }

    public void loadNotificationInfoListFromSharedPreferencies(){
        hiveList.clear();
        db = new SQLiteHandler(getApplicationContext());
        //TODO:
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(db.getUserDetails(session.getLoggedUser()).get("id"),getApplicationContext().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("hiveList", "");
        Log.d(TAG, "loadedPreferencies " + mPrefs.getString("hiveList", ""));
        if (json.isEmpty()) {
            hiveList.clear();
        } else {
            Type type = new TypeToken<List<HiveBaseInfo>>() {
            }.getType();
            hiveList = gson.fromJson(json, type);
            Log.d(TAG, "Loaded list count: "+hiveList.size());
        }
    }
}
