package com.diabetes.bloodsugar.action;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SPRDialog {

    public static void showDialogViewAddType(Activity activity, final Listener.OnDialogYesNoListener listener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_view_add_type);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onYesClick();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onNoClick();
        });

        dialog.show();
    }

    public static void showDialogAdd(Activity activity, final Listener.OnDialogAddCoverListener listener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_check_add);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btnAdd = dialog.findViewById(R.id.btnAdd);
        TextView btnCover = dialog.findViewById(R.id.btnCover);

        btnAdd.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onAddClick();
        });

        btnCover.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onCoverClick();
        });

        dialog.show();
    }

    public static SweetAlertDialog showDialogYesNo(Activity context, String title, String yes, String no, Listener.OnDialogYesNoListener listener) {
        if (context.isFinishing()) return null;
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setCancelButton(no, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                listener.onNoClick();
            }
        });
        pDialog.setConfirmButton(yes, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
                listener.onYesClick();
            }
        });
        pDialog.setTitleText(title);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }
}
