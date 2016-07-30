package com.sbtn.androidtv.request.datacallback;

/**
 * Created by hoanguyen on 5/24/16.
 */
public abstract class BaseDataCallbackRequest {

    private int code;
    private int error = -1;
    private String message;

    public int getCode() {
        return code;
    }

    public int getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == 0;
    }

    public String getMessage() {
        return message;
    }
}
