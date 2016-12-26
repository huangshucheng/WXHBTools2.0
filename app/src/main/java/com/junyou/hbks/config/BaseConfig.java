package com.junyou.hbks.config;

public class BaseConfig {

    private boolean qqEnabled = true;   //qq抢红包是否启动
    private boolean wcEnabled = true;   //微信抢红包是否启动
    private boolean zfbEnabled = true;  //支付宝是否启动
    private boolean autoUnlock = true;  //是否启动锁黑屏自动抢模式

    private int WXVersionCode;          //手机安装的微信版本号
    private String WXVersionName = "未安装";   //手机安装的微信版本名称

    private int QQVersionCode;
    private String QQVersionNmae = "未安装";

    public int getWXVersionCode() {
        return WXVersionCode;
    }

    public void setWXVersionCode(int versionCode) {
        this.WXVersionCode = versionCode;
    }

    public String getWXVersionName() {
        return WXVersionName;
    }

    public void setWXVersionName(String versionName) {
        this.WXVersionName = versionName;
    }

    public int getQQVersionCode(){
        return QQVersionCode;
    }

    public void setQQVersionCode(int versionCode){
        this.QQVersionCode = versionCode;
    }

    public String getQQVersionNmae(){
        return QQVersionNmae;
    }

    public void setQQVersionNmae(String versionNmae){
        this.QQVersionNmae = versionNmae;
    }

    public boolean isQqEnabled() {
        return qqEnabled;
    }

    public void setQqEnabled(boolean qqEnabled) {
        this.qqEnabled = qqEnabled;
    }

    public boolean isWcEnabled() {
        return wcEnabled;
    }

    public void setWcEnabled(boolean wcEnabled) {
        this.wcEnabled = wcEnabled;
    }

    public boolean isZfbEnabled(){
        return this.zfbEnabled;
    }

    public void setZfbEnabled(boolean zfbEnabled){
        this.zfbEnabled = zfbEnabled;
    }

    public boolean isAutoUnlock() {
        return autoUnlock;
    }

    public void setAutoUnlock(boolean autoUnlock) {
        this.autoUnlock = autoUnlock;
    }
}
