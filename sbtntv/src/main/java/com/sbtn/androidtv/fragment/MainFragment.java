package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sbtn.androidtv.BuildConfig;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserTempActivity;
import com.sbtn.androidtv.activity.SearchActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.constants.ConstantDefine;
import com.sbtn.androidtv.datamodels.SBTNSetting;
import com.sbtn.androidtv.eventbus.ReloadDataEvent;
import com.sbtn.androidtv.presenter.CardSettingPresenter;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.MainMenuDataCallback;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class MainFragment extends BrowseFragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private BackgroundManager mBackgroundManager;

    private ArrayObjectAdapter mRowsAdapter;
    private boolean iForeReload;
    private boolean isForeground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        loadData();
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new PageRowFragmentFactory(mBackgroundManager));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void setupUi() {
//        setBrandColor(getResources().getColor(R.color.blue_theme));
        // set search icon color
//        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

        setHeadersTransitionOnBackEnabled(true);
        setTitle(getString(R.string.browse_title));
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (row.getHeaderItem().getId() == ConstantDefine.TYPE_SETTING && item instanceof SBTNSetting) {
                    SBTNSetting sbtnSetting = (SBTNSetting) item;
                    if (sbtnSetting.getName().equalsIgnoreCase(getString(R.string.home_settings_accounts))) {
                        Intent intent = new Intent(getActivity(), AccountManagerActivity.class);
                        startActivity(intent);
                    } else if (sbtnSetting.getName().equalsIgnoreCase(getString(R.string.home_settings_list_package))) {
                        Intent intent;
                        if (BuildConfig.SERVICE_URL_VERSION_CODE.equals("v1.6/")) {
                            intent = new Intent(getActivity(), SBTNPackageBrowserTempActivity.class);
                        } else {
                            intent = new Intent(getActivity(), SBTNPackageBrowserActivity.class);
                        }
                        startActivity(intent);
                    } else if (sbtnSetting.getName().equalsIgnoreCase(getString(R.string.home_settings_info))) {
                        String version = "Version: " + BuildConfig.VERSION_NAME;

                        MyDialog.showDialogConfirm(getActivity(), sbtnSetting.getName(), version, null);
                    }
                }
            }
        });

        prepareEntranceTransition();

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
    }

    private void loadData() {
        if (!isForeground) {
            iForeReload = true;
            ALog.d(TAG, "loadData - isForeground = false");
            return;
        }
        showLoading();
        RequestFramework.getMainMenu(getActivity(), new RequestFramework.DataCallBack<MainMenuDataCallback>() {
            @Override
            public void onResponse(final MainMenuDataCallback dataResult) {
                bindingData(dataResult);
                hideLoading();
            }

            @Override
            public void onFailure() {
                hideLoading();
                showRequestError();
            }
        });
    }


    private void bindingData(MainMenuDataCallback dataResult) {
        if (dataResult == null) {
            startEntranceTransition();
            showRequestError();
            return;
        }

        String padding = "\t\t";
        if (mRowsAdapter != null && mRowsAdapter.size() != 0) {
            mRowsAdapter.clear();
        }

        mRowsAdapter.add(new PageRow(new HeaderItem(ConstantDefine.TYPE_HOME, padding + getString(R.string.home).toUpperCase())));

        ArrayList<MainMenuDataCallback.Provider> providers = dataResult.getProvider();
        if (CollectionUtil.isNotEmpty(providers)) {
            mRowsAdapter.add(new DividerRow());
            mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.home_provider))));
            for (MainMenuDataCallback.Provider pro : providers) {
                mRowsAdapter.add(new PageRow(new HeaderItem(ConstantDefine.TYPE_PROVIDER + pro.getId(), padding + pro.getName().toUpperCase())));
            }
        }
        ArrayList<MainMenuDataCallback.Category> categories = dataResult.getCategory();
        if (CollectionUtil.isNotEmpty(categories)) {
            mRowsAdapter.add(new DividerRow());
            mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.home_category))));
            for (MainMenuDataCallback.Category cat : categories) {
                mRowsAdapter.add(new PageRow(new HeaderItem(ConstantDefine.TYPE_CATEGORY + cat.getId(), padding + cat.getName().toUpperCase())));
            }
        }

        if (CacheDataManager.getInstance(getActivity()).isLogin()) {
            mRowsAdapter.add(new DividerRow());
            mRowsAdapter.add(new SectionRow(new HeaderItem(getString(R.string.home_my_purchased))));
            mRowsAdapter.add(new PageRow(new HeaderItem(ConstantDefine.TYPE_PURCHASED_CONTENT, padding +
                    getString(R.string.home_purchased_content).toUpperCase())));

            mRowsAdapter.add(new PageRow(new HeaderItem(ConstantDefine.TYPE_PURCHASED_PACKAGE, padding +
                    getString(R.string.home_purchased_package).toUpperCase())));
        }

        mRowsAdapter.add(new DividerRow());
        ArrayObjectAdapter objectAdapter = new ArrayObjectAdapter(new CardSettingPresenter());
        objectAdapter.add(new SBTNSetting(getString(R.string.home_settings_accounts), R.drawable.icon_setting_account));
        objectAdapter.add(new SBTNSetting(getString(R.string.home_settings_list_package), R.drawable.icon_setting_package));
        objectAdapter.add(new SBTNSetting(getString(R.string.home_settings_info), R.drawable.icon_setting_info));
        mRowsAdapter.add(new ListRow(new HeaderItem(ConstantDefine.TYPE_SETTING, padding + getString(R.string.home_settings).toUpperCase()), objectAdapter));


        startEntranceTransition();
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
        if (iForeReload) {
            iForeReload = false;
            loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReloadDataEvent(ReloadDataEvent event) {
        ALog.d(TAG, event.toString());
        loadData();
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
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                .content(R.string.dialog_content_service_error)
                .title(R.string.error)
                .positiveText(R.string.ok)
                .build();
        dialog.show();
    }

    private static class PageRowFragmentFactory extends BrowseFragment.FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        PageRowFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            mBackgroundManager.setDrawable(null);
            Row row = (Row) rowObj;
            int idRow = (int) row.getHeaderItem().getId();
            if (idRow < 0) {
                if (idRow == ConstantDefine.TYPE_HOME) {
                    return HomeListRowFragment.newInstance();
                } else if (idRow == ConstantDefine.TYPE_PURCHASED_CONTENT) {
                    return PurchasedContentsFragment.newInstance();
                } else if (idRow == ConstantDefine.TYPE_PURCHASED_PACKAGE) {
                    return SBTNPurchasedPackageBrowserFragment.newInstance();
                }
            } else {
                //Provider or Category
                return ProCatFragment.newInstance(idRow, row.getHeaderItem().getName());
            }

            return new RowsFragment();
        }
    }
}
