package com.junyou.hbks.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.junyou.hbks.R;
import com.umeng.analytics.MobclickAgent;

//启动界面

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MobclickAgent.openActivityDurationTrack(false);     //禁止默认的统计方法
        // 使用Handler的postDelayed方法，1秒后执行跳转到SplashTwo
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, 1000);

        /** 设置是否对日志信息进行加密, 默认false(不加密). */
//        AnalyticsConfig.enableEncrypt(boolean enable);//6.0.0版本以前
        MobclickAgent.enableEncrypt(false);//6.0.0版本及以后
    }

    private void goHome() {
        try{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("SplashActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity");
        MobclickAgent.onPause(this);
    }
}
