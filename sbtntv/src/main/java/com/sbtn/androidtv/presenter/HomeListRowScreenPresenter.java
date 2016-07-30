package com.sbtn.androidtv.presenter;

import com.sbtn.androidtv.datamodels.ASHomeDataObject;
import com.sbtn.androidtv.fragment.HomeListRowFragment;
import com.sbtn.androidtv.request.RequestFramework;

/**
 * Created by hoanguyen on 5/17/16.
 */
public class HomeListRowScreenPresenter extends BasePresenter implements HomeScreenInteractor {

    private HomeListRowFragment view;


    public HomeListRowScreenPresenter(HomeListRowFragment view) {
        this.view = view;
    }

    @Override
    public void loadHomeScreenData() {


        view.showLoading();
        RequestFramework.getHomeScreenData(view.getActivity(), new RequestFramework.DataCallBack<ASHomeDataObject>() {
            @Override
            public void onResponse(ASHomeDataObject response) {
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
