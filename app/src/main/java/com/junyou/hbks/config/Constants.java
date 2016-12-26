package com.junyou.hbks.config;

public class Constants {

    // 签名：8a4d7ee09f2db1dc793dcd0f9aeafc1f
    // 包名：com.junyou.hbks
    public static final String APP_ID = "wxc49e0229ed593c33";   //app_ID
    //商号
    public static final String PARTNER_ID = "1400959502";
    //微信公众平台商户模块和商户约定的密钥
    public static final String PARTNER_KEY="d14fe370bdf1664c34b258d65f8d3509";
    //统一下单接口
    public static final String ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //查询订单接口
    public static final String QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    //回调接口
    public static final String NOTIFY_URL = "http://www.zjhzjykj.com/";
    //数据保存
    public static final String MONEY_NUM = "money_num";

    //服务是否能够开启 若有天数，能抢红包，若没有天数，不能抢红包
    public static final String IS_SERVICE_ON = "isserviceon";         //服务是否开启  true开启  false 关闭
    public static final String IS_ALLLIFEUSE = "isalllifeuse";        //是否终身使用
    public static final String ISOPEN_FLAG = "isopen_flag";
    public static final String LEFT_DAYS_COUNT = "left_days_count";  //剩余的天数
    public static final String IS_NEW_DAY = "is_new_day";           //是否新的一天
    public static final String USE_DAY = "use_day";                 //用户使用的天数
    public static final String ORDER_NUM = "order_num";             //订单号

}
