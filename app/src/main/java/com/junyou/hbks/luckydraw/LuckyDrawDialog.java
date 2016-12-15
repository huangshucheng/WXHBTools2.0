package com.junyou.hbks.luckydraw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.Constants;
import com.junyou.hbks.R;
import com.junyou.hbks.SettingActivity;
import com.junyou.hbks.Utils.LocalSaveUtil;
import com.junyou.hbks.Utils.UmengUtil;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;

public class LuckyDrawDialog extends Dialog implements RotatePlate.AnimationEndListener,View.OnClickListener {

    private RotatePlate mRotateP = null;
    private LuckyDrawLayout mLuckyDrawL = null;
    private ImageView mGoBtn = null;
    private ImageButton mLuckydraw_closeBtn = null;

    private ImageButton mDraw_first_btn = null;
    private ImageButton mDraw_second_btn = null;
    private ImageButton mDraw_third_btn = null;
    private ImageButton mDraw_fourth_btn = null;

    private ImageButton mDraw_dec_btn = null;
    private ImageButton mDraw_plus_btn = null;

    private TextView mDraw_buycoinNum_txt= null;

    private static Activity mActivity;
    //    private String[] strs = {"华为手机","谢谢惠顾","iPhone 6s","mac book","魅族手机","小米手机"};
    private String[] strs = {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};
//    {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};

    private TextView coint_num_text;
    private TextView point_num_text;

    public LuckyDrawDialog(Context context) {
        super(context);
        mActivity = (Activity) context;
    }

    public LuckyDrawDialog(Context context, int themeResId) {
        super(context, themeResId);
        mActivity = (Activity) context;
    }

    protected LuckyDrawDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_luckydraw);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mLuckyDrawL = (LuckyDrawLayout) findViewById(R.id.lucky_draw_layout);
        mLuckyDrawL.startLuckLight();
        mRotateP = (RotatePlate) findViewById(R.id.lucky_draw_plate_layout);
        mRotateP.setAnimationEndListener(this);

        initUI();

        if (mActivity != null && mLuckyDrawL != null){
            mLuckyDrawL.post(new Runnable() {
                @Override
                public void run() {
                    int height =  getWindow().getDecorView().getHeight();
                    int width = getWindow().getDecorView().getWidth();

                    int backHeight = 0;

                    int MinValue = Math.min(width,height);
                    MinValue -= AngleUtil.dip2px(mActivity,10)*2;
                    backHeight = MinValue/2;

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mLuckyDrawL.getLayoutParams();
                    lp.width = MinValue;
                    lp.height = MinValue;

                    mLuckyDrawL.setLayoutParams(lp);

                    MinValue -= AngleUtil.dip2px(mActivity,28)*2;
                    lp = (RelativeLayout.LayoutParams) mRotateP.getLayoutParams();
                    lp.height = MinValue;
                    lp.width = MinValue;

                    mRotateP.setLayoutParams(lp);

                    lp = (RelativeLayout.LayoutParams) mGoBtn.getLayoutParams();
                    lp.topMargin += backHeight;
                    lp.topMargin -= (mGoBtn.getHeight()/2);
                    mGoBtn.setLayoutParams(lp);

                    getWindow().getDecorView().requestLayout();
                }
            });
        }
        Log.i("TAG","draw,oncreate....");
    }

    private void initUI(){
        mLuckydraw_closeBtn = (ImageButton) findViewById(R.id.luckydraw_close_btn);
        if (mLuckydraw_closeBtn != null){
            mLuckydraw_closeBtn.setOnClickListener(onClickClose);
        }

        mGoBtn = (ImageView)findViewById(R.id.go_rotate_btn);
        if (mGoBtn != null){
            mGoBtn.setOnClickListener(onClickRotate);
        }

        mDraw_first_btn = (ImageButton) findViewById(R.id.draw_change_first);
        if (null != mDraw_first_btn){
            mDraw_first_btn.setOnClickListener(this);
        }

        mDraw_second_btn = (ImageButton) findViewById(R.id.draw_change_second);
        if (null != mDraw_second_btn){
            mDraw_second_btn.setOnClickListener(this);
        }

        mDraw_third_btn = (ImageButton) findViewById(R.id.draw_change_third);
        if (null != mDraw_third_btn){
            mDraw_third_btn.setOnClickListener(this);
        }

        mDraw_fourth_btn = (ImageButton) findViewById(R.id.draw_change_fourth);
        if (null != mDraw_fourth_btn){
            mDraw_fourth_btn.setOnClickListener(this);
        }

        mDraw_dec_btn = (ImageButton) findViewById(R.id.draw_dec_btn);
        if (mDraw_dec_btn != null){
            mDraw_dec_btn.setOnClickListener(this);
        }

        mDraw_plus_btn = (ImageButton) findViewById(R.id.draw_plus_btn);
        if (mDraw_plus_btn != null){
            mDraw_plus_btn.setOnClickListener(this);
        }

        coint_num_text = (TextView) findViewById(R.id.draw_coinNum_text);
        point_num_text = (TextView) findViewById(R.id.draw_title_3);
        mDraw_buycoinNum_txt = (TextView) findViewById(R.id.draw_buycoin_number);
    }

    @Override
    public void endAnimation(int position) {
        if (mGoBtn != null && mLuckyDrawL != null && mActivity !=null){
            mGoBtn.setEnabled(true);
            mLuckyDrawL.setDelayTime(500);
            Toast.makeText(mActivity,"Position = "+position+","+strs[position],Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TAG","draw,onstart....");
        if (coint_num_text != null){
            coint_num_text.setText( "" + LocalSaveUtil.getCoinNum());
        }
        if (point_num_text != null){
            point_num_text.setText("" + LocalSaveUtil.getPointNum());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("TAG","draw,onstop....");
    }

    private View.OnClickListener onClickRotate = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            Log.i("TAG","rotate....");
            try {
                mRotateP.startRotate(-1);
//        mRotateP.startRotateNull(-1);//每次都转到谢谢惠顾
                mLuckyDrawL.setDelayTime(100);
                mGoBtn.setEnabled(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener onClickClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.draw_change_first:
                //钱购买金币
                Log.i("TAG","钱购买金币");
            {
                if (mDraw_buycoinNum_txt != null) {
                    if (ComFunction.networkInfo(mActivity)) {
                        if (ComFunction.isWechatAvilible(mActivity)) {
                            try {
                                if (null != WXPayUtil.getInstance()) {
                                    SharedPreferences sharedP = mActivity.getSharedPreferences("config", mActivity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedP.edit();
                                    editor.putString(Constants.MONEY_NUM, mDraw_buycoinNum_txt.getText() + "00");
                                    editor.apply();
                                    WXPayUtil.getInstance().new GetPrepayIdTask().execute();
                                }
                                UmengUtil.YMpurchase_num(mActivity);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(mActivity, "您未安装微信!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mActivity, "网络未连接!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
                break;
            case R.id.draw_change_second:
                //积分兑换一个月VIP
                Log.i("TAG","积分兑换一个月VIP");
                break;
            case R.id.draw_change_third:
                //积分兑换三个月VIP
                Log.i("TAG","积分兑换三个月VIP");
                break;
            case R.id.draw_change_fourth:
                //积分兑换终身VIP
                Log.i("TAG","积分兑换终身VIP");
                break;
            case R.id.draw_plus_btn:
                //加号按钮
                Log.i("TAG","加号按钮");
            {
                if (mDraw_buycoinNum_txt != null) {
                    int num = Integer.valueOf(mDraw_buycoinNum_txt.getText().toString());
                    if (num < 100) {
                        int n = num + 1;
                        mDraw_buycoinNum_txt.setText("" + n);
                    }else{
                        Log.i("TAG", "10...");
                    }
                }
            }
                break;
            case R.id.draw_dec_btn:
                //减号按钮
                Log.i("TAG","减号按钮");
            {
                if (mDraw_buycoinNum_txt != null) {
                    int num = Integer.valueOf(mDraw_buycoinNum_txt.getText().toString());
                    if (num > 1) {
                        int n = num - 1; //String.valueOf(num - 1)
                        mDraw_buycoinNum_txt.setText("" + n);
                    } else {
                        Log.i("TAG", "0...");
                    }
                }
            }
                break;
            default:
                break;
        }
    }
}
