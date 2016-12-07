package com.junyou.hbks.Utils;

import android.content.Context;
import android.os.Environment;

//金币，积分等数据存储
public class LocalSaveUtil {

//是否有sd卡
    public static boolean isHavedSDcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

//获取SDcard根目录
    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().toString();
    }
}

