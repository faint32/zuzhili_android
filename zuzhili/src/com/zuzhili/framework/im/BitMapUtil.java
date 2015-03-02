package com.zuzhili.framework.im;

import android.graphics.Bitmap;

import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.ui.activity.BaseActivity;

/**
 * Created by zuosl on 14-2-25.
 */
public class BitMapUtil {

    //获取位图
    public static Bitmap getBitmap(BaseActivity activity,Bitmap resource ){
        int height = 0;
        int width = 0;
        if(resource.getHeight() >= resource.getWidth()) {
            if(resource.getWidth() > 200) {
                height = DensityUtil.dip2px(activity, 100);
                width = DensityUtil.dip2px(activity, 100);
            } else {
                width = resource.getWidth();
                height = width;
            }
        } else if(resource.getWidth() >= resource.getHeight()) {
            if(resource.getHeight() > 200) {
                height = DensityUtil.dip2px(activity, 100);
                width = DensityUtil.dip2px(activity, 100);
            } else {
                height = resource.getHeight();
                width = height;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, width, height);
        return  bitmap;
    }



}
