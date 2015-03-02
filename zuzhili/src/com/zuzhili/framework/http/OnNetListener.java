package com.zuzhili.framework.http;

import com.lidroid.xutils.http.RequestParams;
/**
 * @Title: OnNetListener.java
 * @Package: com.zuzhili.framework.http
 * @Description: 网络回调监听
 * @author: gengxin
 * @date: 2014-1-16
 */
public interface OnNetListener {
	public abstract void OnNetSuccess(RequestParams params);
	public abstract void OnNetFailure(RequestParams params);
}