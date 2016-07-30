package com.sbtn.androidtv.request;

import com.sbtn.androidtv.utils.ALog;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by hoanguyen on 6/9/16.
 */
public abstract class ObservableManager<T extends Object> {
    private static final String TAG = "ObservableManager";
    private RequestFramework.DataCallBack<T> callBack;

    public ObservableManager(RequestFramework.DataCallBack<T> callBack) {
        this.callBack = callBack;
    }

    protected abstract T run() throws IOException;

    /**
     * Thực thi ở background rồi trả data về ở MainUIThread
     */
    public void executeReturnMainUIThread() {
        execute(true);
    }

    /**
     * Thực thi ở background rồi trả data về ở Background thread đó luôn
     */
    public void executeReturnBackgroundThread() {
        execute(false);
    }

    private void execute(boolean isReturnUI) {
        Observable integerObservable = Observable.defer(new Func0<Observable<T>>() {

            @Override
            public Observable<T> call() {
                T response = null;
                try {
                    response = run();

                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.just(null);
                }

                return Observable.just(response);
            }
        });

        integerObservable.subscribeOn(Schedulers.newThread())
                .observeOn(isReturnUI ? AndroidSchedulers.mainThread() : Schedulers.immediate())
                .subscribe(new Subscriber<T>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   ALog.e(TAG, "ObservableManager - onError()", e);
                                   if (callBack != null)
                                       callBack.onFailure();
                               }

                               @Override
                               public void onNext(T data) {
                                   if (callBack != null)
                                       callBack.onResponse(data);
                               }
                           }
                );
    }
}



