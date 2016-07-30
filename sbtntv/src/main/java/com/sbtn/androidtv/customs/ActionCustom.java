package com.sbtn.androidtv.customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.Action;
import android.util.TypedValue;
import android.view.KeyEvent;

/**
 * Created by hoanguyen on 5/10/16.
 */
public class ActionCustom {
    public static Drawable getStyledDrawable(Context context, int index) {
        TypedValue outValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(
                android.support.v17.leanback.R.attr.playbackControlsActionIcons, outValue, false)) {
            return null;
        }
        TypedArray array = context.getTheme().obtainStyledAttributes(outValue.data,
                android.support.v17.leanback.R.styleable.lbPlaybackControlsActionIcons);
        Drawable drawable = array.getDrawable(index);
        array.recycle();
        return drawable;
    }

    /**
     * An action displaying an icon for skip next.
     */
    public static class SkipNextAction extends Action {
        Drawable drawableIcon;
        String labelText;

        public SkipNextAction(Context context) {
            super(android.support.v17.leanback.R.id.lb_control_skip_next);
            drawableIcon = getStyledDrawable(context,
                    android.support.v17.leanback.R.styleable.lbPlaybackControlsActionIcons_skip_next);
            setIcon(drawableIcon);
            labelText = context.getString(android.support.v17.leanback.R.string.lb_playback_controls_skip_next);
            setLabel1(labelText);
            addKeyCode(KeyEvent.KEYCODE_MEDIA_NEXT);
        }

        public void hideIcon() {
            setIcon(null);
            setLabel1("");
            removeKeyCode(KeyEvent.KEYCODE_MEDIA_NEXT);
        }

        public void showIcon() {
            setIcon(drawableIcon);
            setLabel1(labelText);
            addKeyCode(KeyEvent.KEYCODE_MEDIA_NEXT);
        }
    }

}