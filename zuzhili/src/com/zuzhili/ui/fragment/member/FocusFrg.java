package com.zuzhili.ui.fragment.member;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.utils.Utils;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-5.
 */
public class FocusFrg extends BaseMemberFrg {

    @Override
    protected HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("ids", mSession.getIds());
            params.put("start", "0");
        }
        return params;
    }

    @Override
    protected void reset() {
        if (memberAdapter != null && mSession.isUIShouldUpdate(Constants.PAGE_MEMBERS_FOCUS)) {
            memberAdapter.clearList();
            memberAdapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_MEMBERS_FOCUS);
        }
    }

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_FOCUS_MEMBERS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
