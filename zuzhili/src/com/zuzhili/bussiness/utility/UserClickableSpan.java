package com.zuzhili.bussiness.utility;

import com.zuzhili.R;
import com.zuzhili.ui.activity.loginreg.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * 用户名被点击
 * @Title: UserClickableSpan.java
 * @author taoliuh@gmail.com 
 * @date 2014-1-20 下午2:45:24
 * @version 0.1
 */
public class UserClickableSpan extends ClickableSpan {

	private String mIds;
	private String mUsername;
	private Context mContext;
	private OnSpanClickListener mOnSpanClickListener;
	public UserClickableSpan(Context context, OnSpanClickListener onSpanClickListener){
		this.mContext = context;
		this.mOnSpanClickListener = onSpanClickListener;
	}
	public void setIds(String ids) {
		this.mIds = ids;
	}

	public UserClickableSpan getSelf() {
		return new UserClickableSpan(mContext, mOnSpanClickListener);
	}

	public void setUsername(String username) {
		this.mUsername = username;
	}
	
	@Override
	public void updateDrawState(TextPaint paint) {
		super.updateDrawState(paint);
		paint.setUnderlineText(false);
		paint.setColor(mContext.getResources().getColor(R.color.weibo_title));
	}

	@Override
	public void onClick(View view) {
		Intent it = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(it);
		if(mOnSpanClickListener != null){
			mOnSpanClickListener.onSpanClick();
		}
	}
		
	public interface OnSpanClickListener {
		public void onSpanClick();
	}
}
