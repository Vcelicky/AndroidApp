package com.example.jozef.vcelicky.app;

/**
 * Created by Jozef on 10. 11. 2017.
 */

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://147.175.149.151/BeeWebpage/public/login/user";
  
    // Server user register url
    public static String URL_REGISTER = "http://147.175.149.151/BeeWebpage/public/register/user";

    // Server order create url
    public static String URL_ORDER = "http://147.175.149.151/BeeWebpage/public/order/new2";

    // Server hive handling URLs
    public static String URL_GET_HIVES = "http://team20-17.studenti.fiit.stuba.sk/BeeWebpage/public/user/devices";
    public static String URL_GET_HIVE_INFO = "http://team20-17.studenti.fiit.stuba.sk/BeeWebpage/public/user/measurements/actual";
    public static String URL_GET_HIVE_INFO_DETAILS ="http://team20-17.studenti.fiit.stuba.sk/BeeWebpage/public/user/measurements";
}
