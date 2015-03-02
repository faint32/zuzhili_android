package com.zuzhili.ui.activity.approval;

import android.os.Bundle;

import com.lidroid.xutils.ViewUtils;
import com.zuzhili.R;
import com.zuzhili.ui.activity.BaseActivity;
//审批详情页面
public class ApprovalDetailActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback{
   
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.activity_approval_detail);
		ViewUtils.inject(this);
		setCustomActionBarCallback(this);
	}

	@Override
	public boolean showCustomActionBar() {
		initActionBar(R.drawable.icon_back,  R.drawable.icon_home, getString(R.string.approval_detail), false);
		return true;
	}
	
}
