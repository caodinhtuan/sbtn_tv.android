package com.sbtn.androidtv.constants;

import com.sbtn.androidtv.BuildConfig;

/**
 * Created by onworldtv on 9/12/15.
 */
public class URL {
//    public static final int BUILD_TYPE_RELEASE = 1;
//    public static final int BUILD_TYPE_DEV = -1;
//    TODO Đổi lại khi Release
//    public static int sBUILD_TYPE = BUILD_TYPE_DEV;

//    public static final String VERSION_CODE = "v1.6/";
//    public static final String APP_NAME = "sbtn/";
//    public static final String APP_NAME = "onasia/";

    //    public static final String DOMAIN_NAME_RELEASE = "http://api.onworldtv.com/";
//    public static final String DOMAIN_NAME_RELEASE = "http://ottapi.com/" + VERSION_CODE + APP_NAME;
//    public static final String DOMAIN_NAME_DEV = "http://dev.ottapi.com/" + VERSION_CODE + APP_NAME;


    public static final String DOMAIN_NAME = BuildConfig.BASE_SERVICE_URL + BuildConfig.SERVICE_URL_VERSION_CODE + BuildConfig.SERVICE_URL_APP_NAME;
    public static final String WEB_FORGOT_PASSWORD = BuildConfig.SERVICE_URL_FORGOT_PASS;
    public static final String SEARCH = "search&keyword=";
    public static final String HOME = "home&lang_id=1&item_count=6";
    public static final String DETAIL = "detail/index/";
    public static final String LANGUAGE = "language";
    public static final String MENU = "home/menu";
    public static final String PROVIDER = "contentgroup&lang_id=1&provider_id=";
    public static final String CATEGORY = "contentgroup&lang_id=1&category_id=";
    public static final String VOTING_PROGRAM_LIST = "voting";
    public static final String VOTING_ROUND_DETAIL = "examinee&r_id=";
    public static final String VOTING_PROGRAM_DETAIL = "voting/detail/";
    public static final String VOTING_CANDIDATE_DETAIL = "examinee/detail&id=";
    public static final String REGISTER = "member/register";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String FB_LOGIN = "fblogin";
    public static final String VOTING = "vote";
    public static final String DOWNLOAD = "download";
    public static final String UPDATE_PAYMENT = "payment/success";
    public static final String CHECK_PROMOTION_PAYMENT = "payment/promotion";
    public static final String LIST_ALL_PACKAGE = "package/listall";
    public static final String TRACKING_START = "tracking/start";
    public static final String TRACKING_END = "tracking/end";
    public static final String PING_SERVER = "index/ping";
    public static final String MEMBER_END_VIEW = "member/endview";
    public static final String LANG = "&lang_of_content=";
    public static final String PACKAGE_LIST_ALL_CONTENTS = "package/mycontents";
    public static final String PACKAGE_LIST_PACKAGE_PAYMENT = "package/listpayment";
//    public static final String PACKAGE_LIST_CONTENT_BY_ID = "package/listcontentbypackage?pk_id=";
    public static final String PACKAGE_LIST_CONTENT_BY_ID = "package/listcontentbypackage";


    //    public static final String WEB_FORGOT_PASSWORD = "http://onworldtv.com/en/user/forgot_password";
//    public static final String WEB_FORGOT_PASSWORD_DEV = "http://dev.sbtnapp.com/en/user/forgot_password";
//    public static final String WEB_FORGOT_PASSWORD_RELEASE = "http://sbtnapp.com/en/user/forgot_password";

    public static final String TEMP_LINK_VIDEO = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/ForBiggerFun.m3u8";

}
