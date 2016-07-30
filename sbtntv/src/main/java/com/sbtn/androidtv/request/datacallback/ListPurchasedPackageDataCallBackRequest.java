package com.sbtn.androidtv.request.datacallback;


import com.sbtn.androidtv.datamodels.SBTNPackage;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 5/24/16.
 */
public class ListPurchasedPackageDataCallBackRequest extends BaseDataCallbackRequest {
    private ArrayList<SBTNPackage> data;

    public ArrayList<SBTNPackage> getListPurchasedPackage() {
        return data;
    }
}
