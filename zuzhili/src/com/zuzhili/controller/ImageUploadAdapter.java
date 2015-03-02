package com.zuzhili.controller;

import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.model.multipart.ImageUpload;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @Title: ImageUploadAdapter.java
 * @Package: com.zuzhili.controller
 * @Description: 待上传图片adapter
 * @author: gengxin
 * @date: 2014-2-14
 */
public class  ImageUploadAdapter extends BaseAdapter {

	private List<ImageUpload> imageList;
	private Context context;
    private BitmapUtils bitmapUtils;
	
	public ImageUploadAdapter(Context context, List<ImageUpload> imageList){
		this.context = context;
		this.imageList = imageList;
        this.bitmapUtils = initBitmapUtils();
	}

    private BitmapUtils initBitmapUtils(){
        BitmapUtils utils = new BitmapUtils(context);
        return utils;
    }

	@Override
	public int getCount() {
		return imageList.size() + 1;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_image_publish, null);
		}
		ImageView imageUploadImg = ViewHolder.get(convertView, R.id.image_publish_img);
		TextView descTxt = ViewHolder.get(convertView, R.id.desc_txt);
		if(position == imageList.size()){
			imageUploadImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plus));
            descTxt.setVisibility(View.GONE);
			if(imageList.size() >= 9){
				imageUploadImg.setVisibility(View.GONE);
                descTxt.setVisibility(View.GONE);
			}
		} else {
            bitmapUtils.display(imageUploadImg, imageList.get(position).getFilepath());
            if(ValidationUtils.validationString(imageList.get(position).getDesc())) {
                descTxt.setText(imageList.get(position).getDesc());
                descTxt.setVisibility(View.VISIBLE);
            }
		}
		return convertView;
	}

}
