package com.example.jozef.vcelicky.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jozef.vcelicky.HiveBaseInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_MEASUREMENTS = "measurements";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EXPIRES = "expires";

    // Measurement table column names
    private static final String KEY_TIME = "time";
    private static final String KEY_TEMPIN = "tempIn";
    private static final String KEY_TEMPOUT = "tempOut";
    private static final String KEY_HUMIIN = "humiIn";
    private static final String KEY_HUMIOUT = "humiOut";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_POSITION = "position";
    private static final String KEY_BATTERY = "battery";
    private static final String KEY_DEVICENAME = "deviceName";
    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_LOCATION = "location";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_ROLE + " TEXT,"
                + KEY_TOKEN + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_EXPIRES + " BIGINT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_MEASUREMENT_TABLE = "CREATE TABLE " + TABLE_MEASUREMENTS + " ("
                + KEY_TIME + " BIGINT PRIMARY KEY,"
                + KEY_TEMPIN + " INTEGER,"
                + KEY_TEMPOUT + " INTEGER,"
                + KEY_HUMIIN + " INTEGER,"
                + KEY_HUMIOUT + " INTEGER,"
                + KEY_WEIGHT + " INTEGER,"
                + KEY_POSITION + " BOOLEAN,"
                + KEY_BATTERY + " INTEGER,"
                + KEY_DEVICENAME + " TEXT,"
                + KEY_DEVICEID + " TEXT,"
                + KEY_LOCATION + " TEXT" + ")";
        db.execSQL(CREATE_MEASUREMENT_TABLE);

        Log.i(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);

        // Create tables again
        onCreate(db);
    }

    //    Users table handler methods
    /**
     * Storing user details in database
     * */
    public void addUser(int user_id, String name, String email, int role, String token, String phone, long expires) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user_id); // User ID
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role); // User Role
        values.put(KEY_TOKEN, token); // Token
        values.put(KEY_PHONE, phone);
        values.put(KEY_EXPIRES, expires);
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.i(TAG, "New user inserted into sqlite: " + id);
        Log.i(TAG, values.toString());
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("role_id", String.valueOf(cursor.getInt(3)));
            user.put("token", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.i(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_MEASUREMENTS, null, null);
        db.close();
        Log.i(TAG, "Deleted all users info from sqlite");
    }

    public boolean isExpired() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_EXPIRES
                + " FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            Log.i(TAG, String.valueOf(new Date().getTime() / 1000));
            if(cursor.getLong(0) >= new Date().getTime() / 1000){
                cursor.close();
                db.close();
                return false;
            }
        }
        cursor.close();
        db.close();
        return true;
    }

//    Measurements table handler methods

    public void addMeasurement(long time, int tempIn, int tempOut, int humiIn, int humiOut, int weight, boolean position, int battery, String deviceName, String deviceId, String location){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, time);
        values.put(KEY_TEMPIN, tempIn);
        values.put(KEY_TEMPOUT, tempOut);
        values.put(KEY_HUMIIN, humiIn);
        values.put(KEY_HUMIOUT, humiOut);
        values.put(KEY_WEIGHT, weight);
        values.put(KEY_POSITION, position);
        values.put(KEY_BATTERY, battery);
        values.put(KEY_DEVICENAME, deviceName);
        values.put(KEY_DEVICEID, deviceId);
        values.put(KEY_LOCATION, location);

        // Inserting Row
        long id = db.insert(TABLE_MEASUREMENTS, null, values);
        db.close(); // Closing database connection

        Log.i(TAG, "New measurement inserted into sqlite: " + id);
        Log.i(TAG, values.toString());
    }

    //TODO change device name to device id after merge
    public int getUserDevicesCount(){
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_DEVICENAME
                + " FROM " + TABLE_MEASUREMENTS
                + " GROUP BY " + KEY_DEVICENAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public List <HashMap<String, String>> getActualMeasurement() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HashMap<String, String>> devices = new ArrayList<>();
        List<String> deviceIds = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DEVICEID
                + " FROM " + TABLE_MEASUREMENTS
                + " GROUP BY " + KEY_DEVICEID
                + " ORDER BY " + KEY_DEVICENAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do {
                deviceIds.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        Log.i(TAG, "Number of devices: " + cursor.getCount());
        for(int i = 0; i < deviceIds.size(); i++){
            HashMap<String, String> actual = new HashMap<>();
            selectQuery = "SELECT * FROM " + TABLE_MEASUREMENTS
                    + " WHERE " + KEY_DEVICEID + "='" + deviceIds.get(i)
                    + "' ORDER BY " + KEY_TIME + " DESC LIMIT 1";
            cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                actual.put("time", String.valueOf(cursor.getLong(0)));
                actual.put("tempIn", String.valueOf(cursor.getInt(1)));
                actual.put("tempOut", String.valueOf(cursor.getInt(2)));
                actual.put("humiIn", String.valueOf(cursor.getInt(3)));
                actual.put("humiOut", String.valueOf(cursor.getInt(4)));
                actual.put("weight", String.valueOf(cursor.getInt(5)));
                actual.put("position", cursor.getString(6));
                actual.put("battery", String.valueOf(cursor.getInt(7)));
                actual.put("deviceName", cursor.getString(8));
                actual.put("deviceId", cursor.getString(9));
                actual.put("location", cursor.getString(10));
                devices.add(actual);
                Log.i(TAG, "Fetching actual measurement from Sqlite: " + actual.toString());
            }
        }
        cursor.close();
        db.close();
        // return actual measurement
        return devices;
    }

    // Get most recent time stamp of measurement based on device ID
    public long getMostRecentTimeStamp(String id){
        long recent = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_TIME
                + " FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_DEVICEID + "='" + id
                + "' ORDER BY " + KEY_TIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            recent = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return recent;
    }

    public ArrayList<HiveBaseInfo> getAllMeasurements(String id){
        ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_DEVICEID + "='" + id
                + "' ORDER BY " + KEY_TIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            HiveBaseInfo record;
            for(int i = 0; i < cursor.getCount(); i++){
                record = new HiveBaseInfo();
                record.setTime(cursor.getLong(0));
                record.setInsideTemperature(cursor.getInt(1));
                record.setOutsideTemperature(cursor.getInt(2));
                record.setInsideHumidity(cursor.getInt(3));
                record.setOutsideHumidity(cursor.getInt(4));
                record.setWeight(cursor.getInt(5));
                record.setAccelerometer(Boolean.parseBoolean(cursor.getString(6)));
                record.setBattery(cursor.getInt(7));
                record.setHiveName(cursor.getString(8));
                record.setHiveId(cursor.getString(9));
                record.setHiveLocation(cursor.getString(10));
                hiveList.add(record);
                cursor.moveToNext();
                Log.i(TAG, "Fetching measurement from SQLite: " + record.getTime());
            }
        }
        cursor.close();
        db.close();
        return hiveList;
    }

    public ArrayList<String> getUserHiveIds(){
        ArrayList<String> hives = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_DEVICEID
                + " FROM " + TABLE_MEASUREMENTS
                + " GROUP BY " + KEY_DEVICEID;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            String hive;
            for(int i = 0; i < cursor.getCount(); i++){
                hive = cursor.getString(0);
                hives.add(hive);
                cursor.moveToNext();
                Log.i(TAG, "Fetching device ID from SQLite: " + hive);
            }
        }
        cursor.close();
        db.close();
        return hives;
    }
}