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

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.fragment.PackageContentFragment;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.StringUtils;
import com.sbtn.androidtv.utils.Utils;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class PackageDetailWithContentsActivity extends BaseActivity {

    private static final String TAG = PackageDetailWithContentsActivity.class.getSimpleName();
    public static final String ARG_PACKAGE = "ARG_PACKAGE";
    private SBTNPackage sbtnPackage;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_package_grid_content);

        sbtnPackage = getIntent().getParcelableExtra(ARG_PACKAGE);
        if (sbtnPackage == null) {
            ALog.d(TAG, "onCreate - itemId invalid!!");
            Utils.showToast(this, "Load data error!!!");
            return;
        }

        setupPackageInfo(sbtnPackage);
        setupListContents(sbtnPackage);
    }

    private Button buttonBuy;

    private void setupPackageInfo(final SBTNPackage sbtnPackage) {
        ViewGroup frameLayout = (ViewGroup) findViewById(R.id.frame_package_info);
        View packageInfoView = LayoutInflater.from(this).inflate(R.layout.view_buy_package, frameLayout);
        TextView name = (TextView) packageInfoView.findViewById(R.id.name);
        TextView price = (TextView) packageInfoView.findViewById(R.id.price);
        TextView description = (TextView) packageInfoView.findViewById(R.id.description);
        TextView duration = (TextView) packageInfoView.findViewById(R.id.duration);
        TextView status = (TextView) packageInfoView.findViewById(R.id.status);
        buttonBuy = (Button) packageInfoView.findViewById(R.id.buttonBuy);
        buttonBuy.setFocusable(true);
        buttonBuy.setFocusableInTouchMode(true);
        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageContentFragment fragment = getFragment();
                if (fragment != null) {
                    fragment.handleShowDialogForPackage(sbtnPackage);
                }
            }
        });

        price.setText(sbtnPackage.getPrice());
        if (StringUtils.isNotEmpty(sbtnPackage.getName())) {
            name.setText(getString(R.string.name_dot) + sbtnPackage.getName());
        } else {
            name.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(sbtnPackage.getDescription())) {
            description.setText(getString(R.string.des_dot) + sbtnPackage.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(sbtnPackage.getDuration())) {
            duration.setText(getString(R.string.du_dot) + sbtnPackage.getDuration());
        } else {
            duration.setVisibility(View.GONE);
        }

        if (sbtnPackage.isBuy()) {
            status.setVisibility(View.VISIBLE);
            status.setText(getString(R.string.du_status) + getString(R.string.purchased));
        }
    }

    private void setupListContents(SBTNPackage sbtnPackage) {
        PackageContentFragment fragment = PackageContentFragment.newInstance(sbtnPackage.getId());

        getFragmentManager().beginTransaction().replace(R.id.frame_fragment_detail_item, fragment,
                PackageContentFragment.TAG).commit();
    }

    private PackageContentFragment getFragment() {
        Fragment fragmentByTag = getFragmentManager().findFragmentByTag(PackageContentFragment.TAG);
        if (fragmentByTag != null) {
            return (PackageContentFragment) fragmentByTag;
        }

        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyListener != null) {
            onKeyListener.onKey(null, keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnKeyListener onKeyListener;

    public void setOnKeyListener(View.OnKeyListener l) {
        onKeyListener = l;
    }

    public void buyButtonFocus() {
        if (buttonBuy != null)
            buttonBuy.requestFocus();
    }
}
