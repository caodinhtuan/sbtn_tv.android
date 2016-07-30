package com.sbtn.androidtv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.ActivityOptionsCompat;

import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.presenter.CardPresenter;
import com.sbtn.androidtv.utils.CollectionUtil;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/27/16.
 */
public class GridMoreHomeFragment extends VerticalGridFragment {
    public static final String ARGS_ARR_DATA = "ARGS_ARR_DATA";
    public static final String ARGS_TITLE = "ARGS_TITLE";
    public static final String TAG = GridMoreHomeFragment.class.getSimpleName();
    private ArrayList<DataDetailItem> mArrData;
    private String mTitle;
    private ArrayObjectAdapter mAdapter;

    public static GridMoreHomeFragment newInstance(ArrayList<DataDetailItem> arrData, String title) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_ARR_DATA, arrData);
        args.putString(ARGS_TITLE, title);

        GridMoreHomeFragment fragment = new GridMoreHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mArrData = args.getParcelableArrayList(ARGS_ARR_DATA);
        mTitle = args.getString(ARGS_TITLE);
        if (CollectionUtil.isEmpty(mArrData)) {
            getActivity().finish();
            return;
        }

        setTitle(mTitle);
        setupRowAdapter();
        setupData();
    }

    private void setupData() {
        mAdapter.addAll(0, mArrData);
        startEntranceTransition();
    }

    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new CardPresenter(mTitle));
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof DataDetailItem) {
                    DataDetailItem dataDetailItem = (DataDetailItem) item;
                    Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, dataDetailItem.getId());

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsItemActivity.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);
                }
            }
        });

        prepareEntranceTransition();
    }
}
