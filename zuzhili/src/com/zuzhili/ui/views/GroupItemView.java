package com.zuzhili.ui.views;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupItemView extends RelativeLayout {
	private Context context;

	private View view;

    @ViewInject(R.id.img_logo)
	private ImageView menuImg;


    @ViewInject(R.id.txt_menu_title)
	private TextView titleTxt;

    @ViewInject(R.id.img_arrow)
	private ImageView arrowImg;

	private OnUpdateHintListener onUpdateHintListener;
	
	public interface OnUpdateHintListener {
		public void updateHint(int unreadCount);
	}
	
	public GroupItemView(Context context) {
		super(context);
		this.context = context;
		init();
        ViewUtils.inject(view);
	}
	
	public GroupItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
        ViewUtils.inject(view);
	}
	
	public OnUpdateHintListener getOnUpdateHintListener() {
		return onUpdateHintListener;
	}

	public void setOnUpdateHintListener(OnUpdateHintListener onUpdateHintListener) {
		this.onUpdateHintListener = onUpdateHintListener;
	}

	private void init() {
		view = LayoutInflater.from(context).inflate(R.layout.view_menu_item, this, true);
	}

	public void setMenuImgResId(int resId) {
        menuImg.setBackgroundResource(resId);
	}
	
	public void setTitle(String title) {
		titleTxt.setText(title);
	}
	
	public ImageView getArrowImg() {
		return arrowImg;
	}
	
	public void setArrowImgVisibility(int visibility) {
		arrowImg.setVisibility(visibility);
	}
	
	public void updateHint(int unreadCount) {
		if(onUpdateHintListener != null) {
			onUpdateHintListener.updateHint(unreadCount);
		}
	}
}
