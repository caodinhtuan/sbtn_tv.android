package com.sbtn.androidtv.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.CollectionUtil;

import java.util.HashSet;
import java.util.Set;


public class AmazonPurchasingListener implements PurchasingListener {

    public static final int ERROR_NO_USER = -100;
    public static final int ERROR_UNKNOWN = -90;
    public static final int ERROR_UNAVAILABLE_PACKAGE = -110;
    public static final int ERROR_PURCHASE_OK_BUT_UPDATE_SERVER_FAILT = -210;

    private static final String TAG = "AmazonPurchasingListener";

    private final AmazonIAPManager iapManager;

    public AmazonPurchasingListener(final AmazonIAPManager iapManager) {
        this.iapManager = iapManager;
    }

    @Override
    public void onUserDataResponse(final UserDataResponse response) {
        ALog.d(TAG, "onGetUserDataResponse: requestId (" + response.getRequestId()
                + ") userIdRequestStatus: "
                + response.getRequestStatus()
                + ")");

        final UserDataResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
            case SUCCESSFUL:
                ALog.d(TAG, "onUserDataResponse: get user id (" + response.getUserData().getUserId()
                        + ", marketplace ("
                        + response.getUserData().getMarketplace()
                        + ") ");

                iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
                break;

            case FAILED:
            case NOT_SUPPORTED:
                ALog.e(TAG, "onUserDataResponse failed, status code is " + status);

                iapManager.setAmazonUserId(null, null);
                break;
        }
    }


    @Override
    public void onProductDataResponse(final ProductDataResponse response) {
        final ProductDataResponse.RequestStatus status = response.getRequestStatus();
        ALog.d(TAG, "onProductDataResponse: RequestStatus (" + status + ")");

        switch (status) {
            case SUCCESSFUL:
                ALog.d(TAG, "onProductDataResponse: successful.  The item data map in this response includes the valid SKUs");
                final Set<String> unavailableSkus = response.getUnavailableSkus();


                if (CollectionUtil.isNotEmpty(unavailableSkus)) {
                    for (String unavailableSku : unavailableSkus) {
                        ALog.e(TAG, "onProductDataResponse _ unavailable skus : " + unavailableSku);
                    }
                    iapManager.handleError(ERROR_UNAVAILABLE_PACKAGE);
                    return;
                }

                iapManager.doPurchaseForSkus(response.getProductData());

                iapManager.enablePurchaseForSkus(response.getProductData());
                iapManager.disablePurchaseForSkus(response.getUnavailableSkus());
                break;
            case FAILED:
            case NOT_SUPPORTED:
                ALog.e(TAG, "onProductDataResponse: failed, should retry request");
                iapManager.handleError(ERROR_UNAVAILABLE_PACKAGE);
                break;
        }
    }


    @Override
    public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
        ALog.d(TAG, "onPurchaseUpdatesResponse: requestId (" + response.getRequestId()
                + ") purchaseUpdatesResponseStatus ("
                + response.getRequestStatus()
                + ") userId ("
                + response.getUserData().getUserId()
                + ")");

        final PurchaseUpdatesResponse.RequestStatus status = response.getRequestStatus();
//        switch (status) {
//            case SUCCESSFUL:
//                iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
//                for (final Receipt receipt : response.getReceipts()) {
//                    iapManager.handleReceipt(receipt, response.getUserData());
//                }
//                if (response.hasMore()) {
//                    PurchasingService.getPurchaseUpdates(false);
//                }
//                iapManager.handlePurchaseOK();
//                break;
//            case FAILED:
//            case NOT_SUPPORTED:
//                ALog.d(TAG, "onProductDataResponse: failed, should retry request");
//
//                iapManager.appendTextView(
//                        "onProductDataResponse: failed, should retry request"
//                );
//                iapManager.disableAllPurchases();
//                break;
//        }

    }


    @Override
    public void onPurchaseResponse(final PurchaseResponse response) {
        final String requestId = response.getRequestId().toString();
        final String userId = response.getUserData().getUserId();
        final PurchaseResponse.RequestStatus status = response.getRequestStatus();
        ALog.d(TAG, "onPurchaseResponse: requestId (" + requestId
                + ") userId ("
                + userId
                + ") purchaseRequestStatus ("
                + status
                + ")");

        switch (status) {
            case SUCCESSFUL:
                final Receipt receipt = response.getReceipt();
                iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
                ALog.d(TAG, "onPurchaseResponse: receipt json:" + receipt.toJSON());

                iapManager.handleReceipt(receipt, response.getUserData());
                iapManager.handlePurchaseOK(receipt.getReceiptId());
                break;
            case ALREADY_PURCHASED:
                ALog.d(TAG, "onPurchaseResponse: already purchased, should never get here for a consumable.");
                // This is not applicable for consumable item. It is only
                // application for entitlement and subscription.
                // check related samples for more details.

                break;
            case INVALID_SKU:
                ALog.e(TAG,
                        "onPurchaseResponse: invalid SKU!  onProductDataResponse should have disabled buy button already.");
                final Set<String> unavailableSkus = new HashSet<String>();
                unavailableSkus.add(response.getReceipt().getSku());
                iapManager.disablePurchaseForSkus(unavailableSkus);
                iapManager.handleError(ERROR_UNAVAILABLE_PACKAGE);
                break;
            case FAILED:
            case NOT_SUPPORTED:
                ALog.e(TAG, "onPurchaseResponse: failed so remove purchase request from local storage");
                iapManager.handleError(ERROR_UNKNOWN);
                break;
        }

    }

}
