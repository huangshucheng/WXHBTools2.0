package com.junyou.hbks.robmoney;

public class ZFBParams {
    /** 支付宝的包名*/
    public static final String PACKAGENAME = "com.eg.android.AlipayGphone";


    public static final String KEY_ZFBREDPACKET = "发来一个红包";
    public static final String KEY_ZFBREDPACKETONE = "在群聊中发了一个红包";
    public static final String KEY_RETURN_DESC = "返回";

    public static final String KEY_GETREDPACKET = "领取红包";

    //聊天界面
    public static final String ZFB_PERSONALMSG = "com.alipay.mobile.chatapp.ui.PersonalChatMsgActivity_";
    public static final String ZFB_GROUPMSG = "com.alipay.mobile.chatapp.ui.GroupChatMsgActivity_";
    //抢红包界面
    public static final String ZFB_PERSONALROB = "com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity";
    public static final String ZFB_GROUPROB = "com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity";
    //红包详情界面
    public static final String ZFB_PERSONALCROWD = "com.alipay.android.phone.discovery.envelope.get.GetRedEnvelopeActivity";
    public static final String ZFB_GROUPCROWD = "com.alipay.android.phone.discovery.envelope.crowd.CrowdHostActivity";
    //用ID找 测试
    public static final String ZFB_CLICIID = "com.alipay.mobile.chatapp:id/chat_msg_layout";
    public static final String ZFB_CLICK_TEST = "com.alipay.mobile.ui:id/title_bar_back_button";
    public static final String ZFB_ICON = "com.alipay.mobile.chatapp:id/chat_msg_avatar";

    public static final int WINDOW_NONE = 0;
    public static final int WINDOW_LUCKYMONEY_RECEIVEUI = 1;
    public static final int WINDOW_LUCKYMONEY_DETAIL = 2;
    public static final int WINDOW_LAUNCHER = 3;

    public static final String CLASS_NAME_IMAGEBUTTON = "android.widget.ImageButton";
}
