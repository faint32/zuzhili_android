package com.zuzhili.controller;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.framework.images.BitmapCache;
import com.zuzhili.framework.images.ImageCallback;
import com.zuzhili.model.multipart.ImageBucket;


public class ImageBucketAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private Activity act;
	private List<ImageBucket> dataList;
	private BitmapCache cache;
	private ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(act).inflate(R.layout.gridview_item_bucket, null);
		}
        ImageView coverImg = ViewHolder.get(convertView, R.id.bucket_list_item_thumb_iv);
        TextView descTxt = ViewHolder.get(convertView, R.id.bucket_desc_tv);
		ImageBucket item = dataList.get(position);
		descTxt.setText(item.getBucketName() + "(" + item.getCount() + ")");
		if (item.imageList != null && item.imageList.size() > 0) {
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			coverImg.setTag(sourcePath);
			cache.displayBmp(coverImg, thumbPath, sourcePath, callback);
		} else {
			coverImg.setImageBitmap(null);
			Log.e(TAG, "no images in bucket " + item.bucketName);
		}
		return convertView;
	}

}
