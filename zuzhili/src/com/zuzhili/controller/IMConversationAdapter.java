package com.zuzhili.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.db.DBHelper;
import com.zuzhili.db.IMGroupInfoTable;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.db.IMUserInfoTable;
import com.zuzhili.db.Table;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.BadgeView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-6-27.
 */
public class IMConversationAdapter extends ResultsAdapter<IMConversation> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener, Response.Listener<String>, Response.ErrorListener {

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    private IMUserInfoTable imUserInfoTable;

    private IMGroupInfoTable imGroupInfoTable;

    private IMMessageTable messageTable;

    private int pos;

    private List<IMConversation> mConversation = new ArrayList<IMConversation>();

    //是否转发页面
    private boolean isForward;

    private OnItemSelectedForDrawListener onItemSelectedForDrawListener;

    public interface OnItemSelectedForDrawListener<T> {
        public void onItemSelected(int position, T item, Drawable drawable);
    }

    public void setOnItemClickedForDrawListener(OnItemSelectedForDrawListener onItemSelectedForDrawListener) {
        this.onItemSelectedForDrawListener = onItemSelectedForDrawListener;
    }

    private OnItemDeleteListener onItemDeleteListener;

    public interface OnItemDeleteListener<T> {
        public void onItemDelete(int position, T item);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public IMConversationAdapter(Context context, ListView listView, ImageLoader imageLoader,boolean isForward) {
        super(context, listView, imageLoader, null);
        this.isForward=isForward;
        characterParser = CharacterParser.getInstance();
        imUserInfoTable = DBHelper.getInstance(context).getUserInfoTable();
        imGroupInfoTable = DBHelper.getInstance(context).getGroupInfoTable();
        messageTable = DBHelper.getInstance(context).getMessageTable();
        update();
    }

    public void update() {
        new GetIMConversationTask().execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        IMConversation item = getItem(position);
        UserInfo friend = null;
        GroupInfo groupInfo = null;
        if (item.getId() != null && item.getId().startsWith("g")) {
            try {
                groupInfo = imGroupInfoTable.queryGroup(item.getId());
                if (groupInfo == null) {
                    pos = position;
                    // 可能是新创建的群发送的消息，本地没有缓存，需要网络获取
                    Task.getGroup(buildRequestGroupParams(item.getId()), this, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                groupInfo = null;
            }
        } else {
            friend = imUserInfoTable.get(item.getId(), item.getListId(), Utils.getIdentity(Session.get(mContext)));
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_im_conversation, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            holder.unreadMsgCountBV = new BadgeView(mContext, holder.badgePosTxt);
            holder.unreadMsgCountBV.setBadgePosition(BadgeView.POSITION_CENTER);
            holder.unreadMsgCountBV.setTypeface(Typeface.DEFAULT);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(holder.groupAvatarRequest != null) {
            holder.groupAvatarRequest.cancelRequest();
        }

        if (item != null) {
            if(!isForward) {
                if (item.getUnReadNum() != null && Integer.valueOf(item.getUnReadNum()) > 0) {
                    holder.unreadMsgCountBV.setText(item.getUnReadNum());
                    holder.unreadMsgCountBV.show(true);
                } else {
                    holder.unreadMsgCountBV.setText(item.getUnReadNum());
                    holder.unreadMsgCountBV.hide();
                }

                if (item.getRecentMessage() != null) {
                    holder.latestMsgContent.setText(item.getRecentMessage());
                }

                if (item.getDateCreated() != null) {
                    holder.lastTalkTime.setText(Utils.getFriendlyTime(Long.valueOf(item.getDateCreated())));
                }

                if (friend != null && friend.getU_name() != null) {
                    holder.name.setText(friend.getU_name());
                    holder.countInGroup.setVisibility(View.GONE);
                    holder.avatar.setVisibility(View.VISIBLE);
                    try {
                        mImageLoader.get(TextUtil.processNullString(friend.getU_icon()), ImageLoader.getImageListener(holder.avatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
                    } catch (Exception e) {
                        holder.avatar.setImageResource(R.drawable.default_user_head_small);
                    }

                } else if (groupInfo != null && groupInfo.getG_name() != null && groupInfo.getG_ucount() != null) {
                    holder.name.setText(groupInfo.getG_name());
                    holder.countInGroup.setVisibility(View.VISIBLE);
                    holder.countInGroup.setText(groupInfo.getG_ucount());
                    if (groupInfo.getZ_type().equals("0")) {
                        holder.countInGroup.setBackgroundResource(R.drawable.yellow);
                    } else {
                        holder.countInGroup.setBackgroundResource(R.drawable.blue);
                    }
                }
            }else {
                holder.onename.setVisibility(View.VISIBLE);
                if (friend != null && friend.getU_name() != null) {
                    holder.onename.setText(friend.getU_name());
                    holder.countInGroup.setVisibility(View.GONE);
                    holder.avatar.setVisibility(View.VISIBLE);
                    try {
                        mImageLoader.get(TextUtil.processNullString(friend.getU_icon()), ImageLoader.getImageListener(holder.avatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
                    } catch (Exception e) {
                        holder.avatar.setImageResource(R.drawable.default_user_head_small);
                    }
                } else if (groupInfo != null && groupInfo.getG_name() != null && groupInfo.getG_ucount() != null) {
                    holder.onename.setText(groupInfo.getG_name());
                    holder.countInGroup.setVisibility(View.VISIBLE);
                    holder.countInGroup.setText(groupInfo.getG_ucount());
                    if (groupInfo.getZ_type().equals("0")) {
                        holder.countInGroup.setBackgroundResource(R.drawable.yellow);
                    } else {
                        holder.countInGroup.setBackgroundResource(R.drawable.blue);
                    }
                }
            }
        }
        return convertView;
    }

    @Override
    public List<IMConversation> parseList(String response) {
        return null;
    }

    @Override
    public void loadNextPage() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.unreadMsgCountBV.hide(true);
        if (onItemSelectedForDrawListener != null) {
            IMConversation item = (IMConversation) parent.getAdapter().getItem(position);
            UserInfo friend = imUserInfoTable.get(item.getId(), item.getListId(), Utils.getIdentity(Session.get(mContext)));
            GroupInfo groupInfo = null;
            try {
                groupInfo = imGroupInfoTable.queryGroup(item.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (friend != null) {
                item.setUserName(friend.getU_name());
                item.setUserAvatar(friend.getU_icon());
            }
            if (groupInfo != null) {
                item.setUserName(groupInfo.getG_name());
                item.setGroupUserCount(groupInfo.getG_ucount());
                item.setOwner(groupInfo.getCreatorid());
                item.setGroupId(groupInfo.getId());
                item.setGroupType(groupInfo.getZ_type());
            }
            Drawable countInGroup = holder.countInGroup.getBackground();
            Drawable avater=holder.avatar.getDrawable();
            if(countInGroup!=null){
                onItemSelectedForDrawListener.onItemSelected(position, item,countInGroup);
            }else if(avater!=null){
                onItemSelectedForDrawListener.onItemSelected(position, item,avater);
            }
        }
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(onItemDeleteListener!=null){
            final IMConversation item = getItem(position-3);
            onItemDeleteListener.onItemDelete(position-3,item);
        }

        return true;
    }


    private HashMap<String, String> buildRequestGroupParams(String groupId) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(Session.get(mContext) != null) {
            params.put("u_id", Session.get(mContext).getUid());
            params.put("u_listid", Session.get(mContext).getListid());
            params.put("y_gid", groupId);
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (jsonObject != null && jsonObject.getString("group") != null) {
                JSONObject group = jsonObject.getJSONObject("group");
                if (group == null || group.size() == 0) {
                    if (pos < mDataList.size()) {
                        dbHelper.getGroupInfoTable().deleteGroupByGroupId(mDataList.get(pos).getGroupId());
                        dbHelper.getMessageTable().deleteIMMessage(mDataList.get(pos).getId(), Session.get(mContext).getListid());
                        mDataList.remove(pos);
                        notifyDataSetChanged();
                    }
                    return;
                }
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setCreatorid(group.getString("creatorid"));
                groupInfo.setG_ucount(group.getString("g_ucount"));
                groupInfo.setId(group.getString("id"));
                groupInfo.setG_name(group.getString("g_name"));
                groupInfo.setG_type(group.getString("g_type"));
                groupInfo.setG_permisson(group.getString("g_permisson"));
                groupInfo.setU_listid(group.getString("u_listid"));
                groupInfo.setG_capacity(group.getString("g_capacity"));
                groupInfo.setG_declared(group.getString("g_declared"));
                groupInfo.setY_gid(group.getString("y_gid"));
                groupInfo.setZ_gid(group.getString("z_gid"));
                groupInfo.setZ_type(group.getString("z_type"));
                groupInfo.setIdentityId(Utils.getIdentity(Session.get(mContext)));

                if (dbHelper.getGroupInfoTable().isExistsGroupId(groupInfo.getY_gid()) != null) {
                    dbHelper.getGroupInfoTable().updateGroupInfo(groupInfo);
                } else {
                    dbHelper.getGroupInfoTable().insertIMGroupInfo(groupInfo);
                }
                // 重新刷新数据
                if (mDataList.size() > 0 && mDataList.size() > pos) {
                    mDataList.get(pos).setGroupId(groupInfo.getId());
                    mDataList.get(pos).setGroupUserCount(groupInfo.getG_ucount());
                    mDataList.get(pos).setIds(Session.get(mContext).getIds());
                    mDataList.get(pos).setListId(groupInfo.getU_listid());
                    mDataList.get(pos).setType(Integer.valueOf(groupInfo.getG_type()));
                    this.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRefresh() {

    }

    class ViewHolder {

        @ViewInject(R.id.txt_contact_onename)
        TextView onename;

        @ViewInject(R.id.txt_contact_name)
        TextView name;

        @ViewInject(R.id.img_group_avatar)
        ImageView avatar;

        @ViewInject(R.id.txt_count_in_group)
        TextView countInGroup;

        ImageLoader.ImageContainer groupAvatarRequest;

        @ViewInject(R.id.txt_last_talk_time)
        TextView lastTalkTime;

        @ViewInject(R.id.txt_lastest_msg)
        TextView latestMsgContent;

        @ViewInject(R.id.txt_badge_position)
        TextView badgePosTxt;

        BadgeView unreadMsgCountBV;

    }



    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    public void filterData(String filterStr) {
        List<IMConversation> tempList = new ArrayList<IMConversation>();

        if (TextUtils.isEmpty(filterStr)) {
            tempList = mConversation;
            isLoading = false;
        } else {
            tempList.clear();
            UserInfo friend = null;
            GroupInfo groupInfo = null;
            String name =null;
            for (IMConversation item : mConversation) {
                if (item.getId() != null && item.getId().startsWith("g")) {
                    try {
                        groupInfo = imGroupInfoTable.queryGroup(item.getId());
                        if (groupInfo == null) {
                            // 可能是新创建的群发送的消息，本地没有缓存，需要网络获取
                            Task.getGroup(buildRequestGroupParams(item.getId()), this, this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        groupInfo = null;
                    }
                    name = groupInfo.getG_name();
                } else {
                    friend = imUserInfoTable.get(item.getId(), item.getListId(), Utils.getIdentity(Session.get(mContext)));
                    name = friend.getU_name();
                }
                if (!TextUtils.isEmpty(name) && (name.indexOf(filterStr.toString().toLowerCase()) != -1 ||name.indexOf(filterStr.toString().toUpperCase()) != -1 ||name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())|| characterParser.getSelling(name).toUpperCase().startsWith(filterStr.toString()))) {
                    tempList.add(item);
                }
            }

            isLoading = true;
        }
        setList(tempList);
        notifyDataSetChanged();
    }

    private class GetIMConversationTask extends AsyncTask<Void, Void, List<IMConversation>> {

        @Override
        protected List<IMConversation> doInBackground(Void... params) {
            List<IMConversation> conversationList = null;
            try {
                Table table = ((BaseActivity) mContext).getDbHelper().getTable();
                table.setDbUtils(((BaseActivity) mContext).getDbHelper().getDbUtils());
                conversationList =  table.queryIMConversation(Utils.getIdentity(Session.get(mContext.getApplicationContext())));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return conversationList;
        }

        @Override
        protected void onPostExecute(final List<IMConversation> conversationList) {
            super.onPostExecute(conversationList);
            mConversation.clear();
            if (conversationList != null)
                mConversation.addAll(conversationList);
            setList(conversationList);
        }
    }
}
