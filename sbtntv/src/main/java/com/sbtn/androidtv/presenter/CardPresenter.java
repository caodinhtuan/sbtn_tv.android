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

import android.graphics.Bitmap;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.PlayItemInterface;
import com.sbtn.androidtv.utils.StringUtils;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = CardPresenter.class.getSimpleName();

    public static final int CARD_WIDTH = 313;
    public static final int CARD_HEIGHT = 176;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private final DisplayImageOptions options;
    private String forceInfoName;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    public CardPresenter(String name) {
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

        forceInfoName = name.trim();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        if (item instanceof PlayItemInterface) {
            PlayItemInterface dataDetailItem = (PlayItemInterface) item;

            if (dataDetailItem.iGetLinkImage() != null) {
                cardView.setTitleText(dataDetailItem.iGetTitle());
                if (StringUtils.isNotEmpty(forceInfoName)) {
                    cardView.setContentText(forceInfoName);
                } else {
                    cardView.setContentText(dataDetailItem.iGetDescription());
                }

                cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

                ImageLoader.getInstance().displayImage(dataDetailItem.iGetLinkImage(), cardView.getMainImageView(), options);
            }
            boolean isSetBadgeImage = false;
            if (item instanceof DataDetailItem) {
                DataDetailItem detailItem = (DataDetailItem) item;
                if (detailItem.isContentFree()) {
                    isSetBadgeImage = true;
                    cardView.setBadgeImage(cardView.getResources().getDrawable(R.drawable.icon_free));
                }
            }

            if (!isSetBadgeImage) {
                cardView.setBadgeImage(null);
            }
        } else {
            cardView.setTitleText("N/A");
            cardView.setContentText("N/A");
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            ImageLoader.getInstance().displayImage("", cardView.getMainImageView(), options);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
