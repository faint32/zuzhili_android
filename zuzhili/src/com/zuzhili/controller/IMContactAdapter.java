package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.BackgroudTask;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.bussiness.utility.pinyin.PinyinComparator;
import com.zuzhili.db.IMUserInfoTable;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.GroupsQueryActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.zuzhili.ui.fragment.im.ContactsFragment.ContactMode;

/**
 * Created by liutao on 14-4-13.
 */
public class IMContactAdapter extends ResultsAdapter<UserInfo> implements SectionIndexer, AdapterView.OnItemClickListener {

    private static final String TAG = IMContactAdapter.class.getSimpleName();

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private List<UserInfo> sourceList;

    /**
     * 已经加入群聊的用户
     */
    private List<UserInfo> alreadyInGroupFriends;

    private Session mSession;

    public Map<String,Boolean> checkBoxStatus = new HashMap<String,Boolean>();

    private PinyinComparator pinyinComparator;

    private OnContactSelectedListener onContactSelectedListener;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;


    // Mode
    private ContactMode mode;

    // indicates whether query all groups or not
    private boolean isQueryAllGroupsAndUsers = false;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object tag = view.getTag();
        int item = -1;
        if (tag instanceof Integer) {
            item = (Integer) tag;

            Intent intent = null;
            HashMap<String, String> params = null;

            switch (item) {
                case R.drawable.my_group_chat:
                    LogUtils.d("我的群聊 was clicked.");
                    params = new HashMap();
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("g_want", "2");
                    params.put("z_type", "0");

                    intent = new Intent(mContext, GroupsQueryActivity.class);
                    intent.putExtra("params", params);
                    intent.putExtra("group.name", "我的群聊");
                    intent.putExtra("group.id", "group.mychat");
                    intent.putExtra("isQueryAllGroupsAndUsers", false);
                    mContext.startActivity(intent);
                    break;

                case R.drawable.org:
                    LogUtils.d("机构 was clicked.");
                    params = new HashMap();
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    if (!isQueryAllGroupsAndUsers)
                        params.put("g_want", "2");
                    params.put("z_type", "1");
                    params.put("parentid", "0");

                    intent = new Intent(mContext, GroupsQueryActivity.class);
                    intent.putExtra("params", params);
                    intent.putExtra("group.name", "机构");
                    intent.putExtra("group.id", "group.org");
                    intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                    mContext.startActivity(intent);
                    break;

                case R.drawable.group:
                    LogUtils.d("群组 was clicked.");
                    params = new HashMap();
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    if (!isQueryAllGroupsAndUsers)
                        params.put("g_want", "2");
                    params.put("z_type", "3");

                    intent = new Intent(mContext, GroupsQueryActivity.class);
                    intent.putExtra("params", params);
                    intent.putExtra("group.name", "群组");
                    intent.putExtra("group.id", "group.group");
                    intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                    mContext.startActivity(intent);
                    break;

                case R.drawable.project:
                    LogUtils.d("项目 was clicked.");
                    params = new HashMap();
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    if (!isQueryAllGroupsAndUsers)
                        params.put("g_want", "2");
                    params.put("z_type", "2");

                    intent = new Intent(mContext, GroupsQueryActivity.class);
                    intent.putExtra("params", params);
                    intent.putExtra("group.name", "项目");
                    intent.putExtra("group.id", "group.project");
                    intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                    mContext.startActivity(intent);
                    break;

                case R.drawable.activity:
                    LogUtils.d("活动 was clicked.");
                    params = new HashMap();
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("g_want", "2");
                    params.put("z_type", "4");

                    intent = new Intent(mContext, GroupsQueryActivity.class);
                    intent.putExtra("params", params);
                    intent.putExtra("group.name", "活动");
                    intent.putExtra("group.id", "group.activity");
                    intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                    mContext.startActivity(intent);
                    break;
            }

            return;
        }

        // Enter the user's public space if the mode is view than select user if the mode is selection.
        if (mode == ContactMode.MODE_VIEW) {
            accessUserSpace((UserInfo) parent.getAdapter().getItem(position));
            return;
        } else {

            if (view instanceof TextView) { // headView
                if (onContactSelectedListener != null) {
                    onContactSelectedListener.onHeadViewClicked();
                }
            } else {

                // 该用户不是聊天室成员
                if (alreadyInGroupFriends == null || (alreadyInGroupFriends != null && !isInGroup(alreadyInGroupFriends, (UserInfo) parent.getAdapter().getItem(position)))) {

                    if (onContactSelectedListener != null) {
                        onContactSelectedListener.onContactSelected((UserInfo) parent.getAdapter().getItem(position));
                    }

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_select_contact);
                    if (checkBox != null) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                        } else {
                            checkBox.setChecked(true);
                        }
                    }
                    checkBoxStatus.put(((UserInfo)parent.getAdapter().getItem(position)).getU_id(), checkBox.isChecked());
                }
            }

        }
    }

    private void accessUserSpace(UserInfo user) {
        Intent intent = new Intent();
        intent.setClass(mContext, CommonSpaceActivity.class);
        intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
        mContext.startActivity(intent);
    }


    public interface OnContactSelectedListener {
        public void onContactSelected(UserInfo userInfo);

        public void onHeadViewClicked();
    }

    public void setOnContactSelectedListener(OnContactSelectedListener onContactSelectedListener) {
        this.onContactSelectedListener = onContactSelectedListener;
    }

    public IMContactAdapter(Context context,
                            ListView listView,
                            ImageLoader imageLoader,
                            Session session,
                            HashMap<String, String> params,
                            BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                            ContactMode mode, boolean isQueryAllGroupsAndUsers) {
        super(context, listView, imageLoader, params);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sourceList = new ArrayList<UserInfo>();
        mSession = session;
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
//        loadNextPage();
        this.mode = mode;
        this.isQueryAllGroupsAndUsers = isQueryAllGroupsAndUsers;
        new GetUserInfoTask().execute();
    }

    public IMContactAdapter(Context context,
                            ListView listView,
                            ImageLoader imageLoader,
                            Session session,
                            List<UserInfo> alreadyInGroupFriends,
                            HashMap<String, String> params,
                            BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                            ContactMode mode, boolean isQueryAllGroupsAndUsers) {
        super(context, listView, imageLoader, params);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sourceList = new ArrayList<UserInfo>();
        this.alreadyInGroupFriends = alreadyInGroupFriends;
        mSession = session;
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
//        loadNextPage();
        this.mode = mode;
        this.isQueryAllGroupsAndUsers = isQueryAllGroupsAndUsers;
        new GetUserInfoTask().execute();
    }

    public void setSourceList(List<UserInfo> sourceList) {
        this.sourceList = sourceList;
    }

    public List<UserInfo> getSourceList(){
        return sourceList;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        final ViewHolder holder;
        UserInfo item = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_add_contact, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if(shouldLoadNextPage(mDataList, position)) {
//            loadedPage++;
//            updateRequestParams(mParams);
//            loadNextPage();
//            mListView.onFooterRefreshBegin();
//            LogUtils.e("load page: " + loadedPage);
//        }

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

        if (mode == ContactMode.MODE_SELECTION) {
            if (alreadyInGroupFriends != null && isInGroup(alreadyInGroupFriends, item)) {
                holder.selectContactCheckbox.setChecked(true);
            } else {
                holder.selectContactCheckbox.setChecked(checkBoxStatus.get(item.getU_id()));
            }
        } else if (mode == ContactMode.MODE_VIEW) {
            holder.selectContactCheckbox.setVisibility(View.GONE);
        }


        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.sortKey.setVisibility(View.VISIBLE);
            holder.sortKey.setText(item.getSortKey());
        } else {
            holder.sortKey.setVisibility(View.GONE);
        }

        // make call button
        if (mode.equals(ContactMode.MODE_VIEW) && !TextUtils.isEmpty(item.getU_phone())) {
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
        if (mode.equals(ContactMode.MODE_VIEW)) {
            holder.startChat.setVisibility(View.VISIBLE);
            holder.startChat.setTag(item);
            holder.startChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo user = (UserInfo) v.getTag();
//                    Toast.makeText(mContext, "Start chat to :" + user.getU_name(), Toast.LENGTH_LONG).show();

                    Intent it = new Intent(mContext, GroupChatActivity.class);
                    it.putExtra(Constants.EXTRA_IM_YGROUPID, user.getY_voip());
                    it.putExtra(Constants.EXTRA_IM_GROUP_NAME, user.getU_name());
                    it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊
                    it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) user);
                    it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);

                    mContext.startActivity(it);
                }
            });
        }

        return convertView;
    }

    @Override
    public List<UserInfo> parseList(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        List<UserInfo> userInfoList = null;
        if (jsonObject.getString("ulist") != null) {
            userInfoList = JSON.parseArray(jsonObject.getString("ulist"), UserInfo.class);

            if (userInfoList != null) {
                UserInfo mySelf = null;
                int i = 0;
                for (UserInfo item : userInfoList) {
                    item.setIdentity(Utils.getIdentity(mSession));
                    item.updateSortKey();
                    if (item.getU_id().equals(mSession.getUid())) {
                        mySelf = item;
                        mSession.setMySelfInfo(mySelf);
                    }
                }
                userInfoList.remove(mySelf);

                while (i < userInfoList.size()) {
                    if (userInfoList.get(i).getU_id().equals("1425")) {
                        userInfoList.remove(userInfoList.get(i));
                    }
                    i++;
                }

                Collections.sort(userInfoList, pinyinComparator);
            }
        }
        final List<UserInfo> list = userInfoList;
        Runnable dbWorker = new Runnable() {
            @Override
            public void run() {
                dbHelper.getUserInfoTable().insert(list, Utils.getIdentity(mSession));
            }
        };
        new BackgroudTask().execute(dbWorker);
        if (userInfoList == null) {
            userInfoList = new ArrayList<UserInfo>();
        }
        return userInfoList;
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        Task.getAllUser(mParams, this, this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
    }

    // for test only
    @Override
    public void onErrorResponse(VolleyError error) {
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        mDataList.addAll(userInfoList);
        notifyDataSetChanged();
    }

    @Override
    public void loadNextPage() {
        Task.getAllUser(mParams, this, this);
    }

//    @Override
//    public void updateRequestParams(Map<String, String> params) {
//        params.put("pageNo", String.valueOf(loadedPage));
//    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mDataList.get(position).getSortKey().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).getSortKey();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    public void sortList(List dataList) {
        // 根据a-z进行排序源数据
        Collections.sort(dataList, pinyinComparator);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    public void filterData(String filterStr) {
        List<UserInfo> userDataList = new ArrayList<UserInfo>();

        if (TextUtils.isEmpty(filterStr)) {
            userDataList = sourceList;
            isLoading = false;
        } else {
            userDataList.clear();
            for (UserInfo userInfo : sourceList) {
                String name = userInfo.getU_name();
                if (name.indexOf(filterStr.toString().toLowerCase()) != -1 ||name.indexOf(filterStr.toString().toUpperCase()) != -1 ||name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString()) || characterParser.getSelling(name).toUpperCase().startsWith(filterStr.toString())) {
                    userDataList.add(userInfo);
                }
            }
            isLoading = true;
        }

        // 根据a-z进行排序
        sortList(userDataList);
        updateDataList(userDataList);
        notifyDataSetChanged();
    }

    @Override
    public void setList(List<UserInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            checkBoxStatus.put(data.get(i).getU_id(), false);
        }
        super.setList(data);
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

    private boolean isInGroup(List<UserInfo> list, UserInfo item) {
        int i = 0;
        for (; i < list.size(); i++) {
            if (item.getU_id().equals(list.get(i).getU_id())) {
                return true;
            }
        }
        return false;
    }

    private class GetUserInfoTask extends AsyncTask<Void, Void, List<UserInfo>> {

        @Override
        protected List<UserInfo> doInBackground(Void... params) {
            List<UserInfo> userInfoList;
            try {
                IMUserInfoTable table = ((BaseActivity) mContext).getDbHelper().getUserInfoTable();
                userInfoList = table.get(mSession.getListid(), Utils.getIdentity(mSession));
                Collections.sort(userInfoList, new PinyinComparator());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return userInfoList;
        }

        @Override
        protected void onPostExecute(List<UserInfo> userInfoList) {
            super.onPostExecute(userInfoList);
            if (userInfoList != null && userInfoList.size() > 0) {
                sourceList = userInfoList;
                Iterator<UserInfo> iterator = userInfoList.iterator();
                while (iterator.hasNext()) {
                    UserInfo next = iterator.next();
                    if (next != null && next.getU_id() != null && next.getU_id().equals(mSession.getUid())) {
                        iterator.remove();
                        mSession.setMySelfInfo(next);
                    }
                    if (next != null && next.getU_id() != null && next.getU_id().equals("1425")) {  // 过滤组织力小助手
                        iterator.remove();
                    }
                }
                setList(userInfoList);
            } else {
                loadNextPage();
            }
        }
    }
}
