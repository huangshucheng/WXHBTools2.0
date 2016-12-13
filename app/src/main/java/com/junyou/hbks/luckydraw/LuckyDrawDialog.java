package com.junyou.hbks.luckydraw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.R;
import com.junyou.hbks.SettingActivity;
import com.junyou.hbks.Utils.UmengUtil;

public class LuckyDrawDialog extends Dialog implements RotatePlate.AnimationEndListener{

    private RotatePlate mRotateP;
    private LuckyDrawLayout mLuckyDrawL;
    private ImageView mGoBtn;
    private ImageButton mLuckydraw_closeBtn;
    private static Activity mActivity;
    //    private String[] strs = {"华为手机","谢谢惠顾","iPhone 6s","mac book","魅族手机","小米手机"};
    private String[] strs = {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};
//    {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};

    private TextView coint_num_text;
    private TextView point_num_text;
    private TextView user_account_text;

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

        mLuckydraw_closeBtn = (ImageButton) findViewById(R.id.luckydraw_close_btn);
        if (mLuckydraw_closeBtn != null){
            mLuckydraw_closeBtn.setOnClickListener(onClickClose);
        }

        mGoBtn = (ImageView)findViewById(R.id.go_rotate_btn);
        if (mGoBtn != null){
            mGoBtn.setOnClickListener(onClickRotate);
        }

        coint_num_text = (TextView) findViewById(R.id.coint_num_text);
        point_num_text = (TextView) findViewById(R.id.point_num_text);
        user_account_text = (TextView) findViewById(R.id.user_account);
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
    }

    @Override
    public void endAnimation(int position) {
        if (mGoBtn != null && mLuckyDrawL != null && mActivity !=null){
            mGoBtn.setEnabled(true);
            mLuckyDrawL.setDelayTime(500);
            Toast.makeText(mActivity,"Position = "+position+","+strs[position],Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener onClickRotate = new View.OnClickListener()
    {
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

    private View.OnClickListener onClickClose = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
