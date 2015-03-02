package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.Session;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fanruikang on 14-7-30.
 */
public class GroupListAdapter extends ResultsAdapter<GroupInfo> {

    public GroupListAdapter(Context context, ListView listView, ImageLoader imageLoader, HashMap params) {
        super(context, listView, imageLoader, params);
        mDataList = new ArrayList<GroupInfo>(0);
    }

    class ViewHolder {
        @ViewInject(R.id.txt_contact_name)
        TextView name;

        @ViewInject(R.id.img_group_avatar)
        ImageView groupAvatar;

        @ViewInject(R.id.txt_count_in_group)
        TextView countInGroup;

        ImageLoader.ImageContainer groupAvatarRequest;

        @ViewInject(R.id.button_group_chat)
        ImageView startGroupChat;

        @ViewInject(R.id.button_enter)
        ImageView enter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        GroupInfo groupInfo = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_im_group, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder.groupAvatarRequest != null) {
            holder.groupAvatarRequest.cancelRequest();
        }

        holder.countInGroup.setText(String.valueOf(groupInfo.getG_ucount()));
        holder.countInGroup.setBackgroundResource(groupInfo.getZ_type().equals("0") ? R.drawable.yellow : R.drawable.blue);
        holder.countInGroup.setVisibility(View.VISIBLE);

        holder.name.setText(TextUtil.processNullString(groupInfo.getG_name()));

        Session session = Session.get(mContext);
        if (groupInfo.getIsmember() == null || groupInfo.getIsmember().equals("1")) {
            holder.startGroupChat.setTag(groupInfo);
            holder.startGroupChat.setVisibility(View.VISIBLE);
            holder.startGroupChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupInfo info = (GroupInfo) v.getTag();

                    Intent intent = new Intent(mContext, GroupChatActivity.class);
                    intent.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) info);
                    intent.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, info.getG_ucount());
                    intent.putExtra(Constants.EXTRA_IM_GROUPID, info.getId());
                    intent.putExtra(Constants.EXTRA_IM_YGROUPID, info.getY_gid());
                    intent.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
                    intent.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, info.getG_type());
                    intent.putExtra(Constants.EXTRA_IM_GROUP_NAME, info.getG_name());
                    intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);

                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.startGroupChat.setVisibility(View.GONE);
        }

        if (groupInfo.getZ_type() != null && groupInfo.getZ_type().equals("1")) {
            holder.enter.setTag(groupInfo);
            holder.enter.setVisibility(View.VISIBLE);
            holder.enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupInfo info = (GroupInfo) v.getTag();
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) info);
                    intent.setClass(mContext, CommonSpaceActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.enter.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public List parseList(String response) {
        return null;
    }

    @Override
    public void loadNextPage() {

    }

    @Override
    public void onRefresh() {

    }
}
