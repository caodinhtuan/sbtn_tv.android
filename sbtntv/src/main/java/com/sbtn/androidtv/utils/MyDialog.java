package com.sbtn.androidtv.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.sbtn.androidtv.R;

/**
 * Created by hoanguyen on 6/6/16.
 */
public class MyDialog {

    public static void showDialogConfirm(Context context, String title, String msg, MaterialDialog.SingleButtonCallback buttonOKCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(msg)
                .positiveText(R.string.ok)
                .backgroundColorRes(R.color.MDGray);
        if (buttonOKCallback != null) {
            builder.onPositive(buttonOKCallback);
        }

        MaterialDialog dialog = builder.build();

        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();
        dialog.show();
    }

    public static void showDialogConfirm(Context context, String title, String msg, MaterialDialog.SingleButtonCallback buttonOKCallback, DialogInterface.OnDismissListener dismissListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(msg)
                .positiveText(R.string.ok)
                .backgroundColorRes(R.color.MDGray);
        if (buttonOKCallback != null) {
            builder.onPositive(buttonOKCallback);
        }

        MaterialDialog dialog = builder.build();


        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener);
        }
        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();

        dialog.show();
    }

    public static void showDialogServiceError(Context context) {
        showDialogConfirm(context, context.getResources().getString(R.string.error),
                context.getResources().getString(R.string.dialog_content_service_error), null);
    }

    public static void showDialogServiceError(Context context, DialogInterface.OnDismissListener dismissListener) {
        showDialogConfirm(context, context.getResources().getString(R.string.error),
                context.getResources().getString(R.string.dialog_content_service_error), null, dismissListener);
    }

    public static void showDialogPackageForContent(Context context, MaterialDialog.SingleButtonCallback buttonOKCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.dialog_title_warning)
                .content(R.string.dialog_msg_package_for_content)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .backgroundColorRes(R.color.MDGray);
        if (buttonOKCallback != null) {
            builder.onPositive(buttonOKCallback);
        }

        MaterialDialog dialog = builder.build();

        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();

        actionButton = dialog.getActionButton(DialogAction.NEGATIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);

        dialog.show();
    }

    public static void showDialogPackageForMaxDevice(Context context, MaterialDialog.SingleButtonCallback buttonOKCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.dialog_title_warning)
                .content(R.string.dialog_msg_package_for_max_device)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .backgroundColorRes(R.color.MDGray);
        if (buttonOKCallback != null) {
            builder.onPositive(buttonOKCallback);
        }

        MaterialDialog dialog = builder.build();

        MDButton actionButton = dialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);
        actionButton.requestFocus();

        actionButton = dialog.getActionButton(DialogAction.NEGATIVE);
        actionButton.setBackgroundResource(R.drawable.statelist_button);

        dialog.show();
    }
}
