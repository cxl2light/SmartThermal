package com.hq.base.util;

import android.util.Log;

/**
 * Created on 2020/4/6.
 * author :
 * desc :
 */
public class Logger {

    private static volatile boolean enable = true;

    public static void init(boolean enable) {
        Logger.enable = enable;
    }

    public static void d(String tag, String msg) {
        if (!enable) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!enable) {
            return;
        }
        Log.e(tag, msg);
    }

}
