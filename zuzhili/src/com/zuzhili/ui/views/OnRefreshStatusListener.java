package com.zuzhili.ui.views;

public interface OnRefreshStatusListener {
	public void onPullRefreshEnd();
	public boolean onFooterRefreshBegin();
	public void onFooterReset();
}
