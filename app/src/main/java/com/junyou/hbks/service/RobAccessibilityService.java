package com.junyou.hbks.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.UI.RobApp;
import com.junyou.hbks.config.BaseConfig;
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

        if (!SaveCountUtil.getInitialize(getApplicationContext()).getCanRobMoney()){
            LogUtil.i("can not rob money,,,");
            return;
        }else{
            LogUtil.i("can rob money,,,");
        }

        if (WXParams.PACKAGENAME.equals(packageName)){
            if (this.mBaseConfig.isWcEnabled()){
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
            if (this.mBaseConfig.isQqEnabled()){
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
        }else if (ZFBParams.PACKAGENAME.equals(packageName)){
            if (this.mBaseConfig.isZfbEnabled()){
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
    }

    //服务中断
    @Override
    public void onInterrupt() {
        LogUtil.i("service onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("service onDestroy");
        mInstanceService = null;
        super.sendBroadcast(new Intent(RobApp.ACTION_ACCESSIBILITY_SERVICE_DISCONNECT));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.i("service onServiceConnected");
        mInstanceService = this;
        super.sendBroadcast(new Intent(RobApp.ACTION_ACCESSIBILITY_SERVICE_CONNECT));

        AccessibilityServiceInfo info = getServiceInfo();
        info.packageNames = new String[]{"com.tencent.mobileqq","com.tencent.mm","com.eg.android.AlipayGphone"};       //tencent package
        //info.packageNames = new String[]{"com.eg.android.AlipayGphone"};          //支付宝包名称
        setServiceInfo(info);

        //返回键 返回两次
        AccessibilityUtil.performBack(RobAccessibilityService.this);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AccessibilityUtil.performBack(RobAccessibilityService.this);
                AccessibilityUtil.performBack(RobAccessibilityService.this);
//                AccessibilityUtil.performHome(RobAccessibilityService.this);
            }
        },200);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
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
}
