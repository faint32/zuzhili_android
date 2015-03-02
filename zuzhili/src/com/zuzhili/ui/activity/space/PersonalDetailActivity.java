package com.zuzhili.ui.activity.space;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.BaseActivity;

/**
 * Created by liutao on 14-5-30.
 */
public class PersonalDetailActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.webview)
    private WebView wv;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private String url;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.webview_layout);

        ViewUtils.inject(this);

        setCustomActionBarCallback(this);

        progressBar.setVisibility(View.VISIBLE);

        String ids = getIntent().getStringExtra(Constants.EXTRA_IDS);

        url = String.format("http://form.zuzhili.com/form/zzlformdatashow?compformid=3&uid=%1$s&applyid=1&flag=1", ids);

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        wv.setWebViewClient(new MyWebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(false);
        openUrl(url);
    }

    public void openUrl(String url) {
        wv.setInitialScale(100);
        wv.loadUrl(url);
    }

    @Override
    public boolean showCustomActionBar() {
        String name = getIntent().getStringExtra(Constants.EXTRA_USER_NAME);
        if (name == null) {
            name = "";
        }
        initActionBar(R.drawable.icon_back, 0, name, false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    //when we tap a link Android opensthe link in the
    //default browser and not in our activity window.
    //In order to do that we are overriding the shouldOverrideUrlLoading
    //method
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}