package com.junyou.hbks.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class ShareHelper {
    //这些都是分享时，对应的包名和类名。

    // 微信好友
    // package = com.tencent.mm,
    // activity = com.tencent.mm.ui.tools.ShareImgUI

    // 微信朋友圈
    // package = com.tencent.mm,
    // activity = com.tencent.mm.ui.tools.ShareToTimeLineUI

    //qq好友
    // package = com.tencent.mobileqq,
    // activity = com.tencent.mobileqq.activity.JumpActivity

    //手机qq里的发送到电脑
    // package = com.tencent.mobileqq,
    // activity = com.tencent.mobileqq.activity.qfileJumpActivity

    // QQ空间
    // package = com.qzone,
    // activity = com.qzone.ui.operation.QZonePublishMoodActivity

    // 人人
    // package = com.renren.mobile.android,
    // activity = com.renren.mobile.android.publisher.UploadPhotoEffect

    // 陌陌
    // package = com.immomo.momo,
    // activity = com.immomo.momo.android.activity.feed.SharePublishFeedActivity

    // 新浪微博
    // package = com.sina.weibo, activity = com.sina.weibo.EditActivity

    // 腾讯微博
    // package = com.tencent.WBlog,
    // activity = com.tencent.WBlog.intentproxy.TencentWeiboIntent

//packageName = im.yixin, name = im.yixin.activity.share.ShareToSnsActivity
//packageName = im.yixin, name = im.yixin.activity.share.ShareToSessionActivity
//packageName = com.alibaba.android.babylon, name = com.alibaba.android.babylon.biz.im.activity.RecentIMListActivity
//packageName = com.alibaba.android.babylon, name = com.alibaba.android.babylon.biz.home.activity.CreateFeedActivity

    public static boolean isInstalled(Context context, String packageName, String activityName) {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }
    //是否安装骏游连连看
    public static boolean isInstalledJunyouLik(Context context)
    {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++)
            {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.candy.no"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
