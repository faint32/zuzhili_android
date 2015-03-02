package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.TimeUtils;
import com.zuzhili.bussiness.utility.UserClickableSpan;
import com.zuzhili.framework.Session;
import com.zuzhili.model.Member;
import com.zuzhili.model.comment.Comment;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liutao on 14-2-26.
 */
public class CommentAdapter extends ResultsAdapter<Comment> {

    private LayoutInflater mInflater;

    private Session mSession;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    protected HashMap<String, String> nParams;

    public CommentAdapter(Context context,
                          ListView listView,
                          ImageLoader imageLoader,
                          Session session,
                          HashMap<String, String> params,
                          BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                          boolean isDataLoaded) {
        super(context, listView, imageLoader, params);
        this.nParams = params;
        mInflater = LayoutInflater.from(context);
        this.mSession = session;
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if(!isDataLoaded) Task.getFeedComment(params, this, this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        final Comment item = getItem(position);
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_comment, parent, false);
            holder = new ViewHolder();

            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(holder.userHeadRequest != null) {
            holder.userHeadRequest.cancelRequest();
            LogUtils.d("cancel request head image");
        }
        holder.userHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, CommonSpaceActivity.class);
                Member member = item.getIdentity();
                UserInfo user=((BaseActivity)mContext).getDbHelper().getUserInfoTable().getUserByUid(String.valueOf(member.getUserid()),mSession.getListid());
                it.putExtra(Constants.EXTRA_MEMBER, (java.io.Serializable) member);
                it.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                it.putExtra(Constants.EXTRA_VOIP_ID, user.getY_voip());

                if (mSession.isSpaceActivityInstantiated()) {
                    it.putExtra(Constants.EXTRA_ANIM_REVERSE, true);
                }
                mContext.startActivity(it);
            }
        });
        holder.userHeadRequest = mImageLoader.get(item.getHeadimage()
                , ImageLoader.getImageListener(holder.userHeadImg, R.drawable.icon_head, R.drawable.icon_head));

        holder.contentTxt.setText(TextUtil.contentFilterSpan(item.getContent(), mContext, new UserClickableSpan(mContext, null), null, null));

        holder.userNameTxt.setText(item.getName());

        holder.timeTxt.setText(TimeUtils.getTimeMinute(item.getCreateTime()));

        return convertView;
    }

    @Override
    public List<Comment> parseList(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            List<Comment> commentList = JSON.parseArray(jsonObject.getString("commentlist"), Comment.class);
            return commentList;
        } catch (JSONException e) {
            return new ArrayList<Comment>();
        }
    }

    @Override
    public String getIdentity(Session session) {
        return null;
    }

    /**
     * 更新页面位置
     * @param params
     */
    public void updateRequestParams(Map<String, String> params) {
        params.put("start", String.valueOf(loadedPage * Constants.PAGE_SIZE));
        return;
    }

    @Override
    public void loadNextPage() {
        isLoading = true;
        Task.getFeedComment(mParams, this, this);
    }

    @Override
    public void onRefresh() {
        Task.getFeedComment(nParams, this, this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        mListView.onFooterRefreshEnd();
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
    }

    protected class ViewHolder {
        @ViewInject(R.id.img_user_head)
        ImageView userHeadImg;

        public ImageContainer userHeadRequest;

        @ViewInject(R.id.rla_content)
        TextView contentTxt;

        @ViewInject(R.id.txt_user_name)
        TextView userNameTxt;

        @ViewInject(R.id.txt_time)
        TextView timeTxt;

    }
}
