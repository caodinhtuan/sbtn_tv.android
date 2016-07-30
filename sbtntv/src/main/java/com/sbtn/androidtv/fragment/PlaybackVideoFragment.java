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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AccountManagerActivity;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.ErrorActivity;
import com.sbtn.androidtv.activity.PlaybackVideoActivity;
import com.sbtn.androidtv.datamodels.Adv;
import com.sbtn.androidtv.datamodels.AdvData;
import com.sbtn.androidtv.datamodels.Advertisement;
import com.sbtn.androidtv.datamodels.ContentModel;
import com.sbtn.androidtv.datamodels.Episode;
import com.sbtn.androidtv.datamodels.InfoPlayControlItem;
import com.sbtn.androidtv.datamodels.PlayItemInterface;
import com.sbtn.androidtv.datamodels.Related;
import com.sbtn.androidtv.datamodels.TimeLine;
import com.sbtn.androidtv.datamodels.TimeLines;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.presenter.CardPresenter;
import com.sbtn.androidtv.presenter.TimeLineCardPresenter;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.timer.TrackingController;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.DateTimeUtils;
import com.sbtn.androidtv.utils.IntentUtils;

import java.util.ArrayList;

/*
 * Class for video playback with media control
 */
public class PlaybackVideoFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    public static final String TAG = PlaybackVideoFragment.class.getSimpleName();

    private static final boolean SHOW_DETAIL = true;
    private static final boolean HIDE_MORE_ACTIONS = false;
    //    private static final int PRIMARY_CONTROLS = 5;
    private static final int PRIMARY_CONTROLS = 3;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackVideoFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;

    private static final int POS_ID_ROW_CONTROL_PLAY_BACK = 0;
    //    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED = POS_ID_ROW_CONTROL_PLAY_BACK + 1;
    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE = POS_ID_ROW_CONTROL_PLAY_BACK + 1;

    private static final int POS_ID_ROW_DEFAULT_TIME_LINE = 5000;
    private static final int POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE = POS_ID_ROW_DEFAULT_TIME_LINE;

    private static final int TYPE_DETAIL_ITEM = 0;
    private static final int TYPE_RELATED = 1;
    private static final int TYPE_EPISODE = 2;
    private static final int TYPE_TIME_LINE = 3;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;

    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private Handler mHandler;
    private Runnable mRunnable;
    private PlayItemInterface mSelectedItemInterface;
    private int mSelectedItemInterfaceIDDDDDDDOLD;
    private ViewDetail mViewDetailCurrent;

    private int mCurrentPosEpisodeTimeLine = 0;
    private int mCurrentRowTimeLinePlay = 0;

    private OnPlayPauseClickedListener mCallback;
    private int mTypeCurrentPlay = TYPE_DETAIL_ITEM;

    private GetInfoVideoViewListener mGetInfoVideoViewListener;
    private DisplayImageOptions options;

    private AdvData mAdvData;
    private ArrayList<Related> mListRelated;
    private ArrayList<Episode> mListEpisode;
    private int mPlayWhat;
    private int mIdChild;
    private ArrayList<TimeLines> mListTimeLineDay;

    public static PlaybackVideoFragment newInstance(ViewDetail viewDetail, int playWhat, int idChild) {

        Bundle args = new Bundle();
        args.putParcelable(DetailsItemActivity.DATA_DETAIL_ITEM, viewDetail);
        args.putInt(IntentUtils.EXTRA_PLAY_WHAT, playWhat);
        args.putInt(IntentUtils.EXTRA_ID, idChild);
        PlaybackVideoFragment fragment = new PlaybackVideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Second item control action - don't delete
     */
//    private ArrayObjectAdapter mSecondaryActionsAdapter;
//    private RepeatAction mRepeatAction;
//    private ThumbsUpAction mThumbsUpAction;
//    private ThumbsDownAction mThumbsDownAction;
//    private ShuffleAction mShuffleAction;
//    private FastForwardAction mFastForwardAction;
//    private RewindAction mRewindAction;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mViewDetailCurrent = arguments.getParcelable(DetailsItemActivity.DATA_DETAIL_ITEM);
        mPlayWhat = arguments.getInt(IntentUtils.EXTRA_PLAY_WHAT, PlaybackVideoActivity.PLAY_CURRENT);
        mIdChild = arguments.getInt(IntentUtils.EXTRA_ID, -1);
        if (mViewDetailCurrent == null) {
            ALog.e(TAG, "mViewDetail = null");
            getActivity().finish();
            return;
        }

        mHandler = new Handler();
        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(true);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (row.getHeaderItem() == null) {
                    return;
                }
                int id = (int) row.getHeaderItem().getId();
                if (id >= POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE) {
                    if (item instanceof TimeLine) {
                        mSelectedItemInterface = (TimeLine) item;

                        mCurrentRowTimeLinePlay = id - POS_ID_ROW_CONTROL_MORE_VIDEO_TIME_LINE;
                        handleValidateIndexPlayTimeLine((TimeLine) item);
                        mTypeCurrentPlay = TYPE_TIME_LINE;
                        handleLoadVideoItem();
                    }
                } else if (id == POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE) {
                    if (item instanceof Episode) {
                        mSelectedItemInterface = (Episode) item;
                        handleValidateIndexPlayEpisode((Episode) item);
                        mTypeCurrentPlay = TYPE_EPISODE;
                        handleLoadVideoItem();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        handleGetInfoDataItem(mSelectedItemId, true);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty)
                .showImageForEmptyUri(R.drawable.ic_error)
                .showImageOnFail(R.drawable.ic_error)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    private TrackingController mTrackingController;

    @Override
    public void onResume() {
        super.onResume();
        mTrackingController = new TrackingController();
        mTrackingController.register(getActivity());
        bindingData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSelectedItemInterfaceIDDDDDDDOLD > 0) {
            mTrackingController.trackingEnd(mSelectedItemInterfaceIDDDDDDDOLD);
        }
        mTrackingController.unregister();
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

    public void bindingData() {
        if (!mViewDetailCurrent.isPermissionOK()) {
            //khong co quyền xem thi close playback màn hình
            Activity activity = getActivity();
            if (activity != null) {
                Intent dataResult = new Intent();
                dataResult.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, mViewDetailCurrent.getContent().getId());
                dataResult.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_NAME, mViewDetailCurrent.getContent().getName());
                activity.setResult(PlaybackVideoActivity.RESULT_NO_PERMISSION_PLAY, dataResult);
                activity.finish();
            }
            return;
        }
        setupListItemOtherVideos();
        setupRows();
    }

    /**
     * Handle load và parse Adv nếu có thể
     */
    private void handleLoadAdv() {
        //Nếu không phải video hoặc ko có adv thì ko cần load XML cũng như play adv
        if (mViewDetailCurrent == null || mViewDetailCurrent.getContent() == null ||
                mViewDetailCurrent.getContent().getMode() != ContentModel.MODE_VIDEO ||
                CollectionUtil.isEmpty(mViewDetailCurrent.getAdvertisement())) {
            handleLoadVideoItem();
            return;
        }

        //Load parse XML Adv ở đây - video và image luôn
        for (final Advertisement advertisement : mViewDetailCurrent.getAdvertisement()) {
            RequestFramework.parseAdv(getActivity(), advertisement.getLink(), new RequestFramework.DataCallBack<Adv>() {
                @Override
                public void onResponse(Adv dataResult) {
                    if (dataResult == null) return;
                    ALog.i("Adv:\n" + new Gson().toJson(dataResult, Adv.class));
                    if (Adv.TYPE_VIDEO.equals(dataResult.getType())) {
                        if (mAdvData == null)
                            mAdvData = new AdvData();

                        mAdvData.withVideoAdv(dataResult);
                        mAdvData.setSkipTimeVideo((int) advertisement.getSkippableTime());
                        mAdvData.setLinkThumbnailItem(mViewDetailCurrent.getContent().getImage());
                        mAdvData.enableShowAdv();

                        handleLoadVideoItem();

                    } else if (Adv.TYPE_IMAGE.equals(dataResult.getType())) {
                        if (mAdvData == null)
                            mAdvData = new AdvData();

                        mAdvData.withImageAdv(dataResult);
//                        mAdvData.setStartTimeImage(advertisement.getStartTime());
                        mAdvData.setStartTimeImage(5);
                        mAdvData.enableShowAdv();
                    } else {
                        ALog.e(TAG, "RequestFramework.parseAdv - 1 type khác không phải video, image");
                        if (mAdvData != null) {
                            mAdvData.disableShowAdv();
                        }
                        handleLoadVideoItem();
                    }
                }

                @Override
                public void onFailure() {
                    if (mAdvData != null) {
                        mAdvData.disableShowAdv();
                    }
                    ALog.e(TAG, "RequestFramework.parseAdv - parse fail rồi - không biết vì sao");
                    handleLoadVideoItem();
                }
            });
        }

    }

    public void showError(Throwable e) {
        Intent intent = new Intent(getActivity(), ErrorActivity.class);
        startActivity(intent);
    }

    private void setupListItemOtherVideos() {

        mListRelated = mViewDetailCurrent.getRelated();
        mListEpisode = mViewDetailCurrent.getEpisodes();
        mListTimeLineDay = mViewDetailCurrent.getTimelines();

        mSelectedItemInterface = mViewDetailCurrent.getContent();
        mTypeCurrentPlay = TYPE_DETAIL_ITEM;

        //---
        if (mPlayWhat == PlaybackVideoActivity.PLAY_EPISODE) {
            if (CollectionUtil.isNotEmpty(mListEpisode)) {
                if (mIdChild >= 0) {
                    int size = mListEpisode.size();
                    for (int i = 0; i < size; i++) {
                        Episode episode = mListEpisode.get(i);
                        if (episode.getEpisodeId() == mIdChild) {
                            mCurrentPosEpisodeTimeLine = i;
                            mSelectedItemInterface = episode;
                            mTypeCurrentPlay = TYPE_EPISODE;
                            break;
                        }
                    }
                } else {
                    mCurrentPosEpisodeTimeLine = 0;
                    mSelectedItemInterface = mListEpisode.get(0);
                    mTypeCurrentPlay = TYPE_EPISODE;
                }
            } else {
                handleInvalidData();
            }
        } else if (mPlayWhat == PlaybackVideoActivity.PLAY_TIME_LINE) {
            if (CollectionUtil.isNotEmpty(mListTimeLineDay)) {
                if (mIdChild >= 0) {
                    for (int i = 0; i < mListTimeLineDay.size(); i++) {
                        TimeLines timeLines = mListTimeLineDay.get(i);
                        ArrayList<TimeLine> timeLineArr = timeLines.getTimeLine();
                        if (CollectionUtil.isNotEmpty(timeLineArr)) {
                            for (int j = 0; j < timeLineArr.size(); j++) {
                                TimeLine timeLine = timeLineArr.get(j);
                                if (timeLine.getId() == mIdChild) {
                                    mCurrentRowTimeLinePlay = i;
                                    mCurrentPosEpisodeTimeLine = j;
                                    mSelectedItemInterface = timeLine;
                                    mTypeCurrentPlay = TYPE_TIME_LINE;
                                    break;
                                }
                            }

                        } else {
                            handleInvalidData();
                        }
                    }
                }
            } else {
                handleInvalidData();
            }
        }
    }

    private void handleInvalidData() {
        Toast.makeText(getActivity(), "Invalid Data", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    private void handleValidateIndexPlayEpisode(Episode episode) {
        if (CollectionUtil.isEmpty(mListEpisode)) return;

        for (int i = 0; i < mListEpisode.size(); i++) {
            Episode ep = mListEpisode.get(i);
            if (ep.getEpisodeId() == episode.getEpisodeId()) {
                mCurrentPosEpisodeTimeLine = i;
                break;
            }
        }
    }

    private void handleValidateIndexPlayTimeLine(TimeLine timeLine) {
        if (CollectionUtil.isEmpty(mListTimeLineDay)) return;
        ArrayList<TimeLine> timeLines = mListTimeLineDay.get(mCurrentRowTimeLinePlay).getTimeLine();

        for (int i = 0; i < timeLines.size(); i++) {
            TimeLine ep = timeLines.get(i);
            if (ep.getId() == ep.getId()) {
                mCurrentPosEpisodeTimeLine = i;
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnPlayPauseClickedListener) {
            mCallback = (OnPlayPauseClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayPauseClickedListener");
        }

        if (context instanceof PlaybackVideoActivity) {
            mGetInfoVideoViewListener = ((PlaybackVideoActivity) context).getGetInfoVideoViewListener();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mGetInfoVideoViewListener = null;
    }

    /**
     * Setup control on UI
     * 1. addPlaybackControlsRow : info + play/pause , next, previous
     * 2. addOtherRows : row related Videos for quick select
     */
    private void setupRows() {
        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev();
                }
//                else if (action.getId() == mFastForwardAction.getId()) {
//                    Toast.makeText(getActivity(), "TODO: Fast Forward", Toast.LENGTH_SHORT).show();
//                } else if (action.getId() == mRewindAction.getId()) {
//                    Toast.makeText(getActivity(), "TODO: Rewind", Toast.LENGTH_SHORT).show();
//                }
//                if (action instanceof PlaybackControlsRow.MultiAction) {
//                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
//                    notifyChanged(action);
//                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS);

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();
        addOtherRows();

        setAdapter(mRowsAdapter);

        updatePlaybackRow();

        //Auto play video hoặc quảng cáo nếu có
        if (mViewDetailCurrent.getContent().getMode() != ContentModel.MODE_VIDEO ||
                CollectionUtil.isEmpty(mViewDetailCurrent.getAdvertisement())) {
            handleLoadVideoItem();
        } else {
            //có quảng cáo - nên load quảng cáo trước - xíu setupViewDetail sau
            handleLoadAdv();
        }
    }

    private void callActivityPlayItem(PlayItemInterface anInterface) {
        if (mCallback != null) {
            mCallback.onFragmentLoad(anInterface);
            stopProgressAutomation();
            startProgressAutomation();
            setFadingEnabled(true);
        }
        mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);
    }

    private void callActivitySeekTo(int seek) {
        if (mCallback != null)
            mCallback.onFragmentSeek(seek);
    }

    public void togglePlayback(final boolean playPause) {
        if (playPause) {
            startProgressAutomation();
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(true);
            mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);
        } else {
            stopProgressAutomation();
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(false);
            mPlayPauseAction.setIndex(PlayPauseAction.PLAY);
        }
        notifyChanged(mPlayPauseAction);
    }

    private int getDuration() {
        return mGetInfoVideoViewListener != null ? mGetInfoVideoViewListener.getDuration() : 0;
    }

    /**
     * Add control action
     * Apr 29 2016
     * Now just Previous, Play/pause, Next on 1 row action, comment other
     */
    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            InfoPlayControlItem infoPlayControlItem = new InfoPlayControlItem();
            infoPlayControlItem.image = mSelectedItemInterface.iGetLinkImage();
            infoPlayControlItem.title = mSelectedItemInterface.iGetTitle();
            infoPlayControlItem.link = mSelectedItemInterface.iGetLinkPlay();
            infoPlayControlItem.description = mSelectedItemInterface.iGetDescription();
            infoPlayControlItem.id = mSelectedItemInterface.iGetId();
            mPlaybackControlsRow = new PlaybackControlsRow(infoPlayControlItem);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

//        updatePlaybackRow(mCurrentItem);

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
//        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
//        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(getActivity());
//        mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getActivity());
        mSkipPreviousAction = new SkipPreviousAction(getActivity());

        if (CollectionUtil.isNotEmpty(mListEpisode)) {
            mPrimaryActionsAdapter.add(mSkipPreviousAction);
            mPrimaryActionsAdapter.add(mPlayPauseAction);
            mPrimaryActionsAdapter.add(mSkipNextAction);
        } else {
            mPrimaryActionsAdapter.add(mPlayPauseAction);
        }

//        mRepeatAction = new RepeatAction(getActivity());
//        mThumbsUpAction = new ThumbsUpAction(getActivity());
//        mThumbsDownAction = new ThumbsDownAction(getActivity());
//        mShuffleAction = new ShuffleAction(getActivity());

//        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getActivity());
//        mRewindAction = new PlaybackControlsRow.RewindAction(getActivity());

//        if (PRIMARY_CONTROLS > 5) {
//            mPrimaryActionsAdapter.add(mThumbsUpAction);
//        } else {
//            mSecondaryActionsAdapter.add(mThumbsUpAction);
//        }
//        mPrimaryActionsAdapter.add(mSkipPreviousAction);
//        if (PRIMARY_CONTROLS > 3) {
//            mPrimaryActionsAdapter.add(new PlaybackControlsRow.RewindAction(getActivity()));
//        }
//        mPrimaryActionsAdapter.add(mPlayPauseAction);
//        if (PRIMARY_CONTROLS > 3) {
//            mPrimaryActionsAdapter.add(new PlaybackControlsRow.FastForwardAction(getActivity()));
//        }
//        mPrimaryActionsAdapter.add(mSkipNextAction);

//        mSecondaryActionsAdapter.add(mRepeatAction);
//        mSecondaryActionsAdapter.add(mShuffleAction);
//        if (PRIMARY_CONTROLS > 5) {
//            mPrimaryActionsAdapter.add(mThumbsDownAction);
//        } else {
//            mSecondaryActionsAdapter.add(mThumbsDownAction);
//        }
//        mSecondaryActionsAdapter.add(new PlaybackControlsRow.HighQualityAction(getActivity()));
//        mSecondaryActionsAdapter.add(new PlaybackControlsRow.ClosedCaptioningAction(getActivity()));
    }


    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        /**
         * Just one row action
         */
//        adapter = mSecondaryActionsAdapter;
//        if (adapter.indexOf(action) >= 0) {
//            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
//            return;
//        }
    }

    private void updatePlaybackRow() {
        if (mPlaybackControlsRow.getItem() != null) {
            InfoPlayControlItem item = (InfoPlayControlItem) mPlaybackControlsRow.getItem();
            item.title = mSelectedItemInterface.iGetTitle();
            item.link = mSelectedItemInterface.iGetLinkPlay();
            item.image = mSelectedItemInterface.iGetLinkImage();
            item.description = mSelectedItemInterface.iGetDescription();
            item.id = mSelectedItemInterface.iGetId();
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mSelectedItemInterface.iGetLinkImage());
        }

        int duration = getDuration();
        ALog.d(TAG, "duration updatePlaybackRow: " + duration);
        mPlaybackControlsRow.setTotalTime(duration);
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);

        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
    }

    public void updatePlaybackControlsRowTime() {
//        int duration = getDuration();
////        Log.d(TAG, "duration updatePlaybackControlsRowTime: " + duration);
//        mPlaybackControlsRow.setTotalTime(duration);
//        mPlaybackControlsRow.setCurrentTime(0);
//////        mPlaybackControlsRow.setBufferedProgress(0);
////
////
////        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
////

        updatePlaybackRow();
    }

    private void addOtherRows() {
        //Episode
        ArrayList<Episode> episodes = mViewDetailCurrent.getEpisodes();
        if (CollectionUtil.isNotEmpty(episodes)) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
            for (Episode item : episodes) {
                listRowAdapter.add(item);
            }
            HeaderItem header = new HeaderItem(POS_ID_ROW_CONTROL_MORE_VIDEO_EPISODE, getString(R.string.episodes_movies));
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
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
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                }
            }
        }
//        //Related
//        if (mViewDetailCurrent == null) return;
//        ArrayList<Related> related = mViewDetailCurrent.getRelated();
//        if (CollectionUtil.isNotEmpty(related)) {
//            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
//            for (Related item : related) {
//                listRowAdapter.add(item);
//            }
//            HeaderItem header = new HeaderItem(POS_ID_ROW_CONTROL_MORE_VIDEO_RELATED, getString(R.string.related_movies));
//            mRowsAdapter.add(new ListRow(header, listRowAdapter));
//        }
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {

                int updatePeriod = getUpdatePeriod();
                int currentTime = mGetInfoVideoViewListener != null ? mGetInfoVideoViewListener.getCurrentTimePlay() : 0;
                int totalTime = mPlaybackControlsRow.getTotalTime();
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(mGetInfoVideoViewListener != null ? mGetInfoVideoViewListener.getBufferPercent() : 0);

//                ALog.e(TAG, "currentTime - " + currentTime + " || BufferPercent - " + mGetInfoVideoViewListener.getBufferPercent());
                if (totalTime > 0 && totalTime <= currentTime) {
                    next();
                }
                mHandler.postDelayed(this, updatePeriod);
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    public boolean hasNext() {
        return CollectionUtil.isNotEmpty(mListEpisode);
    }

    public void next() {
        if (CollectionUtil.isEmpty(mListEpisode)) {
            return;
        }
        int nextPos = mCurrentPosEpisodeTimeLine + 1;
        if (nextPos >= mListEpisode.size()) {
            showToast("End Of List");
            return;
        }
        mCurrentPosEpisodeTimeLine = nextPos;
        mSelectedItemInterface = mListEpisode.get(nextPos);

        mTypeCurrentPlay = TYPE_EPISODE;
        handleLoadVideoItem();
    }

    private void prev() {
        if (CollectionUtil.isEmpty(mListEpisode)) {
            return;
        }
        int prevPos = mCurrentPosEpisodeTimeLine - 1;
        if (prevPos < 0) {
            showToast("End Of List");
            return;
        }
        mCurrentPosEpisodeTimeLine = prevPos;
        mSelectedItemInterface = mListEpisode.get(prevPos);

        mTypeCurrentPlay = TYPE_EPISODE;
        handleLoadVideoItem();
    }

    /**
     * handle play item, nếu có quảng cáo Adv thì play Adv trước rồi mới play item của mình
     */
    private void handleLoadVideoItem() {
        if (mViewDetailCurrent == null || mViewDetailCurrent.getContent() == null ||
                mViewDetailCurrent.getContent().getMode() != ContentModel.MODE_VIDEO ||
                CollectionUtil.isEmpty(mViewDetailCurrent.getAdvertisement())) {
            forceLoadVideoItem();
        } else {
            if (mCallback != null)
                mCallback.onFragmentShowAdv(mAdvData, Adv.TYPE_VIDEO);
        }
    }

    public void countLoadImageAdv() {
        if (mCallback != null)
            mCallback.onFragmentShowAdv(mAdvData, Adv.TYPE_IMAGE);
    }

    public void forceLoadVideoItem() {
        if (mSelectedItemInterfaceIDDDDDDDOLD > 0) {
            mTrackingController.trackingEnd(mSelectedItemInterfaceIDDDDDDDOLD);
        }
        switch (mTypeCurrentPlay) {
            case TYPE_DETAIL_ITEM:
                if (mSelectedItemInterface != null) {
                    callActivityPlayItem(mSelectedItemInterface);
                    updatePlaybackRow();
                } else {
                    // show error loading
                }
                break;

            case TYPE_EPISODE:
                if (mSelectedItemInterface != null) {
                    callActivityPlayItem(mSelectedItemInterface);
                    updatePlaybackRow();
                } else {
                    // show error loading
                }

                break;

            case TYPE_TIME_LINE:
                if (mSelectedItemInterface != null) {
                    if (mViewDetailCurrent.getContent().getIsLive()) {
                        callActivityPlayItem(mSelectedItemInterface);
                        updatePlaybackRow();
                    } else {
                        int seekTo = 0;
                        if (TextUtils.isEmpty(mSelectedItemInterface.iGetLinkPlay())) {
                            if (mSelectedItemInterface instanceof TimeLine) {
                                TimeLine timeLine = (TimeLine) mSelectedItemInterface;
                                timeLine.setLinkTemp(mViewDetailCurrent.getContent().getLink());
                                seekTo = DateTimeUtils.convertTimeStringToSecond(timeLine.getStart());
                                seekTo *= 1000;
                            } else {
                                showToast("Play Error - TimeLine");
                            }
                        }
                        callActivitySeekTo(seekTo);
                        updatePlaybackRow();
                    }
                } else {
                    // show error loading
                }

                break;
        }

        //TODO post delay để send tracking chỗ này
        if (mSelectedItemInterface != null) {
            mTrackingController.trackingStart(mSelectedItemInterface.iGetId());
            mSelectedItemInterfaceIDDDDDDDOLD = mSelectedItemInterface.iGetId();
        }
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        super.onStop();
    }


    protected void updateVideoImage(String uri) {
        ImageLoader.getInstance().loadImage(uri, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ALog.d(TAG, "onLoadingStarted");
                mPlaybackControlsRow.setImageDrawable(getResources().getDrawable(R.drawable.ic_empty));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mPlaybackControlsRow.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                ALog.d(TAG, "onLoadingFailed");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mPlaybackControlsRow.setImageBitmap(getActivity(), loadedImage);

                mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
                ALog.d(TAG, "onLoadingComplete");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
//                mPlaybackControlsRow.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                ALog.d(TAG, "onLoadingCancelled");
            }
        });
    }

    // Container Activity must implement this interface
    public interface OnPlayPauseClickedListener {
        void onFragmentPlayPause(Boolean playPause);

        void onFragmentLoad(PlayItemInterface item);

        void onFragmentSeek(int seek);

        void onFragmentShowAdv(AdvData advData, String type);

        void onFragmentDismissAdv(AdvData advData, String type);
    }

    public interface GetInfoVideoViewListener {
        int getCurrentTimePlay();

        int getBufferPercent();

        int getDuration();
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            if (item instanceof PlayItemInterface) {
                PlayItemInterface itemInterface = (PlayItemInterface) item;
                viewHolder.getTitle().setText(itemInterface.iGetTitle());
                viewHolder.getSubtitle().setText(itemInterface.iGetTitle());
            } else {
                viewHolder.getTitle().setText("N/A");
                viewHolder.getSubtitle().setText("N/A");
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(PlaybackVideoFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AccountManagerActivity.REQUEST_CODE_LOGIN_FOR_BUY_PACKAGE:
                if (resultCode == Activity.RESULT_OK) {
//                    mAdapter.clear();
//                    loadDetailItem();

                    //TODO load lại item ở đây và play
//                    handleGetInfoDataItem(mSelectedItemId, true);
                }
                break;

            case DetailsItemFragment.REQUEST_CODE_PURCHASE:
                if (resultCode == Activity.RESULT_OK) {
//                    if (dialogPackage != null && dialogPackage.isShowing())
//                        dialogPackage.dismiss();
//
//                    //re-setup adapter and refresh data
//                    mAdapter.clear();
//                    loadDetailItem();
                }
                break;
        }

    }
}
