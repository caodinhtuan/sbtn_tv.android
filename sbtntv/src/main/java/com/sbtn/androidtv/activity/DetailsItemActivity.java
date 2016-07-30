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

package com.sbtn.androidtv.activity;

import android.os.Bundle;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.fragment.DetailsItemFragment;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.Utils;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsItemActivity extends BaseActivity {
    private static final String TAG = "DetailsItemActivity";

    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String DATA_DETAIL_ITEM = "DATA_DETAIL_ITEM";
    public static final String DATA_DETAIL_ITEM_ID = "DATA_DETAIL_ITEM_ID";
    public static final String DATA_DETAIL_ITEM_NAME = "DATA_DETAIL_ITEM_NAME";
    public static final String DATA_DETAIL_ITEM_POSITION = "DATA_DETAIL_ITEM_POSITION";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int itemId = getIntent().getIntExtra(DATA_DETAIL_ITEM_ID, -1);
        if (itemId < 0) {
            ALog.d(TAG, "onCreate - itemId invalid!!");
            Utils.showToast(this, "Load data error!!!");
            return;
        }
        DetailsItemFragment fragment = DetailsItemFragment.newInstance(itemId);

        getFragmentManager().beginTransaction().replace(R.id.frame_fragment_detail_item, fragment, DetailsItemFragment.TAG).commit();
    }

}
