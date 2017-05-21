package com.clock.performance.tools;

import android.app.Application;

import com.clock.performance.tools.anr.ANRError;
import com.clock.performance.tools.anr.ANRLooper;

/**
 * Created by Clock on 2017/5/16.
 */

public class AndroidPerformanceToolsApplication extends Application {

    private final static String TAG = AndroidPerformanceToolsApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        ANRLooper.initialize(new ANRLooper.Builder(this)
                .setFrequency(2000)
                .setIgnoreDebugger(true)
                .setReportAllThreadInfo(true)
                .setAnrLogSaveToSdCard(true)
                .setOnNoRespondingListener(new ANRLooper.OnNoRespondingListener() {
                    @Override
                    public void onNoResponding(ANRError anrError) {
                        anrError.printStackTrace();
                    }
                })
                .build());
    }
}
