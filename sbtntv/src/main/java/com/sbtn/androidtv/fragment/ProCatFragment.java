package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;

import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.GridMoreHomeActivity;
import com.sbtn.androidtv.constants.ConstantDefine;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.HeaderListHomeRow;
import com.sbtn.androidtv.datamodels.Shows;
import com.sbtn.androidtv.presenter.HomeHeaderPresenterSelector;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class ProCatFragment extends RowsFragment {
    public static final String ARGS_ID = "ARGS_ID";
    public static final String ARGS_NAME_ROW = "ARGS_NAME_ROW";
    private ArrayObjectAdapter mAdapter;
    private int mId;
    private boolean isProvider;
    private String mNameRow;

    public static ProCatFragment newInstance(int id, String nameRow) {

        Bundle args = new Bundle();
        args.putInt(ARGS_ID, id);
        args.putString(ARGS_NAME_ROW, nameRow);
        ProCatFragment fragment = new ProCatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setupAdapter();
        loadData();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    private void initData() {
        Bundle args = getArguments();
        mNameRow = args.getString(ARGS_NAME_ROW, "");
        int id = args.getInt(ARGS_ID, -1);
        isProvider = ConstantDefine.isProvider(id);
        mId = ConstantDefine.getProCatId(id);
    }

    private void setupAdapter() {
        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof DataDetailItem) {
                    DataDetailItem dataDetailItem = (DataDetailItem) item;
                    Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, dataDetailItem.getId());

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsItemActivity.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);
                } else if (item instanceof HeaderListHomeRow) {
                    HeaderListHomeRow header = (HeaderListHomeRow) item;
                    Intent intent = new Intent(getActivity(), GridMoreHomeActivity.class);
                    intent.putExtra(GridMoreHomeActivity.EXTRA_ARR_DATA, header.getArrData());
                    intent.putExtra(GridMoreHomeActivity.EXTRA_ARR_TITLE, header.getTitle());

                    startActivity(intent);
                }
            }
        });

//        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
//            @Override
//            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
//                if (item instanceof HeaderListRow) {
//                    itemViewHolder.view.setActivated(false);
//                }
//            }
//        });
    }

    private void loadData() {
        //FIXME now lang = -1 for default
        final int lang = -1;
        showLoading();
        if (isProvider) {
            RequestFramework.getProviders(getActivity(), mId, lang, callBack);
        } else {
            RequestFramework.getCategories(getActivity(), mId, lang, callBack);
        }
    }

    private RequestFramework.DataCallBack<ArrayList<Shows>> callBack = new RequestFramework.DataCallBack<ArrayList<Shows>>() {
        @Override
        public void onResponse(ArrayList<Shows> dataResult) {
            if (dataResult != null) {
                bindingData(dataResult);
            } else {
                showRequestError();
            }
            hideLoading();
        }

        @Override
        public void onFailure() {
            showRequestError();
            hideLoading();
        }
    };

    private void bindingData(ArrayList<Shows> showses) {
        if (showses == null) {
            showRequestError();
            return;
        }
        if (CollectionUtil.isNotEmpty(showses)) {
            for (Shows show : showses) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new HomeHeaderPresenterSelector(show.getShowsName()));
                listRowAdapter.add(new HeaderListHomeRow(show.getShowsName(), show.getShowsDetails()));
                listRowAdapter.addAll(1, show.getShowsDetails());
                HeaderItem header = new HeaderItem(show.getShowsId(), show.getShowsName());
                mAdapter.add(new ListRow(header, listRowAdapter));
            }
        }
    }


    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((BaseActivity) activity).showLoading();
        }
    }

    public void hideLoading() {
        Activity activity = getActivity();
        if (activity != null) {
            ((BaseActivity) activity).hideLoading();
        }
    }

    private void showRequestError() {
        MyDialog.showDialogServiceError(getActivity());
    }
}
