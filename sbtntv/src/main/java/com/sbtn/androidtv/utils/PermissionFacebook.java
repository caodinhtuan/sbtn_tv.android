package com.sbtn.androidtv.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * not all facebook permissions
 *
 * @author hoa.filestring
 * @see https://developers.facebook.com/docs/facebook-login/android/v2.3
 * @see https://developers.facebook.com/docs/facebook-login/permissions/v2.3
 */
public enum PermissionFacebook {

    PUBLIC_PROFILE("public_profile"),
    USER_FRIENDS("user_friends"),
    EMAIL("email");

    private String mValue;
    private static List<String> listPer;

    PermissionFacebook(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public static PermissionFacebook fromValue(String permissionValue) {
        for (PermissionFacebook permission : values()) {
            if (permission.mValue.equals(permissionValue)) {
                return permission;
            }
        }
        return null;
    }

    public static List<String> getPermission() {
        if (listPer == null) {
            //Add more if need
            listPer = new ArrayList<>(3);
            listPer.add(PUBLIC_PROFILE.mValue);
            listPer.add(USER_FRIENDS.mValue);
            listPer.add(EMAIL.mValue);
        }
        return listPer;
    }
}
