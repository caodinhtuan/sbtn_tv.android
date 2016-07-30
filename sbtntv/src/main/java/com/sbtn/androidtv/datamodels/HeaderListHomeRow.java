package com.sbtn.androidtv.datamodels;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/13/16.
 */
public class HeaderListHomeRow extends HeaderListRow {
    final ArrayList<DataDetailItem> arrData;

    public HeaderListHomeRow(String title, ArrayList<DataDetailItem> arrData) {
        super(title);
        this.arrData = arrData;
    }

    public ArrayList<DataDetailItem> getArrData() {
        return arrData;
    }
}
