package com.zuzhili.ui.activity.social;

import android.os.Bundle;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.fragment.WebViewFragment;

public class ConstructSociaActivity extends BaseActivity implements FixedOnActivityResultBugFragment.OnActionBarUpdateListener {

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_root);
        mActionBar.hide();
        String url = Task.API_HOST_URL+"towapaddnetwork?userid="+mSession.getUid();
        attachFragment(R.id.container, WebViewFragment.newInstance(url, Constants.TAG_REGISTER), Constants.TAG_REGISTER);
    }

    @Override
    public void shouldUpdateActionBar() {
        initActionBar(R.drawable.icon_back, 0, "社区_申请创建社区", false);
        finish();
    }
}
