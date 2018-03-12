package com.example.jozef.vcelicky;

import android.os.Handler;

import java.util.Observable;


/**
 * Created by MSI on 12. 3. 2018.
 */

    public class UserDataRepository extends Observable {
        private String mFullName;
        private int mAge;
        private static UserDataRepository INSTANCE = null;

        private UserDataRepository() {
        }

        // Returns a single instance of this class, creating it if necessary.
        public static UserDataRepository getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new UserDataRepository();
            }
            return INSTANCE;
        }

        public void setUserData(String fullName, int age) {
            mFullName = fullName;
            mAge = age;
            setChanged();
            notifyObservers();

        }

    public void myNotifyObservers() {
        setChanged();
        notifyObservers();
    }

        public String getFullName() {
            return mFullName;
        }

        public int getAge() {
            return mAge;
        }





    }



