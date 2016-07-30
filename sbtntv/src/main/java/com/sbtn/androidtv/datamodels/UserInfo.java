package com.sbtn.androidtv.datamodels;

import com.sbtn.androidtv.request.datacallback.UserInfoDataCallback;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class UserInfo {
    private String token;
    private String avatarUrl;
    private String fullName;
    private String memberId;
    private String email;

    public String getToken() {
        return token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public static UserInfo createUserInfoByServerData(UserInfoDataCallback.Data data, String email) {
        if (data == null) return null;

        UserInfo userInfo = new UserInfo();
        userInfo.token = data.getAccessToken();
        userInfo.avatarUrl = data.getAvatarUrl();
        userInfo.fullName = data.getFullName();
        userInfo.memberId = data.getMemberId();
        userInfo.email = email;

        return userInfo;
    }
}
