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
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.framework.Session;
import com.zuzhili.model.folder.MutilFolder;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by addison on 2/21/14.
 * 音视频文件夹列表
 */
public class MutilFolderAdapter extends ResultsAdapter<MutilFolder>{

    public MutilFolderAdapter(Context context, ListView listView, ImageLoader imageLoader, Class<MutilFolder> clazz, HashMap<String, String> params, String folderType) {
        super(context, listView, imageLoader, params);
        if(ValidationUtils.validationString(folderType) && folderType.equals(Constants.MULTI_FOLDER_TYPE_MUSIC)){
            Task.getMusicFolderList(params, this, this);
        } else {
            Task.getVedioFolderList(params, this, this);
        }
        ((BaseActivity)context).showLoading(null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_folder, null);
        TextView nameTxt = ViewHolder.get(convertView, R.id.txt_name);
        TextView descTxt = ViewHolder.get(convertView, R.id.txt_desc);
        TextView countTxt = ViewHolder.get(convertView, R.id.txt_count);
        ImageView lockImg = ViewHolder.get(convertView, R.id.img_lock);

        MutilFolder folder = getItem(position);

        if(ValidationUtils.validationString(folder.getName()))
            nameTxt.setText(folder.getName());
        if(ValidationUtils.validationString(folder.getDescription()))
            descTxt.setText(folder.getDescription());
        if(ValidationUtils.validationString(folder.getAuthority()) && folder.getAuthority().equals("1")){
            lockImg.setVisibility(View.VISIBLE);
        } else {
            lockImg.setVisibility(View.INVISIBLE);
        }
        countTxt.setText(folder.getPhotonum() + "个文件");
        return convertView;
    }

    @Override
    public void updateRequestParams(Map<String, String> params) {

    }

    @Override
    public List<MutilFolder> parseList(String response) {
        try {
            JSONObject rst = (JSONObject) JSON.parseObject(response);
            List<MutilFolder> folderList = JSON.parseArray(rst.getString("list"), MutilFolder.class);
            ((BaseActivity)mContext).removeLoading();
            return folderList;
        } catch (JSONException e) {
            ((BaseActivity)mContext).removeLoading();
            return new ArrayList<MutilFolder>();
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

    }
}
