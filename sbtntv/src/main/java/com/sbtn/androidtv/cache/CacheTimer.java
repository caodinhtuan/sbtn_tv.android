package com.sbtn.androidtv.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoanguyen on 6/20/16.
 */
public class CacheTimer {
    public static final int CACHE_TYPE_HOME = 1111;
    public static final int CACHE_TYPE_PROVIDER = 1112;
    public static final int CACHE_TYPE_CATEGORY = 1113;
    public static final int CACHE_TYPE_DETAIL_ITEM = 1114;

    /**
     * Thời gian hiệu lực của cache với thư mục (15 phút)
     */
    private static final long EXPIRED_TIME_MENU_DATA = 1000 * 60 * 15;
    /**
     * Các mốc thời gian lần cuối request từ server của các mục dữ liệu.<br>
     */
    private Map<String, Long> mTimeLastRequest;

    public CacheTimer() {
    }

    public void setCacheDataUpdated(int cacheType,
                                    String cacheDetail, boolean updated) {
        initLastRequestsTable();

        String key = String.valueOf(cacheType) + cacheDetail;
        Long value;

        if (updated) {
            value = System.currentTimeMillis();
        } else {
            value = 0L;
        }


        if (key != null) {
            mTimeLastRequest.put(key, value);
        }
    }

    public boolean getCacheDataUpdated(int cacheType,
                                       String cacheDetail) {
        if (mTimeLastRequest == null || mTimeLastRequest.isEmpty()) {
            return false;
        }
        Long value;

        value = mTimeLastRequest.get(String.valueOf(cacheType) + cacheDetail);
        if (value == null) {
            return false;
        } else {
            return (System.currentTimeMillis() - value < EXPIRED_TIME_MENU_DATA);
        }
    }

    private void initLastRequestsTable() {
        if (mTimeLastRequest == null) {
            mTimeLastRequest = new HashMap<>();
        }
    }

    public void clearAll() {
        if (mTimeLastRequest != null) {
            mTimeLastRequest.clear();
        }
    }
}
