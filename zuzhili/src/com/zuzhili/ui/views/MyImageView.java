package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	private OnMeasureListener onMeasureListener;
	
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		
		if(onMeasureListener != null){
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }

	public interface OnMeasureListener{
		public void onMeasureSize(int width, int height);
	}
	
}
