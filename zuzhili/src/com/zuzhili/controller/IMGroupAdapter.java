package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.db.model.GroupChatInfo;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.im.GroupsActivity;

import java.util.List;

/**
 * Created by liutao on 14-4-21.
 */
public class IMGroupAdapter extends ResultsAdapter implements AdapterView.OnItemClickListener {

    private Context context;
    private int msgType;
    private String content;
    protected NonPagingResultsAdapter.OnItemSelectedListener onItemSelectedListener;
    private static final int REQ_CODE_GROUP_CHAT = 0;
    public IMGroupAdapter(Context context, ListView listView, ImageLoader imageLoader,int msgType,String content) {
        super(context, listView, imageLoader,null);
        this.context = context;
        this.msgType = msgType;
        this.content = content;
    }

    public void setOnItemClickListener() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        GroupInfo groupInfo = (GroupInfo) getItem(position);

        if (getItem(position) instanceof  GroupInfo) {
            groupInfo = (GroupInfo) getItem(position);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_im_group, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(holder.groupAvatarRequest != null) {
            holder.groupAvatarRequest.cancelRequest();
        }

        if (groupInfo != null) {
            if (groupInfo.getG_ucount() != null) {
                holder.countInGroup.setVisibility(View.VISIBLE);
                holder.countInGroup.setText(String.valueOf(groupInfo.getG_ucount()));
                if (groupInfo.getZ_type().equals("0")) { // 自建群组
                    holder.countInGroup.setBackgroundResource(R.drawable.yellow);
                } else {  // 公共空间群组
                    holder.countInGroup.setBackgroundResource(R.drawable.blue);
                }
            } else {
                holder.countInGroup.setVisibility(View.GONE);
                holder.groupAvatar.setImageResource(R.drawable.default_user_head_small);
            }

            holder.name.setText(TextUtil.processNullString(groupInfo.getG_name()));
        }

        holder.startGroupChat.setVisibility(View.VISIBLE);
        holder.startGroupChat.setTag(groupInfo);
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

                //转发
                if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
                    intent.putExtra(Constants.MSG_FORWORD,content);
                    intent.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
                }else {
                    intent.putExtra(Constants.MSG_FORWORD,content);
                    intent.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
                }
                ((GroupsActivity)mContext).startActivityForResult(intent,REQ_CODE_GROUP_CHAT);
            }
        });

        return convertView;
    }

    @Override
    public List parseList(String response) {
        return null;
    }

    @Override
    public void loadNextPage() {

    }

    public void removeItem(String groupId){
        if(!mDataList.isEmpty()){
            for (int i = 0; i < mDataList.size(); i++) {
                if (((GroupChatInfo)mDataList.get(i)).getGroupId().equals(groupId)) {
                    mDataList.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemSelectedListener != null) {
            if (parent.getAdapter().getItem(position) instanceof GroupChatInfo) {
                onItemSelectedListener.onItemSelected(position, parent.getAdapter().getItem(position));
            }
        }
    }

    @Override
    public void onResponse(Object o) {
        isPullOnRefreshEnd = true;
        mListView.onPullRefreshEnd();
        mListView.onFooterRefreshEnd();
        ((GroupsActivity)mContext).onResponse((String)o);
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        Task.getGroups(((GroupsActivity)mContext).buildRequestParams(), this, this);
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
    }
}
