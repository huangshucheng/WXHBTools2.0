package com.junyou.hbks.UI;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.junyou.hbks.robmoney.QQParams;
import com.junyou.hbks.robmoney.WXParams;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.config.BaseConfig;

public class RobApp extends Application implements Thread.UncaughtExceptionHandler{
    public static final boolean DEBUG = true;
    //辅助服务连接成功
    public static final String ACTION_ACCESSIBILITY_SERVICE_CONNECT = "action.accessibility.service.connect";
    //辅助服务连接中断
    public static final String ACTION_ACCESSIBILITY_SERVICE_DISCONNECT = "action.accessibility.service.disconnect";

    private BaseConfig mBaseConfig = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("RobApp onCreate...");
        initInfo();
        if (!DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    private void initInfo(){
        if(this.mBaseConfig == null){
            this.mBaseConfig = new BaseConfig();
        }

        try {
            PackageInfo wechatPackageInfo = super.getApplicationContext().getPackageManager().getPackageInfo(WXParams.PACKAGENAME, 0);
            if (wechatPackageInfo != null){
                this.mBaseConfig.setWXVersionCode(wechatPackageInfo.versionCode);
                this.mBaseConfig.setWXVersionName(wechatPackageInfo.versionName);
                LogUtil.i("微信 versioncode: "+wechatPackageInfo.versionCode + "versionName: " +wechatPackageInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            PackageInfo qqPackageInfo = super.getApplicationContext().getPackageManager().getPackageInfo(QQParams.PACKAGENAME, 0);
            if (qqPackageInfo != null){
                this.mBaseConfig.setQQVersionCode(qqPackageInfo.versionCode);
                this.mBaseConfig.setQQVersionNmae(qqPackageInfo.versionName);
                LogUtil.i("qq versioncode: "+qqPackageInfo.versionCode + "versionName: " +qqPackageInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public BaseConfig getBaseConfig() {
        return mBaseConfig;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0); // 退出程序
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
    }
}
