package com.chauduong.longleg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.chauduong.longleg.databinding.DialogWarningLockstateBinding;

public class DialogManager {
    public static final int PERMISSION = 1;
    public static final int SAVE_SUCCESSFUL = 2;
    public static final int LOCK_STATE = 3;
    private Context mContext;
    Dialog mCurrentDialog;
    private DialogListener mListener;

    public DialogManager(Context mContext, DialogListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }


    public void show(int TYPE) {
        switch (TYPE) {
            case PERMISSION:
                showPermissionDialog();
                break;
            case SAVE_SUCCESSFUL:
                showSuccesfull();
                break;
            case LOCK_STATE:
                showLockState();
                break;
            default:
                break;
        }
    }

    private void showLockState() {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            return;
        }
        DialogWarningLockstateBinding mDialogWarningLockstateBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_warning_lockstate, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialogWarningLockstateBinding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOKResetDialogClick();
                mCurrentDialog.dismiss();

            }
        });
        mDialogWarningLockstateBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentDialog.dismiss();
            }
        });
        builder.setView(mDialogWarningLockstateBinding.getRoot());
        builder.setCancelable(false);
        mCurrentDialog = builder.create();
        mCurrentDialog.show();
        mCurrentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void showSuccesfull() {
        Toast.makeText(mContext, mContext.getString(R.string.successful), Toast.LENGTH_SHORT).show();
    }

    private void showPermissionDialog() {
        Toast.makeText(mContext, mContext.getString(R.string.permisstion), Toast.LENGTH_SHORT).show();
    }

    interface DialogListener {
        void onOKResetDialogClick();

        void onCancelResetDialogClick();
    }
}
