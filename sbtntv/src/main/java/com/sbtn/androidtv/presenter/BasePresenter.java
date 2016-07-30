package com.sbtn.androidtv.presenter;

import rx.Subscription;

/**
 * Created by hoanguyen on 5/17/16.
 */
public abstract class BasePresenter {

    protected Subscription subscription;

    public void rxUnSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
