package com.sbtn.androidtv.volleys;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by linhnguyen on 12/3/15.
 */
public class Requestor {
    public static JSONObject requestDataJson(RequestQueue requestQueue, String url) {
        JSONObject response = null;
        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, requestFuture, requestFuture);
        requestQueue.add(request);
        try {
            response = requestFuture.get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            L.e(e + "");
        } catch (ExecutionException e) {
            L.e(e + "");
        } catch (TimeoutException e) {
            L.e(e + "");
        }
        return response;
    }

}
