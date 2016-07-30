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

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.text.TextUtils;

import com.sbtn.androidtv.datamodels.ContentModel;
import com.sbtn.androidtv.datamodels.InfoVideoArrayModel;
import com.sbtn.androidtv.datamodels.ViewDetail;
import com.sbtn.androidtv.utils.CollectionUtil;

import java.util.ArrayList;

public class DetailsItemPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        if (item instanceof ViewDetail) {
            ViewDetail dataDetailItem = (ViewDetail) item;

            ContentModel content = dataDetailItem.getContent();
            viewHolder.getTitle().setText(content.getName());
            viewHolder.getSubtitle().setText("");
            viewHolder.getBody().setText(collectData(dataDetailItem));
        }
    }

    private static String collectData(ViewDetail item) {
        if (item == null || item.getContent() == null) return "";

        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtil.isNotEmpty(item.getGenres())) {
            stringBuilder.append("Genre: ");
            stringBuilder.append(collectInfoVideoArrayModel(item.getGenres()));
        }

        if (CollectionUtil.isNotEmpty(item.getCountries())) {
            stringBuilder.append("\n");
            stringBuilder.append("National: ");
            stringBuilder.append(collectInfoVideoArrayModel(item.getCountries()));
        }

        if (!TextUtils.isEmpty(item.getContent().getYear())) {
            stringBuilder.append("\n");
            stringBuilder.append("Release Year: ");
            stringBuilder.append(item.getContent().getYear());
        }

        if (CollectionUtil.isNotEmpty(item.getDirectors())) {
            stringBuilder.append("\n");
            stringBuilder.append("Directors: ");
            stringBuilder.append(collectInfoVideoArrayModel(item.getCountries()));
        }

        if (CollectionUtil.isNotEmpty(item.getActors())) {
            stringBuilder.append("\n");
            stringBuilder.append("Actors: ");
            stringBuilder.append(collectInfoVideoArrayModel(item.getActors()));
        }

        if (!TextUtils.isEmpty(item.getContent().getDescription())) {
            stringBuilder.append("\n");
            stringBuilder.append("Description: ");
            stringBuilder.append(item.getContent().getDescription());
        }

        return stringBuilder.toString();
    }

    private static String collectInfoVideoArrayModel(ArrayList<InfoVideoArrayModel> arrayModels) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtil.isNotEmpty(arrayModels)) {
            boolean isFirst = true;
            for (InfoVideoArrayModel infoVideoArrayModel : arrayModels) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    stringBuilder.append(", ");
                }

                stringBuilder.append(infoVideoArrayModel.getName());
            }
        }
        return stringBuilder.toString();

    }

}
