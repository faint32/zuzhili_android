package com.zuzhili.exception;

import com.android.volley.VolleyError;

@SuppressWarnings("serial")
public class BusinessError extends VolleyError {
	private String error;
	
	public BusinessError() {
		super();
	}
	
	public BusinessError(String detailMessage) {
		this.error = detailMessage;
	}
	
	@Override
	public String getMessage() {
		return error;
	}
}
