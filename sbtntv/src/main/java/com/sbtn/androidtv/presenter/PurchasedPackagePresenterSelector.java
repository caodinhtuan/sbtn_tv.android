package com.sbtn.androidtv.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.sbtn.androidtv.datamodels.HeaderListRow;
import com.sbtn.androidtv.datamodels.SBTNPackage;

/**
 * Created by hoanguyen on 6/17/16.
 */
public class PurchasedPackagePresenterSelector extends PresenterSelector {
    Presenter mPresenterHeader = new HeaderCardPresenter();
    Presenter mPackageCardPresenter = new PackageCardPresenter();
    private Presenter[] mPresenters;

    public PurchasedPackagePresenterSelector() {
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (item instanceof HeaderListRow)
            return mPresenterHeader;
        else if (item instanceof SBTNPackage) {
            return mPackageCardPresenter;
        }

        return null;
    }

    @Override
    public Presenter[] getPresenters() {
        if (mPresenters == null)
            mPresenters = new Presenter[]{mPresenterHeader, mPackageCardPresenter};
        return mPresenters;
    }
}
