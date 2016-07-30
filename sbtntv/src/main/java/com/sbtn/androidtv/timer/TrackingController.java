package com.sbtn.androidtv.timer;

import android.content.Context;

import com.sbtn.androidtv.cache.CacheDataManager;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.DefaultDataCallBackRequest;
import com.sbtn.androidtv.request.datacallback.TrackingDataCallBackRequest;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hoanguyen on 6/14/16.
 */
public class TrackingController {
    public static final int RR_PING_SERVER = 90000;
    private static final String TAG = TrackingController.class.getSimpleName();

    private Context mContext;

    private Timer timerPing;

    public TrackingController() {
    }

    public void register(Context context) {
        this.mContext = context;
        startTimerPing();
    }

    public void unregister() {
        memberEndView();
        this.mContext = null;
        stopTimerPing();
    }

    private boolean checkRegister() {
        if (mContext == null) {
            ALog.e(TAG, "not checkRegister Context");
            return false;
        }
        return true;
    }

    public void trackingStart(final int contentId) {
        ALog.e(TAG, "trackingStart content: " + contentId);
        if (!checkRegister()) return;
        RequestFramework.trackingStart(mContext, String.valueOf(contentId), new RequestFramework.DataCallBack<TrackingDataCallBackRequest>() {
            @Override
            public void onResponse(TrackingDataCallBackRequest dataResult) {
                if (dataResult.isSuccess()) {
                    CacheDataManager.getInstance(mContext).saveTrackingId(contentId, dataResult.getId());
                    ALog.e(TAG, "trackingStart content: " + contentId + " with id: " + dataResult.getId());
                }
            }

            @Override
            public void onFailure() {
                ALog.e(TAG, "trackingStart Failed_Id: " + contentId);
            }
        });
    }

    public void trackingEnd(final int contentId) {
        ALog.e(TAG, "trackingEnd content: " + contentId);
        if (!checkRegister()) return;
        String id = CacheDataManager.getInstance(mContext).removeTrackingId(contentId);

        if (StringUtils.isNotEmpty(id)) {
            RequestFramework.trackingEnd(mContext, id, new RequestFramework.DataCallBack<DefaultDataCallBackRequest>() {
                @Override
                public void onResponse(DefaultDataCallBackRequest dataResult) {

                }

                @Override
                public void onFailure() {
                    ALog.e(TAG, "trackingEnd Failed_Id: " + contentId);
                }
            });
        }
    }


    private void startTimerPing() {
        ALog.e(TAG, "startTimerPing");
        if (timerPing == null)
            this.timerPing = new Timer();

        timerPing.schedule(new TimerTask() {
            @Override
            public void run() {
                RequestFramework.pingServer(mContext, new RequestFramework.DataCallBack<DefaultDataCallBackRequest>() {
                    @Override
                    public void onResponse(DefaultDataCallBackRequest dataResult) {

                    }

                    @Override
                    public void onFailure() {
                        ALog.e(TAG, "pingServer Failed");
                    }
                });
            }
        }, RR_PING_SERVER, RR_PING_SERVER);
    }

    private void stopTimerPing() {
        if (timerPing != null) {
            timerPing.cancel();
        }
        ALog.e(TAG, "stopTimerPing");
    }

    private void memberEndView() {
        RequestFramework.memberEndView(mContext, new RequestFramework.DataCallBack<DefaultDataCallBackRequest>() {
            @Override
            public void onResponse(DefaultDataCallBackRequest dataResult) {

            }

            @Override
            public void onFailure() {
                ALog.e(TAG, "memberEndView Failed");
            }
        });
    }
}
