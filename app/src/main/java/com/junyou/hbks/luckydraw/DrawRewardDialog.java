package com.junyou.hbks.luckydraw;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.junyou.hbks.R;

public class DrawRewardDialog extends Dialog implements View.OnClickListener{

    public RelativeLayout mDraw_reward_bg = null;
    public ImageView mDraw_reward_content = null;

    public DrawRewardDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_drawreward);
        initUI();
    }

    private void initUI(){
        mDraw_reward_bg = (RelativeLayout) findViewById(R.id.draw_reward_bg);
        mDraw_reward_content = (ImageView) findViewById(R.id.draw_reward_content);
        if (mDraw_reward_bg != null){
            mDraw_reward_bg.setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
         if(v.getId() == R.id.draw_reward_bg){
            Log.i("TAG","点击背景");
        }
    }

    public void setDrawRewardBg(int rId){
        if (null != mDraw_reward_bg){
            mDraw_reward_bg.setBackgroundResource(rId);
        }
    }

    public void setDrawRewardContent(int rId){
        if (null != mDraw_reward_content){
            mDraw_reward_content.setImageResource(rId);
        }
    }

}
