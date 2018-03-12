package com.example.jozef.vcelicky;

import java.util.Observable;


/**
 * Created by MSI on 12. 3. 2018.
 */

    public class NotificationObservable extends Observable {
        private String mFullName;
        private int mAge;
        private static NotificationObservable INSTANCE = null;



    NotificationInfo notificationInfo;


        private NotificationObservable() {
        }

        // Returns a single instance of this class, creating it if necessary.
        public static NotificationObservable getInstance() {
            if(INSTANCE == null) {
                INSTANCE = new NotificationObservable();
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

    public NotificationInfo getNotificationInfo() {
        return notificationInfo;
    }

    public void setNotificationInfo(NotificationInfo notificationInfo) {
        this.notificationInfo = notificationInfo;
    }



    }



