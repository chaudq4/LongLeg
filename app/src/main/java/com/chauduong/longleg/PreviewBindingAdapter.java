package com.chauduong.longleg;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

public class PreviewBindingAdapter {
    @BindingAdapter("bind:setImageBitmap")
    public static void setImageBitmap(ImageView mImageView, Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    @BindingAdapter("setVisibility")
    public static void setVisibility(View view, Bitmap bitmap) {
        if (bitmap != null) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("setEditTextSize")
    public static void setEditTextSize(EditText view, int widthOrHeight) {
        view.setText(String.valueOf(widthOrHeight));
    }

}
