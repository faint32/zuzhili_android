package com.zuzhili.ui.fragment.atme;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.AtMeCommentAdapter;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.images.ImageCacheManager;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-13.
 */
public class AtMeCommentFrg extends BaseAtMeFrg {


    /**
     * 切换社区后需要更新数据
     */
    public void update() {
        setCacheType();
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_AT_COMMENT)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_AT_COMMENT);
        }
        if (adapter == null) {
            adapter = new AtMeCommentAdapter(activity
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    ,
                    buildRequestParams()
                    , mSession
                    , CacheType.CACHE_GET_AT_ME_COMMENT_INFO
                    , AtMeCommentAdapter.REQUEST_GET_AT_ME_COMMENT_INFO
                    , this
                    , false);
        }
        adapter.setListView(pullRefreshListView);
        adapter.setOnRefreshListener();
        pullRefreshListView.setAdapter(adapter);
        pullRefreshListView.setOnItemClickListener(((AtMeCommentAdapter) adapter).atMeCommentOnClickListener);
    }

    @Override
    protected HashMap<String, String> buildRequestParams() {

        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("listid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_AT_ME_COMMENT_INFO;
    }

}
