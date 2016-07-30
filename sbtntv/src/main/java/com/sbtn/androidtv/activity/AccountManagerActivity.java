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

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookSdk;
import com.sbtn.androidtv.BuildConfig;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.fragment.AccountInfoFragment;
import com.sbtn.androidtv.fragment.LoginRegisterFragment;

public class AccountManagerActivity extends BaseActivity {
    private static final String TAG = "AccountManagerActivity";

    public static final String EXTRA_LOGIN_FOR_BUY_PACKAGE = "EXTRA_LOGIN_FOR_BUY_PACKAGE";
    public static final int REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE = 101;
    public static final int REQUEST_CODE_DEFAULT = 0;

    private FragmentManager mFragmentManager;
    private int mRequestCode = REQUEST_CODE_DEFAULT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mFragmentManager = getFragmentManager();
        Intent intent = getIntent();
        if (intent != null) {
            mRequestCode = intent.getIntExtra(EXTRA_LOGIN_FOR_BUY_PACKAGE, REQUEST_CODE_DEFAULT);
        }
        handleShowFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LoginRegisterFragment fragment = (LoginRegisterFragment)
                mFragmentManager.findFragmentByTag(LoginRegisterFragment.TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void handleShowFragment() {
        boolean login = CacheDataManager.getInstance(this).isLogin();
        if (login) {
            showAccountInfoForm();
        } else {
            showLoginRegisterForm();
        }
    }

    private void showLoginRegisterForm() {
        mFragmentManager.beginTransaction().replace(R.id.account_manager_frame, LoginRegisterFragment.newInstance(), LoginRegisterFragment.TAG).commit();
    }

    private void showAccountInfoForm() {
        if (mRequestCode == REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        mFragmentManager.beginTransaction().replace(R.id.account_manager_frame, AccountInfoFragment.newInstance(), AccountInfoFragment.TAG).commit();
    }

    MaterialDialog dialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_MENU) {
            //For debug quick login
            if (dialog == null)
                dialog = new MaterialDialog.Builder(this).backgroundColorRes(R.color.MDGray)
                        .title("Chọn account muốn đăng nhập nào Bro!!!")
                        .items(R.array.list_account_debug)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                String[] split = text.toString().split(",");
                                String email = split[0];
                                String pass = split[1];

                                LoginRegisterFragment fragment = (LoginRegisterFragment)
                                        mFragmentManager.findFragmentByTag(LoginRegisterFragment.TAG);
                                if (fragment != null) {
                                    fragment.forceLogin(email, pass);
                                }
                            }
                        }).build();
            if (!dialog.isShowing())
                dialog.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
