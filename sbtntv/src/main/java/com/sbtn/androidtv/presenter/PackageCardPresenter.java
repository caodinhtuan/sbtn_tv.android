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

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.SBTNPackage;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class PackageCardPresenter extends Presenter {
    private static final String TAG = "PackageCardPresenter";

    private static final int CARD_WIDTH = 313;
    private static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    public PackageCardPresenter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//        ALog.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };
        cardView.getMainImageView().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        if (item instanceof SBTNPackage) {
            SBTNPackage dataDetailItem = (SBTNPackage) item;

            cardView.setTitleText(dataDetailItem.getName());
            if (dataDetailItem.isBuy()) {
                cardView.setContentText(viewHolder.view.getResources().getString(R.string.purchased));
                cardView.getMainImageView().setImageResource(dataDetailItem.isPromotion() ? R.drawable.icon_buy_ok_promotion : R.drawable.icon_buy_ok);
            } else {
                cardView.setContentText(dataDetailItem.getPrice());
                cardView.getMainImageView().setImageResource(dataDetailItem.isPromotion() ? R.drawable.icon_buy_add_promotion : R.drawable.icon_buy_add);
            }

            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        } else {
            cardView.setTitleText("N/A");
            cardView.setContentText("N/A");
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
//        ALog.d(TAG, "onUnbindViewHolder");
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
