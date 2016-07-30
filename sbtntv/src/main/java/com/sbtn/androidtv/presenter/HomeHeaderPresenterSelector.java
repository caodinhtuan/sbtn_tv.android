package com.sbtn.androidtv.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.sbtn.androidtv.datamodels.HeaderListRow;
import com.sbtn.androidtv.datamodels.PlayItemInterface;

/**
 * Created by hoanguyen on 6/17/16.
 */
public class HomeHeaderPresenterSelector extends PresenterSelector {
    Presenter mPresenterHeader;
    Presenter mPresenterCard;
    private Presenter[] mPresenters;

    public HomeHeaderPresenterSelector(String name) {
        mPresenterHeader = new HeaderCardPresenter();
        mPresenterCard = new CardPresenter(name);
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (item instanceof HeaderListRow)
            return mPresenterHeader;
        else if (item instanceof PlayItemInterface) {
            return mPresenterCard;
        }

        return null;
    }

    @Override
    public Presenter[] getPresenters() {
        if (mPresenters == null)
            mPresenters = new Presenter[]{mPresenterHeader, mPresenterCard};
        return mPresenters;
    }
}
