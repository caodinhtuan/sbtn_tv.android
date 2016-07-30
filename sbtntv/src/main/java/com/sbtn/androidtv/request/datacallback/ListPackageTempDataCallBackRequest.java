package com.sbtn.androidtv.request.datacallback;

import com.sbtn.androidtv.datamodels.SBTNPackage;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class ListPackageTempDataCallBackRequest extends BaseDataCallbackRequest {
    private ArrayList<SBTNPackage> data;

    public ArrayList<SBTNPackage> getData() {
        return data;
    }
}
