package com.clock.performance.tools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.clock.performance.tools.anr.ANRSamplesActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_anr_samples).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_anr_samples) {
            Intent anrSamplesIntent = new Intent(this, ANRSamplesActivity.class);
            startActivity(anrSamplesIntent);
        }
    }
}
