package com.sbtn.androidtv.request.datacallback;

/**
 * Created by hoanguyen on 5/18/16.
 */
public class UserInfoDataCallback extends BaseDataCallbackRequest {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String accessToken;
        private String avatarUrl;
        private String fullName;
        private String memberId;

        public String getAccessToken() {
            return accessToken;
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
    }
}
