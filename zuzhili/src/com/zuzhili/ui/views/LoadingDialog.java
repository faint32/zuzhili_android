package com.zuzhili.ui.views;


import com.zuzhili.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class LoadingDialog extends Dialog {
	private static LoadingDialog loadingDialog = null;
	private static Animation animation;

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LoadingDialog getLoadingDialog(Context context) {
		loadingDialog = new LoadingDialog(context, R.style.LoadingDialog);
		loadingDialog.setContentView(R.layout.view_loading);
		loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		// 加载动画
		animation = AnimationUtils.loadAnimation(context, R.anim.anim_load);
		loadingDialog.setCancelable(true);
		return loadingDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (loadingDialog == null) {
			return;
		}
		ImageView loadImage = (ImageView) loadingDialog.findViewById(R.id.loading);
		// 使用ImageView显示动画
		loadImage.startAnimation(animation);
	}

	/**
	 * 
	 * [Summary] setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public LoadingDialog setTitile(String strTitle) {
		return loadingDialog;
	}

	/**
	 * 
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) loadingDialog.findViewById(R.id.dialog_loadingcontent);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return loadingDialog;
	}
}
