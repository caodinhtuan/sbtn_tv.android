package com.sbtn.androidtv.customs.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

/**
 * Created by hoanguyen on 5/27/16.
 */
public class MyAnimButton extends Button {
    public static final String TAG = "MyAnimButton";
    private boolean isAniming;
    private YoYo.YoYoString animVisible;
    private YoYo.YoYoString animGone;

    private final static int timeAnim = 500;

    public MyAnimButton(Context context) {
        super(context);
    }

    public MyAnimButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAnimButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyAnimButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            //ALog.d(TAG, "setVisibility - VISIBLE");
            if (animVisible != null && animVisible.isRunning()) {
                //ALog.d(TAG, "setVisibility - VISIBLE - animVisible running");
                return;
            }
            animVisible();

        } else if (visibility == GONE) {
            //ALog.d(TAG, "setVisibility - GONE");
            if (animGone != null && animGone.isRunning()) {
                //ALog.d(TAG, "setVisibility - VISIBLE - animGone running");
                return;
            }
            animGone();
        } else
            super.setVisibility(visibility);
    }

    private void animVisible() {
        //ALog.d(TAG, "setVisibility - VISIBLE - doing");
        animVisible = YoYo.with(Techniques.FadeInDown).duration(timeAnim)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        MyAnimButton.super.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        MyAnimButton.super.setAlpha(1);
//                        Techniques.SlideInDown.getAnimator().reset(MyAnimButton.this);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(this);

    }

    private void animGone() {
        //ALog.d(TAG, "setVisibility - GONE - doing");
        animGone = YoYo.with(Techniques.SlideOutUp).duration(timeAnim)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        MyAnimButton.super.setVisibility(VISIBLE);
                        MyAnimButton.super.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        MyAnimButton.super.setVisibility(GONE);
                        setAlpha(1);
//                        Techniques.SlideOutUp.getAnimator().reset(MyAnimButton.this);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(this);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        //ALog.d(TAG, "onVisibilityChanged - " + visibility);
    }
}
