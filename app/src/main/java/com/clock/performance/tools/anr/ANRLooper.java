package com.clock.performance.tools.anr;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * ANRLooper
 * <p>
 * 使用线程轮询的方式监听App是否产生ANR
 * <p>
 * Created by Clock 2017/5/16.
 */

public class ANRLooper implements Runnable {

    private final static String TAG = ANRLooper.class.getSimpleName();
    private final static String ANR_LOOPER_THREAD_NAME = "anr-looper-thread";
    /**
     * 最小的轮询频率
     */
    private final static long MIN_FREQUENCY = 5000;

    private static ANRLooper sLooper;
    /**
     * Application的Context
     */
    private Context appContext;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private volatile int tickCounter = 0;
    private Runnable ticker = new Runnable() {
        @Override
        public void run() {
            tickCounter = (tickCounter + 1) % Integer.MAX_VALUE;
        }
    };
    /**
     * 轮询的时间频率（单位：s）
     */
    private long frequency;
    /**
     * 是否忽略debug产生的ANR
     */
    private boolean ignoreDebugger;
    /**
     * 发生ANR时是否上报所有线程的ANR信息
     */
    private boolean reportAllThreadInfo;
    /**
     * 反生ANR时回调
     */
    private OnNoRespondingListener onNoRespondingListener;

    private boolean isStop = true;

    /**
     * 初始化ANRLooper，在Application中做初始化
     *
     * @param configuration
     */
    public static void initialize(Configuration configuration) {
        if (sLooper == null) {
            synchronized (ANRLooper.class) {
                if (sLooper == null) {
                    sLooper = new ANRLooper();
                }
            }
            sLooper.init(configuration);
        }
    }

    public static ANRLooper getANRLooper() {
        if (sLooper == null) {
            throw new IllegalStateException("未使用initialize方法初始化ANRLooper");
        }
        return sLooper;
    }

    private ANRLooper() {
    }

    private void init(Configuration configuration) {
        this.appContext = configuration.appContext;
        this.frequency = configuration.frequency < MIN_FREQUENCY ? MIN_FREQUENCY : configuration.frequency;
        this.ignoreDebugger = configuration.ignoreDebugger;
        this.reportAllThreadInfo = configuration.reportAllThreadInfo;
        this.onNoRespondingListener = configuration.onNoRespondingListener;
    }

    @Override
    public void run() {
        int lastTickNumber;
        while (!isStop) {
            lastTickNumber = tickCounter;
            uiHandler.post(ticker);

            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            if (lastTickNumber == tickCounter) {
                if (!ignoreDebugger && Debug.isDebuggerConnected()) {
                    Log.w(TAG, "当前由调试模式引起消息阻塞引起ANR，可以通过setIgnoreDebugger(true)来忽略调试模式造成的ANR");
                    continue;
                }

                ANRError anrError;
                if (reportAllThreadInfo) {
                    anrError = ANRError.getUiThread();
                } else {
                    anrError = ANRError.getAllThread();
                }

                if (onNoRespondingListener != null){
                    onNoRespondingListener.onNoResponding(anrError);
                }
                break;
            }

        }
    }

    /**
     * 开始监测ANR
     */
    public synchronized void start(){
        if (isStop){
            isStop = false;
            Thread anrThread = new Thread(this);
            anrThread.setName(ANR_LOOPER_THREAD_NAME);
            anrThread.start();
        }
    }

    /**
     * 停止检测ANR
     */
    public synchronized void stop(){
        if (!isStop){
            isStop = true;
        }
    }

    public static class Builder {
        /**
         * Application的Context
         */
        private Context appContext;
        /**
         * 轮询的时间频率（单位：s）
         */
        private long frequency;
        /**
         * 是否忽略debug产生的ANR
         */
        private boolean ignoreDebugger;
        /**
         * 发生ANR时是否上报所有线程的ANR信息
         */
        private boolean reportAllThreadInfo = false;
        /**
         * 反生ANR时回调
         */
        private OnNoRespondingListener onNoRespondingListener;

        public Builder(Context appContext) {
            this.appContext = appContext;
        }

        /**
         * 设置轮询的时间频率
         *
         * @param frequency
         * @return
         */
        public Builder setFrequency(long frequency) {
            this.frequency = frequency;
            return this;
        }

        /**
         * 设置是否忽略debugger模式引起的ANR
         *
         * @param ignoreDebugger
         * @return
         */
        public Builder setIgnoreDebugger(boolean ignoreDebugger) {
            this.ignoreDebugger = ignoreDebugger;
            return this;
        }

        /**
         * 设置发生ANR时，是否上报所有的线程信息，默认是false
         *
         * @param reportAllThreadInfo
         * @return
         */
        public Builder setReportAllThreadInfo(boolean reportAllThreadInfo) {
            this.reportAllThreadInfo = reportAllThreadInfo;
            return this;
        }

        /**
         * 设置发生ANR时，回调监听
         *
         * @param onNoRespondingListener
         * @return
         */
        public Builder setOnNoRespondingListener(OnNoRespondingListener onNoRespondingListener) {
            this.onNoRespondingListener = onNoRespondingListener;
            return this;
        }

        public Configuration build() {
            Configuration configuration = new Configuration();
            configuration.appContext = appContext;
            configuration.frequency = frequency;
            configuration.ignoreDebugger = ignoreDebugger;
            configuration.onNoRespondingListener = onNoRespondingListener;
            return configuration;
        }
    }

    private static class Configuration {
        /**
         * Application的Context
         */
        private Context appContext;
        /**
         * 轮询的时间频率（单位：s）
         */
        private long frequency;
        /**
         * 是否忽略debug产生的ANR
         */
        private boolean ignoreDebugger;
        /**
         * 发生ANR时是否上报所有线程的ANR信息
         */
        private boolean reportAllThreadInfo;
        /**
         * 反生ANR时回调
         */
        private OnNoRespondingListener onNoRespondingListener;
    }

    /**
     * ANR回调接口
     */
    public static interface OnNoRespondingListener {
        /**
         * 发生ANR时产生回调(在非UI线程中回调)
         *
         * @param anrError
         */
        public void onNoResponding(ANRError anrError);
    }
}
