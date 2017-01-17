package com.junyou.hbks.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class BtnBlinkUtil {

    public static void startBlink( View view ) {
        if (null == view) {
            return;
        }
        Animation alphaAnimation = new AlphaAnimation( 1, 0 );
        alphaAnimation.setDuration( 500 );
        alphaAnimation.setInterpolator( new LinearInterpolator() );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode( Animation.REVERSE );
        view.startAnimation( alphaAnimation );
    }

    public static void stopBlink( View view ){
        if( null == view ){
            return;
        }
        view.clearAnimation( );
    }
}
