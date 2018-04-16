package com.example.jozef.vcelicky.helper;

/**
 * Created by Jozef on 08. 12. 2017.
 */

public class FieldChecker {
    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    public static boolean isNameValid(String name){
        return name.contains(" ");
    }

    public static boolean isPhoneNumberValid(String phone){
        return phone.matches("^[+]?[0-9]{10,14}$");
    }
}
