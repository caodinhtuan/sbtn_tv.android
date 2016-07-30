/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.MainActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.HeaderListPackageRow;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.presenter.PurchasedPackagePresenterSelector;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.ListPurchasedPackageDataCallBackRequest;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.util.ArrayList;

/*
 * LeanbackDetailsFragment extends DetailsItemFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class SBTNPurchasedPackageBrowserFragment extends BasePackageLeanBackDetailFragment implements BrowseFragment.MainFragmentAdapterProvider {
    private static final int REQUEST_CODE_LIST_PACKAGE_SCREEN = 12345;
    public static final String TAG = SBTNPurchasedPackageBrowserFragment.class.getSimpleName();
    public static final String EXTRA_LIST_PACKAGE = "EXTRA_LIST_PACKAGE";

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private ArrayList<SBTNPackage> mSBTNPackageGroupArrayList;

    public static SBTNPurchasedPackageBrowserFragment newInstance() {
        return new SBTNPurchasedPackageBrowserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setupAdapter();
        setupHeaderTransitionListener();
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void loadData() {
        showLoading();
        hideEmptyText();
        RequestFramework.getPurchasedPackagePayment(getActivity(), new RequestFramework.DataCallBack<ListPurchasedPackageDataCallBackRequest>() {
            @Override
            public void onResponse(ListPurchasedPackageDataCallBackRequest dataResult) {
                if (dataResult != null) {
                    bindingData(dataResult.getListPurchasedPackage());
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
        });
    }

    private void setupHeaderTransitionListener() {
        Activity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).addHeadersTransitionListener(new BrowseFragment.BrowseTransitionListener() {
                @Override
                public void onHeadersTransitionStop(boolean withHeaders) {
                    super.onHeadersTransitionStop(withHeaders);
                    if (!withHeaders && isVisible() && button != null) {
                        button.requestFocus();
                    }
                }
            });
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
        hideEmptyText();
    }

    View viewEmptyContents;
    View button;

    private void hideEmptyText() {
        if (getView() != null && viewEmptyContents != null) {
            ViewGroup viewById = (ViewGroup) getView().getParent();
            viewById.removeView(viewEmptyContents);
        }
    }

    private void bindingData(ArrayList<SBTNPackage> dataResult) {
        if (dataResult == null) {
            showRequestError();
            return;
        }
        mSBTNPackageGroupArrayList = dataResult;
        if (mSBTNPackageGroupArrayList.isEmpty()) {
            if (getView() != null) {
                ViewGroup rootView = (ViewGroup) getView().getParent();
                viewEmptyContents = LayoutInflater.from(getActivity()).inflate(R.layout.custom_empty_contents, null);
                button = viewEmptyContents.findViewById(R.id.buttonBuyNow);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SBTNPackageBrowserActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_LIST_PACKAGE_SCREEN);
                    }
                });

                if (!((MainActivity) getActivity()).isHeaderShow()) {
                    button.requestFocus();
                }

                rootView.addView(viewEmptyContents);
            }
            return;
        }

        setupRelatedListRow();
    }

    private void showRequestError() {
        MyDialog.showDialogServiceError(getActivity());
    }

    private void setupAdapter() {
        if (mPresenterSelector == null) {
            mPresenterSelector = new ClassPresenterSelector();
        }
        if (mAdapter == null) {
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
//            setupRelatedListRow();
            setupRelatedListRowPresenter();
        } else {
            mAdapter.clear();
        }

        setAdapter(mAdapter);
    }

    private void setupRelatedListRow() {
        if (CollectionUtil.isNotEmpty(mSBTNPackageGroupArrayList)) {
            for (int i = 0; i < mSBTNPackageGroupArrayList.size(); i++) {
                SBTNPackage item = mSBTNPackageGroupArrayList.get(i);
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new PurchasedPackagePresenterSelector());
//                listRowAdapter.add(new HeaderListPackageRow(getString(R.string.home_purchased_content), item.getId()));
                listRowAdapter.add(item);
                ArrayList<SBTNPackage> packageExtras = item.getPackageExtras();
                if (CollectionUtil.isNotEmpty(packageExtras)) {
//                    listRowAdapter.addAll(2, packageExtras);
                    listRowAdapter.addAll(1, packageExtras);
                }
                HeaderItem header = new HeaderItem(i, item.getName());
                mAdapter.add(new ListRow(header, listRowAdapter));
            }
        }
    }

    private void setupRelatedListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
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

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof SBTNPackage) {
                SBTNPackage sbtnPackage = (SBTNPackage) item;


//                Intent intent = new Intent(getActivity(), PackageDetailWithContentsActivity.class);
//                intent.putExtra(PackageDetailWithContentsActivity.ARG_PACKAGE, sbtnPackage);
//                startActivity(intent);

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
            } else if (item instanceof HeaderListPackageRow) {


//                Intent intent = new Intent(getActivity(), PackageDetailWithContentsActivity.class);
//                intent.putExtra(PackageDetailWithContentsActivity.ARG_PACKAGE, ((HeaderListPackageRow) item).getPkId());
//                startActivity(intent);


            }
        }
    }

    @Override
    protected void clearAndReloadData() {
        mAdapter.clear();
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIST_PACKAGE_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
            }
        }
    }

    @Override
    public BrowseFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    private BrowseFragment.MainFragmentAdapter mMainFragmentAdapter =
            new BrowseFragment.MainFragmentAdapter(this) {
                @Override
                public void setEntranceTransitionState(boolean state) {
                    SBTNPurchasedPackageBrowserFragment.this.setEntranceTransitionState(state);
                }
            };

    private void setEntranceTransitionState(boolean state) {
        //nothing
    }
}
