package com.zuzhili.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.TimeUtils;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.comment.Comment;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.comment.CommentEditActivity;
import com.zuzhili.ui.activity.trendrelated.TrendDetailActivity;
import com.zuzhili.ui.views.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-3-13.
 */
public class AtMeCommentAdapter extends ResultsAdapter<Comment> implements Response.Listener<String>, Response.ErrorListener {

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    public AtMeCommentOnClickListener atMeCommentOnClickListener = new AtMeCommentOnClickListener();

    public static final String REQUEST_GET_AT_ME_COMMENT_INFO = "request.get.at.me.comment.info";
    public static final String REQUEST_GET_RECEIVED_COMMETNS = "request.get.received.comments";
    public static final String REQUEST_GET_SENDED_COMMETNS = "request.get.sended.comments";
    public static final String REQUEST_DELETE_COMMENT = "request.delete.comment";

    private Session mSession;

    private String requestType;

    private String lastRequestType;     // 上次请求的类型

    private int deleteItemPosition;     // 删除的评论的在列表中的位置

    public AtMeCommentAdapter(Context context,
                              ListView listView,
                              ImageLoader imageLoader,
                              HashMap<String, String> params,
                              Session session,
                              String cacheType,
                              String requestType,
                              BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                              boolean isDataLoaded) {
        super(context, listView, imageLoader, params, cacheType);
        this.mSession = session;
        this.requestType = requestType;
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if (!isDataLoaded)
            Task.getCache(context, params, this, this, cacheType, getIdentity(session));
        loadNextPage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_at_me_comment, parent, false);
            holder = new ViewHolder();

            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (shouldLoadNextPage(mDataList, position)) {

            loadedPage++;
            updateRequestParams(mParams);
            loadNextPage();
            mListView.onFooterRefreshBegin();
            LogUtils.e("load page: " + loadedPage);
        }

        if (holder.userHeadRequest != null) {
            holder.userHeadRequest.cancelRequest();
            LogUtils.d("cancel request head image");
        }
        LogUtils.e("position: " + position);
        if (requestType.equals(REQUEST_GET_SENDED_COMMETNS)) {
            holder.userHeadRequest = mImageLoader.get(mSession.getUserhead()
                    , ImageLoader.getImageListener(holder.userHead, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } else {
            holder.userHeadRequest = mImageLoader.get(TextUtil.processNullString(item.getHeadimage())
                    , ImageLoader.getImageListener(holder.userHead, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        }

        holder.time.setText(TimeUtils.getTimeMinute(item.getCreateTime()));

        if (requestType.equals(REQUEST_GET_SENDED_COMMETNS)) {
            holder.name.setText("我");
        } else {
            holder.name.setText(item.getName());
        }


        showComment(holder, item.getContent());

        if (requestType.equals(REQUEST_GET_RECEIVED_COMMETNS)) {
            holder.source.setText(mContext.getString(R.string.reply));
            holder.msg_who.setText("我的");
            holder.msg_type.setText(getCommentType(item.getType())+" ");
            holder.msg_info.setText(TextUtil.contentFilter2(item.getAbsmini(),mContext));
//            showSource(holder, mContext.getString(R.string.reply) + "我的" + getCommentType(item.getType()) + " ~" + item.getAbsmini() + "~");
        } else {
            //评论
            if (item.getTocommentid() == 0) {
                holder.source.setText(mContext.getString(R.string.reply));
                holder.msg_who.setText(TextUtil.replaceName(item.getName()));
                holder.msg_type.setText("的"+getCommentType(item.getType())+" ");
                holder.msg_info.setText(TextUtil.contentFilter2(item.getAbsmini(),mContext));
//                showSource(holder, mContext.getString(R.string.reply) + "~" + item.getName() + "~的" + getCommentType(item.getType()) + " ~" + item.getAbsmini() + "~");
            } else {
                holder.source.setText(mContext.getString(R.string.reply_to));
                holder.msg_who.setText(TextUtil.replaceName(item.getName()));
                holder.msg_type.setText("的评论 ");
                holder.msg_info.setText(TextUtil.contentFilter2(item.getAbsmini(),mContext));
//                showSource(holder, mContext.getString(R.string.reply_to) + "~" + item.getName() + "~的评论 ~" + item.getAbsmini() + "~");
            }
        }


        return convertView;
    }

    private void showComment(ViewHolder holder, String comment) {
        if (comment != null && !TextUtils.isEmpty(comment)) {
            comment = TextUtil.Html2Text(comment.replace("null", "")).trim().toString();
            if (comment.length() > 140) {
                comment = comment.substring(0, 140) + "...";
            }
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(TextUtil.contentFilter(comment, mContext));
        } else {
            holder.comment.setVisibility(View.GONE);
        }
    }

    private String getCommentType(int type) {
        String commentType;
        switch (type) {
            case 32:
                commentType = "任务";//32
                break;
            case 35:
                commentType = "联系人";//35
                break;
            case 37:
                commentType = "机构";//37
                break;
            case 3:
                commentType = "文字";//3
                break;
            case 6:
                commentType = "图片";//6
                break;
            case 5:
                commentType = "图片册";//5
                break;
            case 16:
                commentType = "音频";//16
                break;
            case 17:
                commentType = "音频册";//17
                break;
            case 18:
                commentType = "视频";//18
                break;
            case 19:
                commentType = "视频册";//19
                break;
            case 33:
                commentType = "通知";//33
                break;
            case 34:
                commentType = "行业资讯";//34
                break;
            case 38:
                commentType = "投票";//38
                break;

            default:
                commentType = "内容";
                break;
        }
        return commentType;
    }

    private void showSource(ViewHolder holder, String source) {
        if (source != null && !TextUtils.isEmpty(source)) {
            source = TextUtil.Html2Text(source.replace("null", "")).trim().toString();
            if (source.length() > 140) {
                source = source.substring(0, 140) + "...";
            }
            holder.source.setVisibility(View.VISIBLE);
            holder.source.setText(TextUtil.contentFilter(source, mContext));
        } else {
            holder.source.setVisibility(View.GONE);
        }
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
    public void loadNextPage() {
        isLoading = true;
        sendRequest();
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        sendRequest();
    }

    private void sendRequest() {
        if (requestType.equals(REQUEST_GET_AT_ME_COMMENT_INFO)) {
            Task.getAtMeCommentInfo(mParams, this, this);
        } else if (requestType.equals(REQUEST_GET_RECEIVED_COMMETNS)) {
            Task.getReceivedComments(mParams, this, this);
        } else if (requestType.equals(REQUEST_GET_SENDED_COMMETNS)) {
            Task.getSendedComments(mParams, this, this);
        }
    }

    private HashMap<String, String> buildRequestDeleteCommentParams(Comment item) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("ids", mSession.getIds());
            params.put("commentid", String.valueOf(item.getId()));
        }
        return params;
    }

    private class AtMeCommentOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            CustomDialog dialog = new CustomDialog(mContext, R.style.popDialog);
            if (requestType.equals(REQUEST_GET_AT_ME_COMMENT_INFO) || requestType.equals(REQUEST_GET_RECEIVED_COMMETNS)) {
                dialog.setList(new String[]{"回复", "查看原文"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent();
                        if (which == 0) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("回复 ")
                                    .append("@")
                                    .append(((Comment) parent.getAdapter().getItem(position)).getIdentity().getName())
                                    .append("(")
                                    .append(((Comment) parent.getAdapter().getItem(position)).getIdentity().getId())
                                    .append("):");
                            it.setClass(mContext, CommentEditActivity.class);
                            it.putExtra(Constants.ACTION, Constants.ACTION_COMMENT);
                            it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(((Comment) parent.getAdapter().getItem(position)).getAbsid()));
                            it.putExtra(Constants.EXTRA_TREND_TOCOMMENTID, String.valueOf(((Comment) parent.getAdapter().getItem(position)).getTocommentid()));
                            it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_AT_ME_COMMENT);
                            it.putExtra(Constants.EXTRA_AT_INFO, builder.toString());
                            mContext.startActivity(it);
                        } else if (which == 1) {
                            it.setClass(mContext, TrendDetailActivity.class);
                            it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(((Comment) parent.getAdapter().getItem(position)).getAbsid()));
                            it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_AT_ME_COMMENT);
                            mContext.startActivity(it);
                        }
                        dialog.cancel();
                    }
                });
            } else if (requestType.equals(REQUEST_GET_SENDED_COMMETNS)) {
                dialog.setList(new String[]{"删除", "查看原文"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent();
                        if (which == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("删除该条评论？");
                            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    lastRequestType = requestType;
                                    requestType = REQUEST_DELETE_COMMENT;
                                    deleteItemPosition = position - mListView.getHeaderViewsCount();
                                    Task.deleteComment(buildRequestDeleteCommentParams((Comment) parent.getAdapter().getItem(position)), AtMeCommentAdapter.this, AtMeCommentAdapter.this);
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                            AlertDialog dl = builder.create();
                            dl.show();

                        } else if (which == 1) {
                            it.setClass(mContext, TrendDetailActivity.class);
                            it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(((Comment) parent.getAdapter().getItem(position)).getAbsid()));
                            it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_AT_ME_COMMENT);
                            mContext.startActivity(it);
                        }
                        dialog.cancel();
                    }
                });
            }
            dialog.show();
        }
    }

    @Override
    public void onResponse(String response) {
        if (requestType.equals(REQUEST_GET_AT_ME_COMMENT_INFO)
                || requestType.equals(REQUEST_GET_RECEIVED_COMMETNS)
                || requestType.equals(REQUEST_GET_SENDED_COMMETNS)) {
            super.onResponse(response);
            handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
            SyncUtils.TriggerRefresh();
        } else if (requestType.equals(REQUEST_DELETE_COMMENT)) {
            Utils.makeEventToast(mContext, mContext.getString(R.string.delete_comment_success), false);
            requestType = lastRequestType;
            mDataList.remove(deleteItemPosition);
            List<Comment> newList = new ArrayList<Comment>();
            newList.addAll(mDataList);
            setList(newList);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
    }

    class ViewHolder {

        @ViewInject(R.id.txt_user_name)
        public TextView name;

        @ViewInject(R.id.txt_comment)
        public TextView comment;

        @ViewInject(R.id.txt_source)
        public TextView source;

        @ViewInject(R.id.txt_name)
        public TextView msg_who;

        @ViewInject(R.id.txt_type)
        public TextView msg_type;

        @ViewInject(R.id.txt_info)
        public TextView msg_info;

        @ViewInject(R.id.txt_time)
        public TextView time;

        @ViewInject(R.id.img_user_head)
        public ImageView userHead;

        public ImageLoader.ImageContainer userHeadRequest;
    }
}
