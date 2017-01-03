package com.junyou.hbks.config;

import java.util.StringTokenizer;

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
    public static final String PKG_NAME = "com.junyou.hbks";
    public static final String ACK_NAME = "com.junyou.hbks.UI.MainActivity";

    /*
        "000023"     {360}               //360平台（1.30更新）
        "000002"     {jifeng}               //机锋
        "000005"     {anzhi}               //安智（1.30更新）
        "000007"     {baidu91}               //百度91助手
        "000009"     {yingyonghui}               //应用汇
        "000116"     {wandoujia}               //豌豆荚（1.30更新）
        "000225"     {ppzhushou}               //pp助手(和UC合并)
        "000800"     {sougou}               //搜狗手机助手

        "100061"     {yingyongbao}                 //腾讯应用宝
        "000066"     {xiaomi}                 //小米应用商店
        "000054"     {huawei}                 //华为开发者联盟
        "000020"     {OPPO}                 //OPPO开放平台  可可游戏中心(OPPO)（1.30更新）
        "000084"     {jinli}                 //金立开发者平台
        "000008"     {mumayi}                 //木蚂蚁开发者平台
        "000368"     {vivo}                 //voio开发者平台,步步高应用商店(vivo)（1.30更新）
        "000016"     {lianxiang}                 //联想
        "000014"     {meizu}                 //魅族（1.30更新）

        "000398"     {zhian}                 //酷安应用商店
        "000017"     {youyi}                 //优亿市场
        "000323"     {paojiao}                 //泡椒应用商店
        "160210"     {mogu}                 //蘑菇市场
        "000065"     {woshangdian}                 //沃商店
        "000013"     {chinamm}                 //中国移动开发者商店
        "000072"     {3Ganzhuo}                 //3G安卓市场
        "000097"     {wangyi}                 //网易应用市场
        "100039"     {anzhuoshichang}                 //安卓市场

        //不知名字
        "150001"     {anbeishichang}               //安贝市场
        "150002"     {anfenwang}               //安粉网
        "150003"     {anzhuozhijia}               //安卓之家
        "150004"     {anzhuoyuan}               //安卓园
        "150005"     {yingyongbei}               //应用贝

        "150006"     {anruanshichang}             //安软市场
        "150007"     {duote}             //多特应用商店
        "150008"     {huajun}             //华军软件园
        "150009"     {maopaotang}             //冒泡堂
        "150010"     {zhuole}             //卓乐
        "150011"     {kuchuan}             //酷传应用商店
    */

}
