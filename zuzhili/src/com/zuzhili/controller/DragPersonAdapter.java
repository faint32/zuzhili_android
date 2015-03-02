package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.db.model.GroupChatInfo;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.im.ChatRoomSettingsActivity;
import com.zuzhili.ui.activity.im.NewConversationActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-4-27.
 */
public class DragPersonAdapter extends BaseAdapter implements Response.Listener<String>, Response.ErrorListener {

    private Context context;

    private List<UserInfo> friendInfoList;

    private ImageLoader imageLoader;

    private ViewHolder holder;

    private String groupId;

    private Session session;

    private OnRemoveUserListener onRemoveUserListener;

    public void setOnRemoveUserListener(OnRemoveUserListener onRemoveUserListener) {
        this.onRemoveUserListener = onRemoveUserListener;
    }

    public List<UserInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public interface OnRemoveUserListener {
        public void onRemoveUser(int chatRoomNum);
    }

    public DragPersonAdapter(Context context, ArrayList<UserInfo> friendInfoList, Session session, String groupId) {
        this.context = context;
        this.imageLoader = ImageCacheManager.getInstance().getImageLoader();
        this.friendInfoList = friendInfoList;
        this.session = session;
        this.groupId = groupId;
    }

    @Override
    public int getCount() {
         return friendInfoList.size();
    }

    @Override
    public UserInfo getItem(int position) {
        return friendInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final UserInfo item = friendInfoList.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_user, parent, false);
        holder = new ViewHolder();
        ViewUtils.inject(holder, convertView);

        if(holder.userAvatarRequest != null) {
            holder.userAvatarRequest.cancelRequest();
        }

        if (item.getUserFlag() == 0) {
            holder.userAvatarRequest = imageLoader.get(TextUtil.processNullString(item.getU_icon())
                    , ImageLoader.getImageListener(holder.userAvatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
            holder.name.setText(TextUtil.processNullString(item.getU_name()));

            if(item.getU_id().equals(session.getUid())){
                item.setShowLeftTopRemoveUserIcon(false);
            }
            holder.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isShowLeftTopRemoveUserIcon()) {
                        final HashMap<String, String> params = new HashMap<String, String>();
                        if(session != null) {
                            params.put("u_id", session.getUid());
                            params.put("u_listid", session.getListid());
                            params.put("groupid", groupId);
                            params.put("members", item.getU_id());
                        }
                        Task.deleteGroupMember(params, DragPersonAdapter.this, DragPersonAdapter.this);
                        friendInfoList.remove(position);
                        if (onRemoveUserListener != null) {
                            onRemoveUserListener.onRemoveUser(friendInfoList.size());
                        }
                        for (int i = 0; i < friendInfoList.size(); i++) {
                            friendInfoList.get(i).setShowLeftTopRemoveUserIcon(false);
                        }

                        // 存放聊天室信息。
                        final GroupChatInfo groupChatInfo = new GroupChatInfo();
                        groupChatInfo.setTime(System.currentTimeMillis());
                        groupChatInfo.setChatRoomNum(friendInfoList.size());
                        groupChatInfo.setName(getTempGroupChatRoomName(friendInfoList));
                        groupChatInfo.setGroupId(groupId);
                        groupChatInfo.setGroupType(Constants.IM_TYPE_GROUP_CHAT);
                        groupChatInfo.setContactJson(JSONObject.toJSONString(friendInfoList));
                        groupChatInfo.setIdentity(Utils.getIdentity(session));

                        if (session.getBinder() != null) {
                            session.getBinder().addGroupChatInfo(groupChatInfo, false);
                            Intent broadCast = new Intent();
                            broadCast.setAction(Constants.ACTION_RECEIVE_CREATE_CONTACT_CHAT);
                            broadCast.putExtra(Constants.EXTRA_IM_NEED_REFRESH_CONTACT_LIST, true);
                            context.sendBroadcast(broadCast);
                        }

                        UserInfo addUserFakeFriendInfo = new UserInfo();
                        UserInfo removeUserFakeFriendInfo = new UserInfo();
                        addUserFakeFriendInfo.setUserFlag(1);
                        removeUserFakeFriendInfo.setUserFlag(2);
                        friendInfoList.add(friendInfoList.size(), addUserFakeFriendInfo);
                        friendInfoList.add(friendInfoList.size(), removeUserFakeFriendInfo);
                        notifyDataSetChanged();
                    } else {
                        // display user homepage
                        Intent intent = new Intent();
                        intent.setClass(context, CommonSpaceActivity.class);
                        intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) item);
                        context.startActivity(intent);
                    }
                }
            });

        } else if (item.getUserFlag() == 1) {
            holder.userAvatar.setImageResource(R.drawable.im_add_user);
            holder.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除假数据（添加和删除聊天室成员）
                    friendInfoList.remove(friendInfoList.size() - 1);
                    friendInfoList.remove(friendInfoList.size() - 1);
                    Intent it = new Intent(context, NewConversationActivity.class);
                    it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, (ArrayList<? extends android.os.Parcelable>) friendInfoList);
                    it.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS);
                    it.putExtra(Constants.EXTRA_IM_GROUPID, groupId);
                    ((ChatRoomSettingsActivity) context).startActivityForResult(it, 0);
                }
            });
        } else {
            holder.userAvatar.setImageResource(R.drawable.im_remove_user);
            holder.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    friendInfoList.remove(friendInfoList.size() - 1);
                    friendInfoList.remove(friendInfoList.size() - 1);
                    for (int i = 0; i < friendInfoList.size(); i++) {
                        if (!friendInfoList.get(i).getU_id().equals(session.getIds())) {
                            friendInfoList.get(i).setShowLeftTopRemoveUserIcon(true);
                        }
                    }
                    notifyDataSetChanged();

                }
            });
        }

        if (item.isShowLeftTopRemoveUserIcon()) {
            holder.leftTopRemoveUserIcon.setVisibility(View.VISIBLE);
        } else {
            holder.leftTopRemoveUserIcon.setVisibility(View.GONE);
        }

        return convertView;
    }



    public void setList(List<UserInfo> list) {
        if(list!=null && list.size()>0){
            this.friendInfoList = list;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {

        @ViewInject(R.id.txt_name)
        TextView name;

        @ViewInject(R.id.img_user_avatar)
        ImageView userAvatar;

        ImageLoader.ImageContainer userAvatarRequest;

        @ViewInject(R.id.img_left_top_remove_user_icon)
        ImageView leftTopRemoveUserIcon;

    }

    private String getTempGroupChatRoomName(List<UserInfo> friendInfoList) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (UserInfo item : friendInfoList) {
            if ((i++) < 4) {
                builder.append(item.getU_name()).append(Constants.SYMBOL_COMMA_CHN);
            } else {
                break;
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.i("volleyError" + volleyError.toString());
    }

    @Override
    public void onResponse(String s) {

    }
}
