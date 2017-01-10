package com.junyou.hbks.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.R;
import com.junyou.hbks.UI.MainActivity;
import com.junyou.hbks.UI.RobApp;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.config.BaseConfig;
import com.junyou.hbks.config.Constants;
import com.junyou.hbks.robmoney.IRobMoney;
import com.junyou.hbks.robmoney.QQParams;
import com.junyou.hbks.robmoney.QQRobMoney;
import com.junyou.hbks.robmoney.WXParams;
import com.junyou.hbks.robmoney.WXRobMoney;
import com.junyou.hbks.robmoney.ZFBParams;
import com.junyou.hbks.robmoney.ZFBRobMoney;
import com.junyou.hbks.utils.AccessibilityUtil;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.utils.SaveCountUtil;
import com.junyou.hbks.utils.TimeManager;

import java.util.Iterator;
import java.util.List;

public class RobAccessibilityService extends AccessibilityService {

    public static RobAccessibilityService mInstanceService = null;
    private IRobMoney WXRobMoney = null;
    private IRobMoney QQRobMoney = null;
    private IRobMoney ZFBRobMoney = null;

    private RobApp mApp = null;
    private BaseConfig mBaseConfig = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mApp = (RobApp) super.getApplication();
        this.mBaseConfig = mApp.getBaseConfig();
        this.WXRobMoney = new WXRobMoney(this,this.mBaseConfig);
        this.QQRobMoney = new QQRobMoney(this,this.mBaseConfig);
        this.ZFBRobMoney = new ZFBRobMoney(this,this.mBaseConfig);
    }

    //接收事件,如触发了通知栏变化、界面变化等
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final  int eventType = event.getEventType();
        CharSequence packageName = event.getPackageName();
//        LogUtil.i("eventType:" + eventType + "  ,packageName:"+ packageName);
//        LogUtil.i("事件类型：" + AccessibilityEvent.eventTypeToString(eventType));

        SaveCountUtil saveUtil = SaveCountUtil.getInitialize(getApplicationContext());
        if (saveUtil == null){
            LogUtil.i("can not rob ,saveUtil is null...");
            return;
        }

        if (!saveUtil.getCanRobMoney()){
            LogUtil.i("can not rob ,switch is off...");
            return;
        }

        if (!TimeManager.getServiceOnOrOff()){
            LogUtil.i("can not rob ,time is out...");
            return;
        }

        if (WXParams.PACKAGENAME.equals(packageName)){

            if (saveUtil.getWcEnabled()){
                //微信抢红包
                switch (eventType) {
                    //通知栏事件
                    case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                        this.WXRobMoney.doNotificationStateChanged(event);
                        break;
                    //代表打开PopupWindow，菜单，对话框的事件，等等。
                    case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                        this.WXRobMoney.doWindowStateChanged(event);
                        break;
                    //表示改变一个窗口的内容的事件，更具体地说是植根于事件的源的子树
                    case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                        this.WXRobMoney.doWindowContentChanged(event);
                        break;
                }
            }
        }else if (QQParams.PACKAGENAME.equals(packageName)){
            if (saveUtil.getQqEnabled()){
                //QQ抢红包
                switch (eventType) {
                    case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                        this.QQRobMoney.doNotificationStateChanged(event);
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                        this.QQRobMoney.doWindowStateChanged(event);
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                        this.QQRobMoney.doWindowContentChanged(event);
                        break;
                }
            }
        }/*
        else if (ZFBParams.PACKAGENAME.equals(packageName)){
            if (saveUtil.getZfbEnabled()){
                //支付宝抢红包
                switch (eventType) {
                    case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                        this.ZFBRobMoney.doNotificationStateChanged(event);
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                        this.ZFBRobMoney.doWindowStateChanged(event);
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                        this.ZFBRobMoney.doWindowContentChanged(event);
                        break;
                }
            }
        }
        */
    }

    @Override
    protected boolean onGesture(int gestureId) {
        LogUtil.i("手势id:" + gestureId);
        return super.onGesture(gestureId);
    }

    //服务中断
    @Override
    public void onInterrupt() {
//        LogUtil.i("service onInterrupt");
        super.sendBroadcast(new Intent(RobApp.ACTION_ACCESSIBILITY_SERVICE_DISCONNECT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LogUtil.i("service onDestroy");
        stopForeground(true);
        mInstanceService = null;
        super.sendBroadcast(new Intent(RobApp.ACTION_ACCESSIBILITY_SERVICE_DISCONNECT));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
//        LogUtil.i("service onServiceConnected");
        super.onServiceConnected();
        super.sendBroadcast(new Intent(RobApp.ACTION_ACCESSIBILITY_SERVICE_CONNECT));
        mInstanceService = this;
        openNotifocation();
        AccessibilityServiceInfo info = getServiceInfo();
        info.packageNames = new String[]{"com.tencent.mobileqq","com.tencent.mm","com.eg.android.AlipayGphone"};       //tencent package
        //info.packageNames = new String[]{"com.eg.android.AlipayGphone"};          //支付宝包名称
        setServiceInfo(info);
        ComponentName cName = new ComponentName(Constants.PKG_NAME,Constants.ACK_NAME);
        ComFunction.startAPP(getApplicationContext(),cName,null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("service onStartCommand");
//        return super.onStartCommand(intent, flags, startId);
        //防止服务被系统kill掉
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning(){
        if (mInstanceService == null){
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) mInstanceService.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = mInstanceService.getServiceInfo();
        if (info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if (i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if (!isConnect) {
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotifocation()
    {
//      NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIndent)
                .setSmallIcon(R.mipmap.notify_icon)//设置状态栏里面的图标（小图标）
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)//设置可以清除
                .setOngoing(true)
                .setContentTitle("红包快手")//设置下拉列表里的标题
                .setContentText("亲,红包快手已经开启啦!");//设置上下文内容
        try{
            Notification notification = builder.build();
            startForeground(Notification.FLAG_ONGOING_EVENT, notification);
        }catch (Exception e){
            e.printStackTrace();
        }

        /*
        关于Notification的Flags
        notification.flags = Notification.FLAG_NO_CLEAR; // 点击清除按钮时就会清除消息通知,但是点击通知栏的通知时不会消失
        notification.flags = Notification.FLAG_ONGOING_EVENT; // 点击清除按钮不会清除消息通知,可以用来表示在正在运行
        notification.flags |= Notification.FLAG_AUTO_CANCEL搜索; // 点击清除按钮或点击通知后会自动消失
        notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
        */
    }
}
