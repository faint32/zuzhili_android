package com.zuzhili.bussiness;

import java.util.List;

public interface OnResponseListener<T> {
	
	public void onResponse(List<T> response);
	public void onResponseError(Exception exption);
	
}	
