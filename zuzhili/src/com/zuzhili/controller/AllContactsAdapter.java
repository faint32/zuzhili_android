package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.Session;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kj on 2014/10/29.
 */
public class AllContactsAdapter extends ResultsAdapter<Object>{

    public AllContactsAdapter(Context context, ListView listView, ImageLoader imageLoader, HashMap<String, String> params) {
        super(context, listView, imageLoader, params);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object obj=getItem(position);
        convertView=null;
        if(obj instanceof  UserInfo){
            final ViewHolder holder;
            UserInfo item=(UserInfo)obj;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_item_add_contact, parent, false);
                holder = new ViewHolder();
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (holder.userAvatarRequest != null) {
                holder.userAvatarRequest.cancelRequest();
            }

            try {
                holder.userAvatarRequest = mImageLoader.get(TextUtil.processNullString(item.getU_icon())
                        , ImageLoader.getImageListener(holder.userAvatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
            } catch (Exception e) {
                holder.userAvatar.setImageResource(R.drawable.default_user_head_small);
            }

            holder.name.setText(TextUtil.processNullString(item.getU_name().trim()));

            holder.selectContactCheckbox.setVisibility(View.GONE);

            holder.sortKey.setVisibility(View.GONE);

            // make call button
            if (!TextUtils.isEmpty(item.getU_phone())) {
                holder.makeCall.setVisibility(View.VISIBLE);
                holder.makeCall.setTag(item);
                holder.makeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo item = (UserInfo) v.getTag();
                        LogUtils.d("Make call to :" + item.getU_name());
                        Uri number = Uri.parse("tel:" + item.getU_phone());
                        Intent intent = new Intent(Intent.ACTION_DIAL, number);
                        mContext.startActivity(intent);
                    }
                });
            } else {
                holder.makeCall.setVisibility(View.GONE);
            }

            // start chat button
            holder.startChat.setVisibility(View.VISIBLE);
            holder.startChat.setTag(item);
            holder.startChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo user = (UserInfo) v.getTag();

                    Intent it = new Intent(mContext, GroupChatActivity.class);
                    it.putExtra(Constants.EXTRA_IM_YGROUPID, user.getY_voip());
                    it.putExtra(Constants.EXTRA_IM_GROUP_NAME, user.getU_name());
                    it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊
                    it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) user);
                    it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);

                    mContext.startActivity(it);
                }
            });

        }else{
            final ViewHolder2 holder2;
            GroupInfo groupInfo=(GroupInfo)obj;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_item_im_group, parent, false);
                holder2 = new ViewHolder2();
                ViewUtils.inject(holder2, convertView);
                convertView.setTag(holder2);
            } else {
                holder2 = (ViewHolder2) convertView.getTag();
            }

            if (holder2.groupAvatarRequest != null) {
                holder2.groupAvatarRequest.cancelRequest();
            }

            holder2.countInGroup.setText(String.valueOf(groupInfo.getG_ucount()));
            holder2.countInGroup.setBackgroundResource(groupInfo.getZ_type().equals("0") ? R.drawable.yellow : R.drawable.blue);
            holder2.countInGroup.setVisibility(View.VISIBLE);

            holder2.name.setText(TextUtil.processNullString(groupInfo.getG_name()));

            Session session = Session.get(mContext);
            if (groupInfo.getIsmember() == null || groupInfo.getIsmember().equals("1")) {
                holder2.startGroupChat.setTag(groupInfo);
                holder2.startGroupChat.setVisibility(View.VISIBLE);
                holder2.startGroupChat.setOnClickListener(new View.OnClickListener() {
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
                holder2.startGroupChat.setVisibility(View.GONE);
            }

            if (groupInfo.getZ_type() != null && groupInfo.getZ_type().equals("1")) {
                holder2.enter.setTag(groupInfo);
                holder2.enter.setVisibility(View.VISIBLE);
                holder2.enter.setOnClickListener(new View.OnClickListener() {
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
                holder2.enter.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    @Override
    public List<Object> parseList(String response) {
        return null;
    }

    @Override
    public void loadNextPage() {

    }

    @Override
    public void onRefresh() {

    }

    class ViewHolder {

        @ViewInject(R.id.txt_sort_key)
        TextView sortKey;

        @ViewInject(R.id.txt_contact_name)
        TextView name;

        @ViewInject(R.id.img_user_avatar)
        ImageView userAvatar;

        ImageLoader.ImageContainer userAvatarRequest;

        @ViewInject(R.id.checkbox_select_contact)
        CheckBox selectContactCheckbox;

        @ViewInject(R.id.button_make_call)
        ImageView makeCall;

        @ViewInject(R.id.button_start_chat)
        ImageView startChat;
    }



    class ViewHolder2 {
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
}
