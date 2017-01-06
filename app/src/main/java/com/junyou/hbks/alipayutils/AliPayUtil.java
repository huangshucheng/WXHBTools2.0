package com.junyou.hbks.alipayutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.junyou.hbks.config.AliConfig;
import com.junyou.hbks.utils.LogUtil;

import java.util.Map;

public class AliPayUtil {

    private Context mContext = null;
    private static AliPayUtil mInstance = null;

    public AliPayUtil(Context context){
        this.mContext = context;
    }

    public static AliPayUtil getInitialize(Context context){
        if (null == mInstance){
            mInstance = new AliPayUtil(context);
        }
        return mInstance;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @SuppressWarnings("unused")
        public void handleMessage(Message msg){
            switch (msg.what){
                case AliConfig.SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知
                    if (null != payResult){
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case AliConfig.SDK_AUTH_FLAG:
                    break;
            }
        }
    };

    //支付宝支付业务  钱 例：0.01元
    public void AliPayV2(String moneyNum) {
        if (TextUtils.isEmpty(""+ moneyNum)){
            return;
        }
        if (TextUtils.isEmpty(AliConfig.APPID) || (TextUtils.isEmpty(AliConfig.RSA2_PRIVATE) && TextUtils.isEmpty(AliConfig.RSA_PRIVATE))) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            LogUtil.i("需要配置APPID | RSA_PRIVATE");
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (AliConfig.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = AliOrderInfoUtil.buildOrderParamMap(AliConfig.APPID,moneyNum ,rsa2);
        if (null == params){
            return;
        }
        String orderParam = AliOrderInfoUtil.buildOrderParam(params);

        String privateKey = rsa2 ? AliConfig.RSA2_PRIVATE : AliConfig.RSA_PRIVATE;
        String sign = AliOrderInfoUtil.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogUtil.i("支付返回:"+ result.toString());

                Message msg = new Message();
                msg.what = AliConfig.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //get the sdk version. 获取SDK版本号
    public void getSDKVersion() {
        PayTask payTask = new PayTask((Activity) mContext);
        String version = payTask.getVersion();
        Toast.makeText(mContext, version, Toast.LENGTH_SHORT).show();
    }

    //原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
    /**
     * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
     * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
     * 商户可以根据自己的需求来实现
     */
    /*
    public void AliH5Pay(View v) {
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        String url = "http://m.taobao.com";
        // url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        mContext.startActivity(intent);
    }
    */
}
