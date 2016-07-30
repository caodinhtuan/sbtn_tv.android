package com.sbtn.androidtv.customs.views;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.presenter.CardPresenter;

/**
 * Created by hoanguyen on 6/13/16.
 */
public class MyImageCardView extends ImageCardView {
    private TextView tvHeaderTitle;

    public MyImageCardView(Context context, int themeResId) {
        super(context, themeResId);
    }

    public MyImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageCardView(Context context) {
        super(context);
    }

    public MyImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHeaderTitle(String text) {
        String newText = text.replace(" ", "\n");
        if (tvHeaderTitle == null) {
            tvHeaderTitle = new TextView(getContext());
            tvHeaderTitle.setGravity(Gravity.CENTER);
            tvHeaderTitle.setSingleLine(false);
            tvHeaderTitle.setTextColor(getResources().getColor(R.color.white_tv));
            tvHeaderTitle.setLayoutParams(new ViewGroup.LayoutParams(CardPresenter.CARD_WIDTH, CardPresenter.CARD_HEIGHT));
        }
        tvHeaderTitle.setText(newText);
        ((ViewGroup) getMainImageView().getParent()).addView(tvHeaderTitle);
        getMainImageView().setVisibility(GONE);
    }

    public void clearHeader() {
        if (tvHeaderTitle != null) {
            ((ViewGroup) getMainImageView().getParent()).removeView(tvHeaderTitle);
            getMainImageView().setVisibility(INVISIBLE);
            setTitleText("");
            setContentText("");
        }
    }

    @Override
    public void setMainImageDimensions(int width, int height) {
        super.setMainImageDimensions(width, height);
        getMainImageView().setVisibility(VISIBLE);
    }
}
