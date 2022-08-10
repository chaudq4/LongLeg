package com.chauduong.longleg;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.chauduong.longleg.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener, DialogManager.DialogListener {
    public static final int SCALE = 5;
    private static final int SELECT_GALLERY_IMAGE = 1;
    private static final String TAG = "chauanh";
    private FrameLayout btnSave, btnReset, btnOpen, btnFlip;
    private ImageData mImageData;
    private ActivityMainBinding mActivityMainBinding;
    private SeekBar sbValue;
    private RelativeLayout lineOne, lineTwo;
    private int heightBitmap, yLineOne, yLineTwo;
    private DialogManager mDialogManager;
    private int valueTemp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initView();
        initListener();
        initData();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initData() {
        mImageData = new ImageData(this);
        mActivityMainBinding.setMImageData(mImageData);
        mDialogManager = new DialogManager(this, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        btnOpen.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        sbValue.setOnSeekBarChangeListener(this);
        lineOne.setOnTouchListener(this);
        lineTwo.setOnTouchListener(this);
        btnFlip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mImageData.getBmpOriginal() == null) return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        valueTemp = sbValue.getProgress();
                        sbValue.setProgress(0);
                        break;
                    case MotionEvent.ACTION_UP:
                        sbValue.setProgress(valueTemp);
                        break;
                }
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                sbValue.setOnSeekBarChangeListener(null);
                mImageData.setBmpPreview(null);
                sbValue.setProgress(0);
                sbValue.setOnSeekBarChangeListener(this);
                mImageData.setFilePath(data.getData());
                Bitmap decodeBitmapOriginal = Utils.getBitmapFromGallery(this, data.getData(), SCALE);
                Bitmap bmpFullOriginal = Utils.getBitmapFromGallery(this, data.getData());
                mImageData.setBmpOriginal(decodeBitmapOriginal);
                mImageData.setBmpFullOriginal(bmpFullOriginal);
                updateParamsLine();
            }
        }
    }

    private void updateParamsLine() {
        mActivityMainBinding.imgOriginal.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams lp = mActivityMainBinding.viewOne.getLayoutParams();
                lp.height = getResources().getDimensionPixelSize(R.dimen.line_height);
                lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                ;
                mActivityMainBinding.viewOne.setLayoutParams(lp);
                mActivityMainBinding.lineOne.setGravity(Gravity.CENTER_HORIZONTAL);
                lp = mActivityMainBinding.viewTwo.getLayoutParams();
                lp.height = getResources().getDimensionPixelSize(R.dimen.line_height);
                lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                mActivityMainBinding.viewTwo.setLayoutParams(lp);
                mActivityMainBinding.lineTwo.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }, 100);

    }

    private void initView() {
        btnOpen = mActivityMainBinding.btnOpen;
        btnReset = mActivityMainBinding.btnReset;
        btnSave = mActivityMainBinding.btnSave;
        sbValue = mActivityMainBinding.sbValue;
        lineOne = mActivityMainBinding.lineOne;
        lineTwo = mActivityMainBinding.lineTwo;
        btnFlip = mActivityMainBinding.btnFlip;
        mActivityMainBinding.cardView.setBackgroundResource(R.drawable.cardview_bg);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpen:
                openImageFromGallery();
                break;
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnSave:
                mImageData.save(longLeg(mImageData.getBmpFullOriginal(), sbValue.getProgress()));
                break;
            default:
                break;
        }
    }

    private void reset() {
        sbValue.setProgress(0);
    }

    public void openImageFromGallery() {
        Dexter.withActivity((Activity) this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                        } else {
                            mDialogManager.show(DialogManager.PERMISSION);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap finalBitmap = longLeg(mImageData.getBmpOriginal(), progress);
                mImageData.setBmpPreview(finalBitmap);
            }
        }).start();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private Bitmap longLeg(Bitmap bmpOriginal, int progress) {
        heightBitmap = mActivityMainBinding.imgOriginal.getHeight();
        yLineOne = (int) mActivityMainBinding.lineOne.getY();
        yLineTwo = (int) mActivityMainBinding.lineTwo.getY();
        if (bmpOriginal == null) return null;
        int width = bmpOriginal.getWidth();
        int height = bmpOriginal.getHeight();
        int fragmentOne = (yLineOne * height) / heightBitmap;
        int fragmentTwo = (yLineTwo * height) / heightBitmap;
        Bitmap bmpOne = Bitmap.createBitmap(bmpOriginal, 0, 0, width, fragmentOne);
        Bitmap bmpTwo = Bitmap.createBitmap(bmpOriginal, 0, fragmentOne, width, fragmentTwo - fragmentOne);
        Bitmap bmpThree = Bitmap.createBitmap(bmpOriginal, 0, fragmentTwo, width, height - fragmentTwo);
        Bitmap bmpScale = Bitmap.createScaledBitmap(bmpTwo, width, (int) (bmpTwo.getHeight() * (1 + 0.01 * progress)), false);
        Bitmap bmpMergeOneTwo = Utils.mergeBitmaps(bmpOne, bmpScale);
        Bitmap bmpFinal = Utils.mergeBitmaps(bmpMergeOneTwo, bmpThree);
        bmpOne.recycle();
        bmpTwo.recycle();
        bmpThree.recycle();
        bmpMergeOneTwo.recycle();
        bmpScale.recycle();
        return bmpFinal;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int thresholdSpace = 15;
        float yDown = 0, moveY;
        float distanceY;
        if ((v.getId() == R.id.lineOne || v.getId() == R.id.lineTwo) && sbValue.getProgress() != 0) {
            mDialogManager.show(DialogManager.LOCK_STATE);
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                yDown = event.getY();
                if (v.getId() == R.id.lineOne) {
                    ViewGroup.LayoutParams lp = mActivityMainBinding.viewOne.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.line_height_press);
                    lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                    mActivityMainBinding.viewOne.setLayoutParams(lp);
                    mActivityMainBinding.lineOne.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                if (v.getId() == R.id.lineTwo) {
                    ViewGroup.LayoutParams lp = mActivityMainBinding.viewTwo.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.line_height_press);
                    lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                    mActivityMainBinding.viewTwo.setLayoutParams(lp);
                    mActivityMainBinding.lineTwo.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getY();
                distanceY = moveY - yDown;
                float newY = v.getY() + distanceY;
                if (v.getId() == R.id.lineOne) {
                    float yTwo = lineTwo.getY();
                    if ((v.getY() + distanceY) < yTwo - thresholdSpace) {
                        v.setY(newY);
                        yDown = moveY;
                    }
                }
                if (v.getId() == R.id.lineTwo) {
                    float yOne = lineOne.getY();
                    if ((v.getY() + distanceY) > yOne + thresholdSpace) {
                        v.setY(newY);
                        yDown = moveY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.lineOne) {
                    ViewGroup.LayoutParams lp = mActivityMainBinding.viewOne.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.line_height);
                    lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                    mActivityMainBinding.viewOne.setForegroundGravity(Gravity.CENTER);
                    mActivityMainBinding.viewOne.setLayoutParams(lp);
                    mActivityMainBinding.lineOne.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                if (v.getId() == R.id.lineTwo) {
                    ViewGroup.LayoutParams lp = mActivityMainBinding.viewTwo.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.line_height);
                    lp.width = mActivityMainBinding.imgOriginal.getDrawable().getIntrinsicWidth();
                    mActivityMainBinding.viewTwo.setLayoutParams(lp);
                    mActivityMainBinding.lineTwo.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                break;
        }
        return false;
    }

    @Override
    public void onOKResetDialogClick() {
        reset();
    }

    @Override
    public void onCancelResetDialogClick() {

    }
}
