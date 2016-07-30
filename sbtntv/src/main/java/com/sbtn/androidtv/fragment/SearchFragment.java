package com.sbtn.androidtv.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.sbtn.androidtv.activity.DetailsItemActivity;
import com.sbtn.androidtv.activity.SearchActivity;
import com.sbtn.androidtv.datamodels.PlayItemInterface;
import com.sbtn.androidtv.presenter.CardPresenter;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.SearchDataCallBackRequest;
import com.sbtn.androidtv.utils.CollectionUtil;

/**
 * Created by hoanguyen on 6/9/16.
 */
public class SearchFragment extends android.support.v17.leanback.app.DetailsFragment {

    public static final String TAG = SearchFragment.class.getSimpleName();
    private ArrayObjectAdapter mRowsAdapter;
    private String oldKeySearch;
    private SearchActivity searchActivity;

    int rowIdSelected = -1;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        searchActivity = (SearchActivity) context;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                rowIdSelected = (int) row.getId();
//                ALog.e(TAG, "rowIdSelected " + rowIdSelected);


//                ListRow listRow;
//                ArrayObjectAdapter adapter;
//                HeaderItem headerItem;
//                int i;
//                switch ((int) row.getId()) {
//                    case 0:
//                        listRow = (ListRow) mRowsAdapter.get(0);
//                        adapter = (ArrayObjectAdapter) (listRow).getAdapter();
//                        i = adapter.indexOf(item);
//                        headerItem = new HeaderItem(0, String.format("Videos (%d/%d):", i, adapter.size()));
//                        listRow.setHeaderItem(headerItem);
////                        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
//                        ALog.e(TAG, "pos " + String.format("Videos (%d/%d):", i, adapter.size()));
//                        break;
//                    case 1:
//                        listRow = (ListRow) mRowsAdapter.get(1);
//                        adapter = (ArrayObjectAdapter) (listRow).getAdapter();
//                        i = adapter.indexOf(item);
//                        headerItem = new HeaderItem(1, String.format("Videos (%d/%d):", i, adapter.size()));
//                        listRow.setHeaderItem(headerItem);
////                        mRowsAdapter.notifyArrayItemRangeChanged(1, 1);
//                        ALog.e(TAG, "pos " + String.format("Videos (%d/%d):", i, adapter.size()));
//                        break;
//
//                }
            }
        });

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

                if (item != null && item instanceof PlayItemInterface) {
                    int idItem = ((PlayItemInterface) item).iGetId();
                    Intent intent = new Intent(getActivity(), DetailsItemActivity.class);
                    intent.putExtra(DetailsItemActivity.DATA_DETAIL_ITEM_ID, idItem);

                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((ImageCardView) itemViewHolder.view).getMainImageView(),
                            DetailsItemActivity.SHARED_ELEMENT_NAME).toBundle();
                    getActivity().startActivity(intent, bundle);
                }

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view == null) return;

        final SearchActivity activity = (SearchActivity) getActivity();
        activity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                ALog.e(TAG, "keyCode " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && (rowIdSelected <= 0 || mRowsAdapter.size() == 0)) {
                    activity.searchFocus();
                }

                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchActivity = null;
    }

    public void loadSearchData(final String query) {
        if (query.equals(oldKeySearch)) return;

        oldKeySearch = query;
        if (TextUtils.isEmpty(query)) {
            ArrayObjectAdapter listRowAdapter;
            HeaderItem header;
            mRowsAdapter.clear();
            listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
            header = new HeaderItem("No search found!!!");
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
            return;
        }

        if (searchActivity != null)
            searchActivity.showLoading();
        RequestFramework.search(getActivity(), query, new RequestFramework.DataCallBack<SearchDataCallBackRequest>() {
            @Override
            public void onResponse(SearchDataCallBackRequest dataResult) {
                if (searchActivity != null)
                    searchActivity.hideLoading();

                ArrayObjectAdapter listRowAdapter;
                HeaderItem header;
                boolean hasData = false;
                mRowsAdapter.clear();
                if (!CollectionUtil.isEmpty(dataResult.getView())) {
                    listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
                    listRowAdapter.addAll(0, dataResult.getView());
                    String headTitle = String.format("Videos (%d):", dataResult.getView().size());
                    header = new HeaderItem(0, headTitle);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    hasData = true;
                }

                if (!CollectionUtil.isEmpty(dataResult.getListen())) {
                    listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
                    listRowAdapter.addAll(0, dataResult.getListen());
                    String headTitle = String.format("Listens (%d):", dataResult.getListen().size());
                    header = new HeaderItem(1, headTitle);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    hasData = true;
                }

                if (!hasData) {
                    mRowsAdapter.clear();
                    listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
                    header = new HeaderItem("No search found!!!");
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                }
            }

            @Override
            public void onFailure() {
                mRowsAdapter.clear();
                ArrayObjectAdapter listRowAdapter;
                HeaderItem header;
                listRowAdapter = new ArrayObjectAdapter(new CardPresenter(""));
                header = new HeaderItem("Search fail!!!");
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
            }
        });
    }
}
