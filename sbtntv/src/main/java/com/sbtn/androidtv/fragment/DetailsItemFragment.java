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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.MainActivity;
import com.sbtn.androidtv.activity.PlaybackVideoActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserActivity;
import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.datamodels.Episode;
import com.sbtn.androidtv.datamodels.Related;
import com.sbtn.androidtv.datamodels.TimeLine;
import com.sbtn.androidtv.datamodels.TimeLines;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.presenter.CardPresenter;
import com.sbtn.androidtv.presenter.DetailScreenPresenter;
import com.sbtn.androidtv.presenter.DetailsItemPresenter;
import com.sbtn.androidtv.presenter.TimeLineCardPresenter;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.IntentUtils;
import com.sbtn.androidtv.utils.MyDialog;
import com.sbtn.androidtv.utils.Utils;

import java.util.ArrayList;

/*
 * LeanbackDetailsFragment extends DetailsItemFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class DetailsItemFragment extends BasePackageLeanBackDetailFragment {
    public static final String TAG = "DetailsItemFragment";

    public static final int REQUEST_CODE_PLAY_ITEM = 1111;
    public static final int REQUEST_CODE_LIST_PACKAGE_SCREEN = 1001;

    private static final int POS_ID_ROW_CONTROL_PLAY_BACK = 0;
    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED = POS_ID_ROW_CONTROL_PLAY_BACK + 1;
    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE = POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED + 1;

    private static final int POS_ID_ROW_DEFAULT_TIME_LINE = 5000;
    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE = POS_ID_ROW_DEFAULT_TIME_LINE;

    private static final int ACTION_PLAY = 1;
    private static final int ACTION_LOGIN_TO_PLAY = 2;
    private static final int ACTION_BUY_START = 500;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private int mIdDataDetailItem;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;

    private DisplayImageOptions options;
    private ViewDetail mViewDetailCurrent;

    private DetailScreenPresenter mDetailScreenPresenter;

    public static DetailsItemFragment newInstance(int itemId) {

        Bundle args = new Bundle();

        args.putInt(DetailsItemActivity.DATA_DETAIL_ITEM_ID, itemId);
        DetailsItemFragment fragment = new DetailsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty)
                .showImageForEmptyUri(R.drawable.ic_error)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        prepareBackgroundManager();

        Bundle args = getArguments();
        mIdDataDetailItem = args.getInt(DetailsItemActivity.DATA_DETAIL_ITEM_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mIdDataDetailItem >= 0) {
            handleLoadContentDetail();
            setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void handleLoadContentDetail() {
        if (mIdDataDetailItem < 0) return;
        setupAdapter();
        if (mDetailScreenPresenter == null) {
            mDetailScreenPresenter = new DetailScreenPresenter(this);
        }

        loadDetailItem();
    }

    private void loadDetailItem() {
        mDetailScreenPresenter.loadDetailItem(mIdDataDetailItem);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    protected void updateBackground(String uri) {

//        ImageSize imageSize = new ImageSize(mMetrics.widthPixels, mMetrics.heightPixels);
//        ImageLoader.getInstance().loadImage(uri, imageSize, options, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                mBackgroundManager.setDrawable(mDefaultBackground);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                mBackgroundManager.setDrawable(mDefaultBackground);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                mBackgroundManager.setBitmap(loadedImage);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                mBackgroundManager.setDrawable(mDefaultBackground);
//            }
//        });
    }

    private void setupAdapter() {
        if (mPresenterSelector == null) {
            mPresenterSelector = new ClassPresenterSelector();
        }
        if (mAdapter == null) {

            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setAdapter(mAdapter);
            setupDetailsOverviewRowPresenter();
        } else {
            mAdapter.clear();
        }
    }

    private void setupDetailsOverviewRow() {
        final DetailsOverviewRow row = new DetailsOverviewRow(mViewDetailCurrent);
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background));
        loadImage(row);
        if (mViewDetailCurrent.isPermissionOK())
            row.addAction(new Action(ACTION_PLAY, getResources().getString(
                    R.string.play)));
        else {
            if (!CacheDataManager.getInstance(getActivity()).isLogin()) {
                row.addAction(new Action(ACTION_LOGIN_TO_PLAY, getResources().getString(
                        R.string.login_to_play)));
            } else {
                row.addAction(new Action(ACTION_PLAY, getResources().getString(R.string.play)));
            }
        }

        mAdapter.add(row);
    }

    private void loadImage(final DetailsOverviewRow row) {
        int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);

        ImageSize imageSize = new ImageSize(width, height);
        if (mViewDetailCurrent == null) return;
        ImageLoader.getInstance().loadImage(mViewDetailCurrent.getContent().iGetLinkImage(), imageSize, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                row.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty));
                mBackgroundManager.setDrawable(mDefaultBackground);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                row.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                row.setImageBitmap(getActivity(), loadedImage);
                mBackgroundManager.setBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                row.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
            }
        });

    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsItemPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsItemActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {

                int actionId = (int) action.getId();
                if (actionId == ACTION_PLAY) {
                    handlePlayItem(PlaybackVideoActivity.PLAY_CURRENT, -1);
                } else if (actionId == ACTION_LOGIN_TO_PLAY) {
                    Intent intent = new Intent(getActivity(), AccountManagerActivity.class);
                    intent.putExtra(AccountManagerActivity.EXTRA_LOGIN_FOR_BUY_PACKAGE, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
                    startActivityForResult(intent, AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE);
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedListRow() {
        //Episode
        ArrayList<Episode> episodes = mViewDetailCurrent.getEpisodes();
        if (CollectionUtil.isNotEmpty(episodes)) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
            for (Episode item : episodes) {
                listRowAdapter.add(item);
            }
            HeaderItem header = new HeaderItem(POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE, getString(R.string.episodes_movies));
            mAdapter.add(new ListRow(header, listRowAdapter));
        }

        //Time Line
        ArrayList<TimeLines> timelines = mViewDetailCurrent.getTimelines();
        if (CollectionUtil.isNotEmpty(timelines)) {
            for (int i = 0; i < timelines.size(); i++) {
                TimeLines timeLines = mViewDetailCurrent.getTimelines().get(i);
                ArrayList<TimeLine> arrTimeline = timeLines.getTimeLine();
                if (CollectionUtil.isNotEmpty(arrTimeline)) {
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new TimeLineCardPresenter());
                    int size = arrTimeline.size();
                    for (int j = 0; j < size; j++) {
                        TimeLine timeLineItem = arrTimeline.get(j);
                        timeLineItem.setLiveTV(mViewDetailCurrent.getContent().getIsLive());
                        listRowAdapter.add(timeLineItem);
                    }
                    HeaderItem header = new HeaderItem(POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE + i,
                            getString(R.string.time_line_movies) + " - " + timeLines.getTitle());
                    mAdapter.add(new ListRow(header, listRowAdapter));
                }
            }
        }

        //Related
        if (mViewDetailCurrent == null) return;
        ArrayList<Related> related = mViewDetailCurrent.getRelated();
        if (CollectionUtil.isNotEmpty(related)) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
            for (Related item : related) {
                listRowAdapter.add(item);
            }
            HeaderItem header = new HeaderItem(POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED, getString(R.string.related_movies));
            mAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    private void setupRelatedListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }


    public void showError(Throwable e) {
//        Intent intent = new Intent(getActivity(), ErrorActivity.class);
//        startActivity(intent);
        MyDialog.showDialogServiceError(getActivity(), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getActivity().finish();
            }
        });
    }

    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((DetailsItemActivity) activity).showLoading();
        }
    }

    public void hideLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((DetailsItemActivity) activity).hideLoading();
        }
    }

    public void bindingData(ViewDetail detailItem) {
        mViewDetailCurrent = detailItem;
        if (mViewDetailCurrent == null) {
            showError(null);
            return;
        }

        setupDetailsOverviewRow();

        setupRelatedListRow();
        setupRelatedListRowPresenter();

        //TODO data load background
        //            updateBackground(mDataDetailItem.getImage());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            ALog.i(TAG, "onItemClicked: " + item + " row " + row);
            if (row.getHeaderItem() == null) {
                ALog.i(TAG, "onItemClicked: row.getHeaderItem()  == null");
                return;
            }

            int id = (int) row.getHeaderItem().getId();
            if (id >= POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE) {
                if (item instanceof TimeLine) {
                    mIdDataDetailItem = ((TimeLine) item).getId();
//                    handleLoadContentDetail();
                    handlePlayItem(PlaybackVideoActivity.PLAY_TIME_LINE, mIdDataDetailItem);
                }
            } else {
                switch (id) {
                    case POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED:
                        if (item instanceof Related) {
                            Related selectedItem = (Related) item;
                            mIdDataDetailItem = selectedItem.getRelatedId();
                            handleLoadContentDetail();
                        }
                        break;

                    case POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE:
                        if (item instanceof Episode) {
                            mIdDataDetailItem = ((Episode) item).getEpisodeId();
//                            handleLoadContentDetail();
                            handlePlayItem(PlaybackVideoActivity.PLAY_EPISODE, mIdDataDetailItem);
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void clearAndReloadData() {
        mAdapter.clear();
        loadDetailItem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE:
            case REQUEST_CODE_LIST_PACKAGE_SCREEN:
                if (resultCode == Activity.RESULT_OK) {
                    mAdapter.clear();
                    loadDetailItem();
                }
                break;

//            case REQUEST_CODE_PURCHASE:
//                if (resultCode == Activity.RESULT_OK) {
//                    if (dialogPackage != null && dialogPackage.isShowing())
//                        dialogPackage.dismiss();
//
//                    //re-setup adapter and refresh data
//                    mAdapter.clear();
//                    loadDetailItem();
//                }
//                break;

            case REQUEST_CODE_PLAY_ITEM:
                if (resultCode == PlaybackVideoActivity.RESULT_NO_PERMISSION_PLAY && data != null) {
                    mIdDataDetailItem = data.getIntExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, -1);
                    String nameItem = data.getStringExtra(DetailsItemActivity.DATA_DETAIL_ITEM_NAME);
                    mAdapter.clear();
                    loadDetailItem();

                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                            .title(R.string.dialog_title_warning)
                            .content(getString(R.string.dialog_msg_no_permission_to_play, nameItem))
                            .positiveText(R.string.ok)
                            .build();

                    MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
                    actionButton.requestFocus();
                    actionButton.setBackground(getResources().getDrawable(R.drawable.statelist_button));

                    dialog.show();
                }
                break;
        }
    }

    /**
     * @param type    public static final int PLAY_CURRENT = 0;
     *                public static final int PLAY_EPISODE = 1;
     *                public static final int PLAY_TIME_LINE = 2;
     * @param idChild
     */
    private void handlePlayItem(int type, int idChild) {
        if (mViewDetailCurrent.isPermissionOK()) {
            Intent intent = new Intent(getActivity(), PlaybackVideoActivity.class);
            intent.putExtra(IntentUtils.EXTRA_OBJECT_PARCELABLE, mViewDetailCurrent);
            intent.putExtra(IntentUtils.EXTRA_PLAY_WHAT, type);
            intent.putExtra(IntentUtils.EXTRA_ID, idChild);
            startActivity(intent);
        } else {
            //Không có permission để play
            MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Intent intent = new Intent(getActivity(), SBTNPackageBrowserActivity.class);
                    intent.setAction(SBTNPackageBrowserActivity.ACTION_SHOW_LIST_PACKAGE);
                    intent.putExtra(SBTNPackageBrowserActivity.EXTRA_LIST_SBTN_PACKAGE, mViewDetailCurrent.getItemSBTNPackageGroup());
                    startActivityForResult(intent, REQUEST_CODE_LIST_PACKAGE_SCREEN);
                }
            };
            if (mViewDetailCurrent.isPermisionFailMaxDevicePlay()) {
                //Bị max device
                MyDialog.showDialogPackageForMaxDevice(getActivity(), singleButtonCallback);
            } else {
                //Content chưa mua
                MyDialog.showDialogPackageForContent(getActivity(), singleButtonCallback);
            }
        }
    }
}
