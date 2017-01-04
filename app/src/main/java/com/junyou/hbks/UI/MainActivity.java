package com.junyou.hbks.UI;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.config.Constants;
import com.junyou.hbks.R;
import com.junyou.hbks.utils.BtnBlinkUtil;
import com.junyou.hbks.utils.LocalSaveUtil;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.utils.SaveCountUtil;
import com.junyou.hbks.utils.ShareHelper;
import com.junyou.hbks.utils.TimeManager;
import com.junyou.hbks.utils.UmengUtil;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;
import com.junyou.hbks.luckydraw.LuckyDrawDialog;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.junyou.hbks.wxapi.WXUtil;

public class MainActivity extends AppCompatActivity implements AccessibilityManager.AccessibilityStateChangeListener
{
    private AccessibilityManager accessibilityManager;
    SharedPreferences sharedPreferences;
    RelativeLayout mainLayoutHeader;
    private static MainActivity instance;
    private ServiceStateBroadcast serviceStateBroadcast;
    private NotificationManager mNotificationManager;

    private ImageButton setting_imagebtn;
    private ImageButton help_imagebtn;
    private ImageButton open_fuzhubtn;
    private ImageView top_image;

    private TextView num_redpkt;//红包个数
    private TextView num_money;//红包金额
    private TextView marquee_text;//跑马灯文本
    private TextView left_days_text;//剩余天数
    //弹窗
    private Dialog dialog_openSvs;
    private Dialog dialog_openShare;
    public  Dialog dialog_receiveTime;
    private Dialog dialog_tryDays;
    private Dialog dialog_open_vip;

    private Intent bor_intent;//广播消息
    private IWXAPI wxAPI;//sdk 相关
    //帧动画
    private AnimationDrawable animDrawable = null;
    private static boolean setting_flags = true;
    private static final int NOTIFY_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        regToWx();      //注册微信id
        WXPayUtil.init(this);
        //友盟埋点
        MobclickAgent.setDebugMode(true);   //打开友盟埋点数据统计测试
        MobclickAgent.setScenarioType(instance, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UmengUtil.YMPhoneInfo(this);

        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //注册广播(监听service)
        this.serviceStateBroadcast = new ServiceStateBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RobApp.ACTION_ACCESSIBILITY_SERVICE_CONNECT);
        filter.addAction(RobApp.ACTION_ACCESSIBILITY_SERVICE_DISCONNECT);
        registerReceiver(this.serviceStateBroadcast, filter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mainLayoutHeader = (RelativeLayout) findViewById(R.id.layout_header);
        //设置和帮助按钮
        setting_imagebtn = (ImageButton) findViewById(R.id.imageButton_setting);
        if (setting_imagebtn != null){
            setting_imagebtn.setOnClickListener(onClickSetting);
        }
        help_imagebtn = (ImageButton) findViewById(R.id.imageButton_help);
        if (help_imagebtn != null){
            help_imagebtn.setOnClickListener(onClickHelp);
        }
        open_fuzhubtn = (ImageButton) findViewById(R.id.open_fuzhu_btn);
        //顶部图片
        top_image = (ImageView) findViewById(R.id.top_img_show);
        //红包个数标签 金额标签
        num_redpkt = (TextView) findViewById(R.id.packt_num_text);
        num_money = (TextView) findViewById(R.id.money_num_text);
        //剩余天数标签
        left_days_text = (TextView) findViewById(R.id.left_days_text);
        //跑马灯文本
        marquee_text = (TextView) findViewById(R.id.marquee_text);
        //广播
       bor_intent = new Intent("com.junyou.hbks.SETTING");

        LocalSaveUtil.getInitialize(this);
        updateServiceStatus();
        showDatas();
        showDialog();
        TimeManager.init(this);
        newShowLeftDays();
        initTime();
        new TimeThread().start();
        showSettingDialog();
}

    private class ServiceStateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing()) {
                return;
            }
            String action = intent.getAction();
            if (RobApp.ACTION_ACCESSIBILITY_SERVICE_CONNECT.equals(action)) {
                LogUtil.i("service打开");
                    closeNotification();
            } else if (RobApp.ACTION_ACCESSIBILITY_SERVICE_DISCONNECT.equals(action)) {
                LogUtil.i("service关闭");
                openNotifocation();
            }
        }
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(60000);//一分钟检测一侧
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    SharedPreferences sharedP = getSharedPreferences("config",MODE_MULTI_PROCESS);
                    if (sharedP.getBoolean(Constants.IS_ALLLIFEUSE,false)){
                        //终身使用
                        setCurTime(getResources().getString(R.string.forever));
                        return;
                    }
                        int totalTime = TimeManager.getLeftTime();
                        int useTime = TimeManager.getDiffTime();
                        int leftTime = totalTime - useTime;
//                    LogUtil.i("TAG","总时间:" + totalTime + "  使用时间:" + useTime + "  剩余时间:" + TimeManager.minutesToDays(leftTime));
                        setCurTime("" + TimeManager.minutesToDays(leftTime)); //更新时间
                        if (TimeManager.isTimeout()){
                            //没有时间了
                            setCurTime("时间用完");
//                         Log.i("TAG","没有时间了");
                        }
                    break;
            }
        }
    };

    private void setCurTime(CharSequence curtime) {
        if (null != left_days_text){
            left_days_text.setText(curtime + "  ");
        }
    }

    private void showSettingDialog(){
        if(!isServiceEnabled()){
            if (null != dialog_openSvs) {
                dialog_openSvs.show();
            }
        }
    }

    private void initTime(){
        SharedPreferences sharedP = getSharedPreferences("config",MODE_PRIVATE);
        if (sharedP.getBoolean(Constants.IS_ALLLIFEUSE,false)){
            //终身使用
            setCurTime(getResources().getString(R.string.forever));
            return;
        }
            int totalTime = TimeManager.getLeftTime();
            int useTime = TimeManager.getDiffTime();
            int leftTime = totalTime - useTime;
//      Log.i("TAG","总时间:" + totalTime + "  使用时间:" + useTime + "  剩余时间:" + TimeManager.minutesToDays(leftTime));
            setCurTime("" + TimeManager.minutesToDays( leftTime)); //更新时间
            if (TimeManager.isTimeout()){
                //没有时间了
                setCurTime( "时间用完");
            }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotifocation()
    {
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIndent)
                .setSmallIcon(R.mipmap.ic_launcher)//设置状态栏里面的图标（小图标)
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setAutoCancel(false)//设置可以清除
                .setContentTitle("红包快手被关闭了")//设置下拉列表里的标题
                .setContentText("亲,不能抢红包了,快点击我开启!");//设置上下文内容
        try{
            if (mNotificationManager != null){
                mNotificationManager.notify(NOTIFY_ID,builder.build());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeNotification(){
        if (null != mNotificationManager){
            mNotificationManager.cancel(NOTIFY_ID);
        }
    }

    //注册微信id
    private void regToWx() {
        wxAPI = WXAPIFactory.createWXAPI(this,Constants.APP_ID,true);
        wxAPI.registerApp(Constants.APP_ID);
    }

    private void refrishMarqueeText() {
        final String []marquee_lists = {
                getResources().getString(R.string.marquee_word_1),
        };
        //调度器
        Timer timer = new Timer();
        final Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        int num = (int)(Math.random()*12);  //0-11
                        if (null != marquee_text) {
//                            marquee_text.setText(marquee_lists[num]);
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };

        TimerTask task = new TimerTask(){
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 20000,20000);    //20秒之后执行，每20秒执行一次
    }

    private void showDialog() {
        //打开设置弹窗
        View view_1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_openservice, null);
        dialog_openSvs = new Dialog(this,R.style.common_dialog);
        if (dialog_openSvs != null){
            dialog_openSvs.setContentView(view_1);
            dialog_openSvs.setCancelable(false);
        }
        //打开分享弹窗
        View view_2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_share,null);
        dialog_openShare = new Dialog(this,R.style.common_dialog);
        if (dialog_openShare != null){
            dialog_openShare.setContentView(view_2);
        }

        //主页的收到天数弹窗
        View view_3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_receivetime,null);
        dialog_receiveTime = new Dialog(this,R.style.common_dialog);
        if (dialog_receiveTime != null){
            dialog_receiveTime.setContentView(view_3);
            dialog_receiveTime.setCancelable(false);
        }

        if (!LocalSaveUtil.getInitialize(this).getIsGiveThreeDay()){
            View view_4 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_trydays,null);
            dialog_tryDays = new Dialog(this,R.style.common_dialog);
            if (dialog_tryDays != null){
                dialog_tryDays.setContentView(view_4);
                dialog_tryDays.show();
            }
            LocalSaveUtil.getInitialize(this).setIsGiveThreeDay(true);
        }

        View view_5 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_supervip, null);
        dialog_open_vip = new Dialog(this,R.style.common_dialog);
        if (dialog_open_vip != null){
            dialog_open_vip.setContentView(view_5);
            dialog_open_vip.setCancelable(false);
        }
    }

    //右下角显示剩余的天数
    private void newShowLeftDays(){

        SharedPreferences sharedP = getSharedPreferences("config",MODE_PRIVATE);
        if (sharedP.getBoolean(Constants.IS_ALLLIFEUSE,false)){
            setCurTime(getResources().getString(R.string.forever));
            return;
        }

        if (TimeManager.isNewDayFirstEnter()){
            //是否新的一天
            int use_day = TimeManager.getUseDay();
            if (use_day % 3 == 0){
                //每三天弹一次窗
                if (dialog_open_vip != null){
                    dialog_open_vip.show();
                }
            }
        }

        if (TimeManager.isTimeout()){
            //没有时间了
            TimeManager.setServiceOnOrOff(false);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.shareForDays), Toast.LENGTH_LONG).show();
        }else{
            TimeManager.setServiceOnOrOff(true);
        }
    }

    public void showDatas() {
//        LogUtil.i("TAG", "初始总红包数量:"+ SaveCountUtil.getInitialize(this).getRPCount());
//        LogUtil.i("TAG", "初始总资产:"+ SaveCountUtil.getInitialize(this).getMoneyCount());
        //显示数据
        if (num_redpkt != null){
            num_redpkt.setText("" + SaveCountUtil.getInitialize(this).getRPCount());
        }
        if (num_money != null){
            num_money.setText("" + SaveCountUtil.getInitialize(this).getMoneyCount());
        }
    }

    public static MainActivity getInstance() {
        if (instance != null){
            return instance;
        }
        return  null;
    }

    private View.OnClickListener onClickSetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Log.i("TAG","setting");
            try {
                Intent settingAvt = new Intent(MainActivity.this,PersonalCenterActivity.class); //PersonalCenterActivity,SettingActivity
                settingAvt.putExtra("title", "个人中心");
                settingAvt.putExtra("frag_id", "GeneralSettingsFragment");
                startActivity(settingAvt);
                UmengUtil.YMclk_setting(MainActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private  View.OnClickListener onClickHelp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent helpAvt = new Intent(MainActivity.this,helpActivity.class);
                startActivity(helpAvt);
                UmengUtil.YMclk_help(MainActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除监听服务
        if (accessibilityManager != null){
            accessibilityManager.removeAccessibilityStateChangeListener(this);
        }
        if (this.serviceStateBroadcast != null) {
            unregisterReceiver(this.serviceStateBroadcast);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        showDatas();
       // showLeftDays();
        newShowLeftDays();
        MobclickAgent.onPageStart("MainActivity");  //统计页面
        MobclickAgent.onResume(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //按返回键返回桌面
//        LogUtil.i("back.......");
//        moveTaskToBack(true);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
       //updateServiceStatus();
       // showLeftDays();
        newShowLeftDays();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");    //统计页面
        MobclickAgent.onPause(this);
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            LogUtil.i("TAG","service is on");
            SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
            boolean isflag = sharedP.getBoolean(Constants.ISOPEN_FLAG,true);
            if (isflag && sharedPreferences.getBoolean("pref_watch_notification",true)){
                setImagesOnOrOff(true);
            }else{
                if (top_image != null){
                    top_image.setImageResource(R.mipmap.home_bg_radpacket_default_off);
                    stopBgAnimation();
                }
                if (mainLayoutHeader != null){
                    mainLayoutHeader.setBackgroundColor(getResources().getColor(R.color.mainbgOff));
                }
                if (open_fuzhubtn != null){
                    open_fuzhubtn.setImageResource(R.mipmap.button_start_nor);
                }
            }
            if (null != open_fuzhubtn){
                BtnBlinkUtil.stopBlink(open_fuzhubtn);
            }
        } else {
            LogUtil.i("TAG","service is off");
            setImagesOnOrOff(false);
            if (null != open_fuzhubtn){
                BtnBlinkUtil.startBlink(open_fuzhubtn);
            }
        }
    }

    private void setImagesOnOrOff(boolean flag){
        if (flag){
            if (top_image != null){
                top_image.setImageResource(R.mipmap.home_bg_radradpacket_selected_on);
                startBgAnimation();
            }
            if (mainLayoutHeader != null){
                mainLayoutHeader.setBackgroundColor(getResources().getColor(R.color.mainbgOn));
            }
            if (open_fuzhubtn != null){
                open_fuzhubtn.setImageResource(R.mipmap.button_grasping);
            }
        }else{
            if (top_image != null){
                top_image.setImageResource(R.mipmap.home_bg_radpacket_default_off);
                stopBgAnimation();
            }
            if (mainLayoutHeader != null){
                mainLayoutHeader.setBackgroundColor(getResources().getColor(R.color.mainbgOff));
            }
            if (open_fuzhubtn != null){
                open_fuzhubtn.setImageResource(R.mipmap.button_openfz_default);
            }
        }
    }

    private boolean isServiceEnabled() {
        if (accessibilityManager != null){
            List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
            for (AccessibilityServiceInfo info : accessibilityServices) {
                if (info.getId().equals(getPackageName() + "/.service.RobAccessibilityService")) {
                    return true;
                }
            }
        }
        return false;
//        return RobAccessibilityService.isRunning();
    }

    public void openSettings(View view) {

//        UmengUtil.YMMoney_count_bychannel(this,"000007","6.66");  // umeng test
        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedP.edit();
        if (!isServiceEnabled()) {
            if (null != dialog_openSvs) {
                dialog_openSvs.show();
            }
            UmengUtil.YMclk_fuzhu(MainActivity.this);
        }else{
            if (null !=sharedPreferences && sharedPreferences.getBoolean("pref_watch_notification",true)) {
                if (setting_flags){
                    //已经打开，点击关闭
                    if (top_image != null){
                        top_image.setImageResource(R.mipmap.home_bg_radpacket_default_off);
                        stopBgAnimation();
                    }
                    if (mainLayoutHeader != null){
                        mainLayoutHeader.setBackgroundColor(getResources().getColor(R.color.mainbgOff));
                    }
                    if (open_fuzhubtn != null){
                        open_fuzhubtn.setImageResource(R.mipmap.button_start_nor);
                    }
                    setting_flags = !setting_flags;
                    SaveCountUtil.getInitialize(this).setCanRobMoney(false);
                    LogUtil.i("TAG","closeing....");
                }else{
                    //已经关闭，点击打开
                    setImagesOnOrOff(true);
                    setting_flags = !setting_flags;
                    SaveCountUtil.getInitialize(this).setCanRobMoney(true);
                    LogUtil.i("TAG","openning....");
                }
            }
        }
    }

    //右下角获取更多天数按钮
    public void getMoreTime(View view) {
//        Log.i("TAG", "点我获取天数哦");
        if (null != dialog_openShare)
            dialog_openShare.show();
        UmengUtil.YMclk_share(this);
}

    public void superVipClick(View view) {
//        Log.i("TAG", "点击弹出超级VIP弹窗");
        try {
            Intent settingAvt = new Intent(this,VipActivity.class);
            startActivity(settingAvt);
            UmengUtil.YMclk_vipbutton(MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openServiceClick(View view) {
        try {
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            if(null != accessibleIntent){
                startActivity(accessibleIntent);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "遇到一些问题,请手动打开系统设置>辅助服务>微信红包助手", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (null != dialog_openSvs) {
            dialog_openSvs.dismiss();
        }
    }

    public void closeOpenServiceClick(View view) {
//        Log.i("TAG", "点击关闭系统设置提示");
        if (null != dialog_openSvs) {
            dialog_openSvs.dismiss();
        }
    }

    public void closeOpenShare(View view) {
        if (null != dialog_openShare) {
            dialog_openShare.dismiss();
        }
    }

    public void sharePengYouQuanClick(View view) {
//        Log.i("TAG", "点击分享到朋友圈");
        if (null != dialog_openShare) {
            dialog_openShare.dismiss();
            UmengUtil.YMclk_share_wctp(MainActivity.this);
        }
        final String []share_lists = {
                getResources().getString(R.string.share_1),
                getResources().getString(R.string.share_2),
                getResources().getString(R.string.share_3),
                getResources().getString(R.string.share_4)
        };
        int num = (int)(Math.random()*4);  //0-3
        //不使用sdk分享

        final String PackageName = "com.tencent.mm";
        final String ActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI"; //微信朋友圈
        if (ShareHelper.isInstalled(this,PackageName,ActivityName)){
            //图片加文字
            /*
            Bitmap bt= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt, null,null));
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);//带图片分享
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("Kdescription", "红包快手，让红包来的容易点~~");
            startActivity(intent);
            */
            //使用sdk分享
            WXWebpageObject webpage = new WXWebpageObject();
//            webpage.webpageUrl = "http://info.appstore.vivo.com.cn/detail/1643019?source=7";//vivo
//            webpage.webpageUrl = "http://zhushou.360.cn/detail/index/soft_id/3540668";//360
//            webpage.webpageUrl = "http://www.wandoujia.com/apps/com.junyou.hbks";//wandoujia
            webpage.webpageUrl = "http://www.zjhzjykj.com";
//            webpage.webpageUrl = "http://app.meizu.com/apps/public/detail?package_name=com.junyou.hbks"; //魅族
//            webpage.webpageUrl = "http://fx.anzhi.com/share_2711236.html?azfrom=qqfriend"; //安智
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "红包快手 "+share_lists[num];
            msg.description = share_lists[num];
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            msg.thumbData = WXUtil.bmpToByteArray(thumb, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = ShareHelper.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;    //朋友圈
            wxAPI.sendReq(req);
        }else {
            Toast.makeText(getApplicationContext(), "您没有安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareWeiXinClick(View view) {
//        Log.i("TAG", "点击分享给微信朋友");
        if (null != dialog_openShare) {
            dialog_openShare.dismiss();
            UmengUtil.YMclk_share_wct(MainActivity.this);
        }
        final String []share_lists = {
                getResources().getString(R.string.share_1),
                getResources().getString(R.string.share_2),
                getResources().getString(R.string.share_3),
                getResources().getString(R.string.share_4)
        };
        int num = (int)(Math.random()*4);  //0-3

        final String PackageName = "com.tencent.mm";
        final String ActivityName = "com.tencent.mm.ui.tools.ShareImgUI";
        if (ShareHelper.isInstalled(this,PackageName,ActivityName)){

            //文字或链接
            /*
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "红包快手，让红包来的容易点~~");
            startActivity(intent);
               */
            //图片
            /*
            Bitmap bt= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt, null,null));
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");        //分享图片，没有图片用转回到分享文字
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(intent);
            */
            //使用sdk分享
            WXWebpageObject webpage = new WXWebpageObject();
//            webpage.webpageUrl = "http://info.appstore.vivo.com.cn/detail/1643019?source=7";   //vivo
//            webpage.webpageUrl = "http://zhushou.360.cn/detail/index/soft_id/3540668";  //360
//            webpage.webpageUrl = "http://www.wandoujia.com/apps/com.junyou.hbks";//wandoujia
            webpage.webpageUrl = "http://www.zjhzjykj.com";
//            webpage.webpageUrl = "http://app.meizu.com/apps/public/detail?package_name=com.junyou.hbks"; //魅族
//            webpage.webpageUrl = "http://fx.anzhi.com/share_2711236.html?azfrom=qqfriend"; //安智
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "红包快手";
            msg.description = share_lists[num];
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            msg.thumbData = WXUtil.bmpToByteArray(thumb, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = ShareHelper.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;     //好友
            wxAPI.sendReq(req);
        }else {
            Toast.makeText(getApplicationContext(), "您没有安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareQQClick(View view) {
//        Log.i("TAG", "点击分享给QQ好友");
        if (null != dialog_openShare) {
            dialog_openShare.dismiss();
        }
          final String PackageName = "com.tencent.mobileqq";
          final String ActivityName = "com.tencent.mobileqq.activity.JumpActivity"; //qq好友
         if (ShareHelper.isInstalled(this,PackageName,ActivityName)){
             //分享文字给好友
             Intent intent = new Intent(Intent.ACTION_SEND);
             ComponentName component = new ComponentName(PackageName,ActivityName);
             intent.setComponent(component);
             intent.putExtra(Intent.EXTRA_TEXT, "红包快手，让红包来的容易点~");
             intent.setType("text/plain");
             startActivity(intent);
         }else {
             Toast.makeText(getApplicationContext(), "您没有安装手机QQ", Toast.LENGTH_SHORT).show();
         }
    }

    public void shareWeiboClick(View view) {
//        Log.i("TAG", "点击分享到微博");
        if (null != dialog_openShare) {
            dialog_openShare.dismiss();
        }

        if (isSinaWiBoAvilible(this)) {
            //分享文字
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String pakName = "com.sina.weibo";
            intent.setPackage(pakName);
            intent.putExtra(Intent.EXTRA_TEXT, "红包快手，让红包来的容易点~");
            this.startActivity(Intent.createChooser(intent, ""));

            /*
            //图片加文字
            Bitmap bt= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt, null,null));
            Intent intent = new Intent();
            intent.setPackage("com.sina.weibo");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "红包快手，让红包来的容易点~");
            startActivity(intent);
            */
        }else
        {
            Toast.makeText(getApplicationContext(), "您没有安装新浪微博", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSinaWiBoAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void closeReceiveTime(View view) {
//        Log.i("TAG", "关闭收到天数啦");
        if (dialog_receiveTime != null){
            dialog_receiveTime.dismiss();
        }
    }

    public void receive_confirm_click(View view) {
//        Log.i("TAG", "确定收到天数");
        if (dialog_receiveTime != null){
            dialog_receiveTime.dismiss();
        }
    }

    public void receive_getmore_click(View view) {
//        Log.i("TAG", "成为超级VIP");
//        if (dialog_receiveTime != null){
//            dialog_receiveTime.dismiss();
//        }
        //todo 直接进入微信支付6.66元
        if (ComFunction.networkInfo(this)){
            if (ComFunction.isWechatAvilible(this)){
                try{
                    if (null != WXPayUtil.getInstance()){
                        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM,"666");
                        //editor.putString(Constants.MONEY_NUM,"1");
                        editor.commit();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                    }
                    UmengUtil.YMpurchase_num(this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
        }
    }

    public void try_days_click(View view){
        if (dialog_tryDays != null){
            dialog_tryDays.dismiss();
        }
    }

    public void closeTryDays(View view){
        if (dialog_tryDays != null){
            dialog_tryDays.dismiss();
        }
    }

    public void super_vip_click(View view) {
//        if (dialog_open_vip != null){
//            dialog_open_vip.dismiss();
//        }
        //todo 直接进入微信支付6.66元
        if (ComFunction.networkInfo(this)){
            if (ComFunction.isWechatAvilible(this)){
                try{
                    if (null != WXPayUtil.getInstance()){
                        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM,"666");
//                        editor.putString(Constants.MONEY_NUM,"1");
                        editor.commit();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                    }
                    UmengUtil.YMpurchase_num(this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
        }
//        Log.i("TAG", "点击获取超级VIP");
    }

    public void closeOpenSuperVip(View view) {
        if (dialog_open_vip != null){
            dialog_open_vip.dismiss();
        }
    }

    public void luckyDrawClick(View view){
        try {
//            Intent helpAvt = new Intent(MainActivity.this,LuckyDraw.class);
//            startActivity(helpAvt);
            Dialog dialog_luckydraw = new LuckyDrawDialog(this,R.style.dialog_fullscreen);
            if (dialog_luckydraw != null){
                dialog_luckydraw.show();
                dialog_luckydraw.setCanceledOnTouchOutside(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startBgAnimation(){
        if (top_image != null){
            top_image.setImageResource(R.drawable.animation_bg);
            animDrawable = (AnimationDrawable) top_image.getDrawable();
            if (animDrawable != null){
                if (!animDrawable.isRunning()){
                    animDrawable.start();
//                    LogUtil.i("TAG","play animation...");
                }
            }
        }
    }

    private void stopBgAnimation(){
        if (top_image != null){
//            top_image.setImageResource(R.drawable.animation_bg);
            top_image.setImageResource(R.drawable.animation_bg);
            animDrawable = (AnimationDrawable) top_image.getDrawable();
            if (animDrawable != null){
                if (animDrawable.isRunning()){
                    animDrawable.stop();
//                    LogUtil.i("TAG","stop animation...");
                }
                top_image.setImageResource(R.mipmap.home_bg_radpacket_default_off);
            }
        }
    }
}