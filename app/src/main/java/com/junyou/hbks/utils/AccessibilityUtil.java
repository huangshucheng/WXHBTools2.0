package com.junyou.hbks.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AccessibilityUtil {

    /** 通过id查找*/
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    /** 通过文本查找*/
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /** 返回主界面事件*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performHome(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performRecentApp(AccessibilityService service){
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    /**
     * 返回桌面
     */
    public static void gotoDeskTop(AccessibilityService service)
    {
        try{
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            service.getApplicationContext().startActivity(home);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** 返回事件*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void performBack(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /** 点击事件*/
    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return;
        }
        if(nodeInfo.isClickable()) {
            LogUtil.i("可点击。。。。。。");
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            LogUtil.i("不可点击TTTTTTTTTT");
            performClick(nodeInfo.getParent());
        }
    }
}

