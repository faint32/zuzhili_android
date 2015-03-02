package com.zuzhili.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.framework.Session;
import com.zuzhili.model.folder.Album;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by addison on 2/20/14.
 */
public class AlbumAdapter extends ResultsAdapter<Album>{

    public AlbumAdapter(Context context, ListView listView, ImageLoader imageLoader, HashMap<String, String> params) {
        super(context, listView, imageLoader, params);
        Task.getAlbumList(params, this, this);
        ((BaseActivity)context).showLoading(null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_album, null);
        }
        ImageView coverImg = ViewHolder.get(convertView, R.id.img_cover);
        ImageView lockImg = ViewHolder.get(convertView, R.id.img_lock);
        TextView nameTxt = ViewHolder.get(convertView, R.id.txt_name);
        TextView descTxt = ViewHolder.get(convertView, R.id.txt_desc);
        TextView countTxt = ViewHolder.get(convertView, R.id.txt_count);
        mImageLoader.get(item.getCoverphotopath(), ImageLoader.getImageListener(coverImg, R.drawable.photo_default, R.drawable.photo_default));
        if(ValidationUtils.validationString(item.getName()))
            nameTxt.setText(item.getName());
        if(ValidationUtils.validationString(item.getDescription()))
            descTxt.setText(item.getDescription());
        countTxt.setText(String.valueOf(item.getPhotonum())  + "张图片");
        if(ValidationUtils.validationString(item.getAuthority()) && item.getAuthority().equals("1")){
            lockImg.setVisibility(View.VISIBLE);
        } else {
            lockImg.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public void updateRequestParams(Map<String, String> params) {

    }

    @Override
    public List<Album> parseList(String response) {
        try {
            JSONObject rst = (JSONObject) JSON.parseObject(response);
            List<Album> albumList = JSON.parseArray(rst.getString("list"), Album.class);
            ((BaseActivity)mContext).removeLoading();
            return albumList;
        } catch (JSONException e) {
            ((BaseActivity)mContext).removeLoading();
            return new ArrayList<Album>();
        }
    }

    @Override
    public String getIdentity(Session session) {
        return null;
    }

    @Override
    public void loadNextPage() {
        return;
    }

    @Override
    public void onRefresh() {
        this.clearList();
        Task.getAlbumList(mParams, this, this);
    }
}
