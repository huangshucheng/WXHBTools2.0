package com.junyou.hbks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.junyou.hbks.Utils.ShareHelper;
import com.umeng.analytics .MobclickAgent;

import com.junyou.hbks.fragments.AboutFragment;

public class AboutActivity extends FragmentActivity {

    static private AboutActivity instance;
//    private Dialog dialog_openShare;
//    private TextView about_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        prepareSettings();
        instance = this;
//        about_text = (TextView) findViewById(R.id.about_days_text);
        //弹窗
//        View view_2 = LayoutInflater.from(this).inflate(R.layout.dialog_share,null);
//        dialog_openShare = new Dialog(this,R.style.common_dialog);
//        dialog_openShare.setContentView(view_2);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void prepareSettings()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.preferences_fragment, new AboutFragment());
//        fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void about_performBack(View view)
    {
        super.onBackPressed();
    }

    public static AboutActivity getInstance()
    {
        return instance;
    }

    public void about_getMoreTime(View view)
    {
//        Log.i("TAG", "获得更多时间");
//        if (null != about_text)
//        {
////            about_text.setText("111天");
//        }
//        if (null != dialog_openShare)
//        {
//            dialog_openShare.show();
//        }
    }

    public void closeOpenShare(View view)
    {
//        if (null != dialog_openShare)
//        {
//            dialog_openShare.dismiss();
//        }
    }

    public void sharePengYouQuanClick(View view)
    {
//        Log.i("TAG", "点击分享到朋友圈");
//        if (null != dialog_openShare)
//        {
//            dialog_openShare.dismiss();
//        }

        final String PackageName = "com.tencent.mm";
        final String ActivityName = "com.tencent.mm.ui.tools.ShareToTimeLineUI"; //微信朋友圈
        if (ShareHelper.isInstalled(this,PackageName,ActivityName)){
            //图片加文字
            Bitmap bt= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt, null,null));
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);//带图片分享
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("Kdescription", "红包快手，让红包来的容易点~~");
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), "您没有安装微信", Toast.LENGTH_SHORT).show();
        }

    }
    public void shareWeiXinClick(View view)
    {
//        Log.i("TAG", "点击分享给微信朋友");
//        if (null != dialog_openShare)
//        {
//            dialog_openShare.dismiss();
//        }
        final String PackageName = "com.tencent.mm";
        final String ActivityName = "com.tencent.mm.ui.tools.ShareImgUI";
        if (ShareHelper.isInstalled(this,PackageName,ActivityName)){

            //文字或链接
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "红包快手，让红包来的容易点~~");
            startActivity(intent);

            //图片
            /*
            Bitmap bt= BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bt, null,null));
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(PackageName, ActivityName);
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");        //分享图片，没有图片用转回到分享文字
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(intent);
            */
        }else {
            Toast.makeText(getApplicationContext(), "您没有安装微信", Toast.LENGTH_SHORT).show();
        }
    }
}
