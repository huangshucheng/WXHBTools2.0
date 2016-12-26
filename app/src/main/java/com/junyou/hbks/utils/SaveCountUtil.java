package com.junyou.hbks.utils;

import android.content.Context;
import android.content.SharedPreferences;

//统计红包和钱
public class SaveCountUtil {

    private Context mContext;
    private static SaveCountUtil mInstance;

    private static SharedPreferences sharedP =null;
    private static SharedPreferences.Editor editor = null;

    private static final String TOTAL_RP_NUM = "totalnum";
    private static final String TOTAL_MONEY_NUM = "totalmoney";
    public static final String ISOPEN_FLAG = "isopen_flag";

    public SaveCountUtil(Context context) {
        this.mContext = context;
        sharedP = context.getSharedPreferences("config",context.MODE_MULTI_PROCESS);
        editor  = sharedP.edit();
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
}
