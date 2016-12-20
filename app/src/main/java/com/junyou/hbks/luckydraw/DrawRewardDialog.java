package com.junyou.hbks.luckydraw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.junyou.hbks.R;

public class DrawRewardDialog extends Dialog implements View.OnClickListener,LuckyDrawDialog.NotifyPosListener{

    public RelativeLayout mDraw_reward_bg = null;
    public ImageView mDraw_reward_content = null;
    private Intent mBor_intent = null;
    private ImageButton mDraw_reward_closebtn;
    private static Activity mActivity;

    public DrawRewardDialog(Context context) {
        super(context);
        mActivity = (Activity) context;
    }

    public DrawRewardDialog(Context context, int themeResId) {
        super(context, themeResId);
        mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_drawreward);
        initUI();
        LuckyDrawDialog.setNotifyListener(this);
        mBor_intent = new Intent("com.junyou.hbks.drawMsgReceiver");
    }

    private void initUI(){
        mDraw_reward_bg = (RelativeLayout) findViewById(R.id.draw_reward_bg);
        if (mDraw_reward_bg != null){
            mDraw_reward_bg.setOnClickListener(this);
        }
        mDraw_reward_content = (ImageView) findViewById(R.id.draw_reward_content);
        mDraw_reward_closebtn = (ImageButton) findViewById(R.id.draw_reward_closebtn);
        if (mDraw_reward_closebtn != null){
            mDraw_reward_closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
         if(v.getId() == R.id.draw_reward_bg){
//            Log.i("TAG","点击背景");
             this.dismiss();
             //广播发送
             if (mBor_intent != null){
                 //发送广播
                 mBor_intent.putExtra("draw_broadcast", true);
                 mActivity.sendBroadcast(mBor_intent);
             }
        }
    }

    public void setDrawRewardBg(int rId){
        if (null != mDraw_reward_bg){
            mDraw_reward_bg.setBackgroundResource(rId);

        }
    }

    public void setDrawRewardContent(int rId,boolean visible){
        if (null != mDraw_reward_content){
            mDraw_reward_content.setImageResource(rId);
            if (visible){
                mDraw_reward_content.setVisibility(View.VISIBLE);
            }else{
                mDraw_reward_content.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void notifyPos(int pos) {
//        Log.i("TAg","pos: " + pos + " .....");
        switch (pos){
            case 0:
            {
                //一小时
//                Log.i("TAG","一小时");
                setDrawRewardBg(R.mipmap.draw_msg_success_bg);
                setDrawRewardContent(R.mipmap.draw_centent_onehours_under,true);
            }
            break;
            case 1:
            {
                //谢谢惠顾
//                Log.i("TAG","谢谢惠顾");
                setDrawRewardBg(R.mipmap.draw_msg_fail_bg);
                setDrawRewardContent(R.mipmap.draw_centent_fight,false);

            }
            break;
            case 2:
            {
                //三小时
//                Log.i("TAG","三小时");
                setDrawRewardBg(R.mipmap.draw_msg_success_bg);
                setDrawRewardContent(R.mipmap.draw_centent_threehours_under,true);
            }
            break;
            case 3:
            {
                //一个月
//                Log.i("TAG","一个月");
                setDrawRewardBg(R.mipmap.draw_msg_success_bg);
                setDrawRewardContent(R.mipmap.draw_centent_onemouth_under,true);
            }
            break;
            case 4:
            {
                //三个月
//                Log.i("TAG","三个月");
                setDrawRewardBg(R.mipmap.draw_msg_success_bg);
                setDrawRewardContent(R.mipmap.draw_centent_threemouth_under,true);
            }
            break;
            case 5:
            {
                //终身
//                Log.i("TAG","终身");
                setDrawRewardBg(R.mipmap.draw_msg_success_bg);
                setDrawRewardContent(R.mipmap.draw_centent_lifelong_under,true);
            }
            break;
            default:
                break;
        }
    }
}
