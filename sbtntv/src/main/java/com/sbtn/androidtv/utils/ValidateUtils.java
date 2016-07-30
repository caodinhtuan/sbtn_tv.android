package com.sbtn.androidtv.utils;

import android.text.TextUtils;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Created by hoanguyen on 5/20/16.
 */
public class ValidateUtils {

    public static boolean validateEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public static boolean validatePassword(String password) {
        return !(password == null || password.trim().isEmpty() || password.isEmpty() || password.length() < 6);
    }

    public static boolean validateName(String name) {
        return !TextUtils.isEmpty(name) && name.length() <= 30;
    }

    public static boolean validatePromationCode(String code) {
        return !TextUtils.isEmpty(code) && code.length() >= 7 && code.length() <= 10;
    }
}
