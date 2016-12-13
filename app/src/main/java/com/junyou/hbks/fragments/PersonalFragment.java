package com.junyou.hbks.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.junyou.hbks.AboutActivity;
import com.junyou.hbks.R;
import com.junyou.hbks.SettingActivity;
import com.junyou.hbks.Utils.UmengUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/12/13.
 */
public class PersonalFragment extends PreferenceFragment {

    private CheckBoxPreference suopingGrasp_Preference;
    private CheckBoxPreference weixinGrasp_Preference;
    private CheckBoxPreference qqGrasp_Preference;

    private static PersonalFragment instance;

    private static Activity activity;
    public static void init(Activity context){
        activity = context;
    }

    public PersonalFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.personal_preferences);
        setPrefListeners();
        instance = this;
        weixinGrasp_Preference = (CheckBoxPreference)findPreference("pref_weixin_notification");
        weixinGrasp_Preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Log.i("TAG", "key:" + preference.getKey());       //"key:pref_suoping_grasp"
//                Log.i("TAG", "newValue:" + newValue.toString());    //"true false"
                return true;
            }
        });

        qqGrasp_Preference = (CheckBoxPreference)findPreference("pref_qq_grasp");
        qqGrasp_Preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Log.i("TAG", "key:" + preference.getKey());       //"key:pref_suoping_grasp"
//                Log.i("TAG", "newValue:" + newValue.toString());    //"true false"
                return true;
            }
        });
        suopingGrasp_Preference = (CheckBoxPreference) findPreference("pref_suoping_grasp");

        suopingGrasp_Preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Log.i("TAG", "key:" + preference.getKey());       //"key:pref_suoping_grasp"
//                Log.i("TAG", "newValue:" + newValue.toString());    //"true false"
                return true;
            }
        });

        //点击打开关于页面
        Preference prefAbout = findPreference("pref_etc_about");
        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try{
                    Intent aboutAvt = new Intent(getActivity(),AboutActivity.class);
                    startActivity(aboutAvt);
                    if (activity != null){
                        UmengUtil.YMclk_about(activity);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        //点击红包权限设置 打开系统设置
        Preference prefSetting = findPreference("pref_etc_limit");
        prefSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try{
                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    if(null != accessibleIntent){
                        startActivity(accessibleIntent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


    private void setPrefListeners() {

    }

    public static PersonalFragment getInstance()
    {
        if (instance != null){
            return instance;
        }
        return  null;
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("PersonalCenterActivity");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PersonalCenterActivity");
    }
}
