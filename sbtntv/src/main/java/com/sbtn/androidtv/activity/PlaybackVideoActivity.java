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

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sbtn.androidtv.BuildConfig;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.customs.views.CircularProgressBar;
import com.sbtn.androidtv.customs.views.MyAnimButton;
import com.sbtn.androidtv.datamodels.Adv;
import com.sbtn.androidtv.datamodels.AdvData;
import com.sbtn.androidtv.datamodels.PlayItemInterface;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.fragment.PlaybackVideoFragment;
import com.sbtn.androidtv.fragment.PlaybackVideoFragment.GetInfoVideoViewListener;
import com.sbtn.androidtv.fragment.PlaybackVideoFragment.OnPlayPauseClickedListener;
import com.sbtn.androidtv.timer.NextAutoPlayController;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.IntentUtils;


/**
 * PlaybackOverlayActivity for video playback that loads PlaybackOverlayFragment
 */
public class PlaybackVideoActivity extends BaseActivity implements OnPlayPauseClickedListener {
    private static final String TAG = PlaybackVideoActivity.class.getSimpleName();

    public static final int PLAY_CURRENT = 0;
    public static final int PLAY_EPISODE = 1;
    public static final int PLAY_TIME_LINE = 2;
    public static final int RESULT_NO_PERMISSION_PLAY = 306;

    private VideoView mVideoView;
    //Cho quảng cáo
    private VideoView mVideoViewAdv;
    private View mLayoutAdv;
    private ImageView mImageAdv;
    private View mLayoutSkipItem;
    private ImageView mImageThumbnail;
    private TextView mTxtSkipTime;
    private View mViewSkipIn;
    private View mViewClickSkip;
    private MyAnimButton mButtonCloseImageAdv;
    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private ProgressBar mProgressBar;

    //Timer cho skip adv
    private Handler mHandler;
    private Runnable mRunnableSkipVideo;
    private Runnable mRunnableSkipImage;
    private boolean isShowingControlPLayback;

    private CircularProgressBar mNextAutoCircularProgressBar;

    private ViewDetail mViewDetail;
    private int mChildId;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_controls);
        loadViews();

        mHandler = new Handler();

        setupFragment();
    }

    private void setupFragment() {
        Intent intent = getIntent();
        mViewDetail = intent.getParcelableExtra(IntentUtils.EXTRA_OBJECT_PARCELABLE);
        int playWhat = intent.getIntExtra(IntentUtils.EXTRA_PLAY_WHAT, PLAY_CURRENT);
        if (playWhat == PLAY_CURRENT) {
            if (CollectionUtil.isNotEmpty(mViewDetail.getEpisodes())) {
                playWhat = PLAY_EPISODE;
            }
        }
        mChildId = intent.getIntExtra(IntentUtils.EXTRA_ID, -1);
        if (mViewDetail == null) {
            ALog.e(TAG, "mViewDetail = null");
            finish();
            return;
        }
        PlaybackVideoFragment fragment = PlaybackVideoFragment.newInstance(mViewDetail, playWhat, mChildId);

        getFragmentManager().beginTransaction().add(R.id.frame_playback_fragment, fragment, PlaybackVideoFragment.TAG).commit();
        fragment.setFadeCompleteListener(new android.support.v17.leanback.app.PlaybackOverlayFragment.OnFadeCompleteListener() {
            @Override
            public void onFadeInComplete() {
                super.onFadeInComplete();
                disFocusButtonSkipAdv();
                isShowingControlPLayback = true;
            }

            @Override
            public void onFadeOutComplete() {
                super.onFadeOutComplete();
                isShowingControlPLayback = false;
                requestFocusButtonSkipAdv();

                if (mLayoutAdv.getVisibility() == View.VISIBLE && mImageAdv.getVisibility() == View.VISIBLE) {
                    mButtonCloseImageAdv.setVisibility(View.VISIBLE);
                    mButtonCloseImageAdv.requestFocus();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null)
            mVideoView.suspend();
        if (mVideoViewAdv != null)
            mVideoViewAdv.suspend();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        PlaybackVideoFragment fragment = (PlaybackVideoFragment) getFragmentManager().findFragmentByTag(PlaybackVideoFragment.TAG);
        if (fragment == null) {
            ALog.e(TAG, "onKeyUp - PlaybackOverlayFragment = null");
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                fragment.togglePlayback(true);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                fragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    fragment.togglePlayback(false);
                } else {
                    fragment.togglePlayback(true);
                }
                return true;

            case KeyEvent.KEYCODE_MENU:
                //For test
                if (BuildConfig.DEBUG) {
                    new MaterialDialog.Builder(this).backgroundColorRes(R.color.MDGray).positiveText("SEEK").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            int duration = mVideoView.getDuration();
                            int seek = duration * 99 / 100;
                            mVideoView.seekTo(seek);

//                            mNextAutoPlayController.startAutoNext();
                        }
                    }).show();

                    return true;
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Implementation of OnPlayPauseClickedListener
     */
    @Override
    public void onFragmentPlayPause(final Boolean playPause) {
        if (mVideoView == null) {
            Log.e(TAG, "onFragmentPlayPause - PlayItemInterface == null");
            return;
        }

        if (playPause) {
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
                mPlaybackState = LeanbackPlaybackState.PLAYING;
            }
        } else {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mPlaybackState = LeanbackPlaybackState.PAUSED;
            }
        }
    }

    @Override
    public void onFragmentLoad(PlayItemInterface item) {
        if (item == null) {
            Log.e(TAG, "onFragmentLoad - PlayItemInterface == null");
            return;
        }
        if (mVideoView == null) {
            loadViews();
        } else {
            mVideoView.stopPlayback();
        }
        showLoading();

        if (mLayoutAdv != null && mLayoutAdv.getVisibility() != View.GONE) {
            mLayoutAdv.setVisibility(View.GONE);
        }

        if (mVideoView != null && mVideoView.getVisibility() != View.VISIBLE) {
            mVideoView.setVisibility(View.VISIBLE);
        }

        mVideoView.setVideoPath(item.iGetLinkPlay());
        mVideoView.start();
    }

    @Override
    public void onFragmentSeek(int seek) {
        if (mVideoView == null) {
            return;
        }

        mVideoView.seekTo(seek);
    }

    @Override
    public void onFragmentShowAdv(final AdvData advData, String type) {
        if (mVideoView != null) {
            //ko có image adv - nên ko làm gì cả
            if (advData.getVideoAdv() == null || !advData.isShouldShowAdv()) return;
            if (mLayoutAdv != null && mLayoutAdv.getVisibility() != View.VISIBLE) {
                mLayoutAdv.setVisibility(View.VISIBLE);
            }

            if (type.equals(Adv.TYPE_VIDEO)) {
                stopCountSkipAdvTimeAutomation();
                mVideoViewAdv.setVisibility(View.VISIBLE);
                mTxtSkipTime.setText("");
                mImageAdv.setVisibility(View.GONE);
                mViewSkipIn.setVisibility(View.GONE);
                pauseVideoView();
                if (mVideoView != null && mVideoView.getVisibility() != View.GONE) {
                    mVideoView.setVisibility(View.GONE);
                }
                mVideoViewAdv.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        String msg = "";
                        if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                            msg = getString(R.string.video_error_media_load_timeout);
                        } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                            msg = getString(R.string.video_error_server_inaccessible);
                        } else {
                            msg = getString(R.string.video_error_unknown_error);
                        }
                        mVideoViewAdv.stopPlayback();
                        return false;
                    }
                });

                mVideoViewAdv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
//                        hideLoading();
                        mVideoViewAdv.start();
                        mLayoutSkipItem.setVisibility(View.VISIBLE);
                        mImageThumbnail.setVisibility(View.VISIBLE);
                        mTxtSkipTime.setVisibility(View.VISIBLE);
                        mViewSkipIn.setVisibility(View.VISIBLE);
                        startCountSkipAdvTimeAutomation(advData.getSkipTimeVideo());
                    }
                });

                mVideoViewAdv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handleCompleteAdvVideo();
                    }
                });

                mVideoViewAdv.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                                ALog.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                                hideLoading();
                                return true;
                            }
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                                showLoading();
                                ALog.d(TAG, "MEDIA_INFO_BUFFERING_START");
                                return true;
                            }
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                ALog.d(TAG, "MEDIA_INFO_BUFFERING_END");
                                showLoading();
                                return true;
                            }
                        }
                        return false;
                    }
                });

                showLoading();
                Toast.makeText(PlaybackVideoActivity.this, "Ad Loading...", Toast.LENGTH_SHORT).show();
                ImageLoader.getInstance().displayImage(advData.getLinkThumbnailItem(), mImageThumbnail);
                mVideoViewAdv.setVideoPath(advData.getVideoAdv().getLink_src());
                mVideoViewAdv.start();
            } else if (type.equals(Adv.TYPE_IMAGE)) {
                if (advData.getImageAdv() == null || !advData.isShouldShowAdv()) {
                    //ko có image adv - nên ko làm gì cả
                    return;
                }
                startCountSkipAdvImageCount(advData.getImageAdv().getLink_src(), advData.getStartTimeImage());
            }
        }
    }

    private void handleCompleteAdvVideo() {
        Fragment fragmentById = getFragmentManager().findFragmentById(R.id.frame_playback_fragment);
        if (fragmentById != null) {
            PlaybackVideoFragment fragment = (PlaybackVideoFragment) fragmentById;

            fragment.forceLoadVideoItem();
            fragment.countLoadImageAdv();

            mVideoView.setVisibility(View.VISIBLE);
            mLayoutAdv.setVisibility(View.GONE);
            mVideoViewAdv.setVisibility(View.GONE);
            mLayoutSkipItem.setVisibility(View.GONE);
            mImageAdv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentDismissAdv(AdvData advData, String type) {
        //TODO
    }

    public void pauseVideoView() {
        if (mVideoView != null) {

            mPlaybackState = LeanbackPlaybackState.PAUSED;
            mVideoView.pause();
        }
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        if (mProgressBar == null)
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ///
        mVideoViewAdv = (VideoView) findViewById(R.id.videoViewAdv);
        mLayoutAdv = findViewById(R.id.layout_adv);
        mImageAdv = (ImageView) findViewById(R.id.image_adv);

        mLayoutSkipItem = findViewById(R.id.layout_skip_item);
        mImageThumbnail = (ImageView) findViewById(R.id.imgThumbnail);
        mTxtSkipTime = (TextView) findViewById(R.id.txtTimeAdv);
        mViewSkipIn = findViewById(R.id.txtSkipIn);
        mViewClickSkip = findViewById(R.id.viewClickSkip);
        mButtonCloseImageAdv = (MyAnimButton) findViewById(R.id.btCloseImageAdv);
        mNextAutoCircularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar);
        mNextAutoCircularProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextAutoPlayController.stopAutoNext();
            }
        });

        disableFocusView(mVideoView);
        disableFocusView(mProgressBar);
        disableFocusView(mVideoViewAdv);
        disableFocusView(mLayoutAdv);
        disableFocusView(mImageAdv);

        disableFocusView(mLayoutSkipItem);
        disableFocusView(mImageThumbnail);
        disableFocusView(mTxtSkipTime);
        disableFocusView(mViewClickSkip);
        disableFocusView(mButtonCloseImageAdv);

        setupCallbacks();
    }

    private void disableFocusView(View view) {
        if (view == null) return;

        view.setFocusable(false);
        view.setFocusableInTouchMode(false);
    }

    private void setupCallbacks() {

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String msg = "";
                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                    msg = getString(R.string.video_error_media_load_timeout);
                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    msg = getString(R.string.video_error_server_inaccessible);
                } else {
                    msg = getString(R.string.video_error_unknown_error);
                }
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                hideLoading();

                PlaybackVideoFragment fragment = (PlaybackVideoFragment) getFragmentManager().findFragmentById(R.id.frame_playback_fragment);
                if (fragment != null) {
                    fragment.updatePlaybackControlsRowTime();
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                handleCountNextEpisode();
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        ALog.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                        hideLoading();
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        showLoading();
                        ALog.d(TAG, "MEDIA_INFO_BUFFERING_START");
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        ALog.d(TAG, "MEDIA_INFO_BUFFERING_END");
                        showLoading();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private NextAutoPlayController mNextAutoPlayController;

    private void handleCountNextEpisode() {
        PlaybackVideoFragment fragment = (PlaybackVideoFragment) getFragmentManager().findFragmentById(R.id.frame_playback_fragment);
        if (fragment != null && fragment.hasNext()) {
            mNextAutoPlayController.startAutoNext();
            mNextAutoCircularProgressBar.requestFocus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNextAutoPlayController == null) {
            mNextAutoPlayController = new NextAutoPlayController();
        }
        mNextAutoPlayController.register(mNextAutoPlayCallBack);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                // Try to play behind launcher, but if it fails, stop playback.
                stopPlayback();
            }
        } else {
            requestVisibleBehind(false);
        }

        mNextAutoPlayController.unregister();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountSkipAdvTimeAutomation();
    }

    private NextAutoPlayController.NextAutoPlayCallBack mNextAutoPlayCallBack = new NextAutoPlayController.NextAutoPlayCallBack() {
        @Override
        public void showTimerNextAuto() {
            mNextAutoCircularProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideTimerNextAuto() {
            mNextAutoCircularProgressBar.post(new Runnable() {
                @Override
                public void run() {
                    mNextAutoCircularProgressBar.setVisibility(View.GONE);
                    PlaybackVideoFragment fragment = (PlaybackVideoFragment) getFragmentManager().findFragmentByTag(PlaybackVideoFragment.TAG);
                    if (fragment != null) {
                        fragment.next();
                    }
                }
            });
        }

        @Override
        public void updateTimer(final int time, final int percent) {
            mNextAutoCircularProgressBar.post(new Runnable() {
                @Override
                public void run() {
                    mNextAutoCircularProgressBar.setTitle(String.valueOf(time));
                }
            });
        }
    };


    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    public GetInfoVideoViewListener getGetInfoVideoViewListener() {
        return mGetInfoVideoViewListener;
    }

    private GetInfoVideoViewListener mGetInfoVideoViewListener = new GetInfoVideoViewListener() {
        @Override
        public int getCurrentTimePlay() {
            if (mVideoView == null)
                return 0;

            return mVideoView.getCurrentPosition();
        }

        @Override
        public int getBufferPercent() {
            if (mVideoView == null)
                return 0;

            Log.d(TAG, "BufferPercentage: " + mVideoView.getBufferPercentage());

            return mVideoView.getBufferPercentage();
        }

        @Override
        public int getDuration() {
            if (mVideoView == null)
                return 0;

            return mVideoView.getDuration();
        }
    };

    private void startCountSkipAdvTimeAutomation(final int countTime) {
        mRunnableSkipVideo = new Runnable() {
            int time = countTime;

            @Override
            public void run() {
                if (time <= 0) {
                    mViewSkipIn.setVisibility(View.GONE);
                    mTxtSkipTime.setText("Skip Ad >>");
                    mRunnableSkipVideo = null;
                    requestFocusButtonSkipAdv();
                    mViewClickSkip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleCompleteAdvVideo();

                        }
                    });
                    return;
                }
                mTxtSkipTime.setText(String.valueOf(time--));
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnableSkipVideo, 1000);
    }

    private void stopCountSkipAdvTimeAutomation() {
        if (mHandler != null && mRunnableSkipVideo != null) {
            mHandler.removeCallbacks(mRunnableSkipVideo);
            mRunnableSkipVideo = null;
        }
    }

    private void startCountSkipAdvImageCount(final String urlImage, final int countTime) {
        mRunnableSkipVideo = new Runnable() {
            int time = countTime;

            @Override
            public void run() {
                if (time <= 0) {
                    //Đếm đủ rồi - stop thôi
                    stopCountSkipAdvImageCount();

                    //Load image để hiện quảng cáo lên
                    ImageLoader.getInstance().displayImage(urlImage, mImageAdv, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mLayoutAdv.setVisibility(View.VISIBLE);
                            mLayoutSkipItem.setVisibility(View.GONE);
                            mVideoViewAdv.setVisibility(View.GONE);
                            mImageAdv.setVisibility(View.VISIBLE);
                            mButtonCloseImageAdv.setFocusable(true);
//                            mImageAdv.setFocusableInTouchMode(true);
//                            mImageAdv.setClickable(true);
                            mButtonCloseImageAdv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLayoutAdv.setVisibility(View.GONE);
                                    mImageAdv.setVisibility(View.VISIBLE);
//                                    mImageAdv.setFocusable(false);
//                                    mImageAdv.setFocusableInTouchMode(false);
//                                    mImageAdv.setClickable(false);


                                }
                            });
                            mButtonCloseImageAdv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        mButtonCloseImageAdv.setVisibility(View.VISIBLE);
                                    } else {
                                        mButtonCloseImageAdv.setVisibility(View.GONE);
                                    }
                                }
                            });
                            mButtonCloseImageAdv.setVisibility(View.VISIBLE);
                            mButtonCloseImageAdv.requestFocus();
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });

                    return;
                }
                mTxtSkipTime.setText(String.valueOf(time--));
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mRunnableSkipVideo, 1000);
    }

    private void stopCountSkipAdvImageCount() {
        if (mHandler != null && mRunnableSkipImage != null) {
            mHandler.removeCallbacks(mRunnableSkipImage);
            mRunnableSkipImage = null;
        }
    }

    /**
     * Phục vụ việc focus lại button skip adv khi Control overlay button hide
     */
    public void requestFocusButtonSkipAdv() {
        if (isShowingControlPLayback) return;
        if (mLayoutSkipItem.getVisibility() == View.VISIBLE && mRunnableSkipVideo == null) {
            mViewClickSkip.setClickable(true);
            mViewClickSkip.setFocusable(true);
            mViewClickSkip.setFocusableInTouchMode(true);
            mViewClickSkip.requestFocus();
        }

        if (mNextAutoCircularProgressBar.getVisibility() == View.VISIBLE) {
            mNextAutoCircularProgressBar.setClickable(true);
            mNextAutoCircularProgressBar.setFocusable(true);
            mNextAutoCircularProgressBar.setFocusableInTouchMode(true);
            mNextAutoCircularProgressBar.requestFocus();
        }
    }

    public void disFocusButtonSkipAdv() {
        if (mLayoutSkipItem.getVisibility() == View.VISIBLE && mRunnableSkipVideo == null) {
            mViewClickSkip.setClickable(false);
            mViewClickSkip.setFocusable(false);
            mViewClickSkip.setFocusableInTouchMode(false);
            mViewClickSkip.requestFocus();
        }
    }

}
