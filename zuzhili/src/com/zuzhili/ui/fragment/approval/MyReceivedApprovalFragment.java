package com.zuzhili.ui.fragment.approval;

import com.zuzhili.bussiness.utility.Constants;

public class MyReceivedApprovalFragment extends BaseApprovalFrg{

	@Override
	protected void setRequestType() {
		requestType="0";
	}

    @Override
    protected void reset() {
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_APPROVAL_RECEIVE)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_APPROVAL_RECEIVE);
        }
    }
}
