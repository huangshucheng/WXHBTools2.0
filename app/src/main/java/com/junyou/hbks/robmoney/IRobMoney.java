package com.junyou.hbks.robmoney;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public interface IRobMoney {

    public void delayClick(AccessibilityNodeInfo nodeInfo, int time);

     //通知栏事件
    public void doNotificationStateChanged(AccessibilityEvent event);

     //表示改变一个窗口的内容的事件，更具体地说是植根于事件的源的子树
    public void doWindowContentChanged(AccessibilityEvent event);

    //代表打开PopupWindow，菜单，对话框的事件，等等。
    public void doWindowStateChanged(AccessibilityEvent event);
/*
    TYPE_VIEW_CLICKED
    TYPE_VIEW_SELECTED
    TYPE_VIEW_FOCUSED
    TYPE_TOUCH_EXPLORATION_GESTURE_START
    TYPE_GESTURE_DETECTION_START
    TYPE_TOUCH_INTERACTION_START
    TYPE_VIEW_CONTEXT_CLICKED
    */
}
