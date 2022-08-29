package com.chauduong.longleg;

import static com.chauduong.longleg.DialogManager.DISMISS_RESIZE_DIALOG;
import static com.chauduong.longleg.DialogManager.RESIZE_JPG;
import static com.chauduong.longleg.DialogManager.RESIZE_PNG;
import static com.chauduong.longleg.DialogManager.SAVE_SUCCESSFUL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.chauduong.longleg.databinding.CustomProgressDialogBinding;

import java.io.File;
import java.io.FileOutputStream;

public class SaveTask extends AsyncTask<String, Integer, String> {
    private Context mContext;
    private Bitmap finalBitmap;
    private int TYPE;
    CustomProgressDialogBinding customProgressDialogBinding;
    Dialog progressDialog;
    ImageData.ImageDataListener mListener;

    public SaveTask(Context mContext, Bitmap finalBitmap, int TYPE, ImageData.ImageDataListener mListener) {
        this.mContext = mContext;
        this.finalBitmap = finalBitmap;
        this.TYPE = TYPE;
        this.mListener = mListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customProgressDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.custom_progress_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(customProgressDialogBinding.getRoot());
        customProgressDialogBinding.progressBar.setProgress(0);
        customProgressDialogBinding.textView.setText(0 + "%");
        progressDialog = builder.create();
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected String doInBackground(String... strings) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/" + mContext.getString(R.string.app_name));
        myDir.mkdirs();
        publishProgress(5);
        int n = SharedPreferencesManager.getInstance(mContext).getIndex();
        String fileName = null;
        Bitmap.CompressFormat compressFormat = null;
        publishProgress(15);
        if (TYPE == RESIZE_JPG) {
            fileName = "GOM_" + n + ".jpg";
            compressFormat = Bitmap.CompressFormat.JPEG;
        }
        publishProgress(30);
        if (TYPE == RESIZE_PNG) {
            fileName = "GOM_" + n + ".png";
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        File file = new File(myDir, fileName);
        publishProgress(45);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            publishProgress(60);
            finalBitmap.compress(compressFormat, 100, out);
            publishProgress(70);
            out.flush();
            publishProgress(80);
            out.close();
            publishProgress(90);
            publishProgress(100);
            notifyGalleryNewImage(file);
            mListener.onShowDialog(SAVE_SUCCESSFUL);
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        customProgressDialogBinding.progressBar.setProgress(values[0]);
        customProgressDialogBinding.textView.setText(values[0] + "%");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        mListener.onShowDialog(DISMISS_RESIZE_DIALOG);

    }

    public void notifyGalleryNewImage(File file) {
        MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
}
