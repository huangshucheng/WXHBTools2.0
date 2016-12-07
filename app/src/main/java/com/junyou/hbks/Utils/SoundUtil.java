package com.junyou.hbks.Utils;

import android.accessibilityservice.AccessibilityService;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import com.junyou.hbks.R;
import java.util.HashMap;

public class SoundUtil {

    private static HashMap<Integer, Integer> hashMap;
    private static SoundPool soundPool;
    private static int currStreamId;
    private static AccessibilityService _mContext;

    public static void initSoundPool(AccessibilityService context) {
        _mContext = context;
        if (null != _mContext){
            hashMap = new HashMap<Integer, Integer>();
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);//最多能同时播放3个音效
            hashMap.put(1, soundPool.load(_mContext, R.raw.qhb, 1));
        }
    }
    //loop : 音效循环的次数 , 0为不循环 , -1为永远循环;
    public static void playSounds(int sound,int loop) {
        if (null != _mContext){
            AudioManager audioManager = (AudioManager) _mContext.getSystemService(_mContext.AUDIO_SERVICE);
            float currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = currVolume / maxVolume;
            currStreamId = soundPool.play(hashMap.get(sound), volume, volume, 1, loop, 1.0f);
           // Log.i("TAG","currStreamId: " + currStreamId);
           // soundPool.play((int)hashMap.get(sound), currVolume, currVolume, 1, 0, (float) 2.0);
        }

        //实例化AudioManager对象，控制声音
//        AudioManager am = (AudioManager)_activity.getSystemService(_activity.AUDIO_SERVICE);
        //最大音量
//        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
//        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
       // soundPool.stop(currStreamId);        //暂停
        //播放

    }
}
