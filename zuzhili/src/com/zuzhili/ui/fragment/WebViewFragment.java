package com.zuzhili.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.loginreg.LoginActivity;
import com.zuzhili.ui.activity.loginreg.WebViewActivity;

/**
 * Created by liutao on 14-5-30.
 */
public class WebViewFragment extends FixedOnActivityResultBugFragment implements BaseActivity.TimeToShowActionBarCallback {

    private static final String TAG = WebViewFragment.class.getSimpleName();

    @ViewInject(R.id.webview)
    private WebView wv;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private String http = "http://";
    private String www = "www.";
    private String url;
    private String searchUrl;
    private String tag; // fragment tag

    public static WebViewFragment newInstance(String url, String tag) {
        WebViewFragment f = new WebViewFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_URL, url);
        args.putString(Constants.EXTRA_FRAGMENT_TAG, tag);
        f.setArguments(args);
        return f;
    }

    public static WebViewFragment newInstance(String url, String searchUrl, String tag) {
        WebViewFragment f = new WebViewFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_URL, url);
        args.putString(Constants.EXTRA_SEARCH_URL, searchUrl);
        args.putString(Constants.EXTRA_FRAGMENT_TAG, tag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onActionBarUpdateListener = (OnActionBarUpdateListener) activity;
        } catch (ClassCastException e) {
            onActionBarUpdateListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.webview_layout, container, false);
        ViewUtils.inject(this, view);
        wv.getSettings().setUseWideViewPort(true);
        wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
//                        有问题todo
//                        v.requestFocusFromTouch();
                        break;
                    case MotionEvent.ACTION_MOVE :  break;
                    case MotionEvent.ACTION_CANCEL :  break;
                }
                return false;
            }
        });

        progressBar.setVisibility(View.VISIBLE);

//        url = String.format("http://www.zuzhili.com/vote/%1$s/%2$s/votePhone_list.shtml", mSession.getListid(), mSession.getIds());
//        url = "http://www.zuzhili.com/account/644/phoneinvite/0";
        url = getArguments().getString(Constants.EXTRA_URL);
        searchUrl = getArguments().getString(Constants.EXTRA_SEARCH_URL);
        Log.d(TAG, "Web url is: " + url);
        tag = getArguments().getString(Constants.EXTRA_FRAGMENT_TAG);

        activity.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wv.canGoBack()) {
                    wv.goBack();
                } else {

                    activity.removeFragment(activity.getFragment(getArguments()));
                    if (onActionBarUpdateListener != null) {
                        onActionBarUpdateListener.shouldUpdateActionBar();
                    }
                }
            }
        });

        activity.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, WebViewActivity.class);
                it.putExtra("url", searchUrl);
                startActivity(it);
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    activity.mActionBar.show();

                    if (searchUrl != null) {
                        activity.initActionBar(R.drawable.icon_back, R.drawable.icon_action_search, wv.getTitle(), false);
                    } else {
                        activity.initActionBar(R.drawable.icon_back, 0, wv.getTitle(), false);
                    }

                    if (view.getTitle() != null && view.getTitle().equals("登陆")) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
        });
//        chrome联调
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//        {    WebView.setWebContentsDebuggingEnabled(true);}

        wv.setWebViewClient(new MyWebViewClient());
        wv.setFocusableInTouchMode(true);
        wv.setFocusable(true);
        wv.setHapticFeedbackEnabled(true);
        wv.setClickable(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setAppCacheEnabled(false);
        openUrl(url);
        return view;
    }

    public String validateUrl(String url) {
        if (url.startsWith(www)) url = http + url;
        else if (!url.startsWith(http) && !url.startsWith(www))
            url = http + www + url;
        return (url);
    }

    public void getUrl(View v) {
        url = validateUrl(url);
        openUrl(url);
    }

    public void openUrl(String url) {
        wv.setInitialScale(100);
        wv.loadUrl(url);
    }

    //when we tap a link Android opensthe link in the
    //default browser and not in our activity window.
    //In order to do that we are overriding the shouldOverrideUrlLoading
    //method
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            activity.initActionBar(R.drawable.icon_back, 0, view.getTitle(), false);
            if (view.getTitle() != null && view.getTitle().equals("登陆")) {
                // 跳转到原生登陆界面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                return true;
            }
            view.loadUrl(url);
            return false;//Allow WebView to load url
        }
    }
}
