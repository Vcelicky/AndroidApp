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
        return phone.contains("0") ||
                phone.contains("1") ||
                phone.contains("2") ||
                phone.contains("3") ||
                phone.contains("4") ||
                phone.contains("5") ||
                phone.contains("6") ||
                phone.contains("7") ||
                phone.contains("8") ||
                phone.contains("9");
    }
}
