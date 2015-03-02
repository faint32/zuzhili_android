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
import com.zuzhili.model.folder.FileFolder;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by addison on 2/25/14.
 */
public class FileFolderAdapter extends ResultsAdapter<FileFolder> {
    public FileFolderAdapter(Context context, ListView listView, ImageLoader imageLoader, Class<FileFolder> clazz, HashMap<String, String> params) {
        super(context, listView, imageLoader, params);
        Task.getFileFolderList(params, this, this);
        ((BaseActivity)context).showLoading(null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_folder, null);
        TextView nameTxt = ViewHolder.get(convertView, R.id.txt_name);
        TextView descTxt = ViewHolder.get(convertView, R.id.txt_desc);
        ImageView lockImg = ViewHolder.get(convertView, R.id.img_lock);

        FileFolder folder = getItem(position);

        if(ValidationUtils.validationString(folder.getFoldername()))
            nameTxt.setText(folder.getFoldername());
        if(ValidationUtils.validationString(folder.getReservedchar2()))
            descTxt.setText(folder.getReservedchar2());
        if(ValidationUtils.validationString(folder.getAuthority()) && folder.getAuthority().equals("1")){
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
    public List<FileFolder> parseList(String response) {
        try {
            JSONObject rst = (JSONObject) JSON.parseObject(response);
            List<FileFolder> folderList = JSON.parseArray(rst.getString("list"), FileFolder.class);
            ((BaseActivity)mContext).removeLoading();
            return folderList;
        } catch (JSONException e) {
            ((BaseActivity)mContext).removeLoading();
            return new ArrayList<FileFolder>();
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
