package com.clock.performance.tools;

import android.app.Application;

import com.clock.performance.tools.anr.ANRLooper;

/**
 * Created by Clock on 2017/5/16.
 */

public class AndroidPerformanceToolsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ANRLooper.initialize(new ANRLooper.Builder(this)
                .setFrequency(2000)
                .setIgnoreDebugger(true)
                .build());
    }
}
