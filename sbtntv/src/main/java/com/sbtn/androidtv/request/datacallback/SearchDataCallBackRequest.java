package com.sbtn.androidtv.request.datacallback;

import com.sbtn.androidtv.datamodels.Related;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class SearchDataCallBackRequest extends BaseDataCallbackRequest {
    private ArrayList<Related> view;
    private ArrayList<Related> listen;

    public ArrayList<Related> getView() {
        return view;
    }

    public ArrayList<Related> getListen() {
        return listen;
    }
}
