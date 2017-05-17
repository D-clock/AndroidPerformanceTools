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

public class ANRLooper extends Thread {

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

        setName(ANR_LOOPER_THREAD_NAME);
    }

    @Override
    public void run() {
        super.run();
        int lastTickNumber;
        while (!isInterrupted()) {
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

            }

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

        public Configuration build() {
            Configuration configuration = new Configuration();
            configuration.appContext = appContext;
            configuration.frequency = frequency;
            configuration.ignoreDebugger = ignoreDebugger;
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
    }
}
