package com.clock.performance.tools.block;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.clock.performance.tools.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BlockLooper
 * <p>
 * 使用线程轮询的方式监听App是否产生卡顿
 * <p>
 * Created by Clock 2017/5/16.
 */

public class BlockLooper implements Runnable {

    private final static String TAG = BlockLooper.class.getSimpleName();
    private final static String LOOPER_NAME = "block-looper-thread";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
    /**
     * 最小的轮询频率
     */
    private final static long DEFAULT_FREQUENCY = 5000;

    private static BlockLooper sLooper;
    private Context appContext;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private volatile int tickCounter = 0;
    private Runnable ticker = new Runnable() {
        @Override
        public void run() {
            tickCounter = (tickCounter + 1) % Integer.MAX_VALUE;
        }
    };
    private long frequency;
    private boolean ignoreDebugger;
    private boolean reportAllThreadInfo;
    private boolean saveLog;
    private OnBlockListener onBlockListener;

    private boolean isStop = true;

    public static void initialize(Configuration configuration) {
        if (sLooper == null) {
            synchronized (BlockLooper.class) {
                if (sLooper == null) {
                    sLooper = new BlockLooper();
                }
            }
            sLooper.init(configuration);
        }
    }

    public static BlockLooper getBlockLooper() {
        if (sLooper == null) {
            throw new IllegalStateException("未使用initialize方法初始化BlockLooper");
        }
        return sLooper;
    }

    private BlockLooper() {
    }

    private void init(Configuration configuration) {
        this.appContext = configuration.appContext;
        this.frequency = configuration.frequency < DEFAULT_FREQUENCY ? DEFAULT_FREQUENCY : configuration.frequency;
        this.ignoreDebugger = configuration.ignoreDebugger;
        this.reportAllThreadInfo = configuration.reportAllThreadInfo;
        this.onBlockListener = configuration.onBlockListener;
        this.saveLog = configuration.saveLog;
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

                BlockError blockError;
                if (!reportAllThreadInfo) {
                    blockError = BlockError.getUiThread();
                } else {
                    blockError = BlockError.getAllThread();
                }

                if (onBlockListener != null) {
                    onBlockListener.onBlock(blockError);
                }

                if (saveLog) {
                    if (StorageUtils.isMounted()) {
                        File logDir = getLogDirectory();
                        saveLogToSdcard(blockError, logDir);
                    } else {
                        Log.w(TAG, "sdcard is unmounted");
                    }
                }
                break;
            }

        }
    }

    private void saveLogToSdcard(BlockError blockError, File dir) {
        if (blockError == null) {
            return;
        }
        if (dir != null && dir.exists() && dir.isDirectory()) {
            String fileName = getLogFileName();
            File logFile = new File(dir, fileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                    PrintStream printStream = new PrintStream(new FileOutputStream(logFile, false), true);
                    blockError.printStackTrace(printStream);
                    printStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getLogDirectory() {
        File cacheDir = appContext.getExternalCacheDir();
        if (cacheDir != null) {
            File logDir = new File(cacheDir, "block");
            if (!logDir.exists()) {
                boolean successful = logDir.mkdirs();
                if (successful) {
                    return logDir;
                } else {
                    return null;
                }
            } else {
                return logDir;
            }
        }
        return null;
    }

    private String getLogFileName() {
        String timeStampString = DATE_FORMAT.format(new Date());
        String fileName = timeStampString + ".trace";
        return fileName;
    }

    public synchronized void start() {
        if (isStop) {
            isStop = false;
            Thread blockThread = new Thread(this);
            blockThread.setName(LOOPER_NAME);
            blockThread.start();
        }
    }

    public synchronized void stop() {
        if (!isStop) {
            isStop = true;
        }
    }

    public static class Builder {
        private Context appContext;
        private long frequency;
        private boolean ignoreDebugger;
        private boolean reportAllThreadInfo = false;
        private boolean saveLog;
        private OnBlockListener onBlockListener;

        public Builder(Context appContext) {
            this.appContext = appContext;
        }

        public Builder setFrequency(long frequency) {
            this.frequency = frequency;
            return this;
        }

        /**
         * 设置是否忽略debugger模式引起的卡顿
         *
         * @param ignoreDebugger
         * @return
         */
        public Builder setIgnoreDebugger(boolean ignoreDebugger) {
            this.ignoreDebugger = ignoreDebugger;
            return this;
        }

        /**
         * 设置发生卡顿时，是否上报所有的线程信息，默认是false
         *
         * @param reportAllThreadInfo
         * @return
         */
        public Builder setReportAllThreadInfo(boolean reportAllThreadInfo) {
            this.reportAllThreadInfo = reportAllThreadInfo;
            return this;
        }

        public Builder setSaveLog(boolean saveLog) {
            this.saveLog = saveLog;
            return this;
        }

        /**
         * 设置发生卡顿时的回调
         *
         * @param onBlockListener
         * @return
         */
        public Builder setOnBlockListener(OnBlockListener onBlockListener) {
            this.onBlockListener = onBlockListener;
            return this;
        }

        public Configuration build() {
            Configuration configuration = new Configuration();
            configuration.appContext = appContext;
            configuration.frequency = frequency;
            configuration.ignoreDebugger = ignoreDebugger;
            configuration.reportAllThreadInfo = reportAllThreadInfo;
            configuration.saveLog = saveLog;
            configuration.onBlockListener = onBlockListener;
            return configuration;
        }
    }

    private static class Configuration {
        private Context appContext;
        private long frequency;
        private boolean ignoreDebugger;
        private boolean reportAllThreadInfo;
        private boolean saveLog;
        private OnBlockListener onBlockListener;
    }

    public static interface OnBlockListener {
        /**
         * 发生ANR时产生回调(在非UI线程中回调)
         *
         * @param blockError
         */
        public void onBlock(BlockError blockError);
    }
}
