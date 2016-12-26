package com.junyou.hbks.robmoney;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.config.BaseConfig;
import com.junyou.hbks.utils.AccessibilityUtil;

public abstract class BaseRobMoney implements IRobMoney{

    private Handler myHandler = null;
    private AccessibilityService mService = null;
    private BaseConfig mBaseConfig = null;

    public BaseRobMoney(AccessibilityService service, BaseConfig baseConfig){
        this.mService = service;
        this.mBaseConfig = baseConfig;
    }

    @Override
    public void delayClick(final AccessibilityNodeInfo nodeInfo, int time) {
        if(time==0){
            AccessibilityUtil.performClick(nodeInfo);
            nodeInfo.recycle();
        }else{
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccessibilityUtil.performClick(nodeInfo);
                    nodeInfo.recycle();
                }
            }, time);
        }
    }

    public Handler getHandler() {
        if(myHandler == null) {
            myHandler = new Handler(Looper.getMainLooper());
        }
        return myHandler;
    }

    public AccessibilityService getService(){
        return this.mService;
    }

    public BaseConfig getBaseConfig(){
        return this.mBaseConfig;
    }
}
