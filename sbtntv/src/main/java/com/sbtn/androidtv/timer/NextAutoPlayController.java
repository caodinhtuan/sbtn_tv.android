package com.sbtn.androidtv.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hoanguyen on 6/15/16.
 */
public class NextAutoPlayController {
    private static final int COUNT_TIME = 10000;
    private static final int TIME_1S = 1000;
    private int count;
    private NextAutoPlayCallBack mNextAutoPlayCallBack;
    private Timer mTimer;
    private TimerTask runnable;

    public interface NextAutoPlayCallBack {
        void showTimerNextAuto();

        void hideTimerNextAuto();

        void updateTimer(int time, int percent);
    }

    public NextAutoPlayController() {
    }

    public void register(NextAutoPlayCallBack callBack) {
        mNextAutoPlayCallBack = callBack;
    }

    public void unregister() {
        if (runnable != null)
            runnable.cancel();
        if (mTimer != null)
            mTimer.cancel();
    }

    public void startAutoNext() {
        mTimer = new Timer();
        if (mNextAutoPlayCallBack != null) {
            mNextAutoPlayCallBack.showTimerNextAuto();
        }
        count = COUNT_TIME / TIME_1S;
        if (mNextAutoPlayCallBack != null) {
            mNextAutoPlayCallBack.updateTimer(count, 0);
        }

        runnable = new TimerTask() {
            @Override
            public void run() {
                count--;
                if (count < 0) {
                    stopAutoNext();
                } else {
                    if (mNextAutoPlayCallBack != null) {
                        mNextAutoPlayCallBack.updateTimer(count, count * 100 * TIME_1S / COUNT_TIME);
                    }
                }
            }
        };
        mTimer.schedule(runnable, TIME_1S, TIME_1S);
    }

    public void stopAutoNext() {
        if (mNextAutoPlayCallBack != null) {
            mNextAutoPlayCallBack.hideTimerNextAuto();
        }
        if (runnable != null)
            runnable.cancel();
        if (mTimer != null)
            mTimer.cancel();
    }


}
