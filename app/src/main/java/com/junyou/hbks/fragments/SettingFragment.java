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

import com.junyou.hbks.Utils.UmengUtil;
import com.umeng.analytics.MobclickAgent;

import com.junyou.hbks.AboutActivity;
import com.junyou.hbks.SettingActivity;

//PreferenceFragment
public class SettingFragment extends PreferenceFragment
{
    private CheckBoxPreference settingShare_Preference;
    private CheckBoxPreference suopingGrasp_Preference;

    public Dialog dialog_setting_share;

    private static SettingFragment instance;

    private static Activity activity;
    public static void init(Activity context){
        activity = context;
    }

    public SettingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.junyou.hbks.R.xml.general_preferences);
        setPrefListeners();
        instance = this;
        settingShare_Preference = (CheckBoxPreference) findPreference("pref_no_ad");
        //点击立即去广告
        settingShare_Preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference preference)
            {
//                Log.i("TAG", "点击立即去广告");
                try{
                    View view_1 = LayoutInflater.from(SettingActivity.getInstance()).inflate(com.junyou.hbks.R.layout.dialog_settingshare, null);
                    dialog_setting_share = new Dialog(SettingActivity.getInstance(), com.junyou.hbks.R.style.common_dialog);
                    dialog_setting_share.setContentView(view_1);
                    dialog_setting_share.show();
                    //下载骏游连连看后设置为true
                    settingShare_Preference.setChecked(false);
                    if (activity != null){
                        UmengUtil.YMclk_closead(activity);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        //点击锁屏抢红包
        suopingGrasp_Preference = (CheckBoxPreference) findPreference("pref_suoping_grasp");
        /*
        suopingGrasp_Preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference preference)
            {
                Log.i("TAG", "点击锁屏抢红包");
                return false;
            }
        });
        */
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

    public static SettingFragment getInstance()
    {
        if (instance != null){
            return instance;
        }
        return  null;
    }

    private void setPrefListeners() {

    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("SettingActivity");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
    }
    //放在SettingActivity中去实现
    /*
    public void closeDownloadClick(View view)
    {
        Log.i("TAG", "关闭弹窗");
        dialog_setting_share.dismiss();
    }
    public void opendownloadClick(View view)
    {
        Log.i("TAG", "打开下载链接");
    }
    */

}
