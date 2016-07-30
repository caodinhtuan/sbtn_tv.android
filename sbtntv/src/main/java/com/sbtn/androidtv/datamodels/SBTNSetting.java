package com.sbtn.androidtv.datamodels;

import android.support.annotation.DrawableRes;

/**
 * Created by hoanguyen on 6/20/16.
 */
public class SBTNSetting {
    final String name;
    @DrawableRes
    final int icon;

    public SBTNSetting(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
