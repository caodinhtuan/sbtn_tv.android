package com.sbtn.androidtv.presenter;

import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.fragment.DetailsItemFragment;
import com.sbtn.androidtv.request.RequestFramework;

/**
 * Created by hoanguyen on 5/17/16.
 */
public class DetailScreenPresenter extends BasePresenter implements DetailScreenInteractor {

    private DetailsItemFragment view;

    public DetailScreenPresenter(DetailsItemFragment view) {
        this.view = view;
    }

    @Override
    public void loadDetailItem(int itemId) {

        view.showLoading();
        RequestFramework.getDetailScreenDataById(view.getActivity(), itemId, new RequestFramework.DataCallBack<ViewDetail>() {
            @Override
            public void onResponse(ViewDetail response) {
                view.bindingData(response);
                view.hideLoading();
            }

            @Override
            public void onFailure() {
                view.showError(null);
                view.hideLoading();
            }
        });
    }
}
