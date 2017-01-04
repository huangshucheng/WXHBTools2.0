package com.junyou.hbks.wxapi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Bundle;
import android.widget.Toast;
import android.os.Handler;

import com.junyou.hbks.utils.LocalSaveUtil;
import com.junyou.hbks.utils.TimeManager;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WeChatHttpClient;
import com.junyou.hbks.apppayutils.XmlUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.junyou.hbks.config.Constants;
import com.junyou.hbks.utils.UmengUtil;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    Handler mHandler = null;
    private static final String TAG = "TAG";
    private static final String mChinnelId = "vivo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.junyou.hbks.R.layout.activity_wxpay_entry);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);
        api.handleIntent(getIntent(), this);
        TimeManager.init(this);
        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                    {
                        //完成主界面更新,拿到数据
                        try{
                            String orderAmount = (String)msg.obj;
                                if (orderAmount.equals("666")){
//                                Log.i("TAG", "成功收到6.66块钱，发放奖励");
                                TimeManager.addToLeftTime(43200);
                                UmengUtil.YMmoney_count(WXPayEntryActivity.this,0);
                                    UmengUtil.YMMoney_count_bychannel(WXPayEntryActivity.this,mChinnelId,"6.66"); //todo add channel id
                            }else if (orderAmount.equals("1000")){
//                                Log.i("TAG", "成功收到10.00块钱，发放奖励");
                                TimeManager.addToLeftTime(129600);
                                UmengUtil.YMmoney_count(WXPayEntryActivity.this,1);
                                    UmengUtil.YMMoney_count_bychannel(WXPayEntryActivity.this,mChinnelId,"10.00"); //todo add channel id
                            }else if(orderAmount.equals("1800")){
//                                Log.i("TAG", "成功收到18.00块钱，发放奖励");
                                TimeManager.setLifeLongUse(true);//终身使用
                                UmengUtil.YMmoney_count(WXPayEntryActivity.this,2);
                                    UmengUtil.YMMoney_count_bychannel(WXPayEntryActivity.this,mChinnelId,"18.00"); //todo add channel id
                            }else {
                                    for (int i = 1 ; i < 100 ; i++) {
                                        if (orderAmount.equals(String.valueOf(i) + "00")) {//i个金币
                                            LocalSaveUtil.getInitialize(WXPayEntryActivity.this).setCoinNum(i + LocalSaveUtil.getInitialize(WXPayEntryActivity.this).getCoinNum());
                                            UmengUtil.YMMoney_count_bychannel(WXPayEntryActivity.this,mChinnelId,"" + i); //todo add channel id
                                        }
                                    }
                                }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        //todo 支付回调，查订单，之后给用户道具
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX)
        {
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
            int code = baseResp.errCode;
            String msg = "";
            switch (code) {
                case 0:
                    msg = "支付成功！";
                    //todo 订单查询
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                SharedPreferences sharedP=  getSharedPreferences("config",MODE_MULTI_PROCESS);
                                String moneyNum = sharedP.getString(Constants.MONEY_NUM,"null");
                                if(!moneyNum.equals("null")){
                                    if (query()){
                                        mHandler.sendEmptyMessage(0);
                                        Message msg =new Message();
                                        msg.obj = moneyNum;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    break;
                case -1:
                    msg = "支付失败！";
                    break;
                case -2:
                    msg = "您取消了支付!";
                    break;
                default:
                    msg = "支付失败！";
                    break;
            }
            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
        }
        WXPayEntryActivity.this.finish();
    }

    //查询订单
    private boolean query() {
        SharedPreferences sharedP=  getSharedPreferences("config",MODE_MULTI_PROCESS);
        String orderNo = sharedP.getString(Constants.ORDER_NUM,"");
        if (orderNo.isEmpty()) {
            return false;
        }

        List<NameValuePair> packageParams = new ArrayList<NameValuePair>();
        //APPID
        packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
        //商户ID
        packageParams.add(new BasicNameValuePair("mch_id",Constants.PARTNER_ID));
        //随机字符串
        String nonceStr = ComFunction.genNonceStr();
        packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
        //订单号
        packageParams.add(new BasicNameValuePair("out_trade_no", orderNo));
        //生成签名
        String sign = ComFunction.genPackageSign(packageParams);
        packageParams.add(new BasicNameValuePair("sign", sign));
        String xmlstring = XmlUtil.toXml(packageParams);
        try {
//            Log.i(TAG, "queryxmlstring: " + xmlstring);
            //避免商品描述中文字符编码格式造成支付失败
            String url = String.format(Constants.QUERY_URL);
            String ecodeString = new String(xmlstring.toString().getBytes(), "ISO-8859-1");
            byte[] buf = WeChatHttpClient.httpPost(url, ecodeString);
            if (buf != null && buf.length > 0) {
                try {
//                    Log.i(TAG, "queryhttp请求成功");
                    Map<String, String> map = XmlUtil.doXMLParse(new String(buf));
//                    Log.i(TAG, "querybuff: " + new String(buf));
//	                    for (Map.Entry entry : map.entrySet()) {
//	                        Log.i(TAG, "KEY:" + (String) entry.getKey()+"   VALUE:"+(String) entry.getValue());
//	                    }
//	                    Log.i(TAG, "return_code: "+ (String) map.get("return_code"));
                    if ((map.get("return_code")).equals("SUCCESS")) {
//                        Log.i(TAG, "能通信哦");
                        if ((map.get("trade_state")).equals("SUCCESS")) {
//                            Log.i(TAG, "支付成功");
                            return true;
                        }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.i(TAG, "genProductArgs fail, ex = " + e.getMessage());
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WXPayEntryActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WXPayEntryActivity");
        MobclickAgent.onPause(this);
    }
}
