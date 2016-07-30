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

package com.sbtn.androidtv.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.SBTNPackageGroup;
import com.sbtn.androidtv.fragment.SBTNPackageBrowserFragment;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.ListPackageDataCallBackRequest;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.util.ArrayList;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class SBTNPackageBrowserActivity extends BaseActivity {
    private static final String TAG = "SBTNPackageBrowserActivity";
    public static final String EXTRA_LIST_SBTN_PACKAGE = "EXTRA_LIST_SBTN_PACKAGE";
    public static final String ACTION_LOAD_ALL_PACKAGE = "ACTION_LOAD_ALL_PACKAGE";
    public static final String ACTION_SHOW_LIST_PACKAGE = "ACTION_SHOW_LIST_PACKAGE";
    private ArrayList<SBTNPackageGroup> mSBTNPackageGroup;
    private View emptyText;
    private boolean isClickPos;
    private boolean isLoadPackageFromServer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        if (intent == null) {
            ALog.e(TAG, "onCreate - intent = null");
            return;
        }

        String action = intent.getAction();

        if (ACTION_SHOW_LIST_PACKAGE.equals(action)) {
            isLoadPackageFromServer = false;
            mSBTNPackageGroup = intent.getParcelableArrayListExtra(EXTRA_LIST_SBTN_PACKAGE);
            if (CollectionUtil.isEmpty(mSBTNPackageGroup)) {
                ALog.e(TAG, "onCreate - SBTNPackageArrayList empty");
            }
        } else {
            isLoadPackageFromServer = true;
        }

        //Luôn set result ok để cho Detail fragment reload dâata nếu user mua package
        setResult(RESULT_OK);

        emptyText = findViewById(R.id.emptyText);
        if (!CacheDataManager.getInstance(this).isLogin()) {
            MyDialog.showDialogConfirm(this, getString(R.string.warning), getString(R.string.login_first), new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    isClickPos = true;
                    Intent intent = new Intent(SBTNPackageBrowserActivity.this, AccountManagerActivity.class);
                    intent.putExtra(AccountManagerActivity.EXTRA_LOGIN_FOR_BUY_PACKAGE, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
                    startActivityForResult(intent, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!isClickPos)
                        finish();
                }
            });
        } else {
            handleShowData();
        }
    }

    private void handleShowData() {
        if (isLoadPackageFromServer) {
            loadAllListPackage();
        } else {
            bindingData();
        }
    }

    public void clearAndLoadData() {
        if (isLoadPackageFromServer) {
            loadAllListPackage();
        } else {
            finish();
        }
    }

    public void loadAllListPackage() {
        showLoading();
        emptyText.setVisibility(View.GONE);
        RequestFramework.getListAllPackage(this, new RequestFramework.DataCallBack<ListPackageDataCallBackRequest>() {
            @Override
            public void onResponse(ListPackageDataCallBackRequest dataResult) {
                if (dataResult.isSuccess()) {
                    mSBTNPackageGroup = dataResult.getData();
                    if (CollectionUtil.isEmpty(mSBTNPackageGroup)) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        bindingData();
                    }

                } else {
                    showErrorDialog();
                }
                hideLoading();
            }

            @Override
            public void onFailure() {
                showErrorDialog();
                hideLoading();
            }
        });
    }

    private void bindingData() {
        SBTNPackageBrowserFragment fragment = SBTNPackageBrowserFragment.newInstance(mSBTNPackageGroup,isLoadPackageFromServer);

        getFragmentManager().beginTransaction().replace(R.id.frame_fragment_detail_item, fragment, SBTNPackageBrowserFragment.TAG).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE:
                if (resultCode == Activity.RESULT_OK) {
                    loadAllListPackage();
                } else {
                    finish();
                }
                break;
        }
    }

    private void showErrorDialog() {
        MyDialog.showDialogServiceError(SBTNPackageBrowserActivity.this, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }
}
