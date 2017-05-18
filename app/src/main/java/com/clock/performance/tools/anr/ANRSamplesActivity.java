package com.clock.performance.tools.anr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.clock.performance.tools.R;

public class ANRSamplesActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_samples);

        findViewById(R.id.btn_anr_looper).setOnClickListener(this);
        findViewById(R.id.btn_file_observer).setOnClickListener(this);
        findViewById(R.id.btn_ui_thread_create_anr).setOnClickListener(this);
        findViewById(R.id.btn_worker_thread_create_anr).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_anr_looper){
            ANRLooper.getANRLooper().start();

        } else if (viewId == R.id.btn_file_observer){

        } else if (viewId == R.id.btn_ui_thread_create_anr){

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (viewId == R.id.btn_worker_thread_create_anr){

        }
    }
}
