package com.chauduong.longleg;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Utils {
    public static void setFullScreen(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) mContext).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static Bitmap getBitmapFromGallery(Context mContext, Uri path) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(path, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);

        Log.i("chauanh", "getBitmapFromGallery: " + options.outWidth + " " + options.outHeight);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, options.outWidth, options.outHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);

    }

    public static Bitmap getBitmapFromGallery(Context mContext, Uri path, int SCALE) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(path, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        int maxWidthHeight = Math.max(options.outWidth, options.outHeight);
        int scale;
        if (maxWidthHeight < 1000) {
            scale = 1;
        } else {
            scale = SCALE;
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, options.outWidth / scale, options.outHeight / scale);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, options);

    }

    public static Bitmap mergeBitmaps(Bitmap bitmapOne, Bitmap bitmapTwo) {

        // Calculate the size of the merged Bitmap
        int mergedImageWidth = bitmapOne.getWidth();
        int mergedImageHeight = bitmapOne.getHeight() + bitmapTwo.getHeight();

        // Create the return Bitmap (and Canvas to draw on)
        Bitmap mergedBitmap = Bitmap.createBitmap(mergedImageWidth, mergedImageHeight, bitmapOne.getConfig());
        Canvas mergedBitmapCanvas = new Canvas(mergedBitmap);

        // Draw the background image
        mergedBitmapCanvas.drawBitmap(bitmapOne, 0f, 0f, null);

        //Draw the foreground image
        mergedBitmapCanvas.drawBitmap(bitmapTwo, 0, bitmapOne.getHeight(), null);

        return mergedBitmap;

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
