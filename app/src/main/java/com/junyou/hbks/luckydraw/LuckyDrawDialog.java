package com.junyou.hbks.luckydraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.Constants;
import com.junyou.hbks.R;
import com.junyou.hbks.Utils.LocalSaveUtil;
import com.junyou.hbks.Utils.RandomUtil;
import com.junyou.hbks.Utils.TimeManager;
import com.junyou.hbks.Utils.UmengUtil;
import com.junyou.hbks.apppayutils.ComFunction;
import com.junyou.hbks.apppayutils.WXPayUtil;

public class LuckyDrawDialog extends Dialog implements RotatePlate.AnimationEndListener,View.OnClickListener {

    private RotatePlate mRotateP = null;
    private LuckyDrawLayout mLuckyDrawL = null;
    private ImageView mGoBtn = null;
    private ImageView mDraw_rad_msg = null;
    private ImageButton mLuckydraw_closeBtn = null;

    private ImageButton mDraw_first_btn = null;
    private ImageButton mDraw_second_btn = null;
    private ImageButton mDraw_third_btn = null;
    private ImageButton mDraw_fourth_btn = null;

    private ImageButton mDraw_dec_btn = null;
    private ImageButton mDraw_plus_btn = null;

    private TextView mDraw_buycoinNum_txt= null;

    private static Activity mActivity;
    private static NotifyPosListener mNotifyListener;
    private DrawMsgReceiver mMsgReceiver;

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

    //广播接收器
    public class DrawMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("draw_broadcast",true)){
//                Log.i("TAG","receive drawMsgReceiver.......");
                if (LocalSaveUtil.getCoinNum() >= 1){
                    try {
                        mRotateP.startRotate(-1);
                        mLuckyDrawL.setDelayTime(100);
                        mGoBtn.setEnabled(false);
                        LocalSaveUtil.setCoinNum(LocalSaveUtil.getCoinNum() -1 );
                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() + 1);
                        if (coint_num_text != null){
                            coint_num_text.setText("" + LocalSaveUtil.getCoinNum());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
//                    Toast.makeText(mActivity, "金币不足，快去获取金币吧!", Toast.LENGTH_SHORT).show();
                    showDialog();
                }
            }
        }
    }

    public void showDialog(){
        new AlertDialog.Builder(mActivity)
                .setCancelable(true)
                .setMessage("金币不足，快去获取金币吧!")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("TAG","buy coin...");
                    }
                })
                .show();
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
        //注册广播接收
        mMsgReceiver = new DrawMsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.junyou.hbks.drawMsgReceiver");
        mActivity.registerReceiver(mMsgReceiver, intentFilter);

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
//        Log.i("TAG","draw,oncreate....");
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

        mDraw_rad_msg = (ImageView) findViewById(R.id.draw_rad_msg);

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

            if (point_num_text != null){
                point_num_text.setText("" + LocalSaveUtil.getPointNum());
            }

            Dialog dialog_reward= new DrawRewardDialog(mActivity,R.style.dialog_fullscreen);
            if (dialog_reward != null){
                dialog_reward.show();
            }

            if (mNotifyListener != null){
                mNotifyListener.notifyPos(position);
            }

//            Toast.makeText(mActivity,"Position = "+position+","+strs[position],Toast.LENGTH_SHORT).show();
            switch (position){
                case 0:
                {
                    //一小时
//                    Log.i("TAG","一小时");
                    TimeManager.addToLeftTime(60);
                }
                    break;
                case 1:
                {
                    //谢谢惠顾
//                    Log.i("TAG","谢谢惠顾");
                }
                    break;
                case 2:
                {
                    //三小时
//                    Log.i("TAG","三小时");
                    TimeManager.addToLeftTime(180);
                }
                    break;
                case 3:
                {
                    //一个月
//                    Log.i("TAG","一个月");
                    TimeManager.addToLeftTime(43200);
                }
                    break;
                case 4:
                {
                    //三个月
//                    Log.i("TAG","三个月");
                    TimeManager.addToLeftTime(129600);
                }
                    break;
                case 5:
                {
                    //终身
//                    Log.i("TAG","终身");
                    TimeManager.setLifeLongUse(true);
                }
                    break;
                default:
                    break;
            }
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
        if (TimeManager.isDrawNewDay()){
            //免费抽奖提示
            if (mDraw_rad_msg!= null){
                mDraw_rad_msg.setImageResource(R.mipmap.draw_rad_msg_tooltip1);
            }
        }else{
            if (mDraw_rad_msg!= null){
                mDraw_rad_msg.setImageResource(R.mipmap.draw_rad_msg_tooltip2);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActivity.unregisterReceiver(mMsgReceiver);
    }

    private View.OnClickListener onClickRotate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //每天第一次抽奖免费，换图标
            Log.i("TAG","rotate....");
            if (TimeManager.isDrawNewDay()){
                //新的一天进来,免费抽奖  true  抽了奖，false 没抽奖
//                Log.i("TAG","第一次进来抽奖。。。。");
                if (!TimeManager.getFirstDraw()){
                    TimeManager.setFirstDraw(true);
                    TimeManager.setFirstTimeDraw(TimeManager.getCurTimeDraw());
//                    Log.i("TAG","没有抽奖，现在抽了奖。。。。。");
                    //免费抽奖
                    if (mDraw_rad_msg!= null){
                        mDraw_rad_msg.setImageResource(R.mipmap.draw_rad_msg_tooltip2);
                    }

                    try {
                        mRotateP.startRotateNull(-1);//100%转到谢谢惠顾
                        mLuckyDrawL.setDelayTime(100);
                        mGoBtn.setEnabled(false);
                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() + 1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else{
                //金币抽奖
//                Log.i("TAG","不是第一次进来抽奖。。。。");
                if (LocalSaveUtil.getCoinNum() >= 1){
                    try {
//                        mRotateP.startRotate(-1);
                        //0-5包含所有可能 TODO 计算概率
                         mRotateP.startRotateChance(new RandomUtil().startRandom());
                        mLuckyDrawL.setDelayTime(100);
                        mGoBtn.setEnabled(false);
                        LocalSaveUtil.setCoinNum(LocalSaveUtil.getCoinNum() -1 );
                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() + 1);
                        if (coint_num_text != null){
                            coint_num_text.setText("" + LocalSaveUtil.getCoinNum());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //Toast.makeText(mActivity, "金币不足，快去获取金币吧!", Toast.LENGTH_SHORT).show();
                    showDialog();
                }
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
//                Log.i("TAG","钱购买金币");
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
//                Log.i("TAG","积分兑换一个月VIP");
            {
                new AlertDialog.Builder(mActivity)
                        .setCancelable(false)
                        .setMessage("确定兑换一个月VIP吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (LocalSaveUtil.getPointNum() >= 8 ){
                                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() - 8);
                                        if (point_num_text != null){
                                            point_num_text.setText("" + LocalSaveUtil.getPointNum());
                                        }
                                        TimeManager.addToLeftTime(43200);
                                        Toast.makeText(mActivity, "兑换成功!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mActivity, "您的积分不够哦,赶快去获取积分吧!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
                break;
            case R.id.draw_change_third:
                //积分兑换三个月VIP
//                Log.i("TAG","积分兑换三个月VIP");
            {
                new AlertDialog.Builder(mActivity)
                        .setCancelable(false)
                        .setMessage("确定兑换三个月VIP吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (LocalSaveUtil.getPointNum() >= 12){
                                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() - 12);
                                        if (point_num_text != null){
                                            point_num_text.setText("" + LocalSaveUtil.getPointNum());
                                        }
                                        TimeManager.addToLeftTime(129600);
                                        Toast.makeText(mActivity, "兑换成功!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mActivity, "您的积分不够哦,赶快去获取积分吧!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
                break;
            case R.id.draw_change_fourth:
                //积分兑换终身VIP
//                Log.i("TAG","积分兑换终身VIP");
            {
                new AlertDialog.Builder(mActivity)
                        .setMessage("确定兑换终身VIP吗?")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (LocalSaveUtil.getPointNum() >= 20){
                                        LocalSaveUtil.setPointNum(LocalSaveUtil.getPointNum() - 20);
                                        if (point_num_text != null){
                                            point_num_text.setText("" + LocalSaveUtil.getPointNum());
                                        }
                                        SharedPreferences sharedP=  mActivity.getSharedPreferences("config",mActivity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedP.edit();
                                        editor.putBoolean(Constants.IS_ALLLIFEUSE,true);    //终身使用
                                        editor.apply();
                                        Toast.makeText(mActivity, "兑换成功!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mActivity, "您的积分不够哦,赶快去获取积分吧!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
                break;
            case R.id.draw_plus_btn:
                //加号按钮
//                Log.i("TAG","加号按钮");
            {
                if (mDraw_buycoinNum_txt != null) {
                    int num = Integer.valueOf(mDraw_buycoinNum_txt.getText().toString());
                    if (num < 100) {
                        int n = num + 1;
                        mDraw_buycoinNum_txt.setText("" + n);
                    }else{
//                        Log.i("TAG", "10...");
                    }
                }
            }
                break;
            case R.id.draw_dec_btn:
                //减号按钮
//                Log.i("TAG","减号按钮");
            {
                if (mDraw_buycoinNum_txt != null) {
                    int num = Integer.valueOf(mDraw_buycoinNum_txt.getText().toString());
                    if (num > 1) {
                        int n = num - 1; //String.valueOf(num - 1)
                        mDraw_buycoinNum_txt.setText("" + n);
                    } else {
//                        Log.i("TAG", "0...");
                    }
                }
            }
                break;
            default:
                break;
        }
    }

    public interface NotifyPosListener{
        void notifyPos(int pos);
    }

    public static void setNotifyListener(NotifyPosListener l){
        mNotifyListener = l;
    }
}
