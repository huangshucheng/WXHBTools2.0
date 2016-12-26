package com.junyou.hbks.utils;

import android.util.Log;

public class LogUtil {

    public static final String TAG = "TAG";
    public static final boolean DEBUG = true;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }
}
