package com.junyou.hbks.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocalSaveUtil {
    private static final String COIN_NUM = "coin_num";
    private static final String POINT_NUM = "point_num";
    private static final String USER_ACCOUNT = "user_account";

    private static Activity mActivity;
    private static SharedPreferences sharedP =null;
    private static SharedPreferences.Editor editor = null;

    public static void init(Activity act){
        mActivity = act;
        sharedP = mActivity.getSharedPreferences("config",mActivity.MODE_PRIVATE);
        editor  = sharedP.edit();
    }

    public static int getCoinNum(){
        if (null != mActivity){
            int coinNum = mActivity.getSharedPreferences("config",mActivity.MODE_PRIVATE).getInt(COIN_NUM,0);
            return coinNum;
        }
        return  0;
    }

    public static void setCoinNum(int coinNum){
        if (coinNum <0){
            return;
        }
        if (null != editor){
            editor.putInt(COIN_NUM,coinNum);
            editor.apply();
        }
    }

    public static int getPointNum(){
        if (null != mActivity){
            int pointNum = mActivity.getSharedPreferences("config",mActivity.MODE_PRIVATE).getInt(POINT_NUM,0);
            return pointNum;
        }
        return  0;
    }

    public static void setPointNum(int pointNum){
        if (pointNum<0){
            return;
        }
        if (null != editor){
            editor.putInt(POINT_NUM,pointNum);
            editor.apply();
        }
    }

    public static void setAccount(String account){
        if ("".equals(account)){
            return;
        }
        if (null != editor){
            editor.putString(USER_ACCOUNT,account);
            editor.apply();
        }
    }

    public static String getAccount(){
        if (null != mActivity){
            String account = mActivity.getSharedPreferences("config",mActivity.MODE_PRIVATE).getString(USER_ACCOUNT,"null");
            return account;
        }
        return null ;
    }
}

