package com.zuzhili.ui.fragment.comment;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.AtMeCommentAdapter;
import com.zuzhili.db.CacheType;

/**
 * Created by liutao on 14-3-15.
 */
public class SendedCommentFrg extends BaseCommentFrg {

    @Override
    protected void setCacheType() {
        cacheType = CacheType.CACHE_GET_SENDED_COMMENTS;
    }

    @Override
    protected void setRequestType() {
        requestType = AtMeCommentAdapter.REQUEST_GET_SENDED_COMMETNS;
    }

    @Override
    protected void reset() {
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_COMMENT_SEND)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_COMMENT_SEND);
        }
    }
}
