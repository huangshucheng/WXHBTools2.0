package com.junyou.hbks.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveMoneyUtil {

    private Context mContext = null;
    private static SaveMoneyUtil mInstance = null;

    private static SharedPreferences mSharedP =null;
    private static SharedPreferences.Editor mEditor = null;

    private PAYTYPE mPayType = PAYTYPE.VIP_TYPE;

    public enum PAYTYPE {
        VIP_TYPE,
        COIN_TYPE
    }

    private SaveMoneyUtil(Context context){
        this.mContext = context;
        mSharedP = mContext.getSharedPreferences("config",mContext.MODE_MULTI_PROCESS);
        mEditor  = mSharedP.edit();
    }

    public static SaveMoneyUtil getInitialize(Context context) {
        if (mInstance == null) {
            mInstance = new SaveMoneyUtil(context);
        }
        return mInstance;
    }

    public PAYTYPE getPayType(){
      return mPayType;
    }

    public void setPayType(PAYTYPE payType){
        this.mPayType = payType;
    }

    //微信支支付，分单位，例：1800 分
    public void setMoneyCount(String count){
        if ("".equals(count)){
            return;
        }
        if (null != mEditor){
            mEditor.putString("wxmoneycount",count);
            mEditor.apply();
        }
    }

    public String getMoneyCount(){
        if (null != mSharedP){
            String count = mSharedP.getString("wxmoneycount","0.00");
            if (!"0.00".equals(count)){
                return count;
            }
        }
        return "";
    }

    //支付宝支付，元单位，例：18.00 元
    /*
    public void setZFBMoneyCount(String count){
        if ("".equals(count)){
            return;
        }
        if (null != mEditor){
            mEditor.putString("zfbmoneycount",count);
            mEditor.apply();
        }
    }

    public String getZFBMoneyCount(){
        if (null != mSharedP){
            String count = mSharedP.getString("zfbmoneycount","0.00");
            if (!"0.00".equals(count)){
                return count;
            }
        }
        return "";
    }
*/
}
