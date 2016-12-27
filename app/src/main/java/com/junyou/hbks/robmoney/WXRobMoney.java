package com.junyou.hbks.robmoney;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;
import com.junyou.hbks.config.BaseConfig;
import com.junyou.hbks.config.Constants;
import com.junyou.hbks.utils.AccessibilityUtil;
import com.junyou.hbks.utils.LockScreenUtil;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.utils.SaveCountUtil;

import java.math.BigDecimal;
import java.util.List;

public class WXRobMoney extends BaseRobMoney{

    private int mCurrentWindow = WXParams.WINDOW_NONE;
    private boolean isReceivingHongbao = false;
    private AccessibilityNodeInfo mGrabNode = null;

    public WXRobMoney(AccessibilityService service, BaseConfig baseConfig){
        super(service,baseConfig);
    }
    //setp one
    @Override
    public void doNotificationStateChanged(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            String content = String.valueOf(texts.get(0));
            int index = content.indexOf(":");
            if (index != -1) {
                content = content.substring(index + 1);
            }
            if (content.contains(WXParams.KEY_WECHATREDPACKET)) {
                LogUtil.i("是红包");
                Parcelable data = event.getParcelableData();
                if (data == null || !(data instanceof Notification)) {
                    return;
                }
                this.isReceivingHongbao = true;
                //TODO 解锁屏幕
                if (super.getBaseConfig().isAutoUnlock()&&!LockScreenUtil.isUnlocking()) {
                    LockScreenUtil.getInitialize(getService().getApplicationContext()).UnlockScreen();
                }

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
    //setp two  调用多
    @Override
    public void doWindowContentChanged(AccessibilityEvent event) {
        LogUtil.i("WX目录改变");
        if (mCurrentWindow != WXParams.WINDOW_LAUNCHER) { //不在聊天列表界面，不处理
            return;
        }
        findRedPkt();
    }

    //setp three  调用少  有红包：先状态改变，再目录改变
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void doWindowStateChanged(AccessibilityEvent event) {
        LogUtil.i("WX状态改变");
        CharSequence className = event.getClassName();
        if (className == null) {
            return;
        }
//        LogUtil.i("WX类名:" +className);
        if (WXParams.UI_LAUNCHER.equals(className)) {
            //在聊天界面，去找到红包，点击红包
            this.mCurrentWindow = WXParams.WINDOW_LAUNCHER;
            this.findRedPkt();
            LogUtil.i("在聊天界面");
        }else if(WXParams.UI_LUCKY_MONEY_RECEIVE.equals(className)){
            this.mCurrentWindow = WXParams.WINDOW_LUCKYMONEY_RECEIVEUI;
            //在抢红包界面，有打开按钮
            LogUtil.i("在抢红包界面");
            if (this.isFindOpenPacketButton()){
                LogUtil.i("找到打开按钮。。");
                //找到打开按钮，红包没有被抢完
                this.ClickOpenPacketButton();
            }else{
                LogUtil.i("没找到打开按钮。。。红包被抢完");
                //没有找到打开按钮，红包已经被抢完
                AccessibilityUtil.performBack(getService());
                this.backAndGotoDesktop();
                isReceivingHongbao = false;
            }
        }else if(WXParams.UI_LUCKY_MONEY_DETAIL.equals(className)){
            this.mCurrentWindow = WXParams.WINDOW_LUCKYMONEY_DETAIL;
            //在红包详情界面
            LogUtil.i("在红包详情界面");
            //TODO 查看多少钱
                getMoneyCount();
            if (findBackButton()){
//                LogUtil.i("找到返回按钮");
                this.backAndGotoDesktop();
            }else{
//                LogUtil.i("没有找到返回按钮");
                AccessibilityUtil.performBack(getService());
                this.backAndGotoDesktop();
            }
            isReceivingHongbao = false;
        }else {
            this.mCurrentWindow = WXParams.WINDOW_NONE;
        }
    }

    private void backAndGotoDesktop(){
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                ComponentName cName = new ComponentName(WXParams.PACKAGENAME,WXParams.UI_LAUNCHER);
//                ComFunction.startAPP(getService().getApplicationContext(),cName,null);
//                AccessibilityUtil.performBack(getService());
                AccessibilityNodeInfo backInfo = findBackBtnFromWindow();
                if (null != backInfo){
                    LogUtil.i("找到微信自带返回按钮。。");
                    AccessibilityUtil.performClick(backInfo);
                }else{
                    LogUtil.i("没有找到微信自带返回按钮。。");
                    AccessibilityUtil.performBack(getService());
                }
                AccessibilityUtil.performBack(getService());
//                AccessibilityUtil.performHome(getService());
//              AccessibilityUtil.gotoDeskTop(getService());
                if (LockScreenUtil.isUnlocking()){
                    LockScreenUtil.getInitialize(getService().getApplicationContext()).LockScreen();
                }
            }},0);
    }

    //从聊天窗口寻找返回按钮(微信自带)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private AccessibilityNodeInfo findBackBtnFromWindow() {
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null){
            return null;
        }
        AccessibilityNodeInfo result = null;
        result = AccessibilityUtil.findNodeInfosById(rootNode,"com.tencent.mm:id/ga");
        if (result != null){
            return result;
        }else{
            result = AccessibilityUtil.findNodeInfosByText(rootNode,WXParams.KEY_RETURN_DESC);
            if (result != null ) {
                if (WXParams.CLASS_NAME_IMAGEVIEW.equals(result.getClassName())) {
                    if (WXParams.KEY_RETURN_DESC.equals(result.getContentDescription())) {
                        return  result;
                    }
                }
            }
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void findRedPkt(){
        if (!isReceivingHongbao) {
            return;
        }

        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }

        if (isAtChatWindow(rootNode)){
            //在聊天窗口
            LogUtil.i("在聊天窗口2222222");
            AccessibilityNodeInfo result = this.findRPFromChatWindow(rootNode);
            if (result != null) {
                super.delayClick(result, 0);
                this.isReceivingHongbao = false;
            }
        }else{
            //在微信主页找
//            LogUtil.i("在微信主页222222");
            AccessibilityNodeInfo result = this.findRPFromMsgListWindow(rootNode);
            if (result != null) {
//                LogUtil.i("找到红包2222");
                super.delayClick(result, 0);
                this.isReceivingHongbao = false;
            }else{
//                LogUtil.i("没找到红包2222");
            }
        }
        rootNode.recycle();
    }

    //是否在聊天窗口
    private boolean isAtChatWindow(AccessibilityNodeInfo nodeInfo) {
        boolean flag = false;
        if (nodeInfo == null) {
            return flag;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(WXParams.KEY_RETURN_DESC);
        if (list != null) {
            for (AccessibilityNodeInfo node : list) {
                if (WXParams.CLASS_NAME_IMAGEVIEW.equals(node.getClassName())) {
                    if (WXParams.KEY_RETURN_DESC.equals(node.getContentDescription())) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }
    //从聊天窗口寻找红包
    private AccessibilityNodeInfo findRPFromMsgListWindow(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo result = null;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(WXParams.KEY_WECHATREDPACKET);
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }
    //从聊天窗口寻找红包
    private AccessibilityNodeInfo findRPFromChatWindow(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo result = null;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(WXParams.KEY_GETREDPACKET);
        if (list != null && !list.isEmpty()) {
            result = list.get(list.size() - 1);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean findBackButton(){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return false;
        }

        String backBtnId = null;
        if (getBaseConfig().getWXVersionCode() <= 861){
            backBtnId = "com.tencent.mm:id/fb";
        }else {
            backBtnId = "com.tencent.mm:id/gq";
        }
        AccessibilityNodeInfo info = AccessibilityUtil.findNodeInfosById(rootNode,backBtnId);
        if (info != null){
            AccessibilityUtil.performClick(info);
            return true;
        }
        rootNode.recycle();
        return false;
    }
    //钱数量
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getMoneyCount(){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null){
            return;
        }

        AccessibilityNodeInfo info = AccessibilityUtil.findNodeInfosByText(rootNode,"元");

        if (info == null){
            return;
        }

        if (null == info.getParent()){
            return;
        }

        if (info.getParent().getChildCount()<3){
            return;
        }

        if (!WXParams.CLASS_NAME_TEXTVIEW.equals(info.getParent().getChild(2).getClassName())){
            return;
        }
        String moneyCount = info.getParent().getChild(2).getText().toString();

        if ("".equals(moneyCount)){
            return;
        }
        LogUtil.i("WX钱：" + moneyCount);
        SaveCountUtil countUtil = SaveCountUtil.getInitialize(getService().getApplicationContext());
        if (null != countUtil){
            //红包数量
            countUtil.setRPCount(countUtil.getRPCount() + 1);
            //钱数量
            String saveMn = countUtil.getMoneyCount();
            if (!"".equals(saveMn)){
                try{
                    BigDecimal b1 = new BigDecimal(saveMn);
                    BigDecimal b2 = new BigDecimal(moneyCount);
                    String b3 = b1.add(b2).toString();
                    countUtil.setMoneyCount(b3);
                    LogUtil.i("WX保存后红包：" + countUtil.getRPCount() + "钱：" + countUtil.getMoneyCount());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        rootNode.recycle();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean isFindOpenPacketButton(){
        boolean isFind = false;
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            isFind = false;
        }
        AccessibilityNodeInfo targetNode = null;
        if (getBaseConfig().getWXVersionCode() < WXParams.WECHAT_VERSION_MIN){
            targetNode = AccessibilityUtil.findNodeInfosByText(rootNode, "拆红包");
        }else{
            String buttonId = "com.tencent.mm:id/b43";
            int versionCode = getBaseConfig().getWXVersionCode();
            if (versionCode == 700) {
                buttonId = "com.tencent.mm:id/b2c";
            } else if (versionCode == 840) {
                buttonId = "com.tencent.mm:id/ba_";
            }else if (versionCode == 861){
                buttonId = "com.tencent.mm:id/bag";
            }else if(versionCode == 960){
                buttonId = "com.tencent.mm:id/bdh";
            }

            if (buttonId != null) {
                targetNode = AccessibilityUtil.findNodeInfosById(rootNode, buttonId);
            }

            if (targetNode == null) {
                targetNode = this.getCanGrabNode(rootNode);
            }

            if (targetNode != null){
                isFind = true;
            }else{
                isFind =false;
            }
        }
        return isFind;
    }

    //拆红包
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void ClickOpenPacketButton() {

//      最新:微信 versioncode: 960  versionName: 6.3.32
//      qq versioncode: 442  nversionName: 6.6.1
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        AccessibilityNodeInfo targetNode = null;
        if (getBaseConfig().getWXVersionCode() < WXParams.WECHAT_VERSION_MIN){
            targetNode = AccessibilityUtil.findNodeInfosByText(rootNode, "拆红包");
        }else{
//            com.tencent.mm:id/bag
            String buttonId = "com.tencent.mm:id/b43";
            int versionCode = getBaseConfig().getWXVersionCode();
            if (versionCode == 700) {
                buttonId = "com.tencent.mm:id/b2c";
            } else if (versionCode == 840) {
                buttonId = "com.tencent.mm:id/ba_";
            }else if (versionCode == 861){
                buttonId = "com.tencent.mm:id/bag";
            }else if(versionCode == 960){
                buttonId = "com.tencent.mm:id/bdh";
            }

            if (buttonId != null) {
                targetNode = AccessibilityUtil.findNodeInfosById(rootNode, buttonId);
            }

            if (targetNode == null) {
                targetNode = this.getCanGrabNode(rootNode);
            }

            if (targetNode != null){
                super.delayClick(targetNode, 0);
            }
            rootNode.recycle();
        }
    }

    private AccessibilityNodeInfo getCanGrabNode(AccessibilityNodeInfo rootNode) {
        this.mGrabNode = null;
        this.findCanGrabNode(rootNode);
        return this.mGrabNode;
    }

    private void findCanGrabNode(AccessibilityNodeInfo rootNode) {
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mGrabNode != null) {
                rootNode.recycle();
                return;
            }
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (WXParams.CLASS_NAME_BUTTON.equals(node.getClassName())) {
                this.mGrabNode = node;
                return;
            } else {
                this.findCanGrabNode(node);
            }
        }
    }
}
