package com.sbtn.androidtv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by hoanguyen on 5/20/16.
 */
public class WebViewActivity extends BaseActivity {
    public static final String EXTRA_URL = "EXTRA_URL";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent == null) {
            finish();
            Toast.makeText(WebViewActivity.this, "Url = null", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = intent.getStringExtra(EXTRA_URL);

        if (TextUtils.isEmpty("url")) {
            finish();
            Toast.makeText(WebViewActivity.this, "Url empty", Toast.LENGTH_SHORT).show();
            return;
        }

        webView = new WebView(this.getApplicationContext());
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;

    }
}
