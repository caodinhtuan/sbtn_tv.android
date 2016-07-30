package com.sbtn.androidtv.customs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by hoanguyen on 5/19/16.
 * Custom VideoView để tránh leak Activity memory khi release VideoView
 */
public class MyVideoView extends VideoView {
    public MyVideoView(Context context) {
        super(context.getApplicationContext());
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context.getApplicationContext(), attrs, defStyleAttr, defStyleRes);
    }
}
