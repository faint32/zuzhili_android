package com.zuzhili.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.model.MiniBlog;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.comment.CommentEditActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;
import com.zuzhili.ui.activity.trendrelated.TrendDetailActivity;
import com.zuzhili.ui.views.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态adapter
 *
 * @author taoliuh@gmail.com
 * @version 0.1
 * @Title: TrendAdapter.java
 * @date 2014-1-21 下午2:51:56
 */
public class TrendAdapter extends ResultsAdapter<MiniBlog> implements Response.Listener<String>, Response.ErrorListener {

    public static final String TYPE_PIC = "type.pic";
    public static final String TYPE_FILE = "type.file";
    public static final String TYPE_MEDIA = "type.media";
    public static final String TYPE_NULL = "type.null";

    public static final String REQUEST_TYPE_GET_FEED = "request.type.get.feed";
    public static final String REQUEST_TYPE_GET_COLLECTION = "request.type.get.collection";
    public static final String REQUEST_TYPE_GET_AT_ME_CONTENT = "request.type.get.at.me.content";
    public static final String REQUEST_TYPE_ADD_OR_CANCEL_COLLECTION = "request.type.add.or.cancel.collection";
    public static final String REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS = "request.type.get.specific.user.trends";
    public static final String REQUEST_TYPE_GET_SPECIFIC_GROUP_TERNDS = "request.type.get.specific.group.trends";

    public static final int ITEM_VIEW_TYPE_COUNT = 4;        // listView 中item view的类型数目
    public static final int VIEW_TYPE_TREND_TEXT_ONLY = 0;
    public static final int VIEW_TYPE_TREND_WITH_MULTI_MEDIA = 1;
    public static final int VIEW_TYPE_QUOTED_TREND_WITH_TEXT_ONLY = 2;
    public static final int VIEW_TYPE_QUOTED_TREND_WITH_MUTIL_MEDIA = 3;
    public static final int VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY = 4;
    public static final int VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA = 5;
    public static final int VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY = 6;
    public static final int VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA = 7;

    public TrendOnItemClickListener trendOnItemClickListener = new TrendOnItemClickListener();
    public TrendOnItemLongClickListener trendOnItemLongClickListener = new TrendOnItemLongClickListener();

    private TrendViewHelper trendViewHelper;


    private Session mSession;

    private Member member;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    /**
     * 请求类型 getFeed or getAtMeContentInfo
     */
    private String requestType;

    public TrendAdapter(Context context, ListView listView,
                        ImageLoader imageLoader,
                        HashMap<String, String> params,
                        Session session,
                        BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                        String cacheType,
                        String requestType,
                        boolean isDataLoaded) {
        super(context, listView, imageLoader, params, cacheType);
        this.mSession = session;
        this.requestType = requestType;
        trendViewHelper = new TrendViewHelper(context, imageLoader);
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if (!isDataLoaded)
            Task.getCache(context, params, this, this, cacheType, getIdentity(session));
        loadNextPage();
    }

    public TrendAdapter(Context context, ListView listView,
                        ImageLoader imageLoader,
                        HashMap<String, String> params,
                        Session session,
                        Member member,
                        BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                        String cacheType,
                        String requestType,
                        boolean isDataLoaded) {
        super(context, listView, imageLoader, params, cacheType);
        this.mSession = session;
        this.member = member;
        this.requestType = requestType;
        trendViewHelper = new TrendViewHelper(context, imageLoader);
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if (!isDataLoaded)
            Task.getCache(context, params, this, this, cacheType, getIdentity(session));
        loadNextPage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        final MiniBlog item = getItem(position);
        TrendViewHolder holder;
        if (convertView == null) {
            convertView = trendViewHelper.populateFitItemView(trendViewHelper.getViewType(item, false), parent);
            holder = new TrendViewHolder();

            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (TrendViewHolder) convertView.getTag();
        }
        if (shouldLoadNextPage(mDataList, position)) {
            loadedPage++;
            updateRequestParams(mParams);
            loadNextPage();
            mListView.onFooterRefreshBegin();
            LogUtils.i("load page: " + loadedPage);
        }

        if (holder.userHeadRequest != null) {
            holder.userHeadRequest.cancelRequest();
            LogUtils.i("cancel request head image");
        }

        holder.userHeadRequest = mImageLoader.get(item.getUserhead()
                , ImageLoader.getImageListener(holder.userHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));

        holder.userHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, CommonSpaceActivity.class);
                UserInfo user = DBHelper.getInstance(mContext).getUserInfoTable().getUserByIds(item.getIds(), item.getListid());
                intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                mContext.startActivity(intent);
            }
        });

        holder.publishTimeTxt.setText(item.getCreatetime());
        holder.userNameTxt.setText(item.getUserName());

        // show footer
        trendViewHelper.showCommentNum(holder, item.getReplynum());
        trendViewHelper.showRepostNum(holder, item.getFowardnum());
        trendViewHelper.showCollectNum(holder, item.getCollectionnum());
        trendViewHelper.showPublishByDeviceType(holder, item.getComefrom());

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TREND_TEXT_ONLY:
                trendViewHelper.showTrendTitle(holder, item.getTitle(), item.isUp());
                trendViewHelper.showTrendContent(holder, item.getContent(), item.getTitle(), item.isUp());
                break;
            case VIEW_TYPE_TREND_WITH_MULTI_MEDIA:
                trendViewHelper.showTrendContent(holder, item.getContent(), item.getTitle(), item.isUp());
                trendViewHelper.showConfigs(holder, item.getConfiglist(), false);
                break;
            case VIEW_TYPE_QUOTED_TREND_WITH_TEXT_ONLY:
                trendViewHelper.showTrendTitle(holder, item.getTitle(), item.isUp());
                trendViewHelper.showQuotedTrendTitle(item.getChildAbs(), holder, item.getChildAbs().getTitle());
                trendViewHelper.showQuotedTrendContent(item.getChildAbs(), holder, item.getChildAbs().getContent());
                break;
            case VIEW_TYPE_QUOTED_TREND_WITH_MUTIL_MEDIA:
                trendViewHelper.showTrendTitle(holder, item.getTitle(), item.isUp());
                trendViewHelper.showQuotedTrendContent(item.getChildAbs(), holder, item.getChildAbs().getContent());
                trendViewHelper.showConfigs(holder, item.getChildAbs().getConfiglist(), true);
                break;

            default:
                break;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        super.getViewTypeCount();
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        LogUtils.i("getViewType: position = " + position + ", view type = " + trendViewHelper.getViewType(mDataList.get(position), false));
        return trendViewHelper.getViewType(mDataList.get(position), false);
    }

    @Override
    public void loadNextPage() {
        isLoading = true;
        if (requestType.equals(REQUEST_TYPE_GET_FEED)) {
            Task.getFeed(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_AT_ME_CONTENT)) {
            Task.getAtMeContentInfo(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS)) {
            Task.getSpecificUserTrends(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_SPECIFIC_GROUP_TERNDS)) {
            Task.getSpecificGroupTrends(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_COLLECTION)) {
            Task.queryUserCollection(mParams, this, this);
        }
    }


    public List<MiniBlog> parseList(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            List<MiniBlog> trendList = null;
            if (jsonObject.getString("json") != null) {
                trendList = JSON.parseArray(jsonObject.getString("json"), MiniBlog.class);
            } else if (jsonObject.getString("list") != null) {
                trendList = JSON.parseArray(jsonObject.getString("list"), MiniBlog.class);
            }
            if (handleProgressBarVisibilityCallback != null) {
                handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
            }
            return trendList;
        } catch (JSONException e) {
            if (handleProgressBarVisibilityCallback != null) {
                handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
            }
            return new ArrayList<MiniBlog>();
        }
    }

    /**
     * 更新页面位置
     *
     * @param params
     */
    public void updateRequestParams(Map<String, String> params) {
        params.put("start", String.valueOf(loadedPage * Constants.PAGE_SIZE));
        return;
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        if (requestType.equals(REQUEST_TYPE_GET_FEED)) {
            Task.getFeed(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_AT_ME_CONTENT)) {
            Task.getAtMeContentInfo(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS)) {
            Task.getSpecificUserTrends(mParams, this, this);
        } else if (requestType.equals(REQUEST_TYPE_GET_COLLECTION)) {
            Task.queryUserCollection(mParams, this, this);
        }
    }

    public class TrendOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent it = new Intent(mContext, TrendDetailActivity.class);
            it.putExtra(Constants.EXTRA_TREND_ITEM, (MiniBlog) parent.getAdapter().getItem(position));
            mContext.startActivity(it);
        }
    }

    public class TrendOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final String[] s = {"转发", "评论", "收藏"};
            promptDialog((MiniBlog) parent.getAdapter().getItem(position), s);
            return false;
        }
    }

    public String getIdentity(Session session) {
        StringBuilder builder = new StringBuilder();
        if (requestType.equals(REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS) && member != null) {
            return builder.append(member.getListid())
                    .append(Constants.SYMBOL_PERIOD)
                    .append(member.getId()).toString();
        } else {
            if (requestType.equals(REQUEST_TYPE_GET_FEED)) {
                return builder.append(session.getListid())
                        .append(Constants.SYMBOL_PERIOD)
                        .append(session.getIds())
                        .append(Constants.SYMBOL_PERIOD)
                        .append(session.getRegion())
                        .append(Constants.SYMBOL_PERIOD)
                        .append(session.getAppType()).toString();
            } else {
                return builder.append(session.getListid())
                        .append(Constants.SYMBOL_PERIOD)
                        .append(session.getIds()).toString();
            }
        }
    }

    private HashMap<String, String> buildRequestAddCollectionParams(MiniBlog item) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("curnetid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("absid", String.valueOf(item.getId()));
        }
        return params;
    }

    private HashMap<String, String> buildRequestCancelCollectionParams(MiniBlog item) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("curnetid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("absid", String.valueOf(item.getId()));
            params.put("listid", mSession.getListid());
        }
        return params;
    }

    private void promptDialog(final MiniBlog item, final String[] str) {
        CustomDialog dialog = new CustomDialog(mContext, R.style.popDialog);
        dialog.setList(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent();
                it.setClass(mContext, CommentEditActivity.class);
                if (which == 0) {
                    it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_AT_ME_CONTENT_ACTION_REPOST);
                    it.putExtra(Constants.ACTION, Constants.ACTION_REPOST);
                    it.putExtra(Constants.EXTRA_TREND_PRIABSID, String.valueOf(item.getId()));

                    if (item.getChildAbs() != null) {
                        it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(item.getChildAbs().getId()));
                        it.putExtra(Constants.EXTRA_TREND_SOURCETEXT, TextUtil.composeReforwdReforwdStr(item.getUserName(), item.getIds(), item.getTitle()));
                    } else {
                        it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(item.getId()));
                    }

                    if("1".equals(item.getMessagetype())){
                        it.putExtra(Constants.EXTRA_REPOST_TEXT, item.getTitle());
                        //TrendViewHolder holder = new TrendViewHolder();
                        //it.putExtra(Constants.EXTRA_REPOST_TEXT, trendViewHelper.getTrendTitle(holder));
                    }

                    mContext.startActivity(it);
                } else if (which == 1) {
                    it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_AT_ME_CONTENT_ACTION_COMMENT);
                    it.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(item.getId()));
                    it.putExtra(Constants.ACTION, Constants.ACTION_COMMENT);
                    mContext.startActivity(it);
                } else {
                    requestType = REQUEST_TYPE_ADD_OR_CANCEL_COLLECTION;
                    if (str[2].equals("收藏")) {
                        Task.addCollection(buildRequestAddCollectionParams(item), TrendAdapter.this, TrendAdapter.this);
                    } else {
                        Task.cancelCollection(buildRequestCancelCollectionParams(item), TrendAdapter.this, TrendAdapter.this);
                    }
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onResponse(String response) {
        if (requestType.equals(TrendAdapter.REQUEST_TYPE_GET_FEED)
                || requestType.equals(TrendAdapter.REQUEST_TYPE_GET_AT_ME_CONTENT)
                || requestType.equals(TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS)
                || requestType.equals(TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_GROUP_TERNDS)
                || requestType.equals(TrendAdapter.REQUEST_TYPE_GET_COLLECTION)
                || requestType.equals(TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_GROUP_TERNDS)) {
            if (requestType.equals(TrendAdapter.REQUEST_TYPE_GET_AT_ME_CONTENT)) {
                SyncUtils.TriggerRefresh();
            }
            super.onResponse(response);

        } else {
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.get("info") != null) {
                Utils.makeEventToast(mContext, jsonObject.get("info").toString(), false);
                return;
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
    }
}
