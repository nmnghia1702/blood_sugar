package com.diabetes.bloodsugar.alarm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.diabetes.bloodsugar.R;


public class AlertDialogPermissionReason extends DialogFragment {

    private DialogListener listener;


    public static AlertDialogPermissionReason getInstance(String message) {
        Bundle args = new Bundle();
        args.putString("message", message);
        AlertDialogPermissionReason frag = new AlertDialogPermissionReason();
        frag.setArguments(args);
        return frag;
    }

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);

        void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogListener) {
            listener = (DialogListener) context;
        } else {
            throw new ClassCastException(context.getClass() + " must implement AlertDialogPermissionReason.DialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(requireArguments().getString("message"))
                .setPositiveButton(getResources().getString(R.string.cancelDialog_positive), (dialogInterface, i)
                        -> listener.onDialogPositiveClick(AlertDialogPermissionReason.this))
                .setNegativeButton(getResources().getString(R.string.cancelDialog_negative), (dialogInterface, i) -> {
                    listener.onDialogNegativeClick(AlertDialogPermissionReason.this);
                    dismiss();
                })
                .setCancelable(false);

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
