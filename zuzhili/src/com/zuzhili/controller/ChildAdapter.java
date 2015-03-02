package com.zuzhili.controller;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.images.NativeImageLoader;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.ui.activity.im.ShowImageActivity;
import com.zuzhili.ui.activity.publish.BigImageActivity;
import com.zuzhili.ui.views.MyImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildAdapter extends BaseAdapter{
	private Point mPoint = new Point(0, 0);
	private GridView mGridView;
	private List<String> list =new ArrayList<String>();
	private List<ImageItem> addedPath=new ArrayList<ImageItem>();
	private Context context;
	protected LayoutInflater mInflater;
    private boolean isChat;
    private static final int FLAG_ACTIVITY_CHOOSE_IMAGE = 1;
    private OnContactSelectedListener onContactSelectedListener;
    private boolean isCheck;
    public interface OnContactSelectedListener {
        public void onContactSelected(String path);
    }

    public void setOnContactSelectedListener(OnContactSelectedListener onContactSelectedListener) {
        this.onContactSelectedListener = onContactSelectedListener;
    }

	public ChildAdapter(Context context,GridView mGridView,boolean isChat) {
		this.mGridView = mGridView;
        this.context=context;
        this.isChat=isChat;
		mInflater = LayoutInflater.from(context);
	}
	
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
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		final String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.view_grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			
			viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, width);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);

        isCheck=false;
        for (ImageItem item :addedPath){
            if(item.getImagePath().equals(path)){
                isCheck=true;
            }
        }
        if(isCheck){
            viewHolder.mCheckBox.setChecked(true);
        }else {
            viewHolder.mCheckBox.setChecked(false);
        }
        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.mCheckBox.isChecked()){
                    //判断大小
                    if(addedPath.size() < 9) {
                        ImageItem imageItem=new ImageItem();
                        imageItem.setImagePath(path);
                        addedPath.add(imageItem);
                        addAnimation(viewHolder.mCheckBox);
                    }else {
                        viewHolder.mCheckBox.setChecked(false);
                    }
                }else {
                    for(int i=0;i<addedPath.size();i++){
                        if(addedPath.get(i).getImagePath().equals(path)){
                            //已经包含这个path了，则干掉
                            addedPath.remove(i);
                        }
                    }
                }
                if (onContactSelectedListener != null) {
                    onContactSelectedListener.onContactSelected(path);
                }
            }
        });

		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mPoint.x, mPoint.y);
                    mImageView.setLayoutParams(params);
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra(BigImageActivity.IMAGE_TYPE,BigImageActivity.TYPE_ALBUM);
                intent.putExtra(Constants.IMAGE_POSITION, position);
                List<ImageItem> photos = new ArrayList<ImageItem>();
                ImageItem imageItem;
                for(String item:list){
                    imageItem=new ImageItem();
                    imageItem.setImagePath(item);
                    photos.add(imageItem);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) addedPath);
                bundle.putSerializable(Constants.BIGIMAGE_PHOTOS, (Serializable) photos);
                intent.putExtras(bundle);
                if(isChat){
                    intent.putExtra(Constants.TAG_CHAT_CONTACTS,true);
                }
                ((ShowImageActivity)context).startActivityForResult(intent,FLAG_ACTIVITY_CHOOSE_IMAGE);
                ((ShowImageActivity)context).overridePendingTransition(R.anim.zoom_enter, 0);
            }
        });
		return convertView;
	}

    public void setList(List<String> mlist){
        if(mlist!=null && mlist.size()>0) {
            list.clear();
            list.addAll(mlist);
            notifyDataSetChanged();
        }
    }

	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	public void setSelectItems(List<ImageItem> list){
        addedPath.clear();
        addedPath.addAll(list);
	}

	public int getSelectItemPosition(){
        int index=0;
        String path;
        for(int i=0; i<list.size();i++){
            path=list.get(i);
            if(addedPath.contains(path)){
                index=i;
            }
        }
		return index;
	}


    public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

}
