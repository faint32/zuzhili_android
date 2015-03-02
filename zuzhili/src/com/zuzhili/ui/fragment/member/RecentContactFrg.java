package com.zuzhili.ui.fragment.member;

import com.zuzhili.db.CacheType;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-5.
 */
public class RecentContactFrg extends BaseMemberFrg {

    @Override
    protected HashMap<String, String> buildRequestParams() {
        return null;
    }

    @Override
    protected void reset() {

    }

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_RECENT_CONTACT_MEMBERS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
