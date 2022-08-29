package com.chauduong.longleg;


import static com.chauduong.longleg.DialogManager.DISMISS_RESIZE_DIALOG;
import static com.chauduong.longleg.DialogManager.RESIZE_JPG;
import static com.chauduong.longleg.DialogManager.RESIZE_PNG;
import static com.chauduong.longleg.DialogManager.SAVE_SUCCESSFUL;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.io.FileOutputStream;

public class ImageData extends BaseObservable {
    private static final int WIDTH_FACEBOOK = 2048;
    private Uri filePath;
    private Bitmap bmpFullOriginal;
    private Bitmap bmpOriginal;
    private Bitmap bmpPreview;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int width, height;
    private ImageDataListener mListener;
    private int widthRecommend, heightRecommend;
    private Bitmap bitmapPreviewTempFullSize;

    public void setBitmapPreviewTempFullSize(Bitmap bitmapPreviewTempFullSize) {
        if (this.bitmapPreviewTempFullSize != null) this.bitmapPreviewTempFullSize.recycle();
        this.bitmapPreviewTempFullSize = bitmapPreviewTempFullSize;
        updateWidthHeightRecommend();
    }

    @Bindable
    public int getWidthRecommend() {
        return widthRecommend;
    }

    public void setWidthRecommend(int widthRecommend) {
        this.widthRecommend = widthRecommend;
        notifyPropertyChanged(BR.widthRecommend);
    }

    @Bindable
    public int getHeightRecommend() {
        return heightRecommend;
    }

    public void setHeightRecommend(int heightRecommend) {
        this.heightRecommend = heightRecommend;
        notifyPropertyChanged(BR.heightRecommend);
    }

    public void updateWidthHeightRecommend() {
        if (bitmapPreviewTempFullSize != null) {
            if (bitmapPreviewTempFullSize.getWidth() < bitmapPreviewTempFullSize.getHeight()) {
                heightRecommend = WIDTH_FACEBOOK;
                widthRecommend = (bitmapPreviewTempFullSize.getWidth() * heightRecommend) / bitmapPreviewTempFullSize.getHeight();
            } else {
                widthRecommend = WIDTH_FACEBOOK;
                heightRecommend = (bitmapPreviewTempFullSize.getHeight() * widthRecommend) / bitmapPreviewTempFullSize.getWidth();
            }
            notifyPropertyChanged(BR.heightRecommend);
            notifyPropertyChanged(BR.widthRecommend);
        }
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBmpFullOriginal() {
        return bmpFullOriginal;
    }

    public void setBmpFullOriginal(Bitmap bmpFullOriginal) {
        this.bmpFullOriginal = bmpFullOriginal;
    }

    public ImageData(Context mContext, ImageDataListener imageDataListener) {
        this.mContext = mContext;
        this.mListener = imageDataListener;
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

    public void save(Bitmap finalBitmap, int TYPE) {
        SaveTask saveTask = new SaveTask(mContext, finalBitmap, TYPE, mListener);
        saveTask.execute();
    }

    public void resizeBitmapAndSave(int TYPE, int newWidth, int newHeight) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmapPreviewTempFullSize, newWidth, newHeight, false);
                new Handler(Looper.getMainLooper()).post(() -> {
                    save(resizedBitmap, TYPE);
                });
            }
        }).start();
    }


    public interface ImageDataListener {
        void onShowDialog(int TYPE);

    }
}
