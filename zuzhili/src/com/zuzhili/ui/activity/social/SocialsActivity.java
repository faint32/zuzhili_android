package com.zuzhili.ui.activity.social;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.model.social.JoinedInSocial;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.SocialFrg;

import java.util.List;

//社区列表
public class SocialsActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    private SocialFrg f;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        super.setContentView(R.layout.activity_frg_container);
        setCustomActionBarCallback(this);
        initData();
    }

    //添加社区列表
    private void initData() {
        setCustomActionBarCallback(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        f = SocialFrg.newInstance(0);
        transaction.add(R.id.lin_container, f).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //是否需要切换社区
        if (getIntent() != null) {
            String listId = getIntent().getStringExtra(Constants.CHANGE_SOCIAL);
            List<JoinedInSocial> socials = mSession.getAccount().getList();
            if (!TextUtils.isEmpty(listId) && socials != null) {
                f.checkSocial(Integer.valueOf(listId));
            }
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(getString(R.string.choicesocial), null, null, false);
        return true;
    }

}
