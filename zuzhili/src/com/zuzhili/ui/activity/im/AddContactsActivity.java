package com.zuzhili.ui.activity.im;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.controller.FriendFaceAdapter;
import com.zuzhili.controller.IMContactAdapter;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.im.ContactsFragment;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.SideBar;
import com.zuzhili.ui.views.gridview.TwoWayGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kj on 14-6-24.
 */
public class AddContactsActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback,BaseActivity.HandleProgressBarVisibilityCallback, Response.Listener<String>, Response.ErrorListener {

    private IMContactAdapter contactAdapter;

    private FriendFaceAdapter friendFaceAdapter;

    @ViewInject(R.id.sidebar)
    private SideBar sideBar;

    @ViewInject(R.id.txt_alphabetic_hint)
    private TextView alphabeticHint;

    @ViewInject(R.id.listView)
    protected PullRefreshListView mListView;

    @ViewInject(R.id.gridView)
    private TwoWayGridView gridView;

    @ViewInject(R.id.btn_confirm)
    private Button confirmBtn;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

    private ProgressDialog progressDialog;

    private String groupId;

    private String y_groupId;

    private ArrayList<UserInfo> alreadyInChatRoomFriends;

    private int msgType;
    private String content;

    /**
     * 来自页面
     */
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_add_contact);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        update();
    }

    @SuppressLint("ResourceAsColor")
    private void update() {
        if (getIntent() != null && getIntent().getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS) != null) {
            alreadyInChatRoomFriends = getIntent().getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
        }
        if (getIntent() != null && getIntent().getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE) != null) {
            from = getIntent().getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE);
        }
        if (getIntent() != null && getIntent().getStringExtra(Constants.EXTRA_IM_GROUPID) != null) {
            groupId = getIntent().getStringExtra(Constants.EXTRA_IM_GROUPID);
        }

        progressBar.setVisibility(View.VISIBLE);
        sideBar.setVisibility(View.VISIBLE);
        sideBar.setTextView(alphabeticHint);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

        if (contactAdapter == null) {
            if (alreadyInChatRoomFriends != null && alreadyInChatRoomFriends.size() > 0) {
                contactAdapter = new IMContactAdapter(this
                        , mListView
                        , ImageCacheManager.getInstance().getImageLoader()
                        , mSession
                        , alreadyInChatRoomFriends
                        , buildRequestParams(1)
                        , this, ContactsFragment.ContactMode.MODE_SELECTION, false);
            } else {
                contactAdapter = new IMContactAdapter(this
                        , mListView
                        , ImageCacheManager.getInstance().getImageLoader()
                        , mSession
                        , buildRequestParams(1)
                        , this, ContactsFragment.ContactMode.MODE_SELECTION, false);
            }

            contactAdapter.setOnContactSelectedListener(new IMContactAdapter.OnContactSelectedListener() {
                @Override
                public void onContactSelected(UserInfo userInfo) {
                    friendFaceAdapter.addOrRemoveItem(userInfo);

                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
                            gridView.smoothScrollBy(1, 1);
                            gridView.smoothScrollToPosition(friendFaceAdapter.getCount());
                        }
                    });
                    showCustomActionBar();
                }

                //选择群聊
                @Override
                public void onHeadViewClicked() {
                    Intent it = new Intent(AddContactsActivity.this, GroupsActivity.class);
                    if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
                        it.putExtra(Constants.MSG_FORWORD,content);
                        it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
                    }else {
                        it.putExtra(Constants.MSG_FORWORD,content);
                        it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
                    }
                    startActivity(it);
                }
            });

            if (getIntent() != null && getIntent().getParcelableArrayListExtra(Constants.EXTRA_IM_ALL_USER) != null) {
                progressBar.setVisibility(View.GONE);
                List<UserInfo> userInfoList = getIntent().getParcelableArrayListExtra(Constants.EXTRA_IM_ALL_USER);
                contactAdapter.setList(userInfoList);
                contactAdapter.setSourceList(userInfoList);
            }

            contactAdapter.setListView(mListView);
            contactAdapter.setOnRefreshListener();

            TextView headView = new TextView(this);
            headView.setText(getString(R.string.group_chat));
            headView.setCompoundDrawablePadding(DensityUtil.dip2px(this, 15));
            headView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_select_group, 0, 0, 0);
            headView.setTextColor(getResources().getColor(R.color.text_black));
            headView.setTextSize(15);

//            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 50));
//            headView.setLayoutParams(params);
//            headView.setGravity(Gravity.CENTER_VERTICAL);
//
//            mListView.addHeaderView(headView);
            mListView.setDividerHeight(DensityUtil.dip2px(this, 1));
            mListView.setAdapter(contactAdapter);
            mListView.setOnItemClickListener(contactAdapter);
        }
        if (friendFaceAdapter == null) {
            friendFaceAdapter = new FriendFaceAdapter(this, ImageCacheManager.getInstance().getImageLoader());
            gridView.setAdapter(friendFaceAdapter);
            friendFaceAdapter.notifyDataSetChanged();
        }

        content = getIntent().getStringExtra(Constants.MSG_FORWORD);
        msgType = getIntent().getIntExtra(Constants.MSG_TYPE, 0);


        //confirmBtn.setEnabled(false);
        //confirmBtn.setText("确认(0)");
        //confirmBtn.setTextColor(getResources().getColor(R.color.white));
//        confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (friendFaceAdapter.getUserInfoList().size() > 1) {    // 群聊
//
//                    if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {
//                        //加人
//                        Task.inviteJoinGroup(buildRequestParams(3), AddContactsActivity.this, AddContactsActivity.this);
//                    } else {
//                        // 创建聊天室并加人
//                        Task.getCreateGroup(buildRequestParams(2), AddContactsActivity.this, AddContactsActivity.this);
//                        progressDialog = new ProgressDialog(AddContactsActivity.this);
//                        progressDialog.setIndeterminate(true);
//                        progressDialog.setMessage(getString(R.string.im_creating_group_chat));
//                        progressDialog.show();
//                    }
//
//                } else if (friendFaceAdapter.getUserInfoList().size() == 1) {
//
//                    if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) { // 加人
//                        Task.inviteJoinGroup(buildRequestParams(3), AddContactsActivity.this, AddContactsActivity.this);
//                    } else {    // 单聊
//                        Intent it = new Intent(AddContactsActivity.this, GroupChatActivity.class);
//                        it.putExtra(Constants.EXTRA_IM_YGROUPID, friendFaceAdapter.getUserInfoList().get(0).getY_voip());
//                        it.putExtra(Constants.EXTRA_IM_GROUP_NAME, friendFaceAdapter.getUserInfoList().get(0).getU_name());
//                        it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊
//                        it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) friendFaceAdapter.getUserInfoList().get(0));
//                        it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
//
//                        if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
//                            it.putExtra(Constants.MSG_FORWORD,content);
//                            it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
//                        }else {
//                            it.putExtra(Constants.MSG_FORWORD,content);
//                            it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
//                        }
//                        startActivityForResult(it, 0);
//                    }
//
//                } else {
//                    confirmBtn.setEnabled(false);
//                }
//            }
//        });
    }

//    TODO 查询
//    private void setSearchView() {
//        getSearchView().setOnQueryTextListener(new OnQueryContactListener());
//        if (getSearchView() != null) {
//            CustomSearchView searchView = getSearchView();
//            searchView.setOnSearchClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setTitleInvisible();
//                }
//            });
//
//            searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
//                @Override
//                public boolean onClose() {
//                    setTitleVisiable();
//                    return false;
//                }
//            });
//
//        }
//    }

    /**
     * 1 全部成员  2 创建聊天室 3加人
     * @param mode
     * @return
     */
    private HashMap<String, String> buildRequestParams(int mode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            switch (mode) {
                case 1:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("client", "1");
//                    params.put("pageNo", "0");
//                    params.put("pageSize", String.valueOf(Constants.PAGE_SIZE));
                    break;
                case 2:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    List<UserInfo> temp=new ArrayList<UserInfo>();
                    temp.addAll(friendFaceAdapter.getUserInfoList());
                    temp.add(0,mSession.getMySelfInfo());
                    params.put("g_name", getTempGroupChatRoomName(temp));
                    params.put("g_type", "2");
                    params.put("g_permission", "0");
                    params.put("g_declared", "");
                    break;
                case 3:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("groupid", groupId);
                    StringBuffer buffer = new StringBuffer();
                    if (friendFaceAdapter != null && friendFaceAdapter.getUserInfoList() != null) {
                        ArrayList<UserInfo> userInfoList = friendFaceAdapter.getUserInfoList();
                        for (UserInfo item : userInfoList) {
                            buffer.append(item.getU_id() + ",");
                        }
                        buffer.deleteCharAt(buffer.length() - 1);
                    }
                    params.put("members", buffer.toString());
                    params.put("confirm", "1");
                    break;
            }
        }
        return params;
    }


    private String getGroupChatFriends(List<UserInfo> userInfoList) {
        StringBuilder builder = new StringBuilder();
        for (UserInfo item : userInfoList) {
            builder.append(item.getU_id()).append(Constants.SYMBOL_COMMA);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String getTempGroupChatRoomName(List<UserInfo> userInfoList) {
        StringBuilder builder = new StringBuilder();
        int i=0;
        if (userInfoList != null) {
            for (UserInfo item : userInfoList) {
                if ((i++) < 3) {
                    builder.append(item.getU_name()).append(Constants.SYMBOL_COMMA_CHN);
                } else {
                    break;
                }
            }
            if(builder.length()>50){
                builder.deleteCharAt(50);
            }else {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    @Override
    public boolean showCustomActionBar() {
        if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS)) {
            initActionBar(R.drawable.icon_back, "确认(" + friendFaceAdapter.getUserInfoList().size() + ")", getString(R.string.title_create_chat_room));
        } else {
            initActionBar(R.drawable.icon_back, "确认(" + friendFaceAdapter.getUserInfoList().size() + ")", getString(R.string.create_new_chat));
        }

        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        Intent it = new Intent(AddContactsActivity.this, ChatRoomSettingsActivity.class);
        it.putExtra(Constants.EXTRA_IS_CHANGE,true);
        setResult(RESULT_OK, it);

        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            performClickOnLeft();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean performClickOnRight() {
        if (friendFaceAdapter.getUserInfoList().size() > 1) {    // 群聊

            if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {
                //加人
                Task.inviteJoinGroup(buildRequestParams(3), AddContactsActivity.this, AddContactsActivity.this);
            } else {
                // 创建聊天室并加人
                Task.getCreateGroup(buildRequestParams(2), AddContactsActivity.this, AddContactsActivity.this);
                progressDialog = new ProgressDialog(AddContactsActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.im_creating_group_chat));
                progressDialog.show();
            }

        } else if (friendFaceAdapter.getUserInfoList().size() == 1) {

            if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) { // 加人
                Task.inviteJoinGroup(buildRequestParams(3), AddContactsActivity.this, AddContactsActivity.this);
            } else {    // 单聊
                Intent it = new Intent(AddContactsActivity.this, GroupChatActivity.class);
                it.putExtra(Constants.EXTRA_IM_YGROUPID, friendFaceAdapter.getUserInfoList().get(0).getY_voip());
                it.putExtra(Constants.EXTRA_IM_GROUP_NAME, friendFaceAdapter.getUserInfoList().get(0).getU_name());
                it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊
                it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) friendFaceAdapter.getUserInfoList().get(0));
                it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);

                if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
                }else {
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
                }
                startActivity(it);
                finish();
            }

        } else {
            confirmBtn.setEnabled(false);
        }
        return super.performClickOnRight();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.i("volleyError"+volleyError.toString());
        progressBar.setVisibility(View.GONE);
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if (volleyError instanceof TimeoutError) {
            Utils.makeEventToast(this, getString(R.string.timeout_create_group_failed), false);
        } else if (volleyError instanceof BusinessError) {
            Utils.makeEventToast(this, getString(R.string.hint_create_group_failed), false);
        }
    }

    @Override
    public void onResponse(String s) {
        progressBar.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getString("groupId") != null && jsonObject.getString("y_gid") != null ) {
            groupId = jsonObject.getString("groupId");
            y_groupId = jsonObject.getString("y_gid");
            Task.inviteJoinGroup(buildRequestParams(3), AddContactsActivity.this, AddContactsActivity.this);
        } else {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            ArrayList<UserInfo> contacts = new ArrayList<UserInfo>();
            contacts.addAll(friendFaceAdapter.getUserInfoList());

            if (from != null && from.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {
                Intent it = new Intent(this, ChatRoomSettingsActivity.class);
                it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, contacts);
                setResult(RESULT_OK, it);
                finish();
            }else {
                // 加上自己
                contacts.add(0, mSession.getMySelfInfo());

                Intent it = new Intent(this, GroupChatActivity.class);
                it.putExtra(Constants.EXTRA_IM_GROUPID, groupId);
                it.putExtra(Constants.EXTRA_IM_YGROUPID, y_groupId);
                it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, String.valueOf(contacts.size()));
                it.putExtra(Constants.EXTRA_IM_GROUP_NAME, getTempGroupChatRoomName(contacts));
                it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
                it.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, "0");
                it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, contacts);
                if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
                }else {
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
                }
                startActivity(it);
                finish();
            }
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    public class OnQueryContactListener implements android.support.v7.widget.SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            contactAdapter.filterData(s.trim());
            return false;
        }

    }

    private void updateSortKey(UserInfo userInfo) {
        //汉字转换成拼音
        String pinyin = CharacterParser.getInstance().getSelling(TextUtil.processNullString(userInfo.getU_name()));
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            userInfo.setSortKey(sortString.toUpperCase());
        } else {
            userInfo.setSortKey("#");
        }
    }

}
