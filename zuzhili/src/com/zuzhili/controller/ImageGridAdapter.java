package com.zuzhili.controller;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.framework.images.BitmapCache;
import com.zuzhili.framework.images.ImageCallback;
import com.zuzhili.model.ImageBean;
import com.zuzhili.model.folder.Photo;
import com.zuzhili.model.multipart.ImageItem;

/**
 * @Title: ImageGridAdapter.java
 * @Package: com.zuzhili.multiselector.adapter
 * @Description: 图片列表
 * @author: gengxin
 * @date: 2014-2-14
 */
public class ImageGridAdapter extends BaseAdapter {

	private final String TAG = getClass().getSimpleName();
	private Activity act;
	//private List<ImageItem> dataList;
	private List<Photo> dataList;
	private BitmapCache cache;
    private ImageLoader mImageLoader;
	private ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
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


//	public ImageGridAdapter(Activity act, List<ImageItem> list) {
//		this.act = act;
//		dataList = list;
//		cache = new BitmapCache();
//	}

	public ImageGridAdapter(Activity act, List<Photo> list,ImageLoader imageLoader) {
		this.act = act;
		dataList = list;
        mImageLoader = imageLoader;
		cache = new BitmapCache();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		private ImageView iv;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.gridview_item_image, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		//final ImageItem item = dataList.get(position);
		final Photo item = dataList.get(position);

//		holder.iv.setTag(item.imagePath);
//		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath, callback);
        mImageLoader.get(item.getUrl_small(), ImageLoader.getImageListener(holder.iv, R.drawable.photo_default, R.drawable.photo_default));
		return convertView;
	}
}
