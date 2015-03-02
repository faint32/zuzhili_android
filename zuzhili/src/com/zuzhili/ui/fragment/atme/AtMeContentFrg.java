package com.zuzhili.ui.fragment.atme;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.TrendAdapter;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.images.ImageCacheManager;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-13.
 */
public class AtMeContentFrg extends BaseAtMeFrg {

    /**
     * 切换社区后需要更新数据
     */
    public void update() {
        setCacheType();
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_AT_CONTENT)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_AT_CONTENT);
        }
        if (adapter == null) {
            adapter = new TrendAdapter(activity
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams()
                    , mSession
                    , this
                    , CacheType.CACHE_GET_AT_ME_CONTENT_INFO
                    , TrendAdapter.REQUEST_TYPE_GET_AT_ME_CONTENT
                    , false);
        }
        adapter.setListView(pullRefreshListView);
        adapter.setOnRefreshListener();
        pullRefreshListView.setAdapter(adapter);
        pullRefreshListView.setOnItemClickListener(((TrendAdapter) adapter).trendOnItemClickListener);
        //pullRefreshListView.setOnItemLongClickListener(((TrendAdapter) adapter).trendOnItemLongClickListener);
    }

    @Override
    protected HashMap<String, String> buildRequestParams() {

        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("ids", mSession.getIds());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_AT_ME_CONTENT_INFO;
    }

}
