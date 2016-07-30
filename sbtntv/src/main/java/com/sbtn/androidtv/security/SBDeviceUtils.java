package com.sbtn.androidtv.security;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import com.sbtn.androidtv.constants.Constant;
import com.sbtn.androidtv.utils.CommonUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by sb4 on 4/20/15.
 */
public class SBDeviceUtils {

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getSDKVersion() {
        @SuppressWarnings("deprecation")
        String sdkversion = android.os.Build.VERSION.SDK;
        return sdkversion;
    }

    public static String getModel() {
        return Build.MODEL;
    }


    public static String getProductName() {
        return Build.PRODUCT;
    }

    public static String getDeviceName() {
        return Build.MANUFACTURER;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    private static String getSerial() {
        String serial = "";
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null)
                    .toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return serial;
    }

    public static String getLocalIpAddress() {
        String result = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        result = inetAddress.getHostAddress();
                        int index = result.indexOf("%");
                        try {
                            result = result.substring(0, index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return result;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context
                        .getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }

    public static String getImei(Context context) {
        String imei = "";
        TelephonyManager mngr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = mngr.getDeviceId();
        return imei;
    }

    public static boolean isAmazonDevice() {
        return android.os.Build.MANUFACTURER.equals(Constant.AMAZON_DEVICE);
    }

    public static String getSimSerial(Context context) {
        String result = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT):
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
            case (TelephonyManager.SIM_STATE_UNKNOWN):
                break;
            case (TelephonyManager.SIM_STATE_READY): {
                result = telephonyManager.getSimSerialNumber();
            }
        }
        return result;
    }

    public static String getGoogleID(Context context) {
        String possibleEmail = "";
        int count = 0;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(
                context.getApplicationContext()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()
                    && account.type.equals("com.google") && count < 3) {
                String tmp = "";
                tmp = account.name.replace("@gmail.com", "");
                tmp = "[" + tmp + "]";
                possibleEmail += tmp;
                count++;
            }
        }

        return possibleEmail;
    }

    @SuppressLint("NewApi")
    public static String getDeviceName(Context context) {
        String userOwner = "";
        try {
            Cursor c = context
                    .getApplicationContext()
                    .getContentResolver()
                    .query(ContactsContract.Profile.CONTENT_URI, null, null,
                            null, null);
            c.moveToFirst();
            userOwner = c.getString(c.getColumnIndex("display_name"));
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userOwner;
    }

    public static String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        carrierName = carrierName.replace(" ", "");
        if (carrierName.equals("VietnamMobileTelecomServicesCompany"))
            carrierName = "Mobifone";
        return carrierName;
    }

    public static String getPlatform() {
        String platform;
        if (isAmazonDevice()) {
            platform = Constant.AMAZON_PLATFORM;
        } else {
            platform = Constant.ANDROID_PLATFORM;
        }
        return platform;
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    public static String getUserAgent(Context context) {
//        return "SBTN_SmartTV/" + CommonUtils.getAppVersion(context) + " (" + getPlatform()
//                + ";" + getOSVersion() + ";" + getModel() + ";" + getCarrier(context)
//                + ")";

        return "SBTN_SmartTV/" + CommonUtils.getAppVersion(context) + " (" + getBrand() + ";" + getPlatform()
                + ";" + getOSVersion() + ";" + getModel()
                + ")";
    }

    public static boolean isRunningOnEmulator() {
        return Build.FINGERPRINT.startsWith("generic")//
                || Build.FINGERPRINT.startsWith("unknown")//
                // ||Build.MODEL.contains("google_sdk")//
                || Build.MODEL.contains("Emulator")//
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion");
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}
