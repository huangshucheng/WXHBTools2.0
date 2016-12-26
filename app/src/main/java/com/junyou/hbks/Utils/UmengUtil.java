package com.junyou.hbks.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UmengUtil {

    /**

     收到的钱，            money_count       浮点类型（float）  ，计算事件
     计费请求次数，        purchase_num    整形(int)    ， 计数事件
     抢红包的次数，        grasp_num           整形(int)     ，计数事件
     imei，               imei_num       （移动设备识别码，识别用户的手机）  *#06#
     imis，               imsi_num       （国际移动用户识别码，识别用户的手机卡）
     机型，                phone_type      计算事件

     分渠道的id，   （友盟自带）
     用的天数，     （友盟自带）

    //计数事件
     clk_alife_vip    (点击vip页面的终身使用vip按钮)
     clk_three_vip    (点击vip页面的开通三个月vip按钮)
     clk_one_vip      (点击vip页面的开通一个月vip按钮)

     clk_about        (点击设置页面的关于按钮)
     clk_closead      (点击设置页面的立即去广告按钮)
     clk_vipbutton    (点击设置页面的成为超级vip按钮)

     clk_share_wctp   (点击分享弹窗的分享到微信朋友圈)
     clk_share_wct    (点击分享弹窗的分享到微信朋友)

     clk_fuzhu        (点击主页的打开系统辅助按钮)
     clk_help         (点击主页的帮助按钮)
     clk_setting      (点击主页的设置按钮)
     clk_share        (点击主页的分享按钮)
     */

    /*
    //结构化事件(怎么做？)
    @param context：当前Activity
    @param keyPath：最大8个
    @param value：数值参数
    @param label：事件标签，事件的一个属性说明(一期只做采样不做计算，二期会对label进行计算)
    public static void onEvent(Context context, List keyPath, int value, String label)
    {agent.onEvent(context, keyPath, value, label); }

    click_share_wctp	点击分享弹窗的分享到微信朋友圈		0	正常	编辑 删除
    click_share_wct	点击分享弹窗的分享到微信朋友		0	正常	编辑 删除
    click_share	点击主页的分享按钮		0	正常	编辑 删除
    click_alife_vip	点击vip页面的终身使用vip按钮		0	正常	编辑 删除
    click_three_vip	点击vip页面的开通三个月vip按钮		0	正常	编辑 删除
    click_one_vip	点击vip页面的开通一个月vip按钮		0	正常	编辑 删除
    click_about	点击设置页面的关于按钮		0	正常	编辑 删除
    click_closead	点击设置页面的立即去广告按钮		0	正常	编辑 删除
    click_vipbutton	点击设置页面的成为超级vip按钮		0	正常	编辑 删除
    click_fuzhu	点击主页的打开系统辅助按钮		0	正常	编辑 删除
    click_help	点击主页的帮助按钮		0	正常	编辑 删除
    click_setting	点击主页的设置按钮
    * */
    public static void YMclk_alife_vip(Context context){
        MobclickAgent.onEvent(context,"clk_alife_vip");
        //context
        //list
        //数值参数
        //事件标签
        List list = new ArrayList();
        list.add("click_alife_vip");
        MobclickAgent.onEvent(context,list,1,"click_alife_vip");
    }

    public static void YMclk_three_vip(Context context){
        MobclickAgent.onEvent(context,"clk_three_vip");

        List list = new ArrayList();
        list.add("click_three_vip");
        MobclickAgent.onEvent(context,list,1,"click_three_vip");
    }

    public static void YMclk_one_vip(Context context){
        MobclickAgent.onEvent(context,"clk_one_vip");

        List list = new ArrayList();
        list.add("click_one_vip");
        MobclickAgent.onEvent(context,list,1,"click_one_vip");
    }

    public static void YMclk_about(Context context){
        MobclickAgent.onEvent(context,"clk_about");

        List list = new ArrayList();
        list.add("click_about");
        MobclickAgent.onEvent(context,list,1,"click_about");
    }

    public static void YMclk_closead(Context context){
        MobclickAgent.onEvent(context,"clk_closead");

        List list = new ArrayList();
        list.add("click_closead");
        MobclickAgent.onEvent(context,list,1,"click_closead");
    }

    public static void YMclk_vipbutton(Context context){
        MobclickAgent.onEvent(context,"clk_vipbutton");

        List list = new ArrayList();
        list.add("click_vipbutton");
        MobclickAgent.onEvent(context,list,1,"click_vipbutton");
    }

    public static void YMclk_share_wctp(Context context){
        MobclickAgent.onEvent(context,"clk_share_wctp");

        List list = new ArrayList();
        list.add("click_share_wctp");
        MobclickAgent.onEvent(context,list,1,"click_share_wctp");
    }

    public static void YMclk_share_wct(Context context){
        MobclickAgent.onEvent(context,"clk_share_wct");

        List list = new ArrayList();
        list.add("click_share_wct");
        MobclickAgent.onEvent(context,list,1,"click_share_wct");
    }

    public static void YMclk_fuzhu(Context context){
        MobclickAgent.onEvent(context,"clk_fuzhu");

        List list = new ArrayList();
        list.add("click_fuzhu");
        MobclickAgent.onEvent(context,list,1,"click_fuzhu");
    }

    public static void YMclk_help(Context context){
        MobclickAgent.onEvent(context,"clk_help");

        List list = new ArrayList();
        list.add("click_help");
        MobclickAgent.onEvent(context,list,1,"click_help");
    }

    public static void YMclk_setting(Context context){
        MobclickAgent.onEvent(context,"clk_setting");

        List list = new ArrayList();
        list.add("click_setting");
        MobclickAgent.onEvent(context,list,1,"click_setting");
    }

    public static void YMclk_share(Context context){
        MobclickAgent.onEvent(context,"clk_share");

        List list = new ArrayList();
        list.add("click_share");
        MobclickAgent.onEvent(context,list,1,"click_share");
    }
    //抢红包次数
    public static void YMgrasp_num(Context context)
    {
        MobclickAgent.onEvent(context,"grasp_num");
    }
    //计费请求次数
    public static void YMpurchase_num(Context context)
    {
        MobclickAgent.onEvent(context,"purchase_num");
    }
    //收到的钱
    public static void YMmoney_count(Context context,int payid)
    {
        int payType  = 0; //付费类型
        Map<String, String> map_value = new HashMap<String, String>();
        switch (payid){
            case 0:
                //包月 6.66元
                map_value.put("oneMonth" , "6.66" );
                payType = 0;
                break;
            case 1:
                //一季度(三个月) 10.00元
                map_value.put("threeMonth" , "10.00" );
                payType = 1;
                break;
            case 2:
                //终身使用 18.00元
                map_value.put("allLife" , "18.00" );
                payType = 2;
                break;
        }
        MobclickAgent.onEventValue(context, "money_count" , map_value, payType);
    }

    //手机识别相关
    public static void YMPhoneInfo(Context context)
    {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String phoneType = android.os.Build.MODEL;  //获得手机机型
        String imei = mTm.getDeviceId();            //移动设备识别码,识别用户的手机
        String imsi = mTm.getSubscriberId();        //国际移动用户识别码,识别用户的手机卡

        Map<String, String> map_ekv = new HashMap<String, String>();
        if (phoneType != null){
            map_ekv.put("phoneType", phoneType);
        }
        if (imei != null){
            map_ekv.put("imei", imei);
        }
        if (imsi != null){
            map_ekv.put("imsi", imsi);
        }
        MobclickAgent.onEvent(context, "phone_type", map_ekv);
//        Log.i("TAG", "手机型号:"+ phoneType+" imei:"+ imei + " imsi:"+ imsi);
    }
    //带渠道号的钱
    public static void YMMoney_count_bychannel(Context context,String channelIdStr,int moneyNum){
        final String []channel_lists = {
                "000023", "000002", "000005",
                "000007", "000009", "000116",
                "000225", "000800", "150001",
                "150002", "150003", "150004",
                "150005", "100061", "000066",
                "000054", "000020", "000084",
                "000008", "000368", "000016",
                "000014"
        };

        if (moneyNum <0 || "".equals(channelIdStr)){
                return;
            }

        Map<String, String> map_ekv = new HashMap<String, String>();
        map_ekv.put(channelIdStr,"" + moneyNum);//渠道，钱
        MobclickAgent.onEvent(context, "money_by_cnannel", map_ekv);
    }
}
