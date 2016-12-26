package com.junyou.hbks.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class LockScreenUtil {
    //锁屏判断
    private KeyguardManager mKeyguardManager ;
    private PowerManager mPowerManager;                 //电源管理
    private PowerManager.WakeLock mWeakLock;            //唤醒锁
    private KeyguardManager.KeyguardLock mKeyguardLock ;//安全锁

    private static boolean mIsUnlocking = false;
    private Context mContext;
    private static LockScreenUtil mInstance;

    public static boolean isUnlocking(){
        return mIsUnlocking;
    }

    private LockScreenUtil(Context context) {
        this.mContext = context;
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public static LockScreenUtil getInitialize(Context context) {
        if (mInstance == null) {
            mInstance = new LockScreenUtil(context);
        }
        return mInstance;
    }
    //屏幕是否亮
    public boolean isScreenLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return this.mPowerManager.isInteractive();
        } else {
            return this.mPowerManager.isScreenOn();
        }
    }
    //屏幕是否锁了
    public boolean isScreenLock() {
        return this.mKeyguardManager.inKeyguardRestrictedInputMode();
    }
    //解锁屏幕
    public void UnlockScreen() {
        try {
            if(!this.isScreenLight()){
                this.mWeakLock = this.mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
                this.mWeakLock.acquire();
                LockScreenUtil.mIsUnlocking = true;
            }
            if (this.isScreenLock()) {
                this.mKeyguardLock = mKeyguardManager.newKeyguardLock("My Lock");
                this.mKeyguardLock.disableKeyguard();
                LockScreenUtil.mIsUnlocking = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //锁屏
    public void LockScreen() {
        LockScreenUtil.mIsUnlocking = false;
        try {
            if (this.mKeyguardLock != null) {
                this.mKeyguardLock.reenableKeyguard();
                this.mKeyguardLock = null;
            }
            if (this.mWeakLock != null) {
                this.mWeakLock.release();
                this.mWeakLock = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
