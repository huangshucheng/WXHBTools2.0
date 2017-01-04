package com.junyou.hbks.robmoney;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.config.BaseConfig;
import com.junyou.hbks.utils.AccessibilityUtil;
import com.junyou.hbks.utils.LockScreenUtil;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.utils.SaveCountUtil;
import com.junyou.hbks.utils.UmengUtil;

import java.math.BigDecimal;
import java.util.List;

public class QQRobMoney extends BaseRobMoney{

    private int mCurrentWindow = QQParams.WINDOW_NONE;
    private boolean isReceivingHongbao = false;
    private AccessibilityNodeInfo mListViewNode = null;

    public QQRobMoney(AccessibilityService service, BaseConfig baseConfig){
        super(service,baseConfig);
    }
    //setp one
    @Override
    public void doNotificationStateChanged(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            String content = String.valueOf(texts.get(0));
            int index = content.indexOf(":") + 1;
            if (index != 0) {
                content = content.substring(index);
            }

            if (content.contains(QQParams.KEY_QQREDPACKET)) {
                LogUtil.i("是qq红包");
                Parcelable data = event.getParcelableData();
                if (data == null || !(data instanceof Notification)) {
                    return;
                }
                this.isReceivingHongbao = true;

                if (super.getBaseConfig().isAutoUnlock()&& !LockScreenUtil.isUnlocking()) {
                    LogUtil.i("解锁屏幕");
                    LockScreenUtil.getInitialize(getService().getApplicationContext()).UnlockScreen();
                }
                //打开通知栏消息
                Notification notification = (Notification) data;
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }else{
                LogUtil.i("不是qq红包");
            }
        }
    }
    //setp two
    //进入app，进入聊天列表，滑动列表等都会调用，频繁调用
    @Override
    public void doWindowContentChanged(AccessibilityEvent event) {
//        LogUtil.i("目录改变");
        if (mCurrentWindow == QQParams.WINDOW_SPLASH_ACTIVITY) {
            this.findRedPktWhenContenChanged(event);
        }
    }

    //setp three
    //进入qq先调用，进入对话框，进入红包，进入红包详情等都会调用,频率低
    @Override
    public void doWindowStateChanged(AccessibilityEvent event) {
//        LogUtil.i("状态改变");
        if (!this.isReceivingHongbao) {
            return;
        }
        CharSequence className = event.getClassName();
        if (className == null) {
            return;
        }

        if (QQParams.UI_SPLASH_ACTIVITY.equals(className)) {
            //在聊天界面,去打开红包
            this.mCurrentWindow = QQParams.WINDOW_SPLASH_ACTIVITY;
            findRedPktWhenStateChanged();
            LogUtil.i("在聊天界面<<<<<<");
        }else if (QQParams.UI_QWALLET_PLUGIN_PROXY_ACTIVITY.equals(className)) {
            //在红包详情界面（第一次拆开红包）
            this.mCurrentWindow = QQParams.WINDOW_QWALLET_PLUGIN_PROXY_ACTIVITY;
            LogUtil.i("在红包详情界面<<<<<<");
            getMoneyCount();
            backAndGotoDesktop();
            this.isReceivingHongbao = false;
        }else if (QQParams.UI_QQLS_ACTIVITY.equals(className)){
            //红包在详情界面(已经领完)
            this.mCurrentWindow = QQParams.WINDOW_QQLS_ACTIVITY;
            LogUtil.i("在已经领完界面<<<<<<");
        }else{
            //在其他界面
            this.mCurrentWindow = QQParams.WINDOW_NONE;
            LogUtil.i("在其他界面<<<<<<");
        }
    }

    private void backAndGotoDesktop(){

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AccessibilityNodeInfo titleLeft = findBackTitleLeft();
            if (titleLeft != null){
                AccessibilityUtil.performClick(titleLeft);
                LogUtil.i("找到列表返回按钮1111");
            }else{
                LogUtil.i("没有找到列表返回按钮1111");
            }
                AccessibilityUtil.performBack(getService());
//               AccessibilityUtil.performHome(getService());
                AccessibilityUtil.gotoDeskTop(getService());
                if (LockScreenUtil.isUnlocking()){
                    LockScreenUtil.getInitialize(getService().getApplicationContext()).LockScreen();
                }
            }},0);
    }

    //返回消息界面按钮
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private AccessibilityNodeInfo findBackTitleLeft(){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        AccessibilityNodeInfo resultInfo = AccessibilityUtil.findNodeInfosById(rootNode,"com.tencent.mobileqq:id/ivTitleBtnLeft");
        if (resultInfo != null){
            if (QQParams.CLASS_NAME_TEXTVIEW.equals(resultInfo.getClassName())){
                if (!"".equals(resultInfo.getContentDescription())){
                    if (QQParams.KEY_RETURN_DESC.equals(resultInfo.getContentDescription().toString())){
                        return resultInfo;
                    }
                }
            }
        }
        return null;
    }

    //找返回按钮
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private AccessibilityNodeInfo findBackButton(){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        //TODO
        AccessibilityNodeInfo resultInfo = null;
        resultInfo = AccessibilityUtil.findNodeInfosById(rootNode,"com.tencent.mobileqq:id/name");
        if (resultInfo != null){
            if (QQParams.CLASS_NAME_IMAGEBUTTON.equals(resultInfo.getClassName())){
                if (QQParams.KEY_CLOSED.equals(resultInfo.getContentDescription().toString())){
                    return resultInfo;
                }
            }
        }
        return null;
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

        if (info.getParent().getChildCount()<4){
            return;
        }

        if (!QQParams.CLASS_NAME_TEXTVIEW.equals(info.getParent().getChild(3).getClassName())){
            return;
        }

        String moneyCount = info.getParent().getChild(3).getText().toString();

        if ("".equals(moneyCount)){
            return;
        }

        LogUtil.i("qq钱：" + moneyCount);
        SaveCountUtil countUtil = SaveCountUtil.getInitialize(getService().getApplicationContext());
        if (null != countUtil){
            //红包数量
            countUtil.setRPCount(countUtil.getRPCount() + 1);
            UmengUtil.YMgrasp_num(getService().getApplicationContext());
            //钱数量
            String saveMn = countUtil.getMoneyCount();
            if (!"".equals(saveMn)){
                try{
                    BigDecimal b1 = new BigDecimal(saveMn);
                    BigDecimal b2 = new BigDecimal(moneyCount);
                    String b3 = b1.add(b2).toString();
                    countUtil.setMoneyCount(b3);
                    UmengUtil.YMGrasb_money(getService().getApplicationContext(),"" + moneyCount);
                    LogUtil.i("QQ保存后红包数：" + countUtil.getRPCount() + "钱数：" + b3);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void findRedPktWhenContenChanged(AccessibilityEvent event){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        boolean isOnChatWin = isOnChatWindow(rootNode);
        if (isOnChatWin){
//            LogUtil.i("在聊天窗口");
            //在聊天窗口，先寻找是否存在其它群或好友的聊天信息
            if(clickOrtherChatMsg(rootNode)){
                LogUtil.i("其他聊天有红包");
                return;
            }
            //TODO 如果聊天列表有红包则拆开
//            findRedPktWhenStateChanged();
        }
        rootNode.recycle();
    }
    //寻找是否存在其它群或好友的聊天信息
    private boolean clickOrtherChatMsg(AccessibilityNodeInfo rootNode){
        boolean flag = false;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(QQParams.KEY_QQREDPACKET);
        if (list != null && !list.isEmpty()) {
            //在聊天页面中，当有别的聊天信息的时，会在标题栏下弹出新信息提示组件
            AccessibilityNodeInfo node = list.get(0);
            if (QQParams.CLASS_NAME_TEXTVIEW.equals(node.getClassName())) {
                CharSequence charSequence = node.getText();
                if (charSequence != null) {
                    String content = String.valueOf(charSequence);
                    int index = content.indexOf(":") + 1;
                    if (index != 0) {
                        content = content.substring(index);
                    }
                    if (content.startsWith(QQParams.KEY_QQREDPACKET)) {
//                        LogUtil.i("别的聊天有红包");
                        super.delayClick(node, 0);
                        this.isReceivingHongbao = true;
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    //TODO 捕获聊天窗口抢红包
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void findRedPktAtChatView(){
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        //取得聊天的列表信息
        rootNode.recycle();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void findRedPktWhenStateChanged(){
        if (!this.isReceivingHongbao) {
            return;
        }

        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        //取得聊天的列表信息
        AccessibilityNodeInfo listView = this.getChatAbsListView(rootNode);
        if (listView != null){
            int childCount = listView.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {//从最后一个开始往上读
                AccessibilityNodeInfo rpNode = listView.getChild(i);
                if (rpNode != null){
                    if (this.isRPNode(rpNode)) {
                        this.openRPNode(rpNode.getChild(rpNode.getChildCount() - 1));
                        LogUtil.i("是红包节点");
                        break;
                    }
                }
            }
            listView.recycle();
        }
        rootNode.recycle();
    }
    //判断辅助节点是否为红包节点
    private boolean isRPNode(AccessibilityNodeInfo rpNode) {
        if (rpNode == null){
            return false;
        }
        boolean flag = false;
        if (rpNode != null){
            if (QQParams.CLASS_NAME_RELATIVELAYOUT.equals(rpNode.getClassName())) {
                int size = rpNode.getChildCount();
                if (size > 0) {
                    if (rpNode.getChild(size - 1) != null){
                        if (QQParams.CLASS_NAME_RELATIVELAYOUT.equals(rpNode.getChild(size - 1).getClassName())) {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;
    }
    //打开红包，指令红包或普通红包
    private void openRPNode(final AccessibilityNodeInfo rpNode){
        if (rpNode == null) {
            return;
        }
        CharSequence nodeDescInfo = rpNode.getContentDescription();
        if (nodeDescInfo != null){
            String desc = String.valueOf(nodeDescInfo);
            if (desc.endsWith(QQParams.KEY_CLICK_GET_COMMAND)){
                //口令红包
                getHandler().post(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        delayClick(rpNode,0);
                        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
                        if (rootNode != null){
                            if(clickInputCommand(rootNode)){
                                clickSendCommand(rootNode);
                            }
                        }
                    }
                });
            }else if(desc.endsWith(QQParams.KEY_CLICK_LOOK_DETAIL)){
                //普通红包，拼手气红包
                super.delayClick(rpNode,0);
            }
        }
    }
    //输入口令
    private boolean clickInputCommand(AccessibilityNodeInfo rootNode){
        boolean flag = false;
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(QQParams.KEY_CLICK_INPUT_COMMAND);
        if(list!=null && !list.isEmpty()){
            AccessibilityNodeInfo node = list.get(0);
            super.delayClick(node,0);
            flag = true;
        }
        return flag;
    }
    //点击发送
    private void clickSendCommand(AccessibilityNodeInfo rootNode){
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(QQParams.KEY_SEND);
        if(list!=null && !list.isEmpty()){
            int size = list.size();
            for(int i = size - 1 ; i >= 0 ; i--){
                AccessibilityNodeInfo node = list.get(i);
                if(QQParams.CLASS_NAME_BUTTON.equals(node.getClassName())){
                    super.delayClick(node,0);
                    break;
                }else{
                    node.recycle();
                }
            }
        }
    }

    private AccessibilityNodeInfo getChatAbsListView(AccessibilityNodeInfo rootNode){
        if (rootNode != null){
            this.mListViewNode = null;
            this.findChatAbsListView(rootNode);
            return this.mListViewNode;
        }
        return null;
    }

    //递归寻找聊天列表信息辅助节点,所有聊天信息都放在此节点下，包括红包
    private void findChatAbsListView(AccessibilityNodeInfo rootNode) {
        if (rootNode == null){
            return;
        }
        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if(this.mListViewNode != null){
                rootNode.recycle();
                return;
            }
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (node != null){
                if (QQParams.CLASS_NAME_ABSLISTVIEW.equals(node.getClassName())) {
                    this.mListViewNode = node;
                    return;
                } else {
                    this.findChatAbsListView(node);
                }
            }
        }
    }

    //判断是否在聊天窗口
    private boolean isOnChatWindow(AccessibilityNodeInfo rootNode) {
        boolean flag = false;
        if (rootNode == null) {
            return flag;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(QQParams.KEY_RETURN_DESC);
        if (list != null) {
            for (AccessibilityNodeInfo node : list) {
                if (QQParams.CLASS_NAME_TEXTVIEW.equals(node.getClassName())) {
                    if (QQParams.KEY_RETURN_DESC.equals(node.getContentDescription())) {
                        flag = true;
                        node.recycle();
                        break;
                    }else{
                        node.recycle();
                    }
                }
            }
        }
        return flag;
    }
}
