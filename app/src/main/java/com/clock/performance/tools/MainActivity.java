package com.clock.performance.tools;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.clock.performance.tools.block.BlockSamplesActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int NEED_PERMISSION_REQUEST_CODE = 1000;
    private final static String[] NEED_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRuntimePermission();
        }
        findViewById(R.id.btn_anr_samples).setOnClickListener(this);
    }


    private void requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage("为了应用能够正常使用，现在需要申请相应权限")
                        .setNeutralButton("一键申请", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, NEED_PERMISSIONS, NEED_PERMISSION_REQUEST_CODE);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, NEED_PERMISSIONS, NEED_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_anr_samples) {
            Intent anrSamplesIntent = new Intent(this, BlockSamplesActivity.class);
            startActivity(anrSamplesIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NEED_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            for (int index = 0; index < permissions.length; index++) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                }
            }
            if (!isAllGranted) {
                Toast.makeText(this, "部分权限未授予，可能导致应用无法正常使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
