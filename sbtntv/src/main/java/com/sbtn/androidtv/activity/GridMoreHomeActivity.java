package com.sbtn.androidtv.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.fragment.GridMoreHomeFragment;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/27/16.
 */
public class GridMoreHomeActivity extends BaseActivity {
    private static final String TAG = GridMoreHomeActivity.class.getSimpleName();

    public static final String EXTRA_ARR_DATA = "EXTRA_ARR_DATA";
    public static final String EXTRA_ARR_TITLE = "EXTRA_ARR_TITLE";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_more_home);
        Intent intent = getIntent();

        ArrayList<DataDetailItem> arrData = intent.getParcelableArrayListExtra(EXTRA_ARR_DATA);
        String title = intent.getStringExtra(EXTRA_ARR_TITLE);
        if (arrData == null) {
            ALog.d(TAG, "onCreate - arrData invalid!!");
            Utils.showToast(this, "Load data error!!!");
            return;
        }
        GridMoreHomeFragment fragment = GridMoreHomeFragment.newInstance(arrData, title);

        getFragmentManager().beginTransaction().replace(R.id.frame_fragment_detail_item, fragment, GridMoreHomeFragment.TAG).commit();
    }
}
