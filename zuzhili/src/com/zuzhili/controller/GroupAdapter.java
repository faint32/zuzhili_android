package com.zuzhili.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.framework.images.NativeImageLoader;
import com.zuzhili.model.ImageBean;
import com.zuzhili.ui.views.MyImageView;

import java.util.List;

public class GroupAdapter extends BaseAdapter{
	private List<ImageBean> list;
	private Point mPoint = new Point(0, 0);
	private GridView mGridView;
	protected LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public GroupAdapter(Context context, List<ImageBean> list, GridView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		String path = mImageBean.getTopImagePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_grid_group_item, null);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);

			viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));
		viewHolder.mImageView.setTag(path);
		
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		
		return convertView;
	}
	
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}
}
