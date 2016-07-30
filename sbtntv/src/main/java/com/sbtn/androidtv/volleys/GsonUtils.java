package com.sbtn.androidtv.volleys;

import com.google.gson.Gson;

/**
 * Created by onworldtvinc on 5/5/16.
 */
public class GsonUtils {
    private static Gson gson;

    public static Gson Gson() {
        if (gson == null) {
            initGson();
        }
        return gson;
    }

    private static void initGson() {
        gson = new Gson();
    }
}
