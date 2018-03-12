package com.example.jozef.vcelicky;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by MSI on 12. 3. 2018.
 */

public class NotificationArchive extends Observable {

    private static NotificationArchive instance = null;
    static private ArrayList<NotificationInfo> notificationInfoList;

    protected NotificationArchive () {
        notificationInfoList = new ArrayList<>();
        // Exists only to defeat instantiation.
    }
    public static NotificationArchive getInstance() {
        if(instance == null) {
            instance = new NotificationArchive();
        }
        return instance;
    }

    public ArrayList<NotificationInfo> getNotificationInfoList() {
        return notificationInfoList;
    }

    public void myNotifyObservers() {
        setChanged();
        notifyObservers();
    }

}
