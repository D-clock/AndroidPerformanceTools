package com.clock.performance.tools;

import android.app.Application;

import com.performance.tools.block.BlockError;
import com.performance.tools.block.BlockLooper;

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
                .setIgnoreDebugger(true)//是否忽略调试引起的卡顿
                .setReportAllThreadInfo(true) //是否打印出卡顿发生时所有线程的堆栈信息
                .setSaveLog(true)//是否保存到log信息到文件
                .setOnBlockListener(new BlockLooper.OnBlockListener() {//当卡顿发生时的回调接口，回调过程发生在异步线程中
                    @Override
                    public void onBlock(BlockError blockError) {
                        blockError.printStackTrace();
                    }
                })
                .build());
    }
}
