package com.junyou.hbks.UI;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.config.Constants;
import com.junyou.hbks.R;
import com.junyou.hbks.utils.LocalSaveUtil;
import com.junyou.hbks.utils.ShareHelper;
import com.junyou.hbks.utils.TimeManager;
import com.junyou.hbks.fragments.PersonalFragment;
import com.junyou.hbks.fragments.SettingFragment;
import com.umeng.analytics.MobclickAgent;

public class PersonalCenterActivity extends AppCompatActivity {

    private static PersonalCenterActivity instance;
    private TextView coins_number; //金币数
    private TextView integral_number; //积分数
    private TextView user_type; //用户类型
//    private ImageView vip_mouth_img; //显示用户vip图片
    private TextView time_text;
    private TextView mUserid_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        prepareSettings();

        coins_number = (TextView) findViewById(R.id.coins_number);
        integral_number = (TextView) findViewById(R.id.integral_number);
        user_type = (TextView) findViewById(R.id.user_type);
        time_text = (TextView) findViewById(R.id.time_text);
        mUserid_text = (TextView) findViewById(R.id.user_id);
        initTime();
        new TimeThread().start();
    }

    public static PersonalCenterActivity getInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    private void prepareSettings() {

        String title, fragId;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            fragId = bundle.getString("frag_id");
        } else {
            title = "个人中心";
            fragId = "GeneralSettingsFragment";
        }

        TextView textView = (TextView) findViewById(R.id.personal_center_bar);
        textView.setText(title);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if ("GeneralSettingsFragment".equals(fragId)) {
            fragmentTransaction.replace(R.id.preferences_fragment, new PersonalFragment());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(60000);
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
//                    Log.i("TAG","总时间:" + totalTime + "  使用时间:" + useTime + "  剩余时间:" + TimeManager.minutesToDays(leftTime));
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

    private void setCurTime(CharSequence curtime) {
        if (null != time_text){
            time_text.setText(curtime);
        }
    }
    private void changeUI(){
        if (user_type != null)
        {
            //使用钱买过的用户就是vip用户
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (coins_number != null){
            coins_number.setText("" + LocalSaveUtil.getCoinNum());
        }
        if (integral_number != null){
            integral_number.setText("" + LocalSaveUtil.getPointNum());
        }
        if (mUserid_text != null){
            mUserid_text.setText("" + LocalSaveUtil.getAccount());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void performBack(View view) {
        super.onBackPressed();
    }


    //检测网络状态
    public boolean networkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void opendownloadClick(View view) {
//        Log.i("TAG", "打开下载骏游连连看");
        /*
        //打开网页才能下载
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "骏游科技");
//        webViewIntent.putExtra("url", "http://www.zjhzjykj.com");
        webViewIntent.putExtra("url", "http://www.zjhzjykj.com/game/ShowClass.asp?ClassID=2");
        startActivity(webViewIntent);
        SettingFragment.getInstance().dialog_setting_share.dismiss();
        */

        SharedPreferences sharedP = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedP.edit();

        boolean isDownload = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isdownloadlink", false);

        if (networkInfo()) {
//            Toast.makeText(getApplicationContext(), "有网络", Toast.LENGTH_SHORT).show();
            if (!isDownload) {
                //没有下载过，直接下载  Todo 判断网络状态
                if (!ShareHelper.isInstalledJunyouLik(this)) {

                    try {
                        (new DownloadUtil()).enqueue("http://www.zjhzjykj.com/images/tgllx-daiji_3009-2.3.0-201605191729.apk", getApplicationContext());
                        //点击直接增加天数
                        TimeManager.addToLeftTime(1440);
                        TimeManager.setServiceOnOrOff(true);
                        editor.putBoolean("isdownloadlink", true);
                        editor.apply();

                        if (SettingFragment.getInstance() != null) {
                            SettingFragment.getInstance().dialog_setting_share.dismiss();
                        }
                        if (MainActivity.getInstance() != null) {
                            MainActivity.getInstance().dialog_receiveTime.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "开始下载，又可以再使用一天了哦~", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "您已经下载过骏游连连看~", Toast.LENGTH_SHORT).show();
                }
            } else {
                //已经下载过，不做操作
                Toast.makeText(getApplicationContext(), "您已经下载过骏游连连看~", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "没有网络", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadUtil {
        public void enqueue(String url, Context context) {
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "tgllx-daiji_3009-2.3.0-201605191729.apk");
            r.allowScanningByMediaScanner();
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(r);
        }
    }

}
