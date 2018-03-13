package com.example.jozef.vcelicky.helper;

/**
 * Created by Jozef on 10. 11. 2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

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

    // Measurement table column names
    private static final String KEY_TIME = "time";
    private static final String KEY_TEMPIN = "tempIn";
    private static final String KEY_TEMPOUT = "tempOut";
    private static final String KEY_HUMIIN = "humiIn";
    private static final String KEY_HUMIOUT = "humiOut";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_POSITION = "position";
    private static final String KEY_DEVICENAME = "deviceName";
    private static final String KEY_BATTERY = "battery";

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
                +  KEY_TOKEN + " TEXT" +")";
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
                + KEY_DEVICENAME + " TEXT" + ")";
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

    /**
     * Storing user details in database
     * */
    public void addUser(int user_id, String name, String email, int role, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user_id); // User ID
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role); // User Role
        values.put(KEY_TOKEN, token); // Token

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
        HashMap<String, String> user = new HashMap<String, String>();
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
        db.close();

        Log.i(TAG, "Deleted all users info from sqlite");
    }

    public void addMeasurement(long time, int tempIn, int tempOut, int humiIn, int humiOut, int weight, boolean position, int battery, String deviceName){
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

        // Inserting Row
        long id = db.insert(TABLE_MEASUREMENTS, null, values);
        db.close(); // Closing database connection

        Log.i(TAG, "New measurement inserted into sqlite: " + id);
        Log.i(TAG, values.toString());
    }

}