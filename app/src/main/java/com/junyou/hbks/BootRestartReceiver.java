package com.junyou.hbks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootRestartReceiver extends BroadcastReceiver {

    //开机启动广播
    private final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION));
        {
            try {
                Intent newIntent = new Intent(context, MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
