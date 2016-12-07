package com.junyou.hbks;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.Utils.ShareHelper;
import com.junyou.hbks.Utils.TimeManager;
import com.junyou.hbks.Utils.UmengUtil;
import com.umeng.analytics.MobclickAgent;

import com.junyou.hbks.fragments.SettingFragment;

public class SettingActivity extends FragmentActivity
{
    private Dialog dialog_open_vip;
    private static SettingActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        ActionBar actionbar =getSupportActionBar();
//        if (null !=actionbar)
//        {
//            actionbar.setDisplayHomeAsUpEnabled(true);
//        }
        loadUI();
        prepareSettings();
        TimeManager.init(this);
        instance = this;
        SettingFragment.init(this);
    }

    public static SettingActivity getInstance()
    {
        if (instance != null){
            return instance;
        }
        return  null;
    }

    private void prepareSettings()
    {

        String title, fragId;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            fragId = bundle.getString("frag_id");
        } else {
            title = "设置";
            fragId = "GeneralSettingsFragment";
        }

        TextView textView = (TextView) findViewById(R.id.settings_bar);
        textView.setText(title);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if ("GeneralSettingsFragment".equals(fragId))
        {
            fragmentTransaction.replace(R.id.preferences_fragment, new SettingFragment());
        }
//        fragmentTransaction.commit(); //  fixed 会出错
        fragmentTransaction.commitAllowingStateLoss();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void performBack(View view)
    {
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadUI()
    {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffE46C62);
    }

    public void superVipClick(View view)
    {
//        Log.i("TAG", "点击弹出超级VIP弹窗");
        /*
        View view_1 = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_supervip, null);
        dialog_open_vip = new Dialog(this,R.style.common_dialog);
        dialog_open_vip.setContentView(view_1);
        dialog_open_vip.show();
        */
        try {
            Intent settingAvt = new Intent(this,VipActivity.class);
            startActivity(settingAvt);
            UmengUtil.YMclk_vipbutton(SettingActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void super_vip_click(View view)
    {
//        dialog_open_vip.dismiss();
//        Log.i("TAG", "点击获取超级VIP");
    }

    public void closeOpenSuperVip(View view)
    {
        if (dialog_open_vip != null)
            dialog_open_vip.dismiss();
    }

    //SettingFragment中的弹窗方法
    public void closeDownloadClick(View view)
    {
//        Log.i("TAG", "关闭弹窗");
        if (SettingFragment.getInstance()!= null )
            SettingFragment.getInstance().dialog_setting_share.dismiss();
    }

    //检测网络状态
    public boolean networkInfo(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void opendownloadClick(View view)
    {
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

        SharedPreferences sharedP = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedP.edit();

        boolean isDownload = getSharedPreferences("config",MODE_PRIVATE).getBoolean("isdownloadlink",false);

        if (networkInfo()){
//            Toast.makeText(getApplicationContext(), "有网络", Toast.LENGTH_SHORT).show();
            if (!isDownload){
                //没有下载过，直接下载  Todo 判断网络状态
                if (!ShareHelper.isInstalledJunyouLik(this)){

                    try {
                        (new DownloadUtil()).enqueue("http://www.zjhzjykj.com/images/tgllx-daiji_3009-2.3.0-201605191729.apk", getApplicationContext());
                        //点击直接增加天数
                        TimeManager.addToLeftTime(1440);
                        TimeManager.setServiceOnOrOff(true);
                        editor.putBoolean("isdownloadlink",true);
                        editor.apply();

                        if (SettingFragment.getInstance()!= null){
                            SettingFragment.getInstance().dialog_setting_share.dismiss();
                        }
                        if (MainActivity.getInstance() != null){
                            MainActivity.getInstance().dialog_receiveTime.show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "开始下载，又可以再使用一天了哦~", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "您已经下载过骏游连连看~", Toast.LENGTH_SHORT).show();
                }
            }else{
                //已经下载过，不做操作
                Toast.makeText(getApplicationContext(), "您已经下载过骏游连连看~", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "没有网络", Toast.LENGTH_SHORT).show();
        }
    }

    //打开APK
    /*
    private void openFile(File file) {
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
    */
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
