package com.sbtn.androidtv.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.RequestId;
import com.google.gson.JsonSyntaxException;
import com.sbtn.androidtv.MyApplication;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.amazon.AmazonIAPManager;
import com.sbtn.androidtv.amazon.AmazonPurchasingListener;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.security.SBDeviceUtils;
import com.sbtn.androidtv.utils.ALog;
import com.sbtn.androidtv.utils.MyDialog;
import com.sbtn.androidtv.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hoanguyen on 5/31/16.
 */
public class AmazonPurchaseActivity extends BaseActivity {
    private static final String TAG = "AmazonPurchaseActivity";
    public static final String EXTRA_SBTN_PACKAGE = "EXTRA_SBTN_PACKAGE";
    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    private AmazonIAPManager amazonIAPManager;
    private SBTNPackage mSBTNPackage;
    private int mItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        setContentView(view);
        //Lấy data SBTN package
        getData();

        setupIAPOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Set<String> setProduct = new HashSet<>();
        setProduct.add(mSBTNPackage.getProduct_id());
        PurchasingService.getProductData(setProduct);
    }

    /**
     * Calls {@link PurchasingService#getUserData()} to get current Amazon
     * user's data and {@link PurchasingService#getPurchaseUpdates(boolean)} to
     * get recent purchase updates
     */
    @Override
    protected void onResume() {
        super.onResume();
        amazonIAPManager.activate();
        PurchasingService.getUserData();
        PurchasingService.getPurchaseUpdates(false);
    }

    /**
     * Deactivate Sample IAP manager on main activity's Pause event
     */
    @Override
    protected void onPause() {
        super.onPause();
        amazonIAPManager.deactivate();
    }

    private void getData() {
        if (!SBDeviceUtils.isAmazonDevice()) {
            MyDialog.showDialogConfirm(this, getString(R.string.warning), "Device don't be supported!!! Just Amazon devices.", null, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
        }

        Intent intent = getIntent();
        if (intent == null) {
            ALog.showToast(this, TAG, "Intent = null");
            finish();
            return;
        }
        String dataPut = intent.getStringExtra(EXTRA_SBTN_PACKAGE);

        mItemId = intent.getIntExtra(EXTRA_ITEM_ID, -1);
//        if (StringUtils.isEmpty(dataPut) || mItemId < 0) {
        if (StringUtils.isEmpty(dataPut)) {
            ALog.showToast(this, TAG, "EXTRA_SBTN_PACKAGE = null || invalid mItemId!");
            finish();
            return;
        }

        try {
            mSBTNPackage = MyApplication.getGson().fromJson(dataPut, SBTNPackage.class);
        } catch (JsonSyntaxException e) {
            ALog.showToast(this, TAG, "JsonSyntaxException error");
            finish();
        }
    }

    /**
     * Setup for IAP SDK called from onCreate. Sets up {@link AmazonIAPManager}
     * to handle InAppPurchasing logic and {@link AmazonPurchasingListener} for
     * listening to IAP API callbacks
     */
    private void setupIAPOnCreate() {
        amazonIAPManager = new AmazonIAPManager(this);
        amazonIAPManager.activate();
        final AmazonPurchasingListener purchasingListener = new AmazonPurchasingListener(amazonIAPManager);
        PurchasingService.registerListener(this.getApplicationContext(), purchasingListener);
    }

    /**
     * Click handler invoked when user clicks button to buy an orange
     * consumable. This method calls {@link PurchasingService#purchase(String)}
     * with the SKU to initialize the purchase from Amazon Appstore
     */
    private void onDoPurchase() {
        //FIXME
        final RequestId requestId = PurchasingService.purchase(mSBTNPackage.getProduct_id());
    }

    public void dataOKDoPurchase() {
        onDoPurchase();
    }

    public void handlePurchaseOK() {
        ALog.i(TAG, "Purchase Ok!! - " + mSBTNPackage.toString());
//        showMessage("Purchase Ok!! - " + mSBTNPackage.toString());
        setResult(RESULT_OK);
        finish();
    }

    public void showMessage(final String message) {
        ALog.showToast(this, TAG, message);
    }

    public void handlePurchaseError(int type) {
        switch (type) {
            case AmazonPurchasingListener.ERROR_NO_USER:
                showMessage("Error!!! No User Login");
                break;

            case AmazonPurchasingListener.ERROR_UNAVAILABLE_PACKAGE:
                showMessage("Error!!! Package's unavailable!");
                break;

            case AmazonPurchasingListener.ERROR_PURCHASE_OK_BUT_UPDATE_SERVER_FAILT:
                showMessage("Purchase Successfully!!! But Fail to update my server! Please, contact admin to supported!");
                break;

            case AmazonPurchasingListener.ERROR_UNKNOWN:
            default:
                showMessage("Error Purchase!!!");
        }
        ALog.i(TAG, "Purchase Fail!! - " + mSBTNPackage.toString());
        finish();
    }

    public SBTNPackage getSBTNPackage() {
        return mSBTNPackage;
    }

    public int getItemId() {
        return mItemId;
    }
}
