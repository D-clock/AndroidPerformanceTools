package com.clock.performance.tools;

import android.app.Application;

import com.clock.performance.tools.block.BlockError;
import com.clock.performance.tools.block.BlockLooper;

/**
 * Created by Clock on 2017/5/16.
 */

public class AndroidPerformanceToolsApplication extends Application {

    private final static String TAG = AndroidPerformanceToolsApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        BlockLooper.initialize(new BlockLooper.Builder(this)
                .setFrequency(5000)//超过5s为卡顿
                .setIgnoreDebugger(true)
                .setReportAllThreadInfo(true)
                .setSaveLog(true)
                .setOnBlockListener(new BlockLooper.OnBlockListener() {
                    @Override
                    public void onBlock(BlockError blockError) {
                        blockError.printStackTrace();
                    }
                })
                .build());
    }
}
