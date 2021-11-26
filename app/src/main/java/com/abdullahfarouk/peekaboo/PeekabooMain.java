package com.abdullahfarouk.peekaboo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.divyanshu.colorseekbar.ColorSeekBar;

import pl.droidsonroids.gif.GifImageView;

public class PeekabooMain extends AppCompatActivity {

    public static int scaleOfDistance = 500;

    private Switch toggleOnOff;
    private Drawable[] imagesDrawableArr = new Drawable[5];
    private ConstraintLayout constraintLayout;
    private GifImageView gifImageView;
    private ImageView gifPauseIv;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 101;
    private ProgressBar spinner;
    private ColorSeekBar colorSeekBar;
    private int lastIndexForDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagesDrawableArr[0] = ContextCompat.getDrawable(this, R.drawable.closest_0);
        imagesDrawableArr[1] = ContextCompat.getDrawable(this, R.drawable.closest_1);
        imagesDrawableArr[2] = ContextCompat.getDrawable(this, R.drawable.closest_2);
        imagesDrawableArr[3] = ContextCompat.getDrawable(this, R.drawable.closest_3);
        imagesDrawableArr[4] = ContextCompat.getDrawable(this, R.drawable.closest_4);

        toggleOnOff = findViewById(R.id.toggleOnOff);
        gifImageView = findViewById(R.id.gifImageView);
        gifPauseIv = findViewById(R.id.gifPauseIv);
        constraintLayout = findViewById(R.id.CL);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        colorSeekBar = (ColorSeekBar) findViewById(R.id.seekBar2);
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {

                int newIndexFound = lastIndexForDistance;
                int[] colorsArray = getResources().getIntArray(R.array.colorArray);
                for (int index = 0; index < colorsArray.length; ++index) {
                    if (i == colorsArray[index]) {
                        newIndexFound = index;
                    }
                }

                if (newIndexFound <= 3) {
                    Glide.with(PeekabooMain.this).load(imagesDrawableArr[0]).into(gifPauseIv);
//                    gifPauseIv.setImageDrawable(imagesDrawableArr[0]);
                } else if (newIndexFound <= 6) {
                    Glide.with(PeekabooMain.this).load(imagesDrawableArr[1]).into(gifPauseIv);
//                    gifPauseIv.setImageDrawable(imagesDrawableArr[1]);
                } else if (newIndexFound <= 9) {
                    Glide.with(PeekabooMain.this).load(imagesDrawableArr[2]).into(gifPauseIv);
//                    gifPauseIv.setImageDrawable(imagesDrawableArr[2]);
                } else if (newIndexFound <= 11) {
                    Glide.with(PeekabooMain.this).load(imagesDrawableArr[3]).into(gifPauseIv);
//                    gifPauseIv.setImageDrawable(imagesDrawableArr[3]);
                } else if (newIndexFound <= 12) {
                    Glide.with(PeekabooMain.this).load(imagesDrawableArr[4]).into(gifPauseIv);
//                    gifPauseIv.setImageDrawable(imagesDrawableArr[4]);
                }


                if (lastIndexForDistance != newIndexFound) {
                    lastIndexForDistance = newIndexFound;
                    scaleOfDistance = lastIndexForDistance * 100;
                }


//
//
//                Log.d("theH","total length of colors array is "+colorsArray.length);
//
//                for(int temp = 0;temp<colorsArray.length;++temp){
//                    Log.d("theH","index is "+getResources().getColor(i)+" and comparing at
//                    "+colorsArray[temp]);
//                    if(getResources().getColor(i) == colorsArray[temp]){
//                        Log.d("theH","color found at index: "+temp);
//                    }
//                }
                Log.d("theH", "=====================================");
            }
        });


        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        checkPermission();

        if (isMyServiceRunning(ProximityService.class)) {
//            gifPauseIv.setVisibility(View.INVISIBLE);
//            gifImageView.setVisibility(View.VISIBLE);
            constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            SharedPreferences settings = getSharedPreferences("switch_state", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("switchkey", true);
            editor.commit();
        } else {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
//            gifPauseIv.setVisibility(View.VISIBLE);
//            gifImageView.setVisibility(View.INVISIBLE);
            SharedPreferences settings = getSharedPreferences("switch_state", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("switchkey", false);
            editor.commit();
        }

        SharedPreferences settings = getSharedPreferences("switch_state", 0);
        final boolean silent = settings.getBoolean("switchkey", false);
        toggleOnOff.setChecked(silent);

        toggleOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleOnOff.isChecked()) {
//                    gifPauseIv.setVisibility(View.INVISIBLE);
//                    gifImageView.setVisibility(View.VISIBLE);
                    startService(new Intent(getApplicationContext(), ProximityService.class));
                    gifImageView.setFreezesAnimation(false);
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                } else {
//                    gifPauseIv.setVisibility(View.VISIBLE);
//                    gifImageView.setVisibility(View.INVISIBLE);
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    stopService(new Intent(getApplicationContext(), ProximityService.class));
                    gifImageView.setFreezesAnimation(true);
                    spinner.setVisibility(View.VISIBLE);
                    closecamera();
                }
            }
        });

        toggleOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    gifPauseIv.setVisibility(View.INVISIBLE);
//                    gifImageView.setVisibility(View.VISIBLE);
                } else {
//                    gifPauseIv.setVisibility(View.VISIBLE);
//                    gifImageView.setVisibility(View.INVISIBLE);
                }
                SharedPreferences settings = getSharedPreferences("switch_state", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
            }
        });
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toggleOnOff.isChecked()) {

        } else {

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
        }

//        else
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }
        }
    }

    private void closecamera() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager activityManager =
                        (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(getPackageName());
                spinner.setVisibility(View.GONE);
                finishAndRemoveTask();
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
        }, 2000);

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

}
