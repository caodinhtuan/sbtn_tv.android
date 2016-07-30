package com.sbtn.androidtv.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.sbtn.androidtv.R;

/**
 * Created by hoanguyen on 5/17/16.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected View mLoadingView;

    public void showLoading() {
        if (mLoadingView == null) {
            mLoadingView = findViewById(R.id.root_loading);
        }

        if (mLoadingView != null && mLoadingView.getVisibility() != View.VISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        }

//        ALog.d(BaseActivity.class.getSimpleName(),"showLoading");
    }

    public void hideLoading() {
        if (mLoadingView != null && mLoadingView.getVisibility() != View.GONE) {
            mLoadingView.setVisibility(View.GONE);
        }
//        ALog.d(BaseActivity.class.getSimpleName(),"hideLoading");
    }
}
