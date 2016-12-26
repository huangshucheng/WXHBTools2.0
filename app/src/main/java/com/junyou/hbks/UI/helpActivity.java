package com.junyou.hbks.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.junyou.hbks.R;
import com.umeng.analytics.MobclickAgent;

public class helpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void performBack(View view)
    {
        super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onPageStart("helpActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("helpActivity");
        MobclickAgent.onPause(this);
    }
}
