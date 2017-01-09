package com.junyou.hbks.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.junyou.hbks.R;
import com.junyou.hbks.alipayutils.AliPayUtil;
import com.junyou.hbks.apppayutils.WXPayUtil;
import com.junyou.hbks.utils.LogUtil;
import com.junyou.hbks.utils.SaveMoneyUtil;

import java.math.BigDecimal;

public class SelectPayDialog extends Dialog {

    private Context mContext = null;

    private ImageButton mClose_btn = null;
    private ImageButton mPay_btn = null;

    private RelativeLayout mLayout_zfb = null;
    private RelativeLayout mLayout_wx = null;

    private ImageView mId_zfb_pay = null;
    private ImageView mId_wx_pay = null;

    private TextView mPay_money_text = null;

    private PAYTYPE mPayType = PAYTYPE.ZFBPAY;

    private enum PAYTYPE {
        ZFBPAY,
        WXPAY
    }

    public SelectPayDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        WXPayUtil.init(mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_selectpay);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initUI();
    }

    private void initUI(){
        mClose_btn = (ImageButton) findViewById(R.id.pay_close_btn);
        if (mClose_btn != null){
            mClose_btn.setOnClickListener(onClickClose);
        }

        mPay_btn = (ImageButton) findViewById(R.id.pay_okbtn);
        if (mPay_btn != null){
            mPay_btn.setOnClickListener(onClickPay);
        }

        mLayout_zfb = (RelativeLayout) findViewById(R.id.pay_bg_1);
        if (mLayout_zfb != null){
            mLayout_zfb.setOnClickListener(onClickZfb);
        }

        mLayout_wx = (RelativeLayout) findViewById(R.id.pay_bg_2);
        if (mLayout_wx != null){
            mLayout_wx.setOnClickListener(onClickWx);
        }

        mId_zfb_pay = (ImageView) findViewById(R.id.id_zfb_pay);
        mId_wx_pay = (ImageView) findViewById(R.id.id_wx_pay);

        mPay_money_text = (TextView) findViewById(R.id.pay_money_text);
        if (mPay_money_text != null){
            if (!"".equals(getMoneyFromShareP())){
                mPay_money_text.setText("" + getMoneyFromShareP() + "元");
            }
        }
    }

    private String getMoneyFromShareP(){
        SaveMoneyUtil instance = SaveMoneyUtil.getInitialize(mContext);
        String money = null;
        if (null != instance){
            money = instance.getMoneyCount();
            if (!"".equals(money)){
                try{
                    BigDecimal b1 = new BigDecimal(money);
                    BigDecimal b2 = new BigDecimal("100");
                    BigDecimal b3 = b1.divide(b2,2,BigDecimal.ROUND_HALF_EVEN);
                    return b3.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private View.OnClickListener onClickClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private View.OnClickListener onClickPay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.i("Pay...."  + (mPayType == PAYTYPE.ZFBPAY ? "支付宝" : "微信"));
            if (mPayType == PAYTYPE.ZFBPAY){
            //支付宝支付
            if (!"".equals(getMoneyFromShareP())){
                AliPayUtil.getInitialize(mContext).AliPayV2("" +getMoneyFromShareP());
            }
            }else if (mPayType == PAYTYPE.WXPAY){
            //微信支付
                WXPayUtil.getInitialize().new GetPrepayIdTask().execute();
            }
            dismiss();
        }
    };

    private View.OnClickListener onClickZfb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        LogUtil.i("pay zfb...");
            mPayType = PAYTYPE.ZFBPAY;
            setBtnImage(mPayType);
        }
    };

    private View.OnClickListener onClickWx = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.i("pay wx...");
            mPayType = PAYTYPE.WXPAY;
            setBtnImage(mPayType);
        }
    };

    private void setBtnImage(PAYTYPE tpye){
        if (tpye == PAYTYPE.ZFBPAY){
            if (mId_zfb_pay != null){
                mId_zfb_pay.setImageResource(R.mipmap.btn_selected);
            }
            if (mId_wx_pay != null){
                mId_wx_pay.setImageResource(R.mipmap.btn_default);
            }
        }else if(tpye == PAYTYPE.WXPAY){
            if (mId_zfb_pay != null){
                mId_zfb_pay.setImageResource(R.mipmap.btn_default);
            }
            if (mId_wx_pay != null){
                mId_wx_pay.setImageResource(R.mipmap.btn_selected);
            }
        }

    }
}
