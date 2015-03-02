package com.zuzhili.ui.activity.loginreg;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.fragment.WebViewFragment;

import org.apache.http.cookie.Cookie;

/**
 * Created by liutao on 9/16/14.
 */
public class WebViewActivity extends BaseActivity  implements FixedOnActivityResultBugFragment.OnActionBarUpdateListener {

    private static final String ARG_URL = "url";
    private static final String ARG_SEARCH_URL = "searchUrl";

    String mUrl;
    String mSearchUrl;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_root);

        if (inState != null) {
            mUrl = inState.getString(ARG_URL);
            mSearchUrl = inState.getString(ARG_SEARCH_URL);
        } else {
            mUrl = getIntent().getStringExtra("url");
            mSearchUrl = getIntent().getStringExtra("searchUrl");
        }

        if (mUrl.startsWith("http://121.42.53.110:8080")) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();
            cookieManager.setCookie("http://121.42.53.110:8080", Session.get(this).getSessionId());
            cookieSyncManager.sync();
        }
        mActionBar.hide();
        attachFragment(R.id.container, WebViewFragment.newInstance(mUrl, mSearchUrl, Constants.TAG_REGISTER), Constants.TAG_REGISTER);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_URL, mUrl);
        outState.putString(ARG_SEARCH_URL, mSearchUrl);
    }

    @Override
    public void shouldUpdateActionBar() {
        initActionBar(R.drawable.icon_back, 0, "", false);
        finish();
    }
}
