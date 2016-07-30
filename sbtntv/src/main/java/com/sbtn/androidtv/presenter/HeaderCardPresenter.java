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

package com.sbtn.androidtv.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.HeaderListRow;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class HeaderCardPresenter extends Presenter {
    private static final String TAG = "HeaderCardPresenter";

    public static final int CARD_WIDTH = 313;
    public static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;


    private static void updateCardBackgroundColor(HeaderCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        view.setBackgroundColor(color);
    }

    public HeaderCardPresenter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//        ALog.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);

        HeaderCardView cardView = new HeaderCardView(parent.getContext());

        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        HeaderCardView cardView = (HeaderCardView) viewHolder.view;
        if (item instanceof HeaderListRow) {
            HeaderListRow head = (HeaderListRow) item;
            cardView.setTextViewHeader(head.getTitle());
        }
        cardView.setMainDimensions(CARD_HEIGHT * 3 / 2, CARD_HEIGHT);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
//        HeaderCardView cardView = (HeaderCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
    }


    private class HeaderCardView extends BaseCardView {

        private TextView mTextViewHeader;
        private boolean mAttachedToWindow;

        public HeaderCardView(Context context) {
            super(context);
            settUpConfig();
        }

        public HeaderCardView(Context context, AttributeSet attrs) {
            super(context, attrs);
            settUpConfig();
        }

        public HeaderCardView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            settUpConfig();
        }

        private void settUpConfig() {
            setFocusable(true);
            setFocusableInTouchMode(true);

            setCardType(BaseCardView.CARD_TYPE_INFO_UNDER);
            setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ACTIVATED);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(R.layout.card_view_header, this);

            mTextViewHeader = (TextView) findViewById(R.id.header_text);
            mTextViewHeader.setAlpha(0);
        }

        public void setTextViewHeader(String headerTitle) {
            mTextViewHeader.setText(headerTitle);
        }


        /**
         * Sets the layout dimensions of the mTextViewHeader.
         */
        public void setMainDimensions(int width, int height) {
            ViewGroup.LayoutParams lp = mTextViewHeader.getLayoutParams();
            lp.width = width;
            lp.height = height;
            mTextViewHeader.setLayoutParams(lp);
        }

        private void fadeIn() {
            mTextViewHeader.setAlpha(0f);
            if (mAttachedToWindow) {
                mTextViewHeader.animate().alpha(1f).setDuration(
                        mTextViewHeader.getResources().getInteger(android.R.integer.config_shortAnimTime));
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            mAttachedToWindow = true;
            if (mTextViewHeader.getAlpha() == 0) {
                fadeIn();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            mAttachedToWindow = false;
            mTextViewHeader.animate().cancel();
            mTextViewHeader.setAlpha(1f);
            super.onDetachedFromWindow();
        }

        @Override
        public void setSelected(boolean selected) {
            updateCardBackgroundColor(this, selected);
            super.setSelected(selected);
        }
    }
}
