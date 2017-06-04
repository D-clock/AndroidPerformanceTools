package com.clock.performance.tools.block;

import android.os.Looper;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Clock on 2017/5/16.
 */

public class BlockError extends Error {

    private BlockError(ThreadStackInfoWrapper.ThreadStackInfo threadStackInfo) {
        super("BlockLooper Catch BlockError", threadStackInfo);
    }


    public static BlockError getUiThread() {
        Thread uiThread = Looper.getMainLooper().getThread();
        StackTraceElement[] stackTraceElements = uiThread.getStackTrace();
        ThreadStackInfoWrapper.ThreadStackInfo threadStackInfo = new ThreadStackInfoWrapper(getThreadNameAndState(uiThread), stackTraceElements)
                .new ThreadStackInfo(null);
        return new BlockError(threadStackInfo);
    }


    public static BlockError getAllThread() {
        final Thread uiThread = Looper.getMainLooper().getThread();
        Map<Thread, StackTraceElement[]> stackTraceElementMap = new TreeMap<Thread, StackTraceElement[]>(new Comparator<Thread>() {
            @Override
            public int compare(Thread lhs, Thread rhs) {
                if (lhs == rhs) {
                    return 0;
                } else if (lhs == uiThread) {
                    return 1;
                } else if (rhs == uiThread) {
                    return -1;
                }
                return rhs.getName().compareTo(lhs.getName());
            }
        });

        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread key = entry.getKey();
            StackTraceElement[] value = entry.getValue();
            if (value.length > 0) {
                stackTraceElementMap.put(key, value);
            }
        }

        //Fix有时候Thread.getAllStackTraces()不包含UI线程的问题
        if (!stackTraceElementMap.containsKey(uiThread)) {
            stackTraceElementMap.put(uiThread, uiThread.getStackTrace());
        }

        ThreadStackInfoWrapper.ThreadStackInfo threadStackInfo = null;
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraceElementMap.entrySet()) {
            Thread key = entry.getKey();
            StackTraceElement[] value = entry.getValue();
            threadStackInfo = new ThreadStackInfoWrapper(getThreadNameAndState(key), value).
                    new ThreadStackInfo(threadStackInfo);
        }

        return new BlockError(threadStackInfo);

    }

    public static String getThreadNameAndState(Thread thread) {
        return thread.getName() + "-state-" + thread.getState();
    }


    private static class ThreadStackInfoWrapper {

        private String nameAndState;
        private StackTraceElement[] stackTraceElements;

        private ThreadStackInfoWrapper(String nameAndState, StackTraceElement[] stackTraceElements) {
            this.nameAndState = nameAndState;
            this.stackTraceElements = stackTraceElements;
        }

        private class ThreadStackInfo extends Throwable {

            private ThreadStackInfo(Throwable throwable) {
                super(nameAndState, throwable);
            }

            @Override
            public synchronized Throwable fillInStackTrace() {
                setStackTrace(stackTraceElements);
                return this;
            }
        }
    }


}
