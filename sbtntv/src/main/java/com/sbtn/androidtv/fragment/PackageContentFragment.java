package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;
import android.view.View;

import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.PackageDetailWithContentsActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.HeaderListRow;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.datamodels.Shows;
import com.sbtn.androidtv.presenter.HomeHeaderPresenterSelector;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.MenuDetailDataCallbackRequest;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class PackageContentFragment extends BasePackageLeanBackDetailFragment {
    public static final String ARGS_ID = "ARGS_ID";
    public static final String ARGS_NAME_ROW = "ARGS_NAME_ROW";
    public static final String TAG = PackageContentFragment.class.getSimpleName();
    private ArrayObjectAdapter mAdapter;
    private int mId;

    int rowIdSelected = -1;


    public static PackageContentFragment newInstance(int id) {

        Bundle args = new Bundle();
        args.putInt(ARGS_ID, id);

        PackageContentFragment fragment = new PackageContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setupAdapter();
        loadData();
//        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    private void initData() {
        Bundle args = getArguments();
        mId = args.getInt(ARGS_ID, -1);
    }

    private void setupAdapter() {

        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof DataDetailItem) {
                    DataDetailItem dataDetailItem = (DataDetailItem) item;
//                ALog.d(TAG, "Item: " + item.toString());
                    Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, dataDetailItem.getId());

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsItemActivity.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);


                }
            }
        });

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                rowIdSelected = (int) row.getId();
            }
        });

    }

    private void loadData() {
        showLoading();
        RequestFramework.getPurchasedContentsByPackageId(getActivity(), mId, callBack);
    }

    private RequestFramework.DataCallBack<MenuDetailDataCallbackRequest> callBack = new RequestFramework.DataCallBack<MenuDetailDataCallbackRequest>() {
        @Override
        public void onResponse(MenuDetailDataCallbackRequest dataResult) {
            if (dataResult != null && dataResult.isSuccess()) {
                bindingData(dataResult);
            } else {
                showRequestError();
            }
            hideLoading();
        }

        @Override
        public void onFailure() {
            showRequestError();
            hideLoading();
        }
    };

    private void bindingData(MenuDetailDataCallbackRequest dataCallbackRequest) {
        ArrayList<Shows> showses = dataCallbackRequest.getShowses();
        if (showses == null) {
            showRequestError();
            return;
        }
        mAdapter.clear();
        if (CollectionUtil.isNotEmpty(showses)) {
            for (int i = 0; i < showses.size(); i++) {
                Shows show = showses.get(i);
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new HomeHeaderPresenterSelector(show.getShowsName()));
                listRowAdapter.add(new HeaderListRow(show.getShowsName()));
                listRowAdapter.addAll(1, show.getShowsDetails());
                HeaderItem header = new HeaderItem(show.getShowsId(), show.getShowsName());
                mAdapter.add(new ListRow(i, header, listRowAdapter));

            }
        }

    }


    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((BaseActivity) activity).showLoading();
        }
    }

    public void hideLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((BaseActivity) activity).hideLoading();
        }
    }

    private void showRequestError() {
        MyDialog.showDialogServiceError(getActivity(), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getActivity().finish();
            }
        });
    }

    @Override
    protected void clearAndReloadData() {
//        loadData();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    public void handleShowDialogForPackage(SBTNPackage sbtnPackage) {
        if (sbtnPackage.isBuy()) {
            getDialogInfoPackage(sbtnPackage).show();
        } else {
            if (!CacheDataManager.getInstance(getActivity()).isLogin()) {
                Intent intent = new Intent(getActivity(), AccountManagerActivity.class);
                intent.putExtra(AccountManagerActivity.EXTRA_LOGIN_FOR_BUY_PACKAGE, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
                startActivityForResult(intent, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
            } else {
                if (sbtnPackage.isPromotion()) {
                    getDialogEnterCode(sbtnPackage).show();
                } else {
                    getDialogBuyPackage(sbtnPackage).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE:
                if (resultCode == Activity.RESULT_OK) {
                    loadData();
                } else {
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PackageDetailWithContentsActivity activity = (PackageDetailWithContentsActivity) getActivity();
        activity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP && (rowIdSelected <= 0 || mAdapter.size() == 0)) {
                    activity.buyButtonFocus();
                }

                return false;
            }
        });
    }
}
