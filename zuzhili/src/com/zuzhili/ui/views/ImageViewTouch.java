package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class ImageViewTouch extends ImageViewTouchBase {
    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnableTrackballScroll(boolean enable) {
    }

    public void postTranslateCenter(float dx, float dy) {
        super.postTranslate(dx, dy);
        center(true, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}