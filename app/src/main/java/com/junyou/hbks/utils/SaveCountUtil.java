package com.junyou.hbks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//统计红包和钱
public class SaveCountUtil{

    private Context mContext;
    private static SaveCountUtil mInstance;

    private static SharedPreferences sharedP =null;
    private static SharedPreferences.Editor editor = null;

    private SharedPreferences mSharedPreferences = null;

    private static final String TOTAL_RP_NUM = "totalnum";
    private static final String TOTAL_MONEY_NUM = "totalmoney";
    private static final String ISOPEN_FLAG = "isopen_flag";

    private static final String IS_WX_ENABLE = "is_wx_enable";
    private static final String IS_QQ_ENABLE = "is_qq_enable";
    private static final String IS_ZFB_ENABLE = "is_zfb_enable";

    public SaveCountUtil(Context context) {
        this.mContext = context;
        sharedP = context.getSharedPreferences("config",context.MODE_MULTI_PROCESS);
        editor  = sharedP.edit();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static SaveCountUtil getInitialize(Context context) {
        if (mInstance == null) {
            mInstance = new SaveCountUtil(context);
        }
        return mInstance;
    }

    public void setRPCount(int count){
        if (count < 0 ){
            return;
        }
        if (null != editor){
            editor.putInt(TOTAL_RP_NUM,count);
            editor.apply();
        }
    }

    public int getRPCount(){
        if (null != mContext){
            int rpNum = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getInt(TOTAL_RP_NUM,0);
            return rpNum;
        }
        return 0;
    }

    public void setMoneyCount(String moneyCount){
        if ("".equals(moneyCount)){
            return;
        }
        if (null != editor){
            editor.putString(TOTAL_MONEY_NUM,moneyCount);
            editor.apply();
        }
    }

    public String getMoneyCount(){
        if (null != mContext){
            String mnCount = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getString(TOTAL_MONEY_NUM,"0.00");
            return mnCount;
        }
        return null;
    }

    public void setCanRobMoney(boolean canRobMoney){
        if (null != editor){
            editor.putBoolean(ISOPEN_FLAG,canRobMoney);
            editor.apply();
        }
    }

    public boolean getCanRobMoney(){
        if (null != mContext){
            boolean mnCount = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getBoolean(ISOPEN_FLAG,true);
            return mnCount;
        }
        return true;
    }

    public boolean getQqEnabled() {
        if (null != mContext){
            boolean enable = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getBoolean(IS_QQ_ENABLE,true);
            return enable;
        }
        return true;
    }

    public void setQqWnable(boolean qqWnable){
        if (null != editor){
            editor.putBoolean(IS_QQ_ENABLE,qqWnable);
            editor.apply();
        }
    }

    public boolean getWcEnabled() {
        if (null != mContext){
            boolean enable = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getBoolean(IS_WX_ENABLE,true);
            return enable;
        }
        return true;
    }

    public void setWcEnable(boolean wcEnable){
        if (null != editor){
            editor.putBoolean(IS_WX_ENABLE,wcEnable);
            editor.apply();
        }
    }

    public boolean getZfbEnabled(){
        if (null != mContext){
            boolean enable = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS).getBoolean(IS_ZFB_ENABLE,true);
            return enable;
        }
        return true;
    }

    public void setZfbEnable(boolean zfbEnable){
        if (null != editor){
            editor.putBoolean(IS_ZFB_ENABLE,zfbEnable);
            editor.apply();
        }
    }
}
