package com.zuzhili.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.framework.images.BitmapCache;
import com.zuzhili.framework.images.ImageCallback;
import com.zuzhili.model.multipart.ImageItem;

import java.util.List;

/**
 * Created by addison on 2/18/14.
 */
public class ImageGalleryAdapter extends BaseAdapter{
    private Context context;
    private List<ImageItem> imageList;
    private BitmapCache cache;

    private ImageCallback callback = new ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals((String) imageView.getTag())) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                } else {
                    LogUtils.d("callback, bmp not match");
                }
            } else {
                LogUtils.d("callback, bmp null");
            }
        }
    };
    public ImageGalleryAdapter(Context context, List<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;
        cache = new BitmapCache();
    }

    @Override
    public int getCount() {
        int count = 0;
        if(imageList != null){
            count = imageList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_gallery,null);
        }
        ImageView img = ViewHolder.get(convertView, R.id.image);
        ImageItem item = imageList.get(position);
        img.setTag(item.getImagePath());
        cache.displayBmp(img, item.thumbnailPath, item.imagePath, callback);
        return convertView;
    }
}
