package com.chauduong.longleg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.chauduong.longleg.databinding.CustomProgressDialogBinding;
import com.chauduong.longleg.databinding.DialogResizeBinding;
import com.chauduong.longleg.databinding.DialogWarningLockstateBinding;

import dmax.dialog.SpotsDialog;

public class DialogManager {
    public static final int PERMISSION = 1;
    public static final int SAVE_SUCCESSFUL = 2;
    public static final int LOCK_STATE = 3;
    public static final int DISMISS_RESIZE_DIALOG = 9;
    public static final int RESIZE = 6;
    public static final int RESIZE_PNG = 7;
    public static final int RESIZE_JPG = 8;
    private Context mContext;
    Dialog mCurrentDialog;
    private DialogListener mListener;
    private ImageData mImageData;
    private Dialog resizeDialog;

    public DialogManager(Context mContext, ImageData mImageData, DialogListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.mImageData = mImageData;
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
            case RESIZE:
                showResizeDialog();
                break;
            case DISMISS_RESIZE_DIALOG:
                dismissResizeDialog();
                break;
            default:
                break;
        }
    }

    private void showResizeDialog() {
        if (mImageData.getFilePath() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        DialogResizeBinding mDialogResizeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_resize, null, false);
        mImageData.updateWidthHeightRecommend();
        mDialogResizeBinding.setMImageData(mImageData);
        mDialogResizeBinding.btnOk.setOnClickListener(view -> {
            int width = Integer.parseInt(mDialogResizeBinding.edtWidth.getText().toString());
            int height = Integer.parseInt(mDialogResizeBinding.edtHeight.getText().toString());
            if (width == 0 || height == 0) return;
            if (mDialogResizeBinding.cbJPG.isChecked()) {
                mListener.onOKResize(RESIZE_JPG, width, height);
            } else if (mDialogResizeBinding.cbPNG.isChecked()) {
                mListener.onOKResize(RESIZE_PNG, width, height);

            }


        });
        mDialogResizeBinding.btnCancel.setOnClickListener(view -> {
            resizeDialog.dismiss();
        });
        mDialogResizeBinding.autoResize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mListener.onAutoCheckClick(compoundButton.isChecked());
                if (compoundButton.isChecked()) {
                    mDialogResizeBinding.edtHeight.setEnabled(false);
                    mDialogResizeBinding.edtWidth.setEnabled(false);
                } else {
                    mDialogResizeBinding.edtHeight.setEnabled(true);
                    mDialogResizeBinding.edtWidth.setEnabled(true);
                }
            }
        });
        builder.setView(mDialogResizeBinding.getRoot());
        resizeDialog = builder.create();
        resizeDialog.show();
        resizeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogResizeBinding.autoResize.setChecked(true);
        mDialogResizeBinding.edtWidth.setEnabled(false);
        mDialogResizeBinding.edtHeight.setEnabled(false);

    }

    private void dismissResizeDialog() {
        if (resizeDialog != null && resizeDialog.isShowing()) {
            resizeDialog.dismiss();
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

        void onOKResize(int type, int width, int height);

        void onAutoCheckClick(boolean isChecked);
    }
}
