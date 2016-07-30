package com.sbtn.androidtv.datamodels;

/**
 * Created by hoanguyen on 6/13/16.
 */
public class HeaderListPackageRow extends HeaderListRow {
    final int pkId;

    public HeaderListPackageRow(String title, int pkId) {
        super(title);
        this.pkId = pkId;
    }

    public int getPkId() {
        return pkId;
    }
}
