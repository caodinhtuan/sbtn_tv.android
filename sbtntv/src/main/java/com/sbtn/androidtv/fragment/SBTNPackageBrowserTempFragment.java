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
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.presenter.PackageCardPresenter;
import com.sbtn.androidtv.utils.CollectionUtil;

import java.util.ArrayList;

/*
 * LeanbackDetailsFragment extends DetailsItemFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class SBTNPackageBrowserTempFragment extends BasePackageLeanBackDetailFragment {
    public static final String TAG = "SBTNPackageBrowserFragment";
    public static final String EXTRA_LIST_PACKAGE = "EXTRA_LIST_PACKAGE";

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private ArrayList<SBTNPackage> mSBTNPackageGroupArrayList;

    public static SBTNPackageBrowserTempFragment newInstance(ArrayList<SBTNPackage> mSBTNPackageArrayList) {

        Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_LIST_PACKAGE, mSBTNPackageArrayList);
        SBTNPackageBrowserTempFragment fragment = new SBTNPackageBrowserTempFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mSBTNPackageGroupArrayList = args.getParcelableArrayList(EXTRA_LIST_PACKAGE);

        setupAdapter();
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void setupAdapter() {
        if (mPresenterSelector == null) {
            mPresenterSelector = new ClassPresenterSelector();
        }
        if (mAdapter == null) {
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setupRelatedListRow();
            setupRelatedListRowPresenter();
        } else {
            mAdapter.clear();
        }

        setAdapter(mAdapter);
    }


    private void setupRelatedListRow() {
        if (CollectionUtil.isNotEmpty(mSBTNPackageGroupArrayList)) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new PackageCardPresenter());
            listRowAdapter.addAll(0, mSBTNPackageGroupArrayList);
            HeaderItem header = new HeaderItem(getString(R.string.package_list));
            mAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    private void setupRelatedListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }


    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((SBTNPackageBrowserActivity) activity).showLoading();
        }
    }

    public void hideLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((SBTNPackageBrowserActivity) activity).hideLoading();
        }
    }

    @Override
    protected void clearAndReloadData() {
        mAdapter.clear();
        ((SBTNPackageBrowserActivity) getActivity()).loadAllListPackage();
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof SBTNPackage) {
                SBTNPackage sbtnPackage = (SBTNPackage) item;

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
        }
    }
}
