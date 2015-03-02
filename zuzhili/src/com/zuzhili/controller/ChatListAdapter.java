package com.zuzhili.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.framework.Session;
import com.zuzhili.model.msg.MsgPairRec;
import android.content.Context;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zuzhili.ui.activity.BaseActivity;

public class ChatListAdapter extends ResultsAdapter<MsgPairRec> {
    protected LayoutInflater mInflater;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    public ChatListAdapter(Context context, ListView listView,
                           ImageLoader imageLoader, Class<MsgPairRec> clazz,
                           HashMap<String, String> params,
                           BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                           boolean isDataLoaded) {
        super(context, listView, imageLoader, params);
        this.mInflater = LayoutInflater.from(context);
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if(!isDataLoaded) Task.getMsgList(params, this, this);
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        Task.getMsgList(mParams, this, this);
    }

    class ViewHolder{
        TextView name;
        TextView content;
        TextView time;
        ImageView head;
        ImageView iconflag;
        Button toast;
        ImageContainer headImageImgRequest;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        MsgPairRec msgpairRec=super.getItem(position);
        View retV=null;
        try{
            retV = mInflater.inflate(R.layout.listview_talkpair_listitem,parent, false);
            ViewHolder temp = new ViewHolder();
            temp.name=(TextView)retV.findViewById(R.id.talkpairitem_name);
            temp.content = (TextView)retV.findViewById(R.id.talkpairitem_msg);
            temp.time = (TextView)retV.findViewById(R.id.talkpairitem_time);
            temp.head = (ImageView)retV.findViewById(R.id.talkpairitem_head);
            temp.iconflag = (ImageView)retV.findViewById(R.id.talkpairitem_iconflag);
            temp.head.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                }
            });
            temp.toast = (Button)retV.findViewById(R.id.talkpairitem_toast_num);
            temp.head.setTag(msgpairRec.getIdentity());
            temp.name.setText(msgpairRec.getIdentity().getName());
            temp.content.setText(msgpairRec.getLastmsg().getContent());

            if(temp.headImageImgRequest != null) {
                temp.headImageImgRequest.cancelRequest();
            }
            temp.headImageImgRequest = mImageLoader.get(msgpairRec.getIdentity().getUserhead()
                    , ImageLoader.getImageListener(temp.head, R.drawable.icon_head, R.drawable.icon_head));
            retV.setTag(temp);
        }catch(InflateException e){

        }
        return retV;
    }
    @Override
    public void updateRequestParams(Map<String, String> params) {

    }
    @Override
    public List<MsgPairRec> parseList(String response) {
        try {
            JSONObject parseObject = JSON.parseObject(response);
            List<MsgPairRec> trendList = JSON.parseArray(parseObject.getString("list"), MsgPairRec.class);
            return trendList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("exception", e.getMessage());
            return new ArrayList<MsgPairRec>();
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

    public void removeItem(int position) {
        super.removeItem(position);
    }
}
