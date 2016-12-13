package com.junyou.hbks;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.switfpass.pay.MainApplication;
//import com.switfpass.pay.activity.PayPlugin;
//import com.switfpass.pay.bean.RequestMsg;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.junyou.hbks.Utils.UmengUtil;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;
import com.umeng.analytics.MobclickAgent;

public class VipActivity extends AppCompatActivity {
    public static VipActivity instance;
    private TextView coint_number; //金币数量
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        instance = this;
        WXPayUtil.init(this);

        coint_number = (TextView) findViewById(R.id.coint_number);//得到金币数量
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static VipActivity getInstance() {
        if (instance != null) {
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

    public void jinbi_jian(View view) {
        //Log.i("TAG", "金币减一个");
        if (coint_number != null) {
            int num = Integer.valueOf(coint_number.getText().toString());
            if (num > 1) {
                int n = num - 1; //String.valueOf(num - 1)
                coint_number.setText("" + n);
            } else {
                //Log.i("TAG", "不回掉");
            }
        }
    }

    public void jinbi_add(View view) {
        //Log.i("TAG", "金币加一个");
        if (coint_number != null) {
            int num = Integer.valueOf(coint_number.getText().toString());
            if (num < 100) {
                int n = num + 1;
                coint_number.setText("" + n);
            }
        }
    }

    public void jinbi_exchange(View view) {
        //Log.i("TAG", "支付兑换");
        if (coint_number != null) {
            if (ComFunction.networkInfo(this)) {
                if (ComFunction.isWechatAvilible(this)) {
                    try {
                        if (null != WXPayUtil.getInstance()) {
                            SharedPreferences sharedP = getSharedPreferences("config", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedP.edit();
                            editor.putString(Constants.MONEY_NUM, coint_number.getText() + "00");
//                        editor.putString(Constants.MONEY_NUM,"1");
                            editor.apply();
                            WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                            UmengUtil.YMclk_one_vip(this);
                        }
                        UmengUtil.YMpurchase_num(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void vip_one_month(View view) {
        if (ComFunction.networkInfo(this)) {
            if (ComFunction.isWechatAvilible(this)) {
                try {
                    if (null != WXPayUtil.getInstance()) {
                        SharedPreferences sharedP = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM, "666");
//                        editor.putString(Constants.MONEY_NUM,"1");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_one_vip(this);
                    }
                    UmengUtil.YMpurchase_num(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
        }
        //Log.i("TAG", "购买一个月");
    }

    public void vip_three_month(View view) {
        //payAmount = "1000";
        if (ComFunction.networkInfo(this)) {
            if (ComFunction.isWechatAvilible(this)) {
                try {
                    if (null != WXPayUtil.getInstance()) {
                        SharedPreferences sharedP = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM, "1000");
//                        editor.putString(Constants.MONEY_NUM,"2");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_three_vip(this);
                    }
                    UmengUtil.YMpurchase_num(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
        }

        // Log.i("TAG", "购买三个月");
    }

    public void vip_all_life(View view) {
        // payAmount = "1800";
        if (ComFunction.networkInfo(this)) {
            if (ComFunction.isWechatAvilible(this)) {
                try {
                    if (null != WXPayUtil.getInstance()) {
                        SharedPreferences sharedP = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedP.edit();
                        editor.putString(Constants.MONEY_NUM, "1800");
//                        editor.putString(Constants.MONEY_NUM,"3");
                        editor.apply();
                        WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                        UmengUtil.YMclk_alife_vip(this);
                    }
                    UmengUtil.YMpurchase_num(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "您未安装微信!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "网络未连接!", Toast.LENGTH_SHORT).show();
        }
        //Log.i("TAG", "购买终身使用");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Vip Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.junyou.hbks/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Vip Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.junyou.hbks/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
