package com.sbtn.androidtv.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.BrowseFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.fragment.MainFragment;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class MainActivity extends BaseActivity {
    boolean isHeaderShow;

    private ArrayList<BrowseFragment.BrowseTransitionListener> mHeadersTransitionListeners;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            isHeaderShow = true;
            setupHeadersTransitionListener(fragment);
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isHeaderShow)
            super.onBackPressed();
        else {
            MyDialog.showDialogConfirm(this, "", getString(R.string.dialog_msg_close_app), new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    finish();
                }
            });
        }
    }


    private void setupHeadersTransitionListener(MainFragment fragment) {
        fragment.setBrowseTransitionListener(new BrowseFragment.BrowseTransitionListener() {
            @Override
            public void onHeadersTransitionStart(boolean withHeaders) {
                super.onHeadersTransitionStart(withHeaders);
                if (CollectionUtil.isNotEmpty(mHeadersTransitionListeners)) {
                    for (BrowseFragment.BrowseTransitionListener listener : mHeadersTransitionListeners) {
                        listener.onHeadersTransitionStart(withHeaders);
                    }
                }
            }

            @Override
            public void onHeadersTransitionStop(boolean withHeaders) {
                super.onHeadersTransitionStop(withHeaders);
                isHeaderShow = withHeaders;

                if (CollectionUtil.isNotEmpty(mHeadersTransitionListeners)) {
                    for (BrowseFragment.BrowseTransitionListener listener : mHeadersTransitionListeners) {
                        listener.onHeadersTransitionStop(withHeaders);
                    }
                }
            }
        });
    }

    public void addHeadersTransitionListener(BrowseFragment.BrowseTransitionListener l) {
        if (mHeadersTransitionListeners == null) {
            mHeadersTransitionListeners = new ArrayList<>();
        }
        mHeadersTransitionListeners.add(l);
    }

    public void removeHeadersTransitionListener(BrowseFragment.BrowseTransitionListener l) {
        if (mHeadersTransitionListeners != null)
            mHeadersTransitionListeners.remove(l);
    }

    public boolean isHeaderShow() {
        return isHeaderShow;
    }
}
