package com.junyou.hbks.luckydraw;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.junyou.hbks.R;

public class LuckyDrawDialog extends Dialog {

    public LuckyDrawDialog(Context context) {
        super(context);
    }

    public LuckyDrawDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LuckyDrawDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_luckydraw);
    }

}
