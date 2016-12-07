package com.junyou.hbks.fragments;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import com.junyou.hbks.AboutActivity;

public class AboutFragment extends PreferenceFragment
{
    public AboutFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.junyou.hbks.R.xml.about_preference);

        //点赞功能
        Preference click_dianzan = findPreference("pref_dianzan");
        click_dianzan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
//                Log.i("TAG", "点赞~");
                return false;
            }
        });

        //跟新功能
        Preference click_update = findPreference("pref_update");
        click_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try{
                    Toast.makeText(AboutActivity.getInstance(), "已经是最新版本~", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AboutActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutActivity");
    }
}
