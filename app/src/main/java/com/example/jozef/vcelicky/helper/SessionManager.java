package com.example.jozef.vcelicky.helper;

/**
 * Created by Jozef on 10. 11. 2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences pref;

    private Editor editor;
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "VcelickyAppLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private List<String> hives = new ArrayList<>();

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    //TODO set correct values on isFirstTime when logging off
    public void setFirstTime(String hiveId, boolean isFirstTime){
        editor.putBoolean(hiveId, isFirstTime);
        editor.commit();
        if(!isFirstTime){
            hives.add(hiveId);
        }
    }

    public void setFirstTime(boolean isFirstTime){
        if(isFirstTime){
            for(String hive : hives){
                editor.putBoolean(hive, isFirstTime);
                editor.commit();
            }
            hives.clear();
        }
    }

    public boolean isFirstTime(String hiveId){return pref.getBoolean(hiveId, true);}
}
