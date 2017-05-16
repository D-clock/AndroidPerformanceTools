package com.clock.performance.tools.anr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.clock.performance.tools.R;

public class ANRActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr);

        findViewById(R.id.btn_watchdog).setOnClickListener(this);
        findViewById(R.id.btn_file_observer).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_watchdog){

        } else if (viewId == R.id.btn_file_observer){

        }
    }
}
