package com.chauduong.longleg;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class ImageData extends BaseObservable implements DialogManager.DialogListener {
    private Uri filePath;
    private Bitmap bmpFullOriginal;
    private Bitmap bmpOriginal;
    private Bitmap bmpPreview;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DialogManager mDialogManager;

    public Bitmap getBmpFullOriginal() {
        return bmpFullOriginal;
    }

    public void setBmpFullOriginal(Bitmap bmpFullOriginal) {
        this.bmpFullOriginal = bmpFullOriginal;
    }

    public ImageData(Context mContext) {
        this.mContext = mContext;
        mDialogManager = new DialogManager(mContext, this);
    }

    @Bindable
    public Bitmap getBmpOriginal() {
        return bmpOriginal;
    }

    public void setBmpOriginal(Bitmap bmpOriginal) {
        this.bmpOriginal = bmpOriginal;
        notifyPropertyChanged(BR.bmpOriginal);
    }

    @Bindable
    public Bitmap getBmpPreview() {
        return bmpPreview;
    }

    public void setBmpPreview(Bitmap bmpPreview) {
        this.bmpPreview = bmpPreview;
        notifyPropertyChanged(BR.bmpPreview);
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public void save(Bitmap finalBitmap) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String root = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString();
                File myDir = new File(root + "/saved_images");
                myDir.mkdirs();
                Random generator = new Random();

                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDialogManager.show(DialogManager.SAVE_SUCCESSFUL);
                        }
                    });


                } catch (Exception e) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            mDialogManager.dissmissProgessDialog();
//                            mDialogManager.showToast(DialogManager.SAVE_ERROR);
                        }
                    });

                }
                notifyGalleryNewImage(file);
            }
        }).start();
    }

    public void notifyGalleryNewImage(File file) {
        MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    @Override
    public void onOKResetDialogClick() {

    }

    @Override
    public void onCancelResetDialogClick() {

    }
}
