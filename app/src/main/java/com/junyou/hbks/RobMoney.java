package com.junyou.hbks;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Parcelable;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.junyou.hbks.Utils.SoundUtil;
import com.junyou.hbks.Utils.UmengUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RobMoney extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static RobMoney instance;

    private static final String WECHAT_DETAILS_EN = "Details";
    private static final String WECHAT_DETAILS_CH = "红包详情";
    private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    private static final String WECHAT_BETTER_LUCK_CH = "手慢了";
    private static final String WECHAT_EXPIRES_CH = "已超过24小时";
    private static final String WECHAT_VIEW_SELF_CH = "查看红包";
    private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";
    private static final String WECHAT_NOTIFICATION_TIP = "[微信红包]";
    private static final String WECHAT_LUCKMONEY_RECEIVE_ACTIVITY = "LuckyMoneyReceiveUI";
    private static final String WECHAT_LUCKMONEY_DETAIL_ACTIVITY = "LuckyMoneyDetailUI";
    private static final String WECHAT_LUCKMONEY_GENERAL_ACTIVITY = "LauncherUI";
    private String currentActivityName = WECHAT_LUCKMONEY_GENERAL_ACTIVITY;

    private boolean mMutex = false, mListMutex = false, mChatMutex = false;
    private SharedPreferences sharedPreferences;
    private AccessibilityNodeInfo rootNodeInfo, mReceiveNode, mUnpackNode;
    private boolean mLuckyMoneyPicked, mLuckyMoneyReceived;
    private int mUnpackCount = 0;
    private boolean mIsEnterWeChatList=false;
    private boolean mIsGraspOver = false;

    //四个标签的存储字符
    private String mTotalNum = "totalnum";
    private String mTotalMoney = "totalmoney";

    //-----------[QQ红包]---------------//
    static final String QQ_HONGBAO_TEXT_KEY = "[QQ红包]";
    private boolean caihongbao = false;
    private AccessibilityNodeInfo rootNodeInfo_1;
    private List<AccessibilityNodeInfo> mReceiveNode_1;
    private boolean mLuckyMoneyReceived_1;
    private String lastFetchedHongbaoId = null;
    private long lastFetchedTime = 0;
    private static final int MAX_CACHE_TOLERANCE = 5000;
    private static final String WECHAT_OPEN_EN = "Open";
    private static final String WECHAT_OPENED_EN = "You've opened";
    private final static String QQ_DEFAULT_CLICK_OPEN = "点击拆开";
    //    private final static String QQ_DEFAULT_HAVE_OPENED = "已拆开";
    private final static String QQ_HONG_BAO_PASSWORD = "口令红包";
    private final static String QQ_CLICK_TO_PASTE_PASSWORD = "点击输入口令";

    //广播
    private MsgReceiver msgReceiver;
    public boolean mIsWeChatOn = true;
    public boolean mIsQQOn = true;

    //-----------锁屏、唤醒相关---------------//
    //锁屏判断
    private boolean isScreenOff;
    private ScreenOnOffReceiver mReceiver;
    private KeyguardManager mKeyguardManager ;
    private KeyguardManager.KeyguardLock mKeyguardLock ;//安全锁
    private PowerManager mPowerManager;                 //电源管理
    private PowerManager.WakeLock mWeakLock;            //唤醒锁
    private boolean isPrepare = false;                  //是否进行了亮屏解锁操作
    /**
     * 广播接收器
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            if (RobMoney.getInstance() != null)
            {
                RobMoney.getInstance().mIsWeChatOn = intent.getBooleanExtra("wechat_broadcast", true);
//            String v_1 = RobMoney.getInstance().mIsWeChatOn==true ? "可接收":"不可接收";
//            Log.i("TAG", "微信消息:" + v_1);

                RobMoney.getInstance().mIsQQOn = intent.getBooleanExtra("qq_broadcast",true);
//            String v_2 = RobMoney.getInstance().mIsQQOn == true ? "可接收":"不可接收";
//            Log.i("TAG", "qq消息" + v_2);
            }
        }
    }

    /**
     * 屏幕是否锁屏判断
     */
    class ScreenOnOffReceiver extends BroadcastReceiver {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {      //暗屏----
//                Log.i("TAG", "已经锁屏");
                if (RobMoney.getInstance()!= null){
                    RobMoney.getInstance().isScreenOff = true;
                }
            }
            else if (Intent.ACTION_SCREEN_ON.equals(action)) { //亮屏----
//                Log.i("TAG", "未锁屏");
                if (RobMoney.getInstance() != null){
                    RobMoney.getInstance().isScreenOff = false;
                }
            }
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.junyou.hbks.SETTING");
        registerReceiver(msgReceiver, intentFilter);
        SoundUtil.initSoundPool(this);
    }

    public static RobMoney getInstance()
    {
        if (instance != null){
            return instance;
        }
        return  null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)     //屏幕内容变化
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)         //屏幕状态变化

        if (sharedPreferences == null)
        {
            return;
        }
        //关闭了红包快手
        if (!sharedPreferences.getBoolean("pref_watch_notification",true))
        {
//            Log.i("TAG", "不能抢红包了，关闭了红包助手");
            return;
        }

        boolean isflagOn = getSharedPreferences("config",MODE_MULTI_PROCESS).getBoolean(Constants.ISOPEN_FLAG,true);
        if (!isflagOn){
//            Log.i("TAG", "不能抢红包了...");
            return;
        }

        boolean isServiceOn = getSharedPreferences("config",MODE_MULTI_PROCESS).getBoolean(Constants.IS_SERVICE_ON,true);
        if (!isServiceOn){
//            Log.i("TAG", "不能抢红包了，没日期了");
            return;
        }

        //test
//        String v_1 = mIsWeChatOn==true ? "可接收":"不可接收";
//        Log.i("TAG", "Rob微信消息:" + v_1);
//        String v_2 = mIsQQOn == true ? "可接收":"不可接收";
//        Log.i("TAG", "Robqq消息" + v_2);

        setCurrentActivityName(event);
        /* 检测通知消息 */
        if (!mMutex)
        {
            //是否是红包的判断，若是红包就打开消息栏进入该软件，若不是红包直接返回
            if (watchNotifications(event)){
                return;
            }
            //若是红包，执行点击红包的操作
            if (mIsQQOn){
                if(openQQHongbao(event)) return;
            }
            //监视微信（貌似这个方法没作用）
//            if (openWeChatHongbao(event)) return;
            mListMutex = false;
        }
        if (!mChatMutex)
        {
            try {
                mChatMutex = true;
                watchChat(event);           //  监视微信
                mChatMutex = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //查找红包列表，执行点击红包事件
    private boolean openWeChatHongbao(AccessibilityEvent event)
    {
        if (mListMutex) return false;

        mListMutex = true;
        AccessibilityNodeInfo eventSource = event.getSource();
        // Not a message
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventSource == null)
        {
            return false;
        }
        List<AccessibilityNodeInfo> nodes = eventSource.findAccessibilityNodeInfosByText(WECHAT_NOTIFICATION_TIP);

        if (!nodes.isEmpty() && currentActivityName.contains(WECHAT_LUCKMONEY_GENERAL_ACTIVITY))
        {
            AccessibilityNodeInfo nodeToClick = nodes.get(0);

            if (nodeToClick == null) return false;

            CharSequence contentDescription = nodeToClick.getContentDescription(); //从红包节点上获取的值

            if (contentDescription != null)
            {
//                Log.i("TAG", "first 打开红包");
                nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);		//自动打开红包
                return true;
            }
        }
        return false;
    }

    //观察通知栏的消息，查找[微信红包]或者[QQ红包关]关键字，打开通知栏消息
    private boolean watchNotifications(AccessibilityEvent event)
    {
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
        {
            return false;
        }
//        Log.i("TAG","通知栏有消息!!!");

        String tip = event.getText().toString();

        /*
        *   微信开 qq开
        *   微信关 qq关
        *   微信开 qq关
        *   微信关 qq开
        *   否则 都开
        * */

        if (mIsWeChatOn && mIsQQOn ){
//            Log.i("TAG", "...都开" );
            if (tip.contains(WECHAT_NOTIFICATION_TIP) || tip.contains(QQ_HONGBAO_TEXT_KEY)) {
                SoundUtil.playSounds(1,0);
                mIsEnterWeChatList = true;
                if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
                    return false;
                }
                Boolean lockScreenLockFlag = sharedPreferences.getBoolean("pref_suoping_grasp", true);
                if (lockScreenLockFlag) {
                    if (!isScreenOn(this)){
                        lightScreen();
                        isPrepare = true;
                    }else{
                        isPrepare = false;
                    }
                    if (isLockOn()){
                        unLock();
                        isPrepare = true;
                    }else{
                        isPrepare = isPrepare | false;
                    }
                    openXiaoxiLan(event);
                    return false;
                }else {
                    openXiaoxiLan(event);
                    return false;
                }
            }
            else {
//                Log.i("TAG","不是红包");
            }
        }else if (!mIsWeChatOn &&  !mIsQQOn){
//            Log.i("TAG", "...都关" );
        }else if (mIsWeChatOn && !mIsQQOn){
//            Log.i("TAG", "...微信开，qq关" );
            if (tip.contains(WECHAT_NOTIFICATION_TIP)) {
//                Log.i("TAG","是微信红包~~~");
                SoundUtil.playSounds(1,0);
                mIsEnterWeChatList = true;
                if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
                    return false;
                }
                Boolean lockScreenLockFlag = sharedPreferences.getBoolean("pref_suoping_grasp", true);
                if (lockScreenLockFlag) {
                    if (!isScreenOn(this)){
                        lightScreen();
                        isPrepare = true;
                    }else{
                        isPrepare = false;
                    }
                    if (isLockOn()){
                        unLock();
                        isPrepare = true;
                    }else{
                        isPrepare = isPrepare | false;
                    }
                    openXiaoxiLan(event);
                    return true;
                }else {
                    openXiaoxiLan(event);
                    return true;
                }
            }
            else {
//                Log.i("TAG","不是红包");
            }
        }else if (!mIsWeChatOn && mIsQQOn){
//            Log.i("TAG", "...微信关，qq开" );
            if (tip.contains(QQ_HONGBAO_TEXT_KEY)) {
//                Log.i("TAG","是qq红包~~~");
                SoundUtil.playSounds(1,0);
                if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
                    return false;
                }
                Boolean lockScreenLockFlag = sharedPreferences.getBoolean("pref_suoping_grasp", true);
                if (lockScreenLockFlag) {
                    if (!isScreenOn(this)){
                        lightScreen();
                        isPrepare = true;
                    }else{
                        isPrepare = false;
                    }
                    if (isLockOn()){
                        unLock();
                        isPrepare = true;
                    }else{
                        isPrepare = isPrepare | false;
                    }
                    openXiaoxiLan(event);
                    return true;
                }else {
                    openXiaoxiLan(event);
                    return true;
                }
            }
            else {
//                Log.i("TAG","不是红包");
            }
        }else{
//            Log.i("TAG", "...其他,默认全开" );
        }
        /*
        if (tip.contains(WECHAT_NOTIFICATION_TIP) || tip.contains(QQ_HONGBAO_TEXT_KEY)) {
                Log.i("TAG","是红包~~~");
                mIsEnterWeChatList = true;
                if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
                    return false;
                }
                Boolean lockScreenLockFlag = sharedPreferences.getBoolean("pref_suoping_grasp", true);
                if (lockScreenLockFlag) {
                    mParcelable = event.getParcelableData();
                    wakeAndUnlock(true);//解锁屏幕
                    return true;
                }else {
                    Parcelable parce = event.getParcelableData();
                    if (parce instanceof Notification) {
                        Notification notification = (Notification) parce;
                        try {
                                notification.contentIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                    }
                        return true;
                }
            }
            else {
                Log.i("TAG","不是红包");
            }
        */
        return true;
    }

    //打开消息栏
    public void openXiaoxiLan(AccessibilityEvent event)
    {
        Parcelable mParcelable = event.getParcelableData();
        if (mParcelable instanceof Notification)
        {
            Notification notification = (Notification) mParcelable;
            try {
                notification.contentIntent.send();
            } catch (PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setCurrentActivityName(AccessibilityEvent event)
    {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            return;
        }

//        Log.i("TAG","window state changed");
        try {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
        } catch (PackageManager.NameNotFoundException e)
        {
            currentActivityName = WECHAT_LUCKMONEY_GENERAL_ACTIVITY;
        }
    }

    private void watchFlagsFromPreference()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onInterrupt()
    {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {

    }

     @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
     @Override
    public void onServiceConnected()
    {
//        Log.i("TAG","onServiceConnected");
        super.onServiceConnected();
        this.watchFlagsFromPreference();
        AccessibilityServiceInfo info = getServiceInfo();
        //这里可以设置多个包名，监听多个应用
        info.packageNames = new String[]{"com.tencent.mobileqq","com.tencent.mm"};
        setServiceInfo(info);

        //锁屏
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(Integer.MAX_VALUE);

        mReceiver = new ScreenOnOffReceiver();
        registerReceiver(mReceiver, filter);

        //返回键 返回两次 todo 返回时候会奔溃
        performGlobalAction(GLOBAL_ACTION_BACK);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        },200);
        //gotoDeskTop();
        openNotifocation();
        //关闭通知
        /*
        try{
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
//        Log.i("TAG","service onStartCommand");
//        return super.onStartCommand(intent, flags, startId);
        //防止服务被系统kill掉
        return super.onStartCommand(intent, START_STICKY, startId);

    }

    private void openNotifocation()
    {
        /*  已经弃用
        //创建到一个Notification对象，设置通知栏上面的图标，时间
        Notification notification = new Notification(R.mipmap.ic_launcher,"红包快手",System.currentTimeMillis());
        //设置当点击通知时会跳转到哪个界面
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, intent, 0);
        //设置通知栏上面的标题，内容等信息
        notification.setLatestEventInfo(this, "hbtools","hongbaokuaishou",pendingIntent);
        //开启前台服务，第一个参数一般有两种选择FLAG_ONGOING_EVENT表示正在运行，这种类型的通知是无法清除掉的
        //另外一种是FLAG_AUTO_CANCEL，这个标志表明点击了这个通知之后，就自行的从通知栏上清除掉。
        startForeground(Notification.FLAG_ONGOING_EVENT, notification);
        */
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
         builder.setContentIntent(contentIndent)
                .setSmallIcon(R.mipmap.notify_icon)//设置状态栏里面的图标（小图标）
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setAutoCancel(true)//设置可以清除
                .setContentTitle("红包快手")//设置下拉列表里的标题
                .setContentText("亲,红包快手已经开启啦!");//设置上下文内容
        //        notificationManager.notify(1,notification);
        try{
            Notification notification = builder.getNotification();
//            Notification notification = builder.build();
            startForeground(Notification.FLAG_ONGOING_EVENT, notification);
        }catch (Exception e){
            e.printStackTrace();
        }

        /*
        *关于Notification的Flags
notification.flags = Notification.FLAG_NO_CLEAR; // 点击清除按钮时就会清除消息通知,但是点击通知栏的通知时不会消失
notification.flags = Notification.FLAG_ONGOING_EVENT; // 点击清除按钮不会清除消息通知,可以用来表示在正在运行
notification.flags |= Notification.FLAG_AUTO_CANCEL搜索; // 点击清除按钮或点击通知后会自动消失
notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
        * */
    }

    @Override
    public void onDestroy()
    {
        //注销广播
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        unregisterReceiver(mReceiver);
        stopForeground(true);
        try{
            if (mWeakLock != null){
                mWeakLock.release();
                mWeakLock = null;
            }
            if (mKeyguardLock!=null) {
                mKeyguardLock.reenableKeyguard();
                mKeyguardLock =null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        Log.i("TAG", "服务销毁了.................");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void watchChat(AccessibilityEvent event)
    {
//        rootNodeInfo = getRootInActiveWindow();
        rootNodeInfo = event.getSource();
        if (rootNodeInfo == null){
            return;
        }

        mReceiveNode = null;
        mUnpackNode = null;

        checkNodeInfo(event.getEventType());
        /* 如果已经接收到红包并且还没有戳开 */
//        if (mIsEnterWeChatList){
//            Log.i("TAG", "mIsEnterWeChatList true...");
//        }else{
//            Log.i("TAG", "mIsEnterWeChatList false...");
//        }
        if (mLuckyMoneyReceived && !mLuckyMoneyPicked && (mReceiveNode != null) && mIsEnterWeChatList)
        {
//            Log.i("TAG","收到红包并且还没有戳开");
            mMutex = true;
            if (mReceiveNode.getParent() != null){
                mReceiveNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            mLuckyMoneyReceived = false;
            mLuckyMoneyPicked = true;
        }
        /* 如果戳开但还未领取 */
        /*
        if (mUnpackCount>=1){
            Log.i("TAG", "mUnpackCount>=1");
        }else{
            Log.i("TAG", "mUnpackCount<1...");
        }
        if (mUnpackNode!=null){
            Log.i("TAG", "mUnpackNode 非空");
        }else{
            Log.i("TAG", "mUnpackNode 空...");
        }
         */

        if (mUnpackCount >= 1 && (mUnpackNode != null) && mIsEnterWeChatList)
        {
            try {
                // 点击一次就够了，这里点击了很多次  ok
//                Log.i("TAG","打开了红包。。。。");
                mUnpackNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                mIsEnterWeChatList =false;
            } catch (Exception e) {
                e.printStackTrace();
                mMutex = false;
                mLuckyMoneyPicked = false;
                mUnpackCount = 0;
            }
        }
        /*
        if (mIsGraspOver == true){
            if (getBackNode() != null){
                if (getBackNode().isClickable()){
                    getBackNode().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i("TAG", " 返回");
                }
            }
            gotoDeskTop();
            if (isPrepare) {
                clean();
            }
            mIsGraspOver = false;
        }
        */
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkNodeInfo(int eventType)
    {
        if (rootNodeInfo == null) {
            return;
        }
        /* 聊天会话窗口，遍历节点匹配“领取红包”和"查看红包" */
        AccessibilityNodeInfo node1 = getTheLastNode(WECHAT_VIEW_OTHERS_CH, WECHAT_VIEW_SELF_CH);
        if (node1 != null && currentActivityName.contains(WECHAT_LUCKMONEY_GENERAL_ACTIVITY))
        {
//          Log.i("TAG","遍历聊天窗口");
            mLuckyMoneyReceived = true;
            mReceiveNode = node1;
            return;
        }

        /* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
        AccessibilityNodeInfo node2 = findOpenButton(rootNodeInfo);
        if (node2 != null &&
                "android.widget.Button".equals(node2.getClassName()) &&
                currentActivityName.contains(WECHAT_LUCKMONEY_RECEIVE_ACTIVITY))
        {
            mUnpackNode = node2;
            mUnpackCount += 1;
//            Log.i("TAG", "有拆红包关键字,待拆红包："+ mUnpackCount +"个");
            return;
        }

        /* 戳开红包，红包已被抢完，遍历节点匹配“红包详情”和“手慢了” */
        boolean hasNodes = this.hasOneOfThoseNodes(
                WECHAT_BETTER_LUCK_CH, WECHAT_DETAILS_CH,
                WECHAT_BETTER_LUCK_EN, WECHAT_DETAILS_EN, WECHAT_EXPIRES_CH);

            if (mMutex && eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                    hasNodes &&
                    (currentActivityName.contains(WECHAT_LUCKMONEY_DETAIL_ACTIVITY) ||
                            currentActivityName.contains(WECHAT_LUCKMONEY_RECEIVE_ACTIVITY)))
        {
//            Log.i("TAG","红包被抢完");
            mMutex = false;
            mLuckyMoneyPicked = false;
            mUnpackCount = 0;
            mUnpackNode = null;
            mIsEnterWeChatList =false;
            mIsGraspOver = true;

            //在这里累加钱  todo  会让apk停止运行

            List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText("元");
            if (!list.isEmpty())
            {
                //下标是2的节点是红包的金额
                for (AccessibilityNodeInfo info:list)
                {
                    //原来是非空
//                    Log.i("TAG", "红包的钱:"+info.getParent().getChild(2).getText().toString());

                    SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                    String mm =  sharedP.getString(mTotalMoney,"");

                    if (info.getParent().getChildCount()<3){
                        continue;
                    }

                    if (!mm.isEmpty())
                    {
                        try{
                            BigDecimal b1 = new BigDecimal(mm);
                            BigDecimal b2 = new BigDecimal(info.getParent().getChild(2).getText().toString());
                            String b3 = b1.add(b2).toString();

//                            Log.i("TAG", "原来的钱："+ mm);
//                            Log.i("TAG", "现在的钱："+ b3);
                            SharedPreferences.Editor editor = sharedP.edit();
                            editor.putString(mTotalMoney,b3);
                            editor.apply();
//                            Log.i("TAG", "存储后的钱："+  String.valueOf(sharedP.getString(mTotalMoney,"0.00")));
                            addTotalNum();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else
                    {
                        try{
                            //原来是空的
                            BigDecimal b1 = new BigDecimal(info.getParent().getChild(2).getText().toString());
                            SharedPreferences.Editor editor = sharedP.edit();
                            editor.putString(mTotalMoney,b1.toString());
//                            Log.i("TAG", "原来没钱,现在的钱："+ b1.toString());
                            editor.apply();
//                            Log.i("TAG", "存储后的钱："+  String.valueOf(sharedP.getString(mTotalMoney,"0.00")));
                            addTotalNum();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            gotoDeskTop();
            if (isPrepare){
                clean();
            }
        }
    }

    /**
     * 返回桌面
     */
    private void gotoDeskTop()
    {
//        Log.i("TAG", "返回桌面");
        try{
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private AccessibilityNodeInfo getBackNode()
    {
        List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText("红包详情");
        if (!list.isEmpty()){
            AccessibilityNodeInfo parentNode = list.get(0).getParent();
            for (int i = 0;i<parentNode.getChildCount();i++){
                if (parentNode.getChild(i).getClassName().equals("android.widget.LinearLayout")){
                    if (parentNode.getChild(i).isClickable()){
                        return parentNode.getChild(i);
                    }
                }
            }
        }
        return  null;
    }

    private AccessibilityNodeInfo getTheLastNode(String... texts)
    {
        int bottom = 0;
        AccessibilityNodeInfo lastNode = null, tempNode;
        List<AccessibilityNodeInfo> nodes;

        for (String text : texts)
        {
            if (text == null) continue;
            nodes = this.rootNodeInfo.findAccessibilityNodeInfosByText(text);
            if (nodes != null && !nodes.isEmpty())
            {
                tempNode = nodes.get(nodes.size() - 1);
                if (tempNode == null) return null;
                Rect bounds = new Rect();
                tempNode.getBoundsInScreen(bounds);
                if (bounds.bottom > bottom)
                {
                    bottom = bounds.bottom;
                    lastNode = tempNode;
                }
            }
        }
        return lastNode;
    }

    private AccessibilityNodeInfo findOpenButton(AccessibilityNodeInfo node)
    {
        if (node == null)
            return null;

        //非layout元素
        if (node.getChildCount() == 0)
        {
            if ("android.widget.Button".equals(node.getClassName()))
                return node;
            else
                return null;
        }

        //layout元素，遍历找button
        AccessibilityNodeInfo button;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            button = findOpenButton(node.getChild(i));
            if (button != null)
                return button;
        }
        return null;
    }

    private boolean hasOneOfThoseNodes(String... texts)
    {
        List<AccessibilityNodeInfo> nodes;
        for (String text : texts)
        {
            if (text == null) continue;

            nodes = this.rootNodeInfo.findAccessibilityNodeInfosByText(text);

            if (nodes != null && !nodes.isEmpty()) return true;
        }
        return false;
    }

    /**
     * QQ红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean openQQHongbao(AccessibilityEvent event)
    {
//        Log.i("TAG", "openQQHongbao////////////////////");
        if (mListMutex){
            return false;
        }
        mListMutex = true;
        rootNodeInfo_1 = event.getSource();

        if (rootNodeInfo_1 == null) {
            return false;
        }

        mLuckyMoneyReceived_1 = false;
        mReceiveNode_1 = null;
        //todo  存数据有问题,MainActivity接受不到
        int eventType = event.getEventType();
        if (eventType== AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||eventType ==AccessibilityEvent.TYPE_VIEW_FOCUSED )
        {
            String className = String.valueOf(event.getClassName());
//            Log.i("TAG", "窗口变化:" + className);
//            if(className.equals("android.widget.FrameLayout"))
            if(className.equals("cooperation.qwallet.plugin.QWalletPluginProxyActivity"))
            {
//                Log.i("TAG", "红包详情界面" + className);
//                List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByText("元");
                List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByText("元");
                if (!list.isEmpty())
                {
//                    Log.i("TAG","有元字 size=" + list.size());
                    for (AccessibilityNodeInfo info:list)
                    {
                        if (info.getParent() == null){
                            continue;
                        }

                        if (info.getParent().getChildCount()<4){
                            continue;
                        }

                        if (!info.getParent().getChild(3).getClassName().equals("android.widget.TextView")){
                            continue;
                        }

                        if (info.getParent().getChild(3).getText().toString().isEmpty()){
                            continue;
                        }

//                            Log.i("TAG", "qq红包有:"+info.getParent().getChild(3).getText().toString() + "元");
                            String oldString = info.getParent().getChild(3).getText().toString();
                            //过滤字符串
                            if (oldString.isEmpty()){
                                continue;
                            }
                            int isContinue = oldString.indexOf(":");
                            int isContinue_1 = oldString.indexOf("免密码");
                            int isContinue_2 = oldString.indexOf("支付密码");     //这里过滤要求输入密码的，发红包的支付确认界面
                            if (isContinue <= 0 && isContinue_1 <= 0 && isContinue_2 <=0)
                            {
//                              Log.i("TAG", "是正确的金额"+ info.getParent().getChild(3).getText().toString()+ "元");
                                //在这里累积钱
                                SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                                String mm =  sharedP.getString(mTotalMoney,"");
                                if (!mm.isEmpty())
                                {
                                    //原来是非空的
                                    try{
                                        BigDecimal b1 = new BigDecimal(mm);
                                        BigDecimal b2 = new BigDecimal(oldString);
                                        String b3 = b1.add(b2).toString();

//                                        Log.i("TAG", "原来的钱："+ mm);
//                                        Log.i("TAG", "现在的钱："+ b3);

                                        SharedPreferences.Editor editor = sharedP.edit();
                                        editor.putString(mTotalMoney,b3);
                                        editor.apply();

//                                        Log.i("TAG", "存储后的钱："+  String.valueOf(sharedP.getString(mTotalMoney,"0.00")));
                                        addTotalNum();  //添加红包个数
                                        //返回桌面
                                        performGlobalAction(GLOBAL_ACTION_BACK);
                                        gotoDeskTop();
                                        if (isPrepare){
                                            clean();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }else
                                {
                                    try{
                                        //原来是空的
                                        BigDecimal b1 = new BigDecimal(oldString);
//                                        Log.i("TAG", "原来没钱,现在的钱："+ b1.toString());
                                        SharedPreferences.Editor editor = sharedP.edit();
                                        editor.putString(mTotalMoney,b1.toString());
                                        editor.apply();
//                                        Log.i("TAG", "存储后的钱："+  String.valueOf(sharedP.getString(mTotalMoney,"0.00")));
                                        addTotalNum();  //添加红包个数
                                        //返回桌面
                                        performGlobalAction(GLOBAL_ACTION_BACK);
                                        gotoDeskTop();
                                        if (isPrepare){
                                            clean();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        /*
                                //返回桌面
                                performGlobalAction(GLOBAL_ACTION_BACK);
                                gotoDeskTop();
                                if (isPrepare){
                                    clean();
                                }
                                */
                    }
                }
            }
        }

        /* 聊天会话窗口，遍历节点匹配“点击拆开”，“口令红包”，“点击输入口令” */
        List<AccessibilityNodeInfo> nodes1 = this.findAccessibilityNodeInfosByTexts(this.rootNodeInfo_1, new String[]{
                QQ_DEFAULT_CLICK_OPEN, QQ_HONG_BAO_PASSWORD, QQ_CLICK_TO_PASTE_PASSWORD});
        if (!nodes1.isEmpty()) {
            String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo_1));
//            Log.i("TAG", "nodeId:"+nodeId + "  lastID:" + lastFetchedHongbaoId);
            if (!nodeId.equals(lastFetchedHongbaoId)) {
//                Log.i("TAG", "有QQ红包字眼出现");
                mLuckyMoneyReceived_1 = true;
                mReceiveNode_1 = nodes1;
            }else {
                return false;
            }
        }else {
                return  false;
             }

        /* 如果已经接收到红包并且还没有戳开 */
        if (mLuckyMoneyReceived_1 && (mReceiveNode_1 != null))
        {
            int size = mReceiveNode_1.size();
            if (size > 0)
            {
                String id = getHongbaoText(mReceiveNode_1.get(size - 1));
                long now = System.currentTimeMillis();
                if (this.shouldReturn(id, now - lastFetchedTime))
                {
                    return false;
                }
                lastFetchedHongbaoId = id;
                lastFetchedTime = now;

                AccessibilityNodeInfo cellNode = mReceiveNode_1.get(size - 1);
                if (null != cellNode)
                {
                    if (cellNode.getText().toString().equals("口令红包已拆开"))
                    {
//                        Log.i("TAG", "红包口令已拆开");
                        return false;
                    }else
                    {
                        //处理普通红包
                        if (!cellNode.isClickable())
                        {
//                            Log.i("TAG","拆开普通红包1");
                            cellNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            mIsEnterWeChatList = false;
                        }else
                        {
                            return false;
                        }
                    }
                }
                //处理口令红包
                if (null != cellNode)
                {
                    if (cellNode.getText().toString().equals(QQ_HONG_BAO_PASSWORD))
                    {

                        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                        if (rowNode == null)
                        {
                            return false;
                        } else
                        {
//                            Log.i("TAG", "口令红包");
                            recycle(rowNode);
                        }
                    }
                }
                mLuckyMoneyReceived_1 = false;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkNodeInfo()
    {
        //Log.i("TAG", "监听聊天窗口");
        if (rootNodeInfo_1 == null)
        {
//            Log.i("TAG", "333");
            return;
        }
         /* 聊天会话窗口，遍历节点匹配“点击拆开”，“口令红包”，“点击输入口令” */
        List<AccessibilityNodeInfo> nodes1 = this.findAccessibilityNodeInfosByTexts(this.rootNodeInfo_1, new String[]{
                QQ_DEFAULT_CLICK_OPEN, QQ_HONG_BAO_PASSWORD, QQ_CLICK_TO_PASTE_PASSWORD, "发送"});

        if (!nodes1.isEmpty())
        {

            String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo_1));
            if (!nodeId.equals(lastFetchedHongbaoId))
            {
//                Log.i("TAG", "有QQ红包字眼出现");
                mLuckyMoneyReceived_1 = true;
                mReceiveNode_1 = nodes1;
            }
            return;
        }
    }

    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts)
    {
        for (String text : texts)
        {
            if (text == null) continue;
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);
            if (!nodes.isEmpty())
            {
                if (text.equals(WECHAT_OPEN_EN) && !nodeInfo.findAccessibilityNodeInfosByText(WECHAT_OPENED_EN).isEmpty())
                {
                    continue;
                }
                return nodes;
            }
        }
        return new ArrayList<>();
    }

    private String getHongbaoText(AccessibilityNodeInfo node)
    {
        /* 获取红包上的文本 */
        String content;
        try {
            AccessibilityNodeInfo i = node.getParent().getChild(0);
            content = i.getText().toString();
        } catch (NullPointerException npe) {
            return null;
        }
        return content;
    }

    private boolean shouldReturn(String id, long duration)
    {
        // ID为空
        if (id == null) return true;
        // 名称和缓存不一致
        if (duration < MAX_CACHE_TOLERANCE && id.equals(lastFetchedHongbaoId))
        {
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info)
    {
        if(info == null){
            return;
        }
//        if(!"android.widget.TextView".equals(info.getClassName())){
//                return;
//        }
        if (info.getChildCount() == 0)
        {
//            Log.e("TAG", "child widget----------------------------" + info.getClassName());
//            Log.e("TAG", "showDialog:" + info.canOpenPopup());
//            Log.e("TAG", "Text：" + info.getText());
//            Log.e("TAG", "windowId:" + info.getWindowId());
            if (info.getText() != null && info.getText().toString().equals(QQ_CLICK_TO_PASTE_PASSWORD))
            {
//                Log.i("TAG","点击输入口令");
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //performGlobalAction(GLOBAL_ACTION_BACK);
            }

            if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals("发送"))
            {
                //点击发送消息
//                Log.i("TAG","点击发送消息");
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                // performGlobalAction(GLOBAL_ACTION_BACK);
//                gotoDeskTop();
            }

        } else
        {
            for (int i = 0; i < info.getChildCount(); i++)
            {
                if (info.getChild(i) != null)
                {
                    recycle(info.getChild(i));
                }
            }
        }
    }
    //统计抢红包个数
    private void addTotalNum()
    {
        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
        int nowNum =sharedP.getInt(mTotalNum,0) + 1;
        SharedPreferences.Editor editor = sharedP.edit();
        editor.putInt(mTotalNum,nowNum);
        editor.apply();
//        Log.i("TAG", "抢到总共:"+ nowNum + "个红包");
        UmengUtil.YMgrasp_num(this);
    }

    //------------------锁屏相关-------------------------//

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void wakeAndUnlock(boolean unlock) {
        if(unlock) {
            //获取电源管理器对象
//            Log.i("TAG", "wakeAndUnlock ");
            //在屏幕休眠的状态下唤醒屏幕
            if (mPowerManager == null)
                mPowerManager=(PowerManager) getSystemService(POWER_SERVICE);
            //保持屏幕常亮
            if (mWeakLock == null){
                mWeakLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK , "WakeLock");
            }
            if (!mPowerManager.isScreenOn())
            {
//                Log.i("TAG", "屏幕暗");
                if(!mWeakLock.isHeld()){
                    mWeakLock.acquire();
//                    mWeakLock = null;
//                    mPowerManager = null;
//                    Log.i("TAG", "屏幕暗1");
                }
            }else{
//                Log.i("TAG", "屏幕亮...");
            }
            //得到键盘锁管理器对象
            if (mKeyguardManager == null){
                mKeyguardManager= (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
            }
            if (mKeyguardLock == null){
                mKeyguardLock = mKeyguardManager.newKeyguardLock("Lock");
            }
            //若在锁屏界面则解锁直接跳过锁屏
            if (mKeyguardManager.inKeyguardRestrictedInputMode())
            {
//                Log.i("TAG", "屏幕是黑的或目前正处于锁住状态????? ");
                if (mKeyguardLock != null){
//                    Log.i("TAG", "mKeyguardLock 非空???");
                    mKeyguardLock.disableKeyguard();
//                    mKeyguardLock = null;
//                    mKeyguardManager = null;
                }else{
//                    Log.i("TAG", "mKeyguardLock 为空???");
                }
                /*
                if (mKeyguardManager.isKeyguardLocked() && mKeyguardManager.isKeyguardSecure()) {
                    //解锁,这个解锁时为了隐藏输密码界面，如果没有密码，只用上面的自动nullactivity就可以了
                    //禁用显示键盘锁定
                    Log.i("TAG", "能解锁///////");
                    mKeyguardLock.disableKeyguard();
                }
                */
            }else{
//                Log.i("TAG", "屏幕不是锁住的???? ");
            }

        }else
        {
            //todo 会闪退
              if (mWeakLock != null){
                if(mWeakLock.isHeld())
                {
                    mWeakLock.release();
                    mWeakLock = null;
//                    Log.i("TAG", "关灯>>>>>");
                }
            }

            if (mKeyguardLock != null){
                mKeyguardLock.reenableKeyguard();
                mKeyguardLock = null;
//                Log.i("TAG", "锁屏>>>>>");
            }

        }
    }

    /**
     * 点亮屏幕
     */
    private void lightScreen() {
        if (mPowerManager == null) {
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
//        Log.e("TAG", "点亮屏幕<<<<<");
        mWeakLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WakeLock");
        mWeakLock.acquire();
    }
    /**
     * 解锁
     */
    private void unLock() {
        if (mKeyguardManager == null) {
            mKeyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
        }
        mKeyguardLock = mKeyguardManager.newKeyguardLock("Lock");
//        Log.e("TAG", "解锁屏幕<<<<<");
        if (mKeyguardManager.inKeyguardRestrictedInputMode())
        {
            mKeyguardLock.disableKeyguard();
        }
    }

    /**
     * 判断屏幕是否亮  亮返回 true
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context
                    .getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            // noinspection deprecation
            return pm.isScreenOn();
        }
    }

    /**
     * 清理环境
     */
    private void clean() {
//        Log.e("TAG", "清理<<<<<");
        if (mKeyguardLock != null) {
            mKeyguardLock.reenableKeyguard();
            mKeyguardLock = null;
        }
        if (mWeakLock != null) {
            mWeakLock.release();
            mWeakLock = null;
        }
    }

    /**
     * 判断是否加了安全锁
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean isLockOn() {
        KeyguardManager kM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (kM != null) {
            if (kM.isKeyguardLocked()) { // && kM.isKeyguardSecure()) {
                return true;
            }
        }
        return false;
    }
}
