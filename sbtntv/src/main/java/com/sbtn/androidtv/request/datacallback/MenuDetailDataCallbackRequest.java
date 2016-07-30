package com.sbtn.androidtv.request.datacallback;

import com.sbtn.androidtv.datamodels.Shows;

import java.util.ArrayList;

/**
 * Created by Steve on 9/6/2015.
 */
public class MenuDetailDataCallbackRequest extends BaseDataCallbackRequest {

    ArrayList<Shows> groups;

    public ArrayList<Shows> getShowses() {
        return groups;
    }

    public MenuDetailDataCallbackRequest(ArrayList<Shows> groups) {
        this.groups = groups;
    }
}
