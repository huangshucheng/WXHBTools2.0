# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\androidDev\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-optimizationpasses 5               # 指定代码的压缩级别
#-dontusemixedcaseclassnames         # 是否使用大小写混合
#-dontskipnonpubliclibraryclasses    #指定不忽略非公共类库
#-dontpreverify                      # 混淆时是否做预校验
#-ignorewarnings                     #屏蔽警告
#-verbose                            # 混淆时是否记录日志
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

#-----------------导入第三方包,但是在当前版本中使用会报 input jar file is specified twice 错误，所以注释掉
#-libraryjars libs/android.support.v4.jar
#-libraryjars libs/BaiduLBS_Android.jar
#-libraryjars libs/commons-httpclient-3.1.jar
#-libraryjars libs/jackson-annotations-2.1.4.jar
#-libraryjars libs/jackson-core-2.1.4.jar
#-libraryjars libs/jackson-databind-2.1.4.jar
#-libraryjars libs/xUtils-2.6.14.jar
#-----------------不需要混淆第三方类库------------------------------------------------------------------
#-dontwarn android.support.v4.**                                             #去掉警告
#-keep class android.support.v4.** { *; }                                    #过滤android.support.v4
#-keep interface android.support.v4.app.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment
#
#-keep class org.apache.**{*;}                                               #过滤commons-httpclient-3.1.jar
#
#-keep class com.fasterxml.jackson.**{*;}                                    #过滤jackson-core-2.1.4.jar等

#-dontwarn com.lidroid.xutils.**                                             #去掉警告
#-keep class com.lidroid.xutils.**{*;}                                       #过滤xUtils-2.6.14.jar
#-keep class * extends java.lang.annotation.Annotation{*;}                   #这是xUtils文档中提到的过滤掉注解
##baidu
#-dontwarn com.baidu.**                                                      #去掉警告
#-dontwarn com.baidu.mapapi.**

#-keep class com.baidu.** {*;}                                               #过滤BaiduLBS_Android.jar
#-keep class vi.com.gdi.bgl.android.**{*;}
#-keep class com.baidu.platform.**{*;}
#-keep class com.baidu.location.**{*;}
#-keep class com.baidu.vi.**{*;}
##alipay
#-keep class com.alipay.android.app.IAlixPay{*;}
#-keep class com.alipay.android.app.IAlixPay$Stub{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}

#-keep class com.alipay.sdk.app.PayTask{ public *;}
#-keep class com.alipay.sdk.app.AuthTask{ public *;}
#wechat
#-keep class com.tencent.mm.sdk.** {*;}

#umeng
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#
#-keep public class [com.junyou.hbks].R$*{
#    public static final int *;
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-----------------不需要混淆系统组件等-------------------------------------------
#-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
#-keep public class * extends android.app.Application   # 保持哪些类不被混淆
#-keep public class * extends android.app.Service       # 保持哪些类不被混淆
#-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
#-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
#-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
#-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
#-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

#----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在-------
#-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
#    native <methods>;
#}
#-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
#    public void *(android.view.View);
#}
#-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
#    public static final android.os.Parcelable$Creator *;
#}