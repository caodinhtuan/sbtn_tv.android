package com.sbtn.androidtv.request;

import android.content.Context;

import com.sbtn.androidtv.BuildConfig;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.constants.Constant;
import com.sbtn.androidtv.constants.URL;
import com.sbtn.androidtv.datamodels.ASHomeDataObject;
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
import com.sbtn.androidtv.security.UtilsSecurity;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.HeaderHelper;
import com.sbtn.androidtv.utils.SBDateTimeUtils;
import com.sbtn.androidtv.utils.StringUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hoanguyen on 5/17/16.
 */
public class NetworkService {
    private final static String HEADER_CUSTOM = "Custom";
    private final static String HEADER_DATE_TIME = "DateTime";
    private final static String HEADER_TOKEN = "RequestToken";
    private final static String HEADER_CONTENT_TYPE = "Content-Type";
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String HEADER_USER_AGENT = "User-Agent";

    private NetworkAPI networkAPI;
    private OkHttpClient okHttpClient;
    //Context này là Application context nên không sợ bị leak
    private Context mContext;
//    private LruCache<Class<?>, Observable<?>> apiObservables;

    public NetworkService(Context context) {
        this(context, getDomainName());
    }

    private static String getDomainName() {
//        switch (URL.sBUILD_TYPE) {
//            case URL.BUILD_TYPE_RELEASE:
//                return URL.DOMAIN_NAME_RELEASE;
//
//            case URL.BUILD_TYPE_DEV:
//            default:
//                return URL.DOMAIN_NAME_DEV;
//        }
        return URL.DOMAIN_NAME;
    }

    public static String getForgotPassWeb() {
//        switch (URL.sBUILD_TYPE) {
//            case URL.BUILD_TYPE_RELEASE:
//                return URL.WEB_FORGOT_PASSWORD_RELEASE;
//
//            case URL.BUILD_TYPE_DEV:
//            default:
//                return URL.WEB_FORGOT_PASSWORD_DEV;
//        }
        return URL.WEB_FORGOT_PASSWORD;
    }

    public NetworkService(Context context, String baseUrl) {
        mContext = context;
        okHttpClient = buildClient();
//        apiObservables = new LruCache<>(10);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        networkAPI = retrofit.create(NetworkAPI.class);
    }

    /**
     * Method to return the API interface.
     *
     * @return
     */
    public NetworkAPI getAPI() {
        return networkAPI;
    }

    /**
     * Method to build and return an OkHttpClient so we can set/get
     * headers quickly and efficiently.
     *
     * @return
     */
    public OkHttpClient buildClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // Do anything with response here
                //if we ant to grab a specific cookie or something..
                return chain.proceed(chain.request());
            }
        });

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //this is where we will add whatever we want to our request headers.
                Request basicRequest = chain.request();
                Request.Builder requestBuilder = basicRequest.newBuilder();
                String agent = UtilsSecurity.getUserAgent(mContext);
                String httpVerb = basicRequest.method();
                String dateTime = SBDateTimeUtils.getCurrentUTCDateTime();
                String authenticate = HeaderHelper.createAuthorizationValue(httpVerb, dateTime,
                        CacheDataManager.getInstance(mContext).getAccessToken(), 78);

                String token = HeaderHelper.createRequestTokenValue(httpVerb, dateTime, Constant.PRIVATE_KEY, 78);

                if (StringUtils.isNotEmpty(basicRequest.header(HEADER_CUSTOM))) {
                    requestBuilder.addHeader(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
                    requestBuilder.addHeader(HEADER_AUTHORIZATION, authenticate);
                    requestBuilder.addHeader(HEADER_USER_AGENT, agent);

                    requestBuilder.removeHeader(HEADER_CUSTOM);
                }
                requestBuilder.addHeader(HEADER_DATE_TIME, dateTime);
                requestBuilder.addHeader(HEADER_TOKEN, token);

                return chain.proceed(requestBuilder.build());
            }
        });

        if (BuildConfig.DEBUG && ALog.ENABLED) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }


//    /**
//     * Method to either return a cached observable or prepare a new one.
//     *
//     * @param unPreparedObservable
//     * @param clazz
//     * @param cacheObservable
//     * @param useCache
//     * @return Observable ready to be subscribed to
//     */
//    public Observable<?> getPreparedObservable(Observable<?> unPreparedObservable, Class<?> clazz, boolean cacheObservable, boolean useCache) {
//
//        Observable<?> preparedObservable = null;
//
//        if (useCache)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
//            preparedObservable = apiObservables.get(clazz);
//
//        if (preparedObservable != null)
//            return preparedObservable;
//
//
//        //we are here because we have never created this observable before or we didn't want to use the cache...
//
//        preparedObservable = unPreparedObservable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread());
//
//        if (cacheObservable) {
//            preparedObservable = preparedObservable.cache();
//            apiObservables.put(clazz, preparedObservable);
//        }
//        return preparedObservable;
//    }

//
//    /**
//     * Method to clear the entire cache of observables
//     */
//    public void clearCache() {
//        apiObservables.evictAll();
//    }

    /**
     * all the Service alls to use for the retrofit requests.
     */
    public interface NetworkAPI {
        @GET(URL.HOME)
        Call<ASHomeDataObject> getHomeData();

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.DETAIL + "{itemId}")
        Call<ViewDetail> getDetailItemData(@Path("itemId") int itemId, @Field("device_id") String device_id);

        @FormUrlEncoded
        @POST(URL.REGISTER)
        Call<UserInfoDataCallback> register(@Field("email") String email, @Field("password") String password);

        @FormUrlEncoded
        @POST(URL.LOGIN)
        Call<UserInfoDataCallback> login(@Field("email") String email, @Field("password") String password);

        @FormUrlEncoded
        @POST(URL.FB_LOGIN)
        Call<UserInfoDataCallback> loginByFacebook(@Field("facebookID") String facebookID, @Field("facebookToken") String facebookToken,
                                                   @Field("fullName") String fullName, @Field("avatar") String avatar, @Field("emailAddress") String emailAddress);

        @Headers(HEADER_CUSTOM + ": true")
        @GET(URL.LOGOUT)
        Call<LogoutCallBack> logout();

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.UPDATE_PAYMENT)
        Call<DefaultDataCallBackRequest> updatePayment(@Field("pkd_id") int pkd_id, @Field("transactionId") String transactionId);

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.CHECK_PROMOTION_PAYMENT)
        Call<DefaultDataCallBackRequest> checkPromotionPayment(@Field("pk_id") int pk_id, @Field("proCode") String proCode);


        @GET(URL.LIST_ALL_PACKAGE)
        @Headers(HEADER_CUSTOM + ": true")
        Call<ListPackageDataCallBackRequest> getListAllPackage();

        @GET(URL.LIST_ALL_PACKAGE)
        @Headers(HEADER_CUSTOM + ": true")
        Call<ListPackageTempDataCallBackRequest> getListAllPackageTemp();

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.TRACKING_START)
        Call<TrackingDataCallBackRequest> trackingStart(@Field("token") String token, @Field("content_id") String content_id, @Field("device") String device,
                                                        @Field("device_id") String device_id, @Field("os") String os);

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.TRACKING_END)
        Call<DefaultDataCallBackRequest> trackingEnd(@Field("token") String token, @Field("id") String id);

        @GET(URL.SEARCH + "{keySearch}")
        Call<SearchDataCallBackRequest> search(@Path("keySearch") String keySearch);

        @GET(URL.MENU)
        Call<MainMenuDataCallback> getMainMenu();

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.PING_SERVER)
        Call<DefaultDataCallBackRequest> pingServer(@Field("device_id") String device_id);

        @FormUrlEncoded
        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.MEMBER_END_VIEW)
        Call<DefaultDataCallBackRequest> memberEndView(@Field("device_id") String device_id);

        @GET(URL.PROVIDER + "{itemId}" + URL.LANG + "{langOfContentId}")
        Call<MenuDetailDataCallbackRequest> getProviders(@Path("itemId") int itemId, @Path("langOfContentId") int langOfContentId);

        @GET(URL.CATEGORY + "{itemId}" + URL.LANG + "{langOfContentId}")
        Call<MenuDetailDataCallbackRequest> getCategories(@Path("itemId") int itemId, @Path("langOfContentId") int langOfContentId);

        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.PACKAGE_LIST_ALL_CONTENTS)
        Call<MenuDetailDataCallbackRequest> getPurchasedContents();

        @Headers(HEADER_CUSTOM + ": true")
        @POST(URL.PACKAGE_LIST_PACKAGE_PAYMENT)
        Call<ListPurchasedPackageDataCallBackRequest> getPurchasedPackagePayment();

        @Headers(HEADER_CUSTOM + ": true")
        @GET(URL.PACKAGE_LIST_CONTENT_BY_ID)
        Call<MenuDetailDataCallbackRequest> getPurchasedContentsByPackageId(@Query("pk_id") int pk_id);
    }
}
