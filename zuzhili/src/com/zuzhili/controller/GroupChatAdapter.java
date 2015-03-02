package com.zuzhili.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.zuzhili.bussiness.socket.model.ChatMessage;
import com.zuzhili.controller.helper.GroupChatViewHelper;
import com.zuzhili.controller.helper.TalkDetailHolder;
import com.zuzhili.ui.activity.BaseActivity;

/**
 * Created by liutao on 14-4-14.
 */
public class GroupChatAdapter extends NonPagingResultsAdapter<ChatMessage> {

    public static final int ITEM_VIEW_TYPE_COUNT = 2;		// listView 中item view的类型数目

    protected BaseActivity activity;

    private GroupChatViewHelper groupChatViewHelper;

    private TalkDetailHolder holder;

    public GroupChatAdapter(Context context,
                            ListView listView,
                            ImageLoader imageLoader) {
        super(context, listView, imageLoader);
        activity = (BaseActivity) context;
        groupChatViewHelper = new GroupChatViewHelper(context,imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage item = getItem(position);

        if(convertView == null) {
            convertView = groupChatViewHelper.populateFitItemView(getViewType(item), parent);
            holder = new TalkDetailHolder();
            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (TalkDetailHolder) convertView.getTag();
        }
        if(getViewType(item) == GroupChatViewHelper.VIEW_TYPE_OTHER) {
        	 groupChatViewHelper.showLeftMsgDetail(item, holder, activity);
        } else {
        	 groupChatViewHelper.showRightMsgDetail(item, holder, activity.mSession.getUserhead(), activity);
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
        return getViewType(getItem(position));
    }

    private int getViewType(ChatMessage item) {
        int type = GroupChatViewHelper.VIEW_TYPE_OTHER;
        if(activity.mSession.getIds().equals(String.valueOf(item.getFriendId()))){
            type= GroupChatViewHelper.VIEW_TYPE_MYSELF;
        }
        return type;
    }
}
