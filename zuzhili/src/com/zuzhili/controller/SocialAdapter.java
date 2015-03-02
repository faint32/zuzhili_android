package com.zuzhili.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.Session;
import com.zuzhili.model.social.Social;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-3-11.
 */
public class SocialAdapter extends ResultsAdapter<Social> {

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    public SocialAdapter(Context context,
                         ListView listView,
                         ImageLoader imageLoader,
                         HashMap<String, String> params,
                         Session session,
                         String cacheType,
                         BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                         boolean isDataLoaded) {
        super(context, listView, imageLoader, params, cacheType);
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        super.mListView.setOnRefreshListener(this);
        if(!isDataLoaded) {
            Task.getCache(context, params, this, this, cacheType, getIdentity(session));
        } else {
            loadNextPage();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Social item = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_social_search, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        if(shouldLoadNextPage(mDataList, position)) {
            loadedPage++;
            updateRequestParams(mParams);
            loadNextPage();
            mListView.onFooterRefreshBegin();
            LogUtils.e("load page: " + loadedPage);
        }

        if(holder.logoRequest != null) {
            holder.logoRequest.cancelRequest();
        }

        holder.logoRequest = mImageLoader.get(TextUtil.processNullString(item.getLogo())
                , ImageLoader.getImageListener(holder.logoImg, R.drawable.default_social_logo, R.drawable.default_social_logo));

        holder.nameTxt.setText(item.getListname());
        holder.descTxt.setText(TextUtil.processNullString(item.getListdesc()));
        return convertView;
    }

    @Override
    public List<Social> parseList(String response) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        JSONObject jsonObject = JSON.parseObject(response);
        List<Social> socialList = JSON.parseArray(jsonObject.getString("list"), Social.class);
        return socialList;
    }

    @Override
    public void loadNextPage() {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        isLoading = true;
        Task.getSocial(mParams, this, this);
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        loadNextPage();
    }

    class ViewHolder {
        @ViewInject(R.id.txt_social_name)
        TextView nameTxt;

        @ViewInject(R.id.txt_social_desc)
        TextView descTxt;

        @ViewInject(R.id.img_social_logo)
        ImageView logoImg;

        ImageLoader.ImageContainer logoRequest;
    }
}
