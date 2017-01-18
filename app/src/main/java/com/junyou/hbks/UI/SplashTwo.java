package com.junyou.hbks.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.junyou.hbks.R;
import com.junyou.hbks.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

public class SplashTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_two);

        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, 3000);
    }

    private void goHome() {
        try{
            Intent intent = new Intent(SplashTwo.this, MainActivity.class);
            SplashTwo.this.startActivity(intent);
            SplashTwo.this.finish();
            LogUtil.i("goHome...................");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void splash_ad_click(View view){
        LogUtil.i("splash_ad_click....");
        goHome();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
