package com.sbtn.androidtv.utils;

//import android.annotation.TargetApi;

import android.content.Context;

/**
 * Created by sb4 on 4/20/15.
 */
public class CommonUtils {

    public static String getAppVersion(Context context) {
        String versionName = "0.0.0";
        try {
            versionName = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            0).versionName;
        } catch (Exception e) {
        }
        return versionName;
    }
//
//    public static String getClientAppID(Context context) {
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(context);
//        String outputString = sharedPreferences.getString("clientAppID", "");
//        String getResultString = "";
//        byte[] decrypted = null;
//        if (!outputString.equals("")) {
//            byte[] byteAccessToken = Base64
//                    .decode(outputString, Base64.NO_WRAP);
//            try {
//                AESEncryptDecrypt encrypter = new AESEncryptDecrypt(Constant.KEY);
//                decrypted = encrypter.decrypt(byteAccessToken);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (decrypted != null) {
//                getResultString = Base64.encodeToString(decrypted,
//                        Base64.NO_WRAP);
//            }
//
//        }
//        return getResultString;
//    }
}
