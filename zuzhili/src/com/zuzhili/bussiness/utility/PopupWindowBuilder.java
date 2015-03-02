package com.zuzhili.bussiness.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
* Created by liutao on 14-3-24.
*/

public class PopupWindowBuilder {
	private PopupWindow popupWindow;
	private Context context;
	private LayoutInflater inflater;
	private View showView;
	private View root;

	public PopupWindowBuilder(Context context, View showView) {
		this.showView = showView;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setContentView(int layout) {
		View view = inflater.inflate(layout, null);
		setContentView(view);
	}

	public PopupWindowBuilder setStyle(int style) {
		popupWindow.setAnimationStyle(style);
		return this;

	}

	public PopupWindowBuilder setContentView(View view) {
           this.root = view;
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return this;
	}

	public PopupWindowBuilder update() {
		popupWindow.update();
		return this;

	}

	public PopupWindowBuilder popupSet() {
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		return this;

	}

    public void dissmiss() {
        popupWindow.dismiss();
    }

	public void showAtDown() {
		popupWindow.showAsDropDown(showView);
	}

	/**
	 * 
	 * @param xOffset
	 * @param yOffset
	 */
	public void showAtUp(int xOffset, int yOffset, 
			WindowManager windowManager) {
		int[] location = new int[2];
		showView.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ showView.getWidth(), location[1] + showView.getHeight());

		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = root.getMeasuredWidth();
		int rootHeight = root.getMeasuredHeight();

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		// int screenHeight = windowManager.getDefaultDisplay().getHeight();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - showView.getMeasuredHeight() + yOffset;
		popupWindow.showAtLocation(showView, Gravity.NO_GRAVITY, xPos, yPos);
	}

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
}
