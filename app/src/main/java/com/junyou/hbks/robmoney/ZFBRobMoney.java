package com.junyou.hbks.robmoney;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.config.BaseConfig;
import com.junyou.hbks.utils.LogUtil;

import java.util.List;

public class ZFBRobMoney extends BaseRobMoney{

    private int mCurrentWindow = ZFBParams.WINDOW_NONE;
    private boolean isReceivingHongbao = false;
    private AccessibilityNodeInfo mGrabNode = null;

    public ZFBRobMoney(AccessibilityService service, BaseConfig baseConfig){
        super(service,baseConfig);
    }
    //通知 step 1
    @Override
    public void doNotificationStateChanged(AccessibilityEvent event) {
        LogUtil.i("ZFB通知栏有消息");
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()){
            String content = String.valueOf(texts.get(0));//获得通知栏信息内容
            LogUtil.i("通知内容: " + content);
            //eg: 小明发来一个红包
            if ("".equals(content)){
                return;
            }
            if (content.contains(ZFBParams.KEY_ZFBREDPACKET) || content.contains(ZFBParams.KEY_ZFBREDPACKETONE)){
                Parcelable data = event.getParcelableData();
                if (data == null || !(data instanceof Notification)) {
                    return;
                }
//                LogUtil.i("消息有: "+ " <发来一个红包> 或 <在群聊中发了一个红包>　字眼");
                LogUtil.i("是红包");
                isReceivingHongbao = true;
                //TODO　解锁屏幕
//                if (super.getBaseConfig().isAutoUnlock()&&!LockScreenUtil.isUnlocking()) {
//                    LockScreenUtil.getInitialize(getService().getApplicationContext()).UnlockScreen();
//                }
                //打开通知信息
                Notification notification = (Notification) data;
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }else{
                LogUtil.i("不是红包");
            }
        }
    }
    //进入app，进入聊天列表，滑动列表等都会调用，频繁调用
    @Override
    public void doWindowContentChanged(AccessibilityEvent event) {
        LogUtil.i("ZFB窗口内容改变");
//        if (!isReceivingHongbao){
//            return;
//        }
        findRedPkt();
    }
    //进入app先调用，进入对话框，进入红包，进入红包详情等都会调用,频率低
    @Override
    public void doWindowStateChanged(AccessibilityEvent event) {
        LogUtil.i("ZFB打开菜单或对话框");
        CharSequence className = event.getClassName();
        if (className == null) {//如果包.类名为空
            return;
        }
//        LogUtil.i("界面名: " + className);
        /**
         * 1.聊天界面
         * 2.抢红包界面，上面有拆红包按钮
         * 3.红包详情界面
         */
        if (className.toString().equals(ZFBParams.ZFB_PERSONALMSG) || className.toString().equals(ZFBParams.ZFB_GROUPMSG)){
            LogUtil.i("聊天界面");
            //在聊天界面，去找到红包，点击红包
//            findRedPkt();
        }else if(className.toString().equals(ZFBParams.ZFB_PERSONALROB) || className.toString().equals(ZFBParams.ZFB_GROUPROB)){
//            在抢红包界面，点击拆开按钮
            LogUtil.i("抢红包界面");
        }else if(className.toString().equals(ZFBParams.ZFB_PERSONALCROWD) || className.toString().equals(ZFBParams.ZFB_GROUPCROWD)){
            //在红包详情界面，返回桌面或首页
            LogUtil.i("详情界面");
            isReceivingHongbao = false;
        }
    }
    //todo 找不到可点击的节点
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void findRedPkt(){
//        if (!isReceivingHongbao){
//            return;
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();

        if (rootNode == null){
            return;
        }

        if (isAtChatWindow(rootNode)){
            //在聊天窗口
//            LogUtil.i("在聊天窗口111111");
            AccessibilityNodeInfo result = this.findRPFromChatWindow(rootNode);
            if (result != null){
                LogUtil.i("有领取红包字");
                super.delayClick(result, 0);
//                this.isReceivingHongbao = false;
            }else{
                LogUtil.i("无领取红包字");
            }
        }else{
            //不在聊天窗口
//            LogUtil.i("不在聊天窗口222222");
        }

//        }
//        AccessibilityNodeInfo clickInfo = AccessibilityUtil.findNodeInfosById(rootNode,ZFBParams.ZFB_CLICK_TEST);
//        AccessibilityNodeInfo clickInfo = AccessibilityUtil.findNodeInfosById(rootNode,ZFBParams.ZFB_CLICIID);
//        AccessibilityNodeInfo clickInfo = AccessibilityUtil.findNodeInfosById(rootNode,ZFBParams.ZFB_ICON);
//        if (null != clickInfo){
//            LogUtil.i("有头像");
////            AccessibilityUtil.performClick(clickInfo.getParent());
//        }else{
//            LogUtil.i("无头像");
//        }
        rootNode.recycle();
    }

    //从聊天窗口寻找红包
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private AccessibilityNodeInfo findRPFromChatWindow(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo result = null;
//        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(ZFBParams.KEY_GETREDPACKET);
//        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("个人红包");
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(ZFBParams.ZFB_CLICIID);
//        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("com.alipay.mobile.chatapp:id/chat_expression_ctr_btn");
        if (list != null && !list.isEmpty()) {
            result = list.get(list.size() - 1);
//            LogUtil.i("className:"+result.getClassName()+", chidcount:" + result.getChildCount()+" ,windwsID:"+result.getWindowId());

            if (result.getParent() != null){
                result = result.getParent();
                AccessibilityNodeInfo parent = result.getParent();
                LogUtil.i("父节点：" + ""+ parent.getClassName());
                if (parent.isClickable()){
                    LogUtil.i("父节点可点击");
                }else{
                    LogUtil.i("父节点不可点击");
                }
            }
        }
        return result;
    }

    //是否在聊天窗口
    private boolean isAtChatWindow(AccessibilityNodeInfo nodeInfo) {
        boolean flag = false;
        if (nodeInfo == null) {
            return flag;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(ZFBParams.KEY_RETURN_DESC);
        if (list != null) {
            for (AccessibilityNodeInfo node : list) {
                if (ZFBParams.CLASS_NAME_IMAGEBUTTON.equals(node.getClassName())) {
                    if (ZFBParams.KEY_RETURN_DESC.equals(node.getContentDescription())) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    private AccessibilityNodeInfo findPktFromChatWindow(AccessibilityNodeInfo rootNode){
        AccessibilityNodeInfo result = null;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(ZFBParams.KEY_GETREDPACKET);
        if (list != null && !list.isEmpty()) {
            result = list.get(list.size() - 1);
        }
        return result;
    }
}
