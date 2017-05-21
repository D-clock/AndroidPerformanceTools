package com.clock.performance.tools.utils;

import android.os.Environment;

/**
 * Created by Clock on 2017/5/21.
 */

public class StorageUtils {

    private StorageUtils() {

    }

    /**
     * SD卡是否挂载
     *
     * @return
     */
    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
