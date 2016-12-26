package com.junyou.hbks.UI;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.junyou.hbks.R;
import com.umeng.analytics.MobclickAgent;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String webViewUrl, webViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        loadUI();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && !bundle.isEmpty()) {
            webViewTitle = bundle.getString("title");
            webViewUrl = bundle.getString("url");

            TextView webViewBar = (TextView) findViewById(R.id.webview_bar);
            webViewBar.setText(webViewTitle);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setHorizontalScrollBarEnabled(false);
            webView.getSettings().setSupportZoom(true);
            webView.setInitialScale(180);
            webView.getSettings().setUseWideViewPort(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.indexOf("apk") > 0) {
                        Toast.makeText(getApplicationContext(), "正在准备下载", Toast.LENGTH_SHORT).show();
                        (new DownloadUtil()).enqueue(url, getApplicationContext());
                        return true;
                    } else {
                        view.loadUrl(url);
                        return false;
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    CookieSyncManager.getInstance().sync();
                }
            });
            webView.loadUrl(webViewUrl);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loadUI() {

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffE46C62);
    }

    public void web_performBack(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WebViewActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WebViewActivity");
        MobclickAgent.onPause(this);
    }

    public class DownloadUtil {
        public void enqueue(String url, Context context) {
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "tgllx-daiji_3009-2.3.0-201605191729.apk");
            r.allowScanningByMediaScanner();
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(r);
        }
    }
}