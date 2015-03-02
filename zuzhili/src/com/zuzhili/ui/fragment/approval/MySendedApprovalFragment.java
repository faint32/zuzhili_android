package com.zuzhili.ui.fragment.approval;

import com.zuzhili.bussiness.utility.Constants;

public class MySendedApprovalFragment extends BaseApprovalFrg{

	@Override
	protected void setRequestType() {
		requestType="1";
	}

    @Override
    protected void reset() {
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_APPROVAL_SEND)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_APPROVAL_SEND);
        }
    }

}
