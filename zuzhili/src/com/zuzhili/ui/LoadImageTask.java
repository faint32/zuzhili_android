package com.zuzhili.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.zuzhili.R;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.VolleyImageUtils;

import java.io.File;

/**
 * Created by liutao on 14-6-23.
 */
public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {


    private ImageView imv;
    private String path;

    public LoadImageTask(ImageView imv) {
        this.imv = imv;
        this.path = imv.getTag().toString();
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        int[] desiredImageDimension = VolleyImageUtils.getDesiredImageDimension(path, DensityUtil.dip2px((Context) params[0], 100), DensityUtil.dip2px((Context) params[0], 100));
        return VolleyImageUtils.getScaledBitmap(new File(path), desiredImageDimension[0], desiredImageDimension[1]);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (!imv.getTag().toString().equals(path)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
            imv.setImageResource(R.drawable.default_social_logo);
        }

        if (result != null && imv != null) {
            imv.setVisibility(View.VISIBLE);
            imv.setImageBitmap(result);
        } else {
            imv.setVisibility(View.GONE);
        }
    }

}