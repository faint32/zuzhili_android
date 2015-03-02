package com.zuzhili.framework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.zuzhili.ui.activity.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /**
     * 写图片文件到SD卡
     *
     * @throws IOException
     */
    public static void saveImage(Context ctx, String filePath,
                                     Bitmap bitmap) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        }
    }

}
