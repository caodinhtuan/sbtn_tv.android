package com.sbtn.androidtv.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sbtn.androidtv.R;
import com.sbtn.androidtv.fragment.SearchFragment;

/**
 * Created by hoanguyen on 6/9/16.
 */
public class SearchActivity extends BaseActivity {

    private EditText edtSearch;
    private View btSearch;
    private SearchFragment searchFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchFragment = SearchFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.frame_search_browse, searchFragment, SearchFragment.TAG).commit();
        setupView();
    }

    private void setupView() {
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                    search();
                }
                return false;
            }
        });
        edtSearch.requestFocus();

        btSearch = findViewById(R.id.buttonSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        String keySearch = edtSearch.getText().toString();
        if (searchFragment != null) {
            searchFragment.loadSearchData(keySearch);
        }
    }

    public void searchFocus() {
        edtSearch.requestFocus();
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
}
