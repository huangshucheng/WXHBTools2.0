package com.junyou.hbks.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.R;
import com.junyou.hbks.utils.LocalSaveUtil;
import com.junyou.hbks.luckydraw.AngleUtil;
import com.junyou.hbks.luckydraw.LuckyDrawLayout;
import com.junyou.hbks.luckydraw.RotatePlate;
import com.junyou.hbks.utils.LogUtil;

public class LuckyDraw extends AppCompatActivity implements RotatePlate.AnimationEndListener{

    private RotatePlate mRotateP;
    private LuckyDrawLayout mLuckyDrawL;
    private ImageView mGoBtn;
//    private String[] strs = {"华为手机","谢谢惠顾","iPhone 6s","mac book","魅族手机","小米手机"};
    private String[] strs = {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};
//    {"一小时使用时间","谢谢惠顾","三小时使用时间","一个月VIP","三个月VIP","终身VIP"};

    private TextView coint_num_text;
    private TextView point_num_text;
    private TextView user_account_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

        mLuckyDrawL = (LuckyDrawLayout) findViewById(R.id.lucky_draw_layout);
        mLuckyDrawL.startLuckLight();
        mRotateP = (RotatePlate) findViewById(R.id.lucky_draw_plate_layout);
        mRotateP.setAnimationEndListener(this);

        mGoBtn = (ImageView)findViewById(R.id.go_rotate_btn);

        coint_num_text = (TextView) findViewById(R.id.coint_num_text);
        point_num_text = (TextView) findViewById(R.id.point_num_text);
        user_account_text = (TextView) findViewById(R.id.user_account);

        mLuckyDrawL.post(new Runnable() {
            @Override
            public void run() {
                int height =  getWindow().getDecorView().getHeight();
                int width = getWindow().getDecorView().getWidth();

                int backHeight = 0;

                int MinValue = Math.min(width,height);
                MinValue -= AngleUtil.dip2px(LuckyDraw.this,10)*2;
                backHeight = MinValue/2;

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mLuckyDrawL.getLayoutParams();
                lp.width = MinValue;
                lp.height = MinValue;

                mLuckyDrawL.setLayoutParams(lp);

                MinValue -= AngleUtil.dip2px(LuckyDraw.this,28)*2;
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

    @Override
    public void endAnimation(int position) {
        mGoBtn.setEnabled(true);
        mLuckyDrawL.setDelayTime(500);
        Toast.makeText(this,"Position = "+position+","+strs[position],Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (coint_num_text != null){
            coint_num_text.setText("金币: " + LocalSaveUtil.getInitialize(this).getCoinNum());
        }
        if (point_num_text != null){
            point_num_text.setText("积分: " + LocalSaveUtil.getInitialize(this).getPointNum());
        }
        if (user_account_text != null){
            user_account_text.setText("账户: " + LocalSaveUtil.getInitialize(this).getAccount());
        }
        LogUtil.i("TAG","resume");
    }

//    public void rotationClick(View view){
//        mRotateP.startRotate(-1);
////        mRotateP.startRotateNull(-1);//每次都转到谢谢惠顾
//        mLuckyDrawL.setDelayTime(100);
//        mGoBtn.setEnabled(false);
//    }

    public void superVipClick(View view)
    {
        try {
            Intent vipAvt = new Intent(this,VipActivity.class);
            startActivity(vipAvt);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void convert_oneMonth(View view){
        LogUtil.i("TAG","one month");
        if (point_num_text != null){
            LocalSaveUtil.getInitialize(this).setPointNum(10 + LocalSaveUtil.getInitialize(this).getPointNum());

            int pointNum = LocalSaveUtil.getInitialize(this).getPointNum();
            point_num_text.setText("积分: " + pointNum);
        }
    }

    public void convert_threeMonth(View view){
        LogUtil.i("TAG","three month");
        if (coint_num_text != null){
            LocalSaveUtil.getInitialize(this).setCoinNum(10 + LocalSaveUtil.getInitialize(this).getCoinNum());

            int coinNum = LocalSaveUtil.getInitialize(this).getCoinNum();
            coint_num_text.setText("金币: " + coinNum);
        }
    }

    public void convert_allLife(View view){
        int idNum = (int) ((Math.random() * 9 + 1) * 100000);
        LogUtil.i("TAG","all life"+ "id:" + idNum);
    }
}
