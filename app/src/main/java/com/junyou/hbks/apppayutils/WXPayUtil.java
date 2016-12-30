package com.junyou.hbks.apppayutils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.junyou.hbks.config.Constants;
import com.junyou.hbks.R;
import com.junyou.hbks.utils.LogUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class WXPayUtil {

    public static WXPayUtil instance;
    public static Activity activity;
    private static final String TAG = "TAG";
    private static IWXAPI api;

    public static void init(Activity activity)
    {
        WXPayUtil.activity = activity;
        api = WXAPIFactory.createWXAPI(activity, Constants.APP_ID,true);
        api.registerApp(Constants.APP_ID);	//注册到微
        instance = new WXPayUtil();
    }

    public static WXPayUtil getInstance(){
        return instance;
    }

    public class GetPrepayIdTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =
                    ProgressDialog.show(WXPayUtil.activity,
                            WXPayUtil.activity.getResources().getString(R.string.app_tip),
                            WXPayUtil.activity.getResources().getString(R.string.getting_prepayid));
        }

        @Override
        protected String doInBackground(String... params) {
            // 网络请求获取预付Id
           // Log.i(TAG, "后台费时操作");
            String url = String.format(Constants.ORDER_URL);
            //Log.i(TAG, "url: " + url);
            String entity = genEntity();
//            Log.i(TAG, "entity: " + entity);
            byte[] buf = WeChatHttpClient.httpPost(url, entity);
            if (buf != null && buf.length > 0) {
                try {
//                    Log.i(TAG, "http request seccess");
                    Map<String, String> map = XmlUtil.doXMLParse(new String(buf));
                   // Log.i(TAG, "buff: " + new String(buf));
                    //Log.i(TAG, "pre_id: "+ (String) map.get("prepay_id"));
                    return (String) map.get("prepay_id");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // Log.i(TAG,"后台操作结束");
            if (dialog != null)
            {
                dialog.dismiss();
            }
            if (s == null){
                //Log.i(TAG, "支付失败111");
                Toast.makeText(WXPayUtil.activity, WXPayUtil.activity.getResources().getString(R.string.get_prepayid_fail), Toast.LENGTH_SHORT).show();
            }else {
                try {
                    //Log.i(TAG,"发送支付订单！");
                    //Toast.makeText(WXPayUtil.activity, WXPayUtil.activity.getResources().getString(R.string.send_payorder), Toast.LENGTH_SHORT).show();
                    sendPayReq((String)s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        //调起支付
        private void sendPayReq(String prepayId) {
            PayReq req = new PayReq();
            req.appId = Constants.APP_ID;
            req.partnerId = Constants.PARTNER_ID;
            req.prepayId = prepayId;
            req.nonceStr = ComFunction.genNonceStr();
            req.timeStamp = String.valueOf(ComFunction.genTimeStamp());
            req.packageValue = "Sign=WXPay";

            List signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
            signParams.add(new BasicNameValuePair("package", req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
            //再次签名
            req.sign = ComFunction.genPackageSign(signParams);
            WXPayUtil.api.sendReq(req);
        }
        //下单参数
        public String genEntity(){
            SharedPreferences sharedP=  WXPayUtil.activity.getSharedPreferences("config",WXPayUtil.activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedP.edit();
            String nonceStr = ComFunction.genNonceStr();
            List<NameValuePair> packageParams = new ArrayList<NameValuePair>();
            //	APPID
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
            //	商品描述
            //packageParams.add(new BasicNameValuePair("body", WXPayUtil.activity.getResources().getString(R.string.wx_pay)));
            packageParams.add(new BasicNameValuePair("body", "微信支付"));
            // 商户ID
            packageParams.add(new BasicNameValuePair("mch_id",Constants.PARTNER_ID));
            // 随机字符�?
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            // 回调接口地址
            packageParams.add(new BasicNameValuePair("notify_url",Constants.NOTIFY_URL));
            // 我们的订单号 todo
            String orderNo = ComFunction.genOutTradNo();
//            Log.i(TAG, "orderNo: "+ orderNo);
            //保存订单号
            editor.putString(Constants.ORDER_NUM,orderNo);
            editor.commit();

//          Log.i(TAG, "保存订单号：" + sharedP.getString(Constants.ORDER_NUM, "null"));
            packageParams.add(new BasicNameValuePair("out_trade_no", orderNo));
            // 提交用户端ip
            packageParams.add(new BasicNameValuePair("spbill_create_ip",ComFunction.getIPAddress()));

            String moneyNum = sharedP.getString(Constants.MONEY_NUM,"null");
//            Log.i(TAG,"钱:" + moneyNum);
            BigDecimal totalFeeBig = new BigDecimal(moneyNum);
            int totalFee = totalFeeBig.multiply(new BigDecimal(1)).intValue();
            // 总金额，单位分!
            packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(totalFee)));
            // 支付类型
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            // 生成签名
            String sign = ComFunction.genPackageSign(packageParams);
//            Log.i(TAG, "签名: " + sign);
            packageParams.add(new BasicNameValuePair("sign", sign));
            String xmlstring = XmlUtil.toXml(packageParams);
            try {
//                Log.i(TAG, "xmlstring: " + xmlstring);
                //避免商品描述中文字符编码格式造成支付失败
                return new String(xmlstring.toString().getBytes(), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                LogUtil.i(TAG, "genProductArgs fail, ex = " + e.getMessage());
            }
            return null;
        }
    }
}
