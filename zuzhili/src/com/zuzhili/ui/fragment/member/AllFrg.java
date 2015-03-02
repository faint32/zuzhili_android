package com.zuzhili.ui.fragment.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.ui.activity.space.SpaceActivity;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-5.
 */
public class AllFrg extends BaseMemberFrg {


    @Override
    protected HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("ids", mSession.getIds());
            params.put("listid", mSession.getListid());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    @Override
    protected void reset() {
        if (memberAdapter != null && mSession.isUIShouldUpdate(Constants.PAGE_MEMBERS_ALL)) {
            memberAdapter.clearList();
            memberAdapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_MEMBERS_ALL);
        }
    }

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_ALL_MEMBERS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
