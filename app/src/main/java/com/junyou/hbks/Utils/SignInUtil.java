package com.junyou.hbks.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.Log;

//签到
public class SignInUtil {

    private static Activity _mActivity = null;
    private static SharedPreferences sharedP =null;
    private static SharedPreferences.Editor editor = null;

    private static final String TIMECOUNT = "timecount";
    private static final String FIRSTTIME = "firsttime";
    private static final String SIGNEDTODAY = "signedtoday";

    public static void init(Activity act){
        _mActivity = act;
        sharedP = _mActivity.getSharedPreferences("config",_mActivity.MODE_PRIVATE);
        editor  = sharedP.edit();
        int isFirstEnter = _mActivity.getSharedPreferences("config",_mActivity.MODE_PRIVATE).getInt("firsttimemark",-99);
        if (isFirstEnter == -99){
        setFirstTime("2016-12-01");
        setSignedToday(false);
            editor.putInt("firsttimemark",99);
            editor.apply();
            editor.putInt(TIMECOUNT,0);
            editor.apply();
            Log.i("TAG","first enter...time:"+ getFirstTime());
        }else{
            Log.i("TAG","not first enter...");
        }
    }

    public static boolean isNewDay(){
    String firstTime = getFirstTime();
    String curTime = getCurTime();
        if (!"".equals(firstTime) && !"".equals(curTime)){
            if (!firstTime.equals(curTime)){
//                Log.i("TAG","new day...");
                setSignedToday(false);
                return true;
            }
        }
//        Log.i("TAG","not new day...");
        return false;
    }

    //获取当前系统时间
    public static String getCurTime() {
        long sysTime = System.currentTimeMillis();
     //   String _time = String.valueOf(DateFormat.format("yyyy-MM-dd HH:mm:ss",sysTime));
        return  String.valueOf(DateFormat.format("yyyy-MM-dd",sysTime));
    }

    public static void setFirstTime(String firstTime){
        if (null != editor) {
            editor.putString(FIRSTTIME,""+ firstTime);
            editor.apply();
        }
    }

    public static String getFirstTime(){
        if (null != _mActivity){
            String leaveTime = _mActivity.getSharedPreferences("config",_mActivity.MODE_PRIVATE).getString(FIRSTTIME,"");
            return leaveTime;
        }
        return  null;
    }

    public static int getSignedCount(){
        if (null != _mActivity){
            int signedCount = _mActivity.getSharedPreferences("config",_mActivity.MODE_PRIVATE).getInt(TIMECOUNT,0);
            if (signedCount >= 0){
                return signedCount;
            }
        }
        return 0;
    }

    public static void addSignedCont(){
            if (null != editor){
                int signedCount = getSignedCount();
                editor.putInt(TIMECOUNT,signedCount + 1);
                editor.apply();
            }
    }

    public static boolean getSignedToday(){
        if (null != _mActivity){
            boolean isSigned = _mActivity.getSharedPreferences("config",_mActivity.MODE_PRIVATE).getBoolean(SIGNEDTODAY,false);
            if (isSigned){
                return true;
            }
            return false;
        }
        return  false;
    }

    public static void setSignedToday(boolean sign){
        if (null != editor){
            editor.putBoolean(SIGNEDTODAY,sign);
            editor.apply();
        }
    }
}
