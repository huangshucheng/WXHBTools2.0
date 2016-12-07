package com.junyou.hbks.wxapi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.junyou.hbks.Utils.TimeManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

import com.junyou.hbks.Constants;
import com.junyou.hbks.MainActivity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

//
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.junyou.hbks.R.layout.activity_wxentry);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);
        api.handleIntent(getIntent(), this);
        TimeManager.init(this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) baseReq);
                break;
            default:
                break;
        }
    }
    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        String result = null;
        SharedPreferences sharedP = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedP.edit();

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:		//发送成功
                result = "分享成功";
                    //增加天数
//                TimeManager.addToLeftTime("1440");

                if (TimeManager.getNewDay()){
                    if (TimeManager.getUseDay()<=10){
                        //分享添加时间
                        // TimeManager.addToLeftTime(1440);  //24*60分钟43200
                        TimeManager.addToLeftTime(180);  //分享获取三小时
                        TimeManager.setServiceOnOrOff(true);
                        TimeManager.setNewDay(false);
                        try{
                            if (MainActivity.getInstance()!= null){
                                MainActivity.getInstance().dialog_receiveTime.show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        //超过十天，不能通过分享获得天数
                        Toast.makeText(getApplicationContext(), "您免费使用超过了十天,不能获得获得分享天数了！", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                //友盟社交埋点
                UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.WEIXIN_CIRCLE, "user_id");
                platform.setGender(UMPlatformData.GENDER.MALE); // optional
                platform.setWeiboId("weiXinId"); // optional
                MobclickAgent.onSocialEvent(this, platform);

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: //发送取消
                result = "分享取消";
//                Log.i("TAG", "分享取消");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED: //发送延迟
                result = "分享被拒绝";
//                Log.i("TAG", "分享被拒绝");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                break;
            default:
                result = "分享失败";	           //未知
//                Log.i("TAG", "分享失败");
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                break;
        }
        WXEntryActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WXEntryActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WXEntryActivity");
        MobclickAgent.onPause(this);
    }
}
