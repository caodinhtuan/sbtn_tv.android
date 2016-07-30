package com.sbtn.androidtv.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.DetailsFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.sbtn.androidtv.MyApplication;
import com.sbtn.androidtv.R;
import com.sbtn.androidtv.activity.AmazonPurchaseActivity;
import com.sbtn.androidtv.datamodels.SBTNPackage;
import com.sbtn.androidtv.request.RequestFramework;
import com.sbtn.androidtv.request.datacallback.DefaultDataCallBackRequest;
import com.sbtn.androidtv.utils.MyDialog;
import com.sbtn.androidtv.utils.StringUtils;
import com.sbtn.androidtv.utils.ValidateUtils;

/**
 * Created by hoanguyen on 6/8/16.
 */
public abstract class BasePackageLeanBackDetailFragment extends DetailsFragment {

    public static final int REQUEST_CODE_PURCHASE = 1010;
    private MaterialDialog dialogPackage;

    protected abstract void clearAndReloadData();

    protected MaterialDialog getDialogBuyPackage(final SBTNPackage mySBTNPackage) {
        dialogPackage = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                .customView(R.layout.dialog_buy_package, true)
                .title(getString(R.string.dialog_title_buy_package))
                .positiveText(R.string.package_buy)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getActivity(), AmazonPurchaseActivity.class);
                        intent.putExtra(AmazonPurchaseActivity.EXTRA_SBTN_PACKAGE, MyApplication.getGson().toJson(mySBTNPackage));
//                        intent.putExtra(AmazonPurchaseActivity.EXTRA_ITEM_ID, mIdDataDetailItem);
                        startActivityForResult(intent, REQUEST_CODE_PURCHASE);
                    }
                })
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build();
        View view = dialogPackage.getCustomView();
        if (view == null) return null;

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView duration = (TextView) view.findViewById(R.id.duration);

        price.setText(mySBTNPackage.getPrice());
        if (StringUtils.isNotEmpty(mySBTNPackage.getName())) {
            name.setText(getString(R.string.name_dot) + mySBTNPackage.getName());
        } else {
            name.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDescription())) {
            description.setText(getString(R.string.des_dot) + mySBTNPackage.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDuration())) {
            duration.setText(getString(R.string.du_dot) + mySBTNPackage.getDuration());
        } else {
            duration.setVisibility(View.GONE);
        }

        MDButton actionButton = dialogPackage.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();
        return dialogPackage;
    }


    protected MaterialDialog getDialogEnterCode(final SBTNPackage mySBTNPackage) {
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_enter_code_promotion, null);
        TextView name = (TextView) customView.findViewById(R.id.name);
        TextView price = (TextView) customView.findViewById(R.id.price);
        TextView description = (TextView) customView.findViewById(R.id.description);
        TextView duration = (TextView) customView.findViewById(R.id.duration);
        final View codeInvalid = customView.findViewById(R.id.code_invalid);
        final EditText enterCode = (EditText) customView.findViewById(R.id.enter_code);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                .customView(customView, true)
                .title(getString(R.string.dialog_title_enter_promotion))
                .positiveText(R.string.package_add_code)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                        if (enterCode != null && !ValidateUtils.validatePromationCode(enterCode.getText().toString())) {
                            if (codeInvalid != null)
                                codeInvalid.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.Shake).duration(200)
                                    .interpolate(new AccelerateDecelerateInterpolator())
                                    .playOn(enterCode);

                            YoYo.with(Techniques.Shake).duration(200)
                                    .interpolate(new AccelerateDecelerateInterpolator())
                                    .playOn(codeInvalid);
                        } else {
                            if (codeInvalid != null)
                                codeInvalid.setVisibility(View.INVISIBLE);

                            Toast.makeText(getActivity(), "Enter code - " + mySBTNPackage.getName(), Toast.LENGTH_SHORT).show();


                            RequestFramework.checkPromotionPayment(getActivity(), -1, mySBTNPackage.getId(), enterCode.getText().toString(),
                                    new RequestFramework.DataCallBack<DefaultDataCallBackRequest>() {
                                        @Override
                                        public void onResponse(DefaultDataCallBackRequest dataResult) {
                                            if (dataResult != null) {
                                                if (dataResult.isSuccess()) {
                                                    if (dialog.isShowing())
                                                        dialog.dismiss();
//                                                    enterCode = null;
//                                                    codeInvalid = null;
                                                    //re-setup adapter and refresh data
//                                                    mAdapter.clear();
//                                                    loadDetailItem();
                                                    clearAndReloadData();
                                                } else {
                                                    if (codeInvalid != null)
                                                        codeInvalid.setVisibility(View.VISIBLE);
                                                    YoYo.with(Techniques.Shake).duration(200)
                                                            .interpolate(new AccelerateDecelerateInterpolator())
                                                            .playOn(enterCode);

                                                    YoYo.with(Techniques.Shake).duration(200)
                                                            .interpolate(new AccelerateDecelerateInterpolator())
                                                            .playOn(codeInvalid);
                                                }
                                            } else {
                                                dialog.dismiss();
//                                                enterCode = null;
//                                                codeInvalid = null;
                                                MyDialog.showDialogServiceError(getActivity());
                                            }
                                        }

                                        @Override
                                        public void onFailure() {
                                            dialog.dismiss();
//                                            enterCode = null;
//                                            codeInvalid = null;
                                            MyDialog.showDialogServiceError(getActivity());
                                        }
                                    });
                        }
                    }
                })
                .cancelable(true)
                .autoDismiss(false)
                .canceledOnTouchOutside(true)
                .build();


        price.setText(mySBTNPackage.getPrice());

        enterCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_PREVIOUS ||
                        actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (codeInvalid != null)
                        codeInvalid.setVisibility(View.GONE);
                }
                return false;
            }
        });


        if (StringUtils.isNotEmpty(mySBTNPackage.getName())) {
            name.setText(getString(R.string.name_dot) + mySBTNPackage.getName());
        } else {
            name.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDescription())) {
            description.setText(getString(R.string.des_dot) + mySBTNPackage.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDuration())) {
            duration.setText(getString(R.string.package_duration) + mySBTNPackage.getDuration());
        } else {
            duration.setVisibility(View.GONE);
        }

        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        enterCode.setAllCaps(true);
        enterCode.requestFocus();
        return dialog;
    }


    protected MaterialDialog getDialogInfoPackage(final SBTNPackage mySBTNPackage) {
        dialogPackage = new MaterialDialog.Builder(getActivity()).backgroundColorRes(R.color.MDGray)
                .customView(R.layout.dialog_buy_package, true)
                .title(getString(R.string.dialog_title_buy_package))
                .positiveText(R.string.package_close)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build();
        View view = dialogPackage.getCustomView();
        if (view == null) return null;

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView duration = (TextView) view.findViewById(R.id.duration);
        TextView status = (TextView) view.findViewById(R.id.status);

        price.setText(mySBTNPackage.getPrice());
        if (StringUtils.isNotEmpty(mySBTNPackage.getName())) {
            name.setText(getString(R.string.name_dot) + mySBTNPackage.getName());
        } else {
            name.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDescription())) {
            description.setText(getString(R.string.des_dot) + mySBTNPackage.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(mySBTNPackage.getDuration())) {
            duration.setText(getString(R.string.du_dot) + mySBTNPackage.getDuration());
        } else {
            duration.setVisibility(View.GONE);
        }

        if (mySBTNPackage.isBuy()) {
            status.setVisibility(View.VISIBLE);
            status.setText(getString(R.string.du_status) + getString(R.string.purchased));
        }

        MDButton actionButton = dialogPackage.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();
        return dialogPackage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_PURCHASE:
                if (resultCode == Activity.RESULT_OK) {
                    if (dialogPackage != null && dialogPackage.isShowing())
                        dialogPackage.dismiss();

                    //re-setup adapter and refresh data
//                    mAdapter.clear();
//                    loadDetailItem();
                    clearAndReloadData();
                }
                break;
        }
    }
}
