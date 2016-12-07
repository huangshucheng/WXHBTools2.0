package com.junyou.hbks;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

//import com.switfpass.pay.MainApplication;
//import com.switfpass.pay.activity.PayPlugin;
//import com.switfpass.pay.bean.RequestMsg;
import com.junyou.hbks.Utils.UmengUtil;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;
import com.umeng.analytics.MobclickAgent;

public class VipActivity extends AppCompatActivity {
    public static VipActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        instance = this;
        WXPayUtil.init(this);
    }

    public static VipActivity getInstance() {
        if (instance != null){
            return instance;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("VipActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("VipActivity");
        MobclickAgent.onPause(this);
    }

    public void performBack(View view) {
        super.onBackPressed();
    }

    public void vip_one_month(View view) {
        if (ComFunction.networkInfo(this)){
            if (ComFunction.isWechatAvilible(this)){
                try{
                    if (null != WXPayUtil.getInstance()){
                        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM,"666");
//                        editor.putString(Constants.MONEY_NUM,"1");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_one_vip(this);
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
       //Log.i("TAG", "购买一个月");
    }

    public void vip_three_month(View view) {
        //payAmount = "1000";
        if (ComFunction.networkInfo(this)){
            if (ComFunction.isWechatAvilible(this)){
                try{
                    if (null != WXPayUtil.getInstance()){
                        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM,"1000");
//                        editor.putString(Constants.MONEY_NUM,"2");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_three_vip(this);
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

       // Log.i("TAG", "购买三个月");
    }

    public void vip_all_life(View view) {
       // payAmount = "1800";
        if (ComFunction.networkInfo(this)){
            if (ComFunction.isWechatAvilible(this)){
                try{
                    if (null != WXPayUtil.getInstance()){
                        SharedPreferences sharedP=  getSharedPreferences("config",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM,"1800");
//                        editor.putString(Constants.MONEY_NUM,"3");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_alife_vip(this);
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
        //Log.i("TAG", "购买终身使用");
    }
}
