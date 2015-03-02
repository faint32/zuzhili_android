package com.zuzhili.ui.fragment.im;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ContactListActivity extends BaseActivity implements Response.Listener<String>, Response.ErrorListener, BaseActivity.TimeToShowActionBarCallback {

    private String title = "返回";
    public static final String LIST_TITLE = "list.title";
    private ListView listView;
    private ContactAdapter adapter;
    private String flag;
    private HashMap<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        setCustomActionBarCallback(this);

        params = (HashMap<String, String>) getIntent().getSerializableExtra("params");
        flag = getIntent().getStringExtra("flag");
        title = getIntent().getStringExtra(LIST_TITLE);

        listView = (ListView) findViewById(R.id.contact_list);
        listView.setDivider(null);
        adapter = new ContactAdapter(ContactListActivity.this, listView, ImageCacheManager.getInstance().getImageLoader(), params);
        adapter.setOnRefreshListener();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo user = (UserInfo) parent.getAdapter().getItem(position);
                user = DBHelper.getInstance(ContactListActivity.this).getUserInfoTable().getUserByIds(String.valueOf(user.get_id()), user.getU_listid());
                if (user == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(ContactListActivity.this, CommonSpaceActivity.class);
                intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                startActivity(intent);
            }
        });

        sendRequest(this, this);
    }

    private void sendRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        if (flag.equals("api.follow")) {
            Task.queryMyLovers(params, listener, errorListener);
        } else if (flag.equals("api.be_follow")) {
            Task.queryMyFans(params, listener, errorListener);
        } else if (flag.equals("api.members")) {
            Task.queryGroupMembers(params, listener, errorListener);
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, title, false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return false;
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

    public class ContactAdapter extends ResultsAdapter<UserInfo> {

        private List<UserInfo> data = new ArrayList<UserInfo>();

        public ContactAdapter(Context context, ListView listView, ImageLoader imageLoader, HashMap<String, String> params) {
            super(context, listView, imageLoader, params);
        }

        public void setData(List<UserInfo> data) {
            if (data == null) {
                data = new ArrayList<UserInfo>();
            }
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public UserInfo getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            UserInfo item = (UserInfo) getItem(position);

            if (convertView == null) {
                convertView = ContactListActivity.this.getLayoutInflater().inflate(R.layout.listview_item_add_contact, parent, false);
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
                holder.userAvatarRequest = ImageCacheManager.getInstance().getImageLoader()
                        .get(TextUtil.processNullString(item.getU_icon()), ImageLoader.getImageListener(holder.userAvatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
            } catch (Exception e) {
                holder.userAvatar.setImageResource(R.drawable.default_user_head_small);
            }

            holder.name.setText(TextUtil.processNullString(item.getU_name()));

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
                        ContactListActivity.this.startActivity(intent);
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

                    user = DBHelper.getInstance(ContactListActivity.this).getUserInfoTable().
                            getUserByUid(user.getU_id(), user.getU_listid());

                    Intent it = new Intent(ContactListActivity.this, GroupChatActivity.class);
                    it.putExtra(Constants.EXTRA_IM_YGROUPID, user.getY_voip());
                    it.putExtra(Constants.EXTRA_IM_GROUP_NAME, user.getU_name());
                    it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");
                    it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) user);
                    it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
                    ContactListActivity.this.startActivity(it);
                }
            });

            return convertView;
        }

        @Override
        public List<UserInfo> parseList(String response) {
            return unserializable(response);
        }

        @Override
        public void loadNextPage() {

        }

        @Override
        public void onRefresh() {
            isPullOnRefreshEnd = false;
            sendRequest(this, this);
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        List<UserInfo> userList = unserializable(s);
        adapter.setData(userList);
    }

    private List<UserInfo> unserializable(String s) {
        List<UserInfo> userList = new ArrayList<UserInfo>();

        UserInfo info = null;

        JSONObject obj = JSON.parseObject(s);
        com.alibaba.fastjson.JSONArray array = null;
        if (flag.equals("api.members")) {
            array = obj.getJSONArray("members");
        } else {
            array = obj.getJSONArray("list");
        }
        Iterator it = array.iterator();
        while (it.hasNext()) {
            JSONObject user = (JSONObject) it.next();

            info = new UserInfo();
            info.set_id(user.getIntValue("id"));
            info.setU_id(user.getString("userid"));
            info.setU_listid(user.getString("listid"));
            info.setU_name(user.getString("name"));
            info.setU_icon(user.getString("userhead150"));
            info.setU_phone(user.getString("phone"));
            userList.add(info);
        }

        return userList;
    }
}
