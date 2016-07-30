package com.sbtn.androidtv.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.google.gson.Gson;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.ASHomeDataObject;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.HeaderListHomeRow;
import com.sbtn.androidtv.datamodels.HomeItem;
import com.sbtn.androidtv.datamodels.Shows;
import com.sbtn.androidtv.datamodels.UserInfo;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.presenter.HomeHeaderPresenterSelector;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoanguyen on 4/27/16.
 */
public class CacheDataManager {
    private static final String PREF_NAME = "app_cache_pref";
    private static final String PREF_KEY_USER_TOKEN = "du_so_to_ken";
    private static final String PREF_KEY_USER_INFO = "du_so_in_fo";
    private static final String PREF_KEY_IS_LOGIN = "du_so_lo_gin";
    private static final String TAG = "CacheDataManager";

    private static CacheDataManager _instance;
    private static final Object mLock = new Object();

    private SharedPreferences mPreferences;
    private Context mContext;
    private String mAccessToken;
    private UserInfo mUserInfo;
    private Boolean mIsLogin;
    private Gson mGson;

    //Data mem cache
    private ASHomeDataObject asHomeDataObject;
    private Map<Integer, ViewDetail> mapsViewDetail;
    private Map<Integer, String> mapsTrackingId;
    private Map<Integer, ArrayList<Shows>> mapsProviders;
    private Map<Integer, ArrayList<Shows>> mapsCategories;
    private CacheTimer mCacheTimer;


    public static CacheDataManager getInstance(Context pContext) {
        synchronized (mLock) {
            if (_instance == null) {
                _instance = new CacheDataManager(pContext);
            }
            return _instance;
        }
    }

    public CacheDataManager(Context context) {
        this.mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        mGson = new Gson();
        mCacheTimer = new CacheTimer();
    }

    public void saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        mUserInfo = userInfo;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREF_KEY_USER_INFO, mGson.toJson(userInfo));
        editor.putBoolean(PREF_KEY_IS_LOGIN, true);
        editor.commit();

        mIsLogin = Boolean.valueOf(true);
        saveAccessToken(userInfo.getToken());

        clearContentData();
    }

    public UserInfo getUserInfo() {
        if (mUserInfo == null) {
            mUserInfo = mGson.fromJson(mPreferences.getString(PREF_KEY_USER_INFO, ""), UserInfo.class);
        }
        return mUserInfo;
    }

    public void handleUserSignOut() {
        mUserInfo = null;
        mAccessToken = null;
        mIsLogin = false;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(PREF_KEY_USER_INFO);
        editor.remove(PREF_KEY_USER_TOKEN);
        editor.remove(PREF_KEY_IS_LOGIN);
        editor.commit();

        clearContentData();
    }

    public void clearContentData() {
        if (asHomeDataObject != null)
            asHomeDataObject = null;

        if (mapsViewDetail != null)
            mapsViewDetail.clear();

        if (mapsTrackingId != null)
            mapsTrackingId.clear();

        if (mapsProviders != null)
            mapsProviders.clear();

        if (mapsCategories != null)
            mapsCategories.clear();

        mCacheTimer.clearAll();
    }

    /**
     * Lưu lại access token
     */
    public void saveAccessToken(String accessToken) {
        if (accessToken == null) {
            return;
        }
        mAccessToken = accessToken;
//        String encPass = SecurityUtil.encryptKey(accessToken, 268);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREF_KEY_USER_TOKEN, accessToken);
        editor.commit();
    }

    public boolean isLogin() {
        if (mIsLogin == null) {
            mIsLogin = Boolean.valueOf(mPreferences.getBoolean(PREF_KEY_IS_LOGIN, false));
        }

        return mIsLogin;
    }

    /**
     * Lấy access token
     */
    public String getAccessToken() {
        if (mAccessToken == null || mAccessToken.isEmpty()) {
//            String encPass = mPreferences.getString(PREF_KEY_USER_TOKEN, null);
//            if (encPass != null) {
//                mAccessToken = SecurityUtil.decryptKey(encPass, 268);
//            }

            mAccessToken = mPreferences.getString(PREF_KEY_USER_TOKEN, null);
        }

        return mAccessToken;
    }

    public void saveASHomeDataObject(ASHomeDataObject asHomeDataObject) {
        this.asHomeDataObject = asHomeDataObject;
        mCacheTimer.setCacheDataUpdated(CacheTimer.CACHE_TYPE_HOME, "", true);
    }

    public ASHomeDataObject getASHomeDataObject() {
        if (mCacheTimer.getCacheDataUpdated(CacheTimer.CACHE_TYPE_HOME, "")) {
            return asHomeDataObject;
        } else {
            asHomeDataObject = null;
            return null;
        }
    }

    public HomeItem getHomeItemView() {
        return getASHomeDataObject() == null ? null : asHomeDataObject.getView();
    }

    public HomeItem getHomeItemListen() {
        return getASHomeDataObject() == null ? null : asHomeDataObject.getListen();
    }

    public void saveViewDetail(int itemId, ViewDetail viewDetail) {
        //TODO for now ko cache detail lại 6-6-2016
//        if (mapsViewDetail == null) {
//            mapsViewDetail = new HashMap<>();
//        }
//        mapsViewDetail.put(itemId, viewDetail);

//        mCacheTimer.setCacheDataUpdated(CacheTimer.CACHE_TYPE_DETAIL_ITEM, itemId+"", true);
    }

    public ViewDetail getViewDetail(int itemId) {
        if (mapsViewDetail == null) return null;

        if (mCacheTimer.getCacheDataUpdated(CacheTimer.CACHE_TYPE_DETAIL_ITEM, itemId + "")) {
            return mapsViewDetail.get(itemId);
        } else {
            mapsViewDetail.remove(itemId);
            return null;
        }
    }

    public boolean removeViewDetail(int itemId) {
        if (mapsViewDetail == null) {
            ALog.e(TAG, "Fail to removeViewDetail - mapsViewDetail = null");
            return false;
        }

        return mapsViewDetail.remove(itemId) != null;
    }

    public void saveTrackingId(int contentId, String trackingId) {
        if (mapsTrackingId == null) {
            mapsTrackingId = new HashMap<>();
        }

        mapsTrackingId.put(contentId, trackingId);
    }

    public String removeTrackingId(int contentId) {
        if (mapsTrackingId == null) {
            return "";
        }

        return mapsTrackingId.remove(contentId);
    }

    public void saveProviders(ArrayList<Shows> shows, int id) {
        if (mapsProviders == null) {
            mapsProviders = new HashMap<>();
        }
        mapsProviders.put(id, shows);
        mCacheTimer.setCacheDataUpdated(CacheTimer.CACHE_TYPE_PROVIDER, "", true);
    }

    public ArrayList<Shows> getProviders(int id) {
        if (mapsProviders == null) {
            return null;
        }

        if (mCacheTimer.getCacheDataUpdated(CacheTimer.CACHE_TYPE_PROVIDER, "")) {
            return mapsProviders.get(id);
        } else {
            mapsProviders.clear();
            return null;
        }
    }

    public void saveCategories(ArrayList<Shows> shows, int id) {
        if (mapsCategories == null) {
            mapsCategories = new HashMap<>();
        }
        mapsCategories.put(id, shows);
        mCacheTimer.setCacheDataUpdated(CacheTimer.CACHE_TYPE_CATEGORY, "", true);
    }

    public ArrayList<Shows> getCategories(int id) {
        if (mapsCategories == null) {
            return null;
        }

        if (mCacheTimer.getCacheDataUpdated(CacheTimer.CACHE_TYPE_CATEGORY, "")) {
            return mapsCategories.get(id);
        } else {
            mapsCategories.clear();
            return null;
        }
    }

    public static int HOME_ID_ROW_VIEW_RECOMMEND;
    public static int HOME_ID_ROW_VIEW_ADDED;
    public static int HOME_ID_ROW_VIEW_POPULAR;

    public static int HOME_ID_ROW_LISTEN_RECOMMEND;
    public static int HOME_ID_ROW_LISTEN_ADDED;
    public static int HOME_ID_ROW_LISTEN_POPULAR;

    public ArrayObjectAdapter getArrayObjectAdapterHomeData(Context context) {
        ArrayObjectAdapter mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
//        CardPresenter cardPresenter = new CardPresenter();
        PresenterSelector presenterSelector = new HomeHeaderPresenterSelector("");

        int countRows = 0;
        Resources resources = context.getResources();
        HomeItem homeItemView = getHomeItemView();
        if (homeItemView != null) {
            ArrayList<DataDetailItem> recommend = homeItemView.getRecommend();
            ArrayList<DataDetailItem> added = homeItemView.getAdded();
            ArrayList<DataDetailItem> popular = homeItemView.getPopular();

            if (CollectionUtil.isNotEmpty(popular)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_popular_videos), popular));
                for (DataDetailItem item : popular) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_popular_videos));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_VIEW_POPULAR = countRows;
                countRows++;
            }

            if (CollectionUtil.isNotEmpty(added)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_recently_videos), added));
                for (DataDetailItem item : added) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_recently_videos));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_VIEW_ADDED = countRows;
                countRows++;
            }

            if (CollectionUtil.isNotEmpty(recommend)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_recommendation_videos), recommend));
                for (DataDetailItem item : recommend) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_recommendation_videos));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_VIEW_RECOMMEND = countRows;
                countRows++;
            }
        }

        HomeItem homeItemListen = getHomeItemListen();
        if (homeItemListen != null) {
            ArrayList<DataDetailItem> recommend = homeItemListen.getRecommend();
            ArrayList<DataDetailItem> added = homeItemListen.getAdded();
            ArrayList<DataDetailItem> popular = homeItemListen.getPopular();

            if (CollectionUtil.isNotEmpty(popular)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_popular_audios), popular));
                for (DataDetailItem item : popular) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_popular_audios));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_LISTEN_POPULAR = countRows;
                countRows++;
            }

            if (CollectionUtil.isNotEmpty(added)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_recently_audios), added));
                for (DataDetailItem item : added) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_recently_audios));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_LISTEN_ADDED = countRows;
                countRows++;
            }

            if (CollectionUtil.isNotEmpty(recommend)) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                listRowAdapter.add(new HeaderListHomeRow(context.getString(R.string.home_recommendation_audios), recommend));
                for (DataDetailItem item : recommend) {
                    listRowAdapter.add(item);
                }
                HeaderItem header = new HeaderItem(countRows, resources.getString(R.string.home_recommendation_audios));
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                HOME_ID_ROW_LISTEN_RECOMMEND = countRows;
                //                countRows++;
            }
        }
        return mRowsAdapter;
    }
}
