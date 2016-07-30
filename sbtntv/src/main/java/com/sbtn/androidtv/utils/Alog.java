package com.sbtn.androidtv.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sbtn.androidtv.BuildConfig;

/**
 * Created by hoanguyen on 5/18/16.
 */
public class ALog {
    static public final boolean ENABLED = BuildConfig.ENABLE_LOG;

    static public final String TAG = "SBTN";

    static public final int MASK_NONE = 0;
    static public final int MASK_VERBOSE = 1;
    static public final int MASK_DEBUG = 2;
    static public final int MASK_INFO = 4;
    static public final int MASK_WARNING = 5;
    static public final int ALL_MASK = MASK_VERBOSE | MASK_DEBUG | MASK_INFO | MASK_WARNING;

    static public final int LEVEL = ALL_MASK;

    static public boolean isMaskEnabled(final int mask) {
        return (LEVEL & mask) > 0;
    }

    private static boolean isLogEnabled(final int mask) {
        return ENABLED && isMaskEnabled(mask);
    }

    static public void d(final String msg) {
        if (isLogEnabled(MASK_DEBUG)) {
            Log.d(TAG, msg);
        }
    }

    static public void i(final String msg) {
        if (isLogEnabled(MASK_INFO)) {
            Log.i(TAG, msg);
        }
    }

    static public void v(final String msg) {
        if (isLogEnabled(MASK_VERBOSE)) {
            Log.v(TAG, msg);
        }
    }

    static public void w(final String msg) {
        if (isLogEnabled(MASK_WARNING)) {
            Log.w(TAG, msg);
        }
    }

    static public void d(final Object objSource, final String msg) {
        Class<?> clazz = objSource.getClass();
        //Triet.Bui Change print method to ignore print String.class
        if (clazz.equals(String.class))
            d(objSource + ": " + msg);
        else
            d(clazz, msg);
    }

    static public void d(final Class<?> clazz, final String msg) {
        d(clazz.getSimpleName() + ": " + msg);
    }


    static public void i(final Object objSource, final String msg) {
        Class<?> clazz = objSource.getClass();
        if (clazz.equals(String.class))
            i(objSource + ": " + msg);
        else
            i(clazz, msg);
    }

    static public void i(final Class<?> clazz, final String msg) {
        i(clazz.getSimpleName() + ": " + msg);
    }

    static public void v(final Object objSource, final String msg) {
        Class<?> clazz = objSource.getClass();
        if (clazz.equals(String.class))
            v(objSource + ": " + msg);
        else
            v(clazz, msg);
    }

    static public void v(final Class<?> clazz, final String msg) {
        v(clazz.getSimpleName() + ": " + msg);
    }

    static public void w(final Object objSource, final String msg) {
        Class<?> clazz = objSource.getClass();
        if (clazz.equals(String.class))
            w(objSource + ": " + msg);
        else
            w(clazz, msg);
    }

    static public void w(final Class<?> clazz, final String msg) {
        w(clazz.getSimpleName() + ": " + msg);
    }

    static public void e(final Class<?> clazz, final String msg,
                         final Throwable e) {
        Log.e(TAG, clazz.getSimpleName() + ": " + msg, e);
    }

    static public void e(final Class<?> clazz, final String msg) {
        Log.e(TAG, clazz.getSimpleName() + ": " + msg);
    }

    static public void e(final Object objSource, final String msg,
                         final Throwable e) {
        e(objSource.getClass(), msg, e);
    }

    static public void e(final Object objSource, final String msg) {
        Class<?> clazz = objSource.getClass();
        if (clazz.equals(String.class))
            e(objSource + ": " + msg);
        else
            e(clazz, msg);
    }

    static public void e(final String msg, final Throwable e) {
        Log.e(TAG, msg, e);
    }

    static public void e(final String msg) {
        Log.e(TAG, msg);
    }

    static public void e(final Throwable e) {
        e("Exception", e);
    }

    static public void showToast(Context context, String tag, String msg) {
        if (ENABLED)
            Toast.makeText(context, tag + " - " + msg, Toast.LENGTH_SHORT).show();
    }
}
