package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.BaseActivity;
import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.GridMoreHomeActivity;
import com.sbtn.androidtv.activity.MainActivity;
import com.sbtn.androidtv.activity.SBTNPackageBrowserActivity;
import com.sbtn.androidtv.datamodels.DataDetailItem;
import com.sbtn.androidtv.datamodels.HeaderListHomeRow;
import com.sbtn.androidtv.datamodels.Shows;
import com.sbtn.androidtv.presenter.HomeHeaderPresenterSelector;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.utils.CollectionUtil;
import com.sbtn.androidtv.utils.MyDialog;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/16/16.
 */
public class PurchasedContentsFragment extends RowsFragment {
    private static final int REQUEST_CODE_LIST_PACKAGE_SCREEN = 12345;
    private ArrayObjectAdapter mAdapter;

    public static PurchasedContentsFragment newInstance() {
        return new PurchasedContentsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAdapter();
        setupHeaderTransitionListener();
        loadData();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
        hideEmptyText();
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
//                ALog.d(TAG, "Item: " + item.toString());
                    Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM, (Serializable) dataDetailItem);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, dataDetailItem.getId());
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_POSITION, (int) row.getHeaderItem().getId());

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
        showLoading();
        hideEmptyText();
        RequestFramework.getPurchasedContents(getActivity(), new RequestFramework.DataCallBack<ArrayList<Shows>>() {
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
        });
    }


    private void setupHeaderTransitionListener() {
        Activity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).addHeadersTransitionListener(new BrowseFragment.BrowseTransitionListener() {
                @Override
                public void onHeadersTransitionStop(boolean withHeaders) {
                    super.onHeadersTransitionStop(withHeaders);
                    if (!withHeaders && isVisible() && button != null) {
                        button.requestFocus();
                    }
                }
            });
        }
    }

    View viewEmptyContents;
    View button;

    private void hideEmptyText() {
        if (getView() != null && viewEmptyContents != null) {
            ViewGroup viewById = (ViewGroup) getView().getParent();
            viewById.removeView(viewEmptyContents);
        }
    }

    private void bindingData(ArrayList<Shows> showses) {
        if (showses == null) {
            showRequestError();
            return;
        }

        if (showses.isEmpty()) {
            if (getView() != null) {
                ViewGroup rootView = (ViewGroup) getView().getParent();
                viewEmptyContents = LayoutInflater.from(getActivity()).inflate(R.layout.custom_empty_contents, null);
                button = viewEmptyContents.findViewById(R.id.buttonBuyNow);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SBTNPackageBrowserActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_LIST_PACKAGE_SCREEN);
                    }
                });

                if (!((MainActivity) getActivity()).isHeaderShow()) {
                    button.requestFocus();
                }

                rootView.addView(viewEmptyContents);
            }
            return;
        }

        if (CollectionUtil.isNotEmpty(showses)) {
            if (mAdapter != null)
                mAdapter.clear();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIST_PACKAGE_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
            }
        }
    }

}
