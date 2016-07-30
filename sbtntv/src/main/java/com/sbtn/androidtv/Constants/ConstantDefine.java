package com.sbtn.androidtv.constants;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class ConstantDefine {
    public final static int TYPE_HOME = -10000;
    public final static int TYPE_SETTING = -11000;
    public final static int TYPE_PURCHASED_CONTENT = -12000;
    public final static int TYPE_PURCHASED_PACKAGE = -13000;
    public final static int TYPE_PROVIDER = 10000;
    public final static int TYPE_CATEGORY = 50000;

    public static boolean isProvider(int id) {
        return id >= ConstantDefine.TYPE_PROVIDER && id < ConstantDefine.TYPE_CATEGORY;
    }

    public static boolean isCategory(int id) {
        return id >= ConstantDefine.TYPE_CATEGORY;
    }

    public static int getProCatId(int id) {
        if (isProvider(id)) {
            return id - TYPE_PROVIDER;
        }
        if (isCategory(id)) {
            return id - TYPE_CATEGORY;
        }
        return -1;
    }

}
