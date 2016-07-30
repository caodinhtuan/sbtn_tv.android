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
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.ErrorActivity;
import com.sbtn.androidtv.activity.GridMoreHomeActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.ASHomeDataObject;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.HeaderListHomeRow;
import com.sbtn.androidtv.presenter.HomeListRowScreenPresenter;

import java.io.Serializable;

public class HomeListRowFragment extends RowsFragment {
    public static final String TAG = "HomeListRowFragment";
    private ArrayObjectAdapter mRowsAdapter;
    private HomeListRowScreenPresenter mHomeScreenPresenter;

    public static HomeListRowFragment newInstance() {

        Bundle args = new Bundle();

        HomeListRowFragment fragment = new HomeListRowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIElements();
        loadDataFromServer();
        setupEventListeners();
    }

    private void loadDataFromServer() {
        if (mHomeScreenPresenter == null)
            mHomeScreenPresenter = new HomeListRowScreenPresenter(this);
        mHomeScreenPresenter.loadHomeScreenData();
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

    public void bindingData(ASHomeDataObject asHomeDataObject) {
        if (asHomeDataObject == null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                    .content(R.string.dialog_content_service_error)
                    .title(R.string.error)
                    .positiveText(R.string.ok)
                    .build();
            dialog.show();
        }
        //Load data trong cache ra
        loadRowsOnWorldTV();
    }

    public void showError(Throwable e) {
        Intent intent = new Intent(getActivity(), ErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    private void setupUIElements() {
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(
                getMainFragmentAdapter());
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof DataDetailItem) {
                DataDetailItem dataDetailItem = (DataDetailItem) item;
//                ALog.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM, (Serializable) dataDetailItem);
                intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, dataDetailItem.getId());
                intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_POSITION, (int) row.getHeaderItem().getId());

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsItemActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);


            } else if (item instanceof HeaderListHomeRow) {
                HeaderListHomeRow header = (HeaderListHomeRow) item;
                Intent intent = new Intent(getActivity(), GridMoreHomeActivity.class);
                intent.putExtra(GridMoreHomeActivity.EXTRA_ARR_DATA, header.getArrData());
                intent.putExtra(GridMoreHomeActivity.EXTRA_ARR_TITLE, header.getTitle());

                startActivity(intent);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            //TODO background không tốt chỗ này
        }
    }

    private void loadRowsOnWorldTV() {
        mRowsAdapter = CacheDataManager.getInstance(getActivity()).getArrayObjectAdapterHomeData(getActivity());
        setAdapter(mRowsAdapter);
    }
}
