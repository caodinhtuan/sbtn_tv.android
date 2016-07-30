package com.sbtn.androidtv.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.callbacks.DataJsonLoadedListener;
import com.sbtn.androidtv.volleys.Requestor;
import com.sbtn.androidtv.volleys.VolleySingleton;

import org.json.JSONObject;


/**
 * Created by linhnguyen on 12/26/15.
 */
public class DataJsonLoadedTask extends AsyncTask<Void, Void, JSONObject> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataJsonLoadedListener dataJsonLoadedListener;
    private String url;

    private boolean enableDialogLoading;
    private ProgressDialog mProgressDialog;
    private Context mContext;

    public DataJsonLoadedTask(Context pContext, DataJsonLoadedListener dataJsonLoadedListener, String url) {
        this.dataJsonLoadedListener = dataJsonLoadedListener;
        volleySingleton = VolleySingleton.getsInstance(pContext);
        this.requestQueue = volleySingleton.getmRequestQueue();
        this.url = url;
        mContext = pContext;
    }

    public DataJsonLoadedTask enableDialogLoading() {
        enableDialogLoading = true;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (enableDialogLoading) {
            showDialogLoading();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return Requestor.requestDataJson(requestQueue, url);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if (enableDialogLoading) {
            dismissDialogLoading();
        }
        if (dataJsonLoadedListener != null) {
            dataJsonLoadedListener.onDataJsonLoaded(jsonObject);
        }
    }

    private void showDialogLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
            mProgressDialog.setCancelable(false);
        }

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void dismissDialogLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
