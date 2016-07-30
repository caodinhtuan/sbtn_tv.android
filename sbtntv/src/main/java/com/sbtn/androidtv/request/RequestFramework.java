package com.sbtn.androidtv.request;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sbtn.androidtv.MyApplication;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.ASHomeDataObject;
import com.sbtn.androidtv.datamodels.Adv;
import com.sbtn.androidtv.datamodels.Shows;
import com.sbtn.androidtv.datamodels.UserInfo;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.request.datacallback.DefaultDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.ListPackageDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.ListPackageTempDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.ListPurchasedPackageDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.LogoutCallBack;
import com.sbtn.androidtv.request.datacallback.MainMenuDataCallback;
import com.sbtn.androidtv.request.datacallback.MenuDetailDataCallbackRequest;
import com.sbtn.androidtv.request.datacallback.SearchDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.TrackingDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.UserInfoDataCallback;
import com.sbtn.androidtv.request.datarequest.FacebookRegisterResource;
import com.sbtn.androidtv.security.SBDeviceUtils;
import com.sbtn.androidtv.utils.ALog;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by hoanguyen on 5/11/16.
 */
public class RequestFramework {

    private static final String TAG = "RequestFramework";

    public interface DataCallBack<T> {
        void onResponse(T dataResult);

        void onFailure();

    }

    private static NetworkService getNetworkService(Context c) {
        if (c == null) return null;
        return ((MyApplication) c.getApplicationContext()).getNetworkService();
    }

    /**
     * Request get data cho home screen
     */
    public static void getHomeScreenData(final Context context, final DataCallBack<ASHomeDataObject> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected ASHomeDataObject run() throws IOException {
                ASHomeDataObject response = null;
                //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
                CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
                response = cacheDataManager.getASHomeDataObject();
                if (response != null)
                    return response;

                //Cache không có thì đi request server
                Call<ASHomeDataObject> callRequest = getNetworkService(context).getAPI().getHomeData();
                Response<ASHomeDataObject> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                cacheDataManager.saveASHomeDataObject(response);
                return response;
            }
        }.executeReturnMainUIThread();
    }


    /**
     * Request lấy data cho màn hình detail hoặc playback bằng id của item
     *
     * @param context
     * @param itemId   Id của item cần request
     * @param callBack
     */
    public static void getDetailScreenDataById(final Context context, final int itemId, final DataCallBack<ViewDetail> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected ViewDetail run() throws IOException {
                ViewDetail response = null;
                //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
                CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
                response = cacheDataManager.getViewDetail(itemId);
                if (response != null)
                    return response;

                //Cache không có thì đi request server
                Call<ViewDetail> callRequest = getNetworkService(context).getAPI().getDetailItemData(itemId, SBDeviceUtils.getDeviceId(context));
                Response<ViewDetail> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                cacheDataManager.saveViewDetail(itemId, response);
                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void register(final Context context, final String email, final String password, final DataCallBack<UserInfoDataCallback> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected UserInfoDataCallback run() throws IOException {
                UserInfoDataCallback response = null;

                Call<UserInfoDataCallback> callRequest = getNetworkService(context).getAPI().register(email, password);
                Response<UserInfoDataCallback> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void login(final Context context, final String email, final String password, final DataCallBack<UserInfoDataCallback> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected UserInfoDataCallback run() throws IOException {
                UserInfoDataCallback response = null;

                Call<UserInfoDataCallback> callRequest = getNetworkService(context).getAPI().login(email, password);
                Response<UserInfoDataCallback> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                    CacheDataManager.getInstance(context).saveUserInfo(UserInfo.createUserInfoByServerData(response.getData(), email));
                }

                return response;
            }
        }.executeReturnMainUIThread();

    }

    public static void loginByFacebook(final Context context, final @NonNull FacebookRegisterResource resource, final DataCallBack<UserInfoDataCallback> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected UserInfoDataCallback run() throws IOException {
                UserInfoDataCallback response = null;

                Call<UserInfoDataCallback> callRequest = getNetworkService(context).getAPI().loginByFacebook(resource.getFacebookID(), resource.getFacebookToken(),
                        resource.getFullName(), resource.getAvatar(), resource.getEmailAddress());
                Response<UserInfoDataCallback> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                    CacheDataManager.getInstance(context).saveUserInfo(UserInfo.createUserInfoByServerData(response.getData(), resource.getEmailAddress()));
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void logOut(final Context context, final DataCallBack<LogoutCallBack> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected LogoutCallBack run() throws IOException {
                LogoutCallBack response = null;

                Call<LogoutCallBack> callRequest = getNetworkService(context).getAPI().logout();
                try {
                    Response<LogoutCallBack> dataResponseCall = callRequest.execute();

                    if (dataResponseCall != null) {
                        response = dataResponseCall.body();
                    }
                } catch (IOException e) {
                    throw e;
                } finally {
                    CacheDataManager.getInstance(context).handleUserSignOut();
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void parseAdv(final Context context, final String link, final DataCallBack<Adv> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected Adv run() throws IOException {
                return VastDAO.Singleton.getAdvContent(link);
            }
        }.executeReturnMainUIThread();
    }

    /**
     * Function update payment sau khi user đã mua thành công package
     *
     * @param context
     * @param callBack
     */
    public static void updatePayment(final Context context, final int viewDetailId, final int pk_id, final String transactionId, final DataCallBack<DefaultDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected DefaultDataCallBackRequest run() throws IOException {
                DefaultDataCallBackRequest response = null;

                Call<DefaultDataCallBackRequest> callRequest = getNetworkService(context).getAPI().updatePayment(pk_id, transactionId);
                Response<DefaultDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                    if (response.isSuccess())
                        CacheDataManager.getInstance(context).clearContentData();
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    /**
     * Function update payment sau khi user đã mua thành công package
     *
     * @param context
     * @param callBack
     */
    public static void checkPromotionPayment(final Context context, final int viewDetailId, final int pk_id, final String proCode, final DataCallBack<DefaultDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected DefaultDataCallBackRequest run() throws IOException {
                DefaultDataCallBackRequest response = null;

                Call<DefaultDataCallBackRequest> callRequest = getNetworkService(context).getAPI().checkPromotionPayment(pk_id, proCode);
                Response<DefaultDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                    if (response.isSuccess())
                        CacheDataManager.getInstance(context).clearContentData();
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }


    public static void getListAllPackage(final Context context, final DataCallBack<ListPackageDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected ListPackageDataCallBackRequest run() throws IOException {
                ListPackageDataCallBackRequest response = null;

                Call<ListPackageDataCallBackRequest> callRequest = getNetworkService(context).getAPI().getListAllPackage();
                Response<ListPackageDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }


    public static void getListAllPackageTemp(final Context context, final DataCallBack<ListPackageTempDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected ListPackageTempDataCallBackRequest run() throws IOException {
                ListPackageTempDataCallBackRequest response = null;

                Call<ListPackageTempDataCallBackRequest> callRequest = getNetworkService(context).getAPI().getListAllPackageTemp();
                Response<ListPackageTempDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void trackingStart(final Context context, final String content_id, final DataCallBack<TrackingDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected TrackingDataCallBackRequest run() throws IOException {
                TrackingDataCallBackRequest response = null;
                String token = CacheDataManager.getInstance(context).getAccessToken();
                Call<TrackingDataCallBackRequest> callRequest = getNetworkService(context).getAPI()
                        .trackingStart(token, content_id, SBDeviceUtils.getDeviceName(), SBDeviceUtils.getDeviceId(context), SBDeviceUtils.getOSVersion());
                Response<TrackingDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void trackingEnd(final Context context, final String id, final DataCallBack<DefaultDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected DefaultDataCallBackRequest run() throws IOException {
                DefaultDataCallBackRequest response = null;
                String token = CacheDataManager.getInstance(context).getAccessToken();
                Call<DefaultDataCallBackRequest> callRequest = getNetworkService(context).getAPI()
                        .trackingEnd(token, id);
                Response<DefaultDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void search(final Context context, final String keySearch, final DataCallBack<SearchDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected SearchDataCallBackRequest run() throws IOException {
                SearchDataCallBackRequest response = null;
                Call<SearchDataCallBackRequest> callRequest = getNetworkService(context).getAPI().search(keySearch);
                Response<SearchDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getMainMenu(final Context context, final DataCallBack<MainMenuDataCallback> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected MainMenuDataCallback run() throws IOException {
                MainMenuDataCallback response = null;
                Call<MainMenuDataCallback> callRequest = getNetworkService(context).getAPI().getMainMenu();
                Response<MainMenuDataCallback> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getProviders(final Context context, final int id, final int lang, final DataCallBack<ArrayList<Shows>> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
        final CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
        ArrayList<Shows> showses = cacheDataManager.getProviders(id);

        if (showses != null) {
            callBack.onResponse(showses);
            return;
        }

        new ObservableManager(callBack) {
            @Override
            protected ArrayList<Shows> run() throws IOException {
                ArrayList<Shows> response = null;
                Call<MenuDetailDataCallbackRequest> callRequest = getNetworkService(context).getAPI().getProviders(id, lang);
                Response<MenuDetailDataCallbackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body().getShowses();
                    cacheDataManager.saveProviders(response, id);
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getCategories(final Context context, final int id, final int lang, final DataCallBack<ArrayList<Shows>> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

        //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
        final CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
        ArrayList<Shows> showses = cacheDataManager.getCategories(id);

        if (showses != null) {
            callBack.onResponse(showses);
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected ArrayList<Shows> run() throws IOException {
                ArrayList<Shows> response = null;
                Call<MenuDetailDataCallbackRequest> callRequest = getNetworkService(context).getAPI().getCategories(id, lang);
                Response<MenuDetailDataCallbackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body().getShowses();
                    cacheDataManager.saveCategories(response, id);
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getPurchasedContents(final Context context, final DataCallBack<ArrayList<Shows>> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

//        //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
//        final CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
//        ArrayList<Shows> showses = cacheDataManager.getCategories(id);
//
//        if (showses != null) {
//            callBack.onResponse(showses);
//            return;
//        }
        new ObservableManager(callBack) {
            @Override
            protected ArrayList<Shows> run() throws IOException {
                ArrayList<Shows> response = null;
                Call<MenuDetailDataCallbackRequest> callRequest = getNetworkService(context).getAPI().getPurchasedContents();
                Response<MenuDetailDataCallbackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body().getShowses();
//                    cacheDataManager.saveCategories(response, id);
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getPurchasedContentsByPackageId(final Context context, final int pkId, final DataCallBack<MenuDetailDataCallbackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }

//        //Đầu tiên lấy data trong cache ra trước - có thì trả giá trị về luôn
//        final CacheDataManager cacheDataManager = CacheDataManager.getInstance(context);
//        ArrayList<Shows> showses = cacheDataManager.getCategories(id);
//
//        if (showses != null) {
//            callBack.onResponse(showses);
//            return;
//        }
        new ObservableManager(callBack) {
            @Override
            protected MenuDetailDataCallbackRequest run() throws IOException {
                MenuDetailDataCallbackRequest response = null;
                Call<MenuDetailDataCallbackRequest> callRequest = getNetworkService(context).getAPI().getPurchasedContentsByPackageId(pkId);
                Response<MenuDetailDataCallbackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null) {
                    response = dataResponseCall.body();
//                    cacheDataManager.saveCategories(response, id);
                }

                return response;
            }
        }.executeReturnMainUIThread();
    }


    public static void pingServer(final Context context, final DataCallBack<DefaultDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected DefaultDataCallBackRequest run() throws IOException {
                DefaultDataCallBackRequest response = null;
                Call<DefaultDataCallBackRequest> callRequest = getNetworkService(context).getAPI().pingServer(SBDeviceUtils.getDeviceId(context));
                Response<DefaultDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }


    public static void memberEndView(final Context context, final DataCallBack<DefaultDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected DefaultDataCallBackRequest run() throws IOException {
                DefaultDataCallBackRequest response = null;
                Call<DefaultDataCallBackRequest> callRequest = getNetworkService(context).getAPI().memberEndView(SBDeviceUtils.getDeviceId(context));
                Response<DefaultDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }

    public static void getPurchasedPackagePayment(final Context context, final DataCallBack<ListPurchasedPackageDataCallBackRequest> callBack) {
        if (context == null) {
            ALog.e(TAG, "context==null");
            callBack.onFailure();
            return;
        }
        new ObservableManager(callBack) {
            @Override
            protected ListPurchasedPackageDataCallBackRequest run() throws IOException {
                ListPurchasedPackageDataCallBackRequest response = null;
                Call<ListPurchasedPackageDataCallBackRequest> callRequest = getNetworkService(context).getAPI().getPurchasedPackagePayment();
                Response<ListPurchasedPackageDataCallBackRequest> dataResponseCall = callRequest.execute();

                if (dataResponseCall != null)
                    response = dataResponseCall.body();

                return response;
            }
        }.executeReturnMainUIThread();
    }
}