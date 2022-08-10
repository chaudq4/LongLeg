package com.chauduong.longleg;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class PreviewBindingAdapter {
    @BindingAdapter("bind:setImageBitmap")
    public static void setImageBitmap(ImageView mImageView, Bitmap bitmap) {
        if (mImageView != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    @BindingAdapter("setVisibility")
    public static void setVisibility(View view, Bitmap bitmap) {
        Log.i("chauanh", "setVisibility: ");
        if (bitmap != null && view != null) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("updateParams")
    public static void updateParams(View view, Bitmap bitmap) {
        view.requestLayout();
    }
}
