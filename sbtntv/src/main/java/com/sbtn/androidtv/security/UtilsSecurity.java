package com.sbtn.androidtv.security;

import android.content.Context;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class UtilsSecurity {
    private static String userAgent;

    public static String getUserAgent(Context context) {
        if (userAgent == null || userAgent.equals(""))
            userAgent = SBDeviceUtils.getUserAgent(context);

        return userAgent;
    }
}
