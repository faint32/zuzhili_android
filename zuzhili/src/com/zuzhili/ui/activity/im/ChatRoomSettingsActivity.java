package com.zuzhili.ui.activity.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.DragPersonAdapter;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.gridview.HFGridView;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-4-27.
 */
public class ChatRoomSettingsActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback,
        DragPersonAdapter.OnRemoveUserListener,
        Response.Listener<String>, Response.ErrorListener {

    @ViewInject(R.id.gridView)
    private HFGridView gridView;

    @ViewInject(R.id.switcher)
    private Switch switcher;

    @ViewInject(R.id.txt_group_name)
    private TextView groupName;

    @ViewInject(R.id.tv_clear_chat)
    private TextView clearchat;

    /*@ViewInject(R.id.txt_group_declared)
    private EditText groupDeclared;*/

    @ViewInject(R.id.btn_dissolve_group)
    private Button dissolveGroupBtn;

    private boolean groupChatFlag;

    private ArrayList<UserInfo> friendInfoList;

    private UserInfo oppositeSide;

    private String groupId;

    private String gName;

    private String chatRoomType;

    private DragPersonAdapter adapter;

    private boolean needGetGroupUserFlag;

    private UserInfo addUserFakeFriendInfo;

    private UserInfo removeUserFakeFriendInfo;

    private GroupInfo group;

    private boolean isReceive=true;

    private SharedPreferences mPreference;

    private String name;

    private boolean shouldClear;

    /**
     * 云通讯群组id
     */
    private String y_groupId;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.layout_expandable_gridview);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);
        initData();
        friendInfoList = new ArrayList<UserInfo>();

        if (oppositeSide != null) {
            friendInfoList.add(oppositeSide);
        }
        if(group!=null){
            if (!needGetGroupUserFlag && friendInfoList != null && friendInfoList.size()>2 && "0".equals(group.getZ_type())) {
                initAddAndRemoveUser();
            } else {
                //获取组内成员
                Task.queryMember(buildRequestParams(1), this, this);
            }
        }

        init();
    }


    private void init(){
        adapter = new DragPersonAdapter(this, friendInfoList, mSession, groupId);
        adapter.setOnRemoveUserListener(this);
        gridView.setNumColumns(4);
        gridView.addFooterView(getFooter());
        gridView.setListener(new HFGridView.HFGridViewListener() {
            @Override
            public void readyToDisposeItems() {
                gridView.setAdapter(adapter);
            }
        });
        dissolveGroupBtn.setOnClickListener(new DissolveGroupBtnOnClickListener());


        if(group!=null && !TextUtils.isEmpty(group.getG_name())){
            groupName.setText(group.getG_name());
        }else{
            groupName.setText(gName);
        }

        if(groupName.getText().toString().indexOf(Constants.SYMBOL_COMMA_CHN)>=0) {
            groupName.setText(R.string.notitle);
        }

        if (!isOrgGroup(group)) {
            groupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomSettingsActivity.this);
                    builder.setTitle("群聊名称");
                    final EditText input = new EditText(ChatRoomSettingsActivity.this);

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(input.getText().length()>50){
                                Toast.makeText(getBaseContext(),R.string.rule, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                  input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                    input.setBackgroundResource(0);
                    if(groupName.getText().toString().equals("暂未设置")) {
                        input.setText("");
                    }else {
                        input.setText(groupName.getText());
                    }
                    builder.setView(input);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            name = input.getText().toString().trim();
                            if(name.length() > 45){
                                name=name.substring(0, 45);
                            }
                            if (!TextUtils.isEmpty(name)) {
                                final HashMap<String, String> params = new HashMap<String, String>();
                                if (mSession != null) {
                                    params.put("u_id", mSession.getUid());
                                    params.put("u_listid", mSession.getListid());
                                    params.put("groupId", groupId);
                                    params.put("name", name);
                                    params.put("permission", "0");
                                    params.put("declared", "");
                                }
                                Task.modifyGroup(params, ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
                                groupName.setText(name);
                                if (group != null) {
                                    group.setG_name(name);
                                }
//                                try {
//                                    dbHelper.getGroupInfoTable().updateGroupInfo(group);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }

                            } else {
                                Toast.makeText(getBaseContext(), R.string.nothing, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

//                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                    textView.setTextColor(getResources().getColor(R.color.light_blue));
                }
            });
        }

        if (mPreference == null) {
            mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        }
        isReceive=mPreference.getBoolean("isReceiveMsg",true);

        //是否接受消息
        switcher.setChecked(isReceive);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    isReceive=true;
                    Task.setGroupMsg_user(buildSRequestParams("0"),ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
                } else {
                    isReceive=false;
                    Task.setGroupMsg_user(buildSRequestParams("1"),ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
                }
                SharedPreferences.Editor edit = mPreference.edit();
                edit.putBoolean("isReceiveMsg",isReceive);
                edit.commit();
            }
        });

        clearchat = (TextView) findViewById(R.id.tv_clear_chat);
        clearchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomSettingsActivity.this);

                builder.setTitle("确认删除聊天记录吗?");
                builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        shouldClear=true;
                        ITask iTask = new ITask(TaskKey.TASK_KEY_DEL_MESSAGE);
                        addTask(iTask);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void handleTaskBackGround(ITask iTask) {
        super.handleTaskBackGround(iTask);
        int key = iTask.getKey();
        if (key == TaskKey.TASK_KEY_DEL_MESSAGE) {
            try {
                getDbHelper().getMessageTable().deleteIMMessage(y_groupId, mSession.getListid());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 1 获取组内成员 2 删群 3 退群 4 查询群组
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
                    params.put("groupid", groupId);
                    break;
                case 2:
                    params.put("u_id", mSession.getUid());
                    params.put("groupid", groupId);
                    break;
                case 3:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("groupid", groupId);
                    break;
                case 4:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("groupid", groupId);
                    break;

            }
        }
        return params;
    }

    private HashMap<String, String> buildSRequestParams(String rule) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("u_id", mSession.getUid());
            params.put("u_listid", mSession.getListid());
            params.put("groupid", groupId);
            params.put("rule", rule);
        }
        return params;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                boolean notChange=data.getBooleanExtra(Constants.EXTRA_IS_CHANGE,false);
                if(notChange){
                    if("0".equals(group.getZ_type())){
                        initAddAndRemoveUser();
                    }
                    adapter.setList(friendInfoList);
                    adapter.notifyDataSetChanged();
                    return;
                }
                // 加人或者踢人后，更新用户列表
                ArrayList<UserInfo> addedUser = data.getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);

                if (friendInfoList != null && addedUser != null && addedUser.size() > 0) {
                    friendInfoList.addAll(friendInfoList.size(), addedUser);

                    String title = getString(R.string.title_group_chat_settings_title);
                    initActionBar(R.drawable.icon_back, 0, title, false);
                    if("0".equals(group.getZ_type())){
                        initAddAndRemoveUser();
                    }
                    adapter.setList(friendInfoList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initData();
        String title = getString(R.string.title_group_chat_settings_title);
        initActionBar(R.drawable.icon_back, 0, title, false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        if (adapter != null && adapter.getFriendInfoList() != null && adapter.getFriendInfoList().size() > 2) {
            friendInfoList = (ArrayList<UserInfo>) adapter.getFriendInfoList();
            if(friendInfoList.size()>2 && isGroupAdmin(group) && "0".equals(group.getZ_type())){
                friendInfoList.remove(friendInfoList.size() - 1);
                friendInfoList.remove(friendInfoList.size() - 1);
            }

            Intent it=new Intent();
//            it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS,friendInfoList);
            it.putExtra(Constants.EXTRA_PARCELABLE_CONTACTS,String.valueOf(friendInfoList.size()));
            String text = groupName.getText().toString();
            if(text.equals(getString(R.string.notitle))){
                text=gName;
            }
            it.putExtra(Constants.EXTRA_IM_GROUP_NAME,text);
            if(shouldClear){
                it.putExtra(Constants.EXTRA_IM_GROUP_CLEAR,true);
                shouldClear=false;
            }

            setResult(RESULT_OK, it);
            if (group != null) {
                try {
                    group.setG_ucount(String.valueOf(friendInfoList.size()));
                    dbHelper.getGroupInfoTable().updateGroupInfo(group);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            friendInfoList.clear();
            friendInfoList = null;

        }
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return performClickOnLeft();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRemoveUser(int chatRoomNum) {
        String title = getString(R.string.title_group_chat_settings_title);
//        title += "(" + chatRoomNum + "人)";
        initActionBar(R.drawable.icon_back, 0, title, false);
    }


    /**
     * 删除群后需要更新群组表
     */
    private class DissolveGroupBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Task.getGroup(buildRequestParams(4),ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomSettingsActivity.this);
            builder.setMessage(getString(R.string.hint_dissolve_group));

            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 删群 或者退出
                    try {
                        dbHelper.getGroupInfoTable().deleteGroupByGroupId(group.getId());
                        dbHelper.getMessageTable().deleteIMMessage(group.getY_gid(), group.getU_listid());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String c_id = group.getCreatorid();
                    if (c_id != null && c_id.equals(mSession.getUid())) {
                        Task.deleteGroup(buildRequestParams(2), ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
                    } else {
                        Task.toOutGroup(buildRequestParams(3), ChatRoomSettingsActivity.this, ChatRoomSettingsActivity.this);
                    }
                    setResult(Activity.RESULT_OK, null);
                    finish();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
    }

    private void initData() {
        group = (GroupInfo) getIntent().getSerializableExtra(Constants.EXTRA_IM_GROUP);
        groupChatFlag = getIntent().getBooleanExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
        friendInfoList = getIntent().getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
        oppositeSide = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_IM_CONTACT);
        groupId = getIntent().getStringExtra(Constants.EXTRA_IM_GROUPID);
        y_groupId = getIntent().getStringExtra(Constants.EXTRA_IM_YGROUPID);
        gName = getIntent().getStringExtra(Constants.EXTRA_IM_GROUPNNAME);
        needGetGroupUserFlag = getIntent().getBooleanExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, false);
        chatRoomType = getIntent().getStringExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE);

        addUserFakeFriendInfo = new UserInfo();
        removeUserFakeFriendInfo = new UserInfo();
        addUserFakeFriendInfo.setUserFlag(1);
        removeUserFakeFriendInfo.setUserFlag(2);
    }

    private View getFooter() {
        View footer = getLayoutInflater().inflate(R.layout.view_footer_chat_room_settings, null);
        groupName = (TextView) footer.findViewById(R.id.txt_group_name);
        switcher=(Switch) footer.findViewById(R.id.switcher);
        dissolveGroupBtn = (Button) footer.findViewById(R.id.btn_dissolve_group);
        if (isOrgGroup(group)) { // 会议室不能解散群
            dissolveGroupBtn.setVisibility(View.GONE);
        }
        return footer;
    }

    private void initAddAndRemoveUser() {
        if (isGroupAdmin(group)) {
            friendInfoList.add(friendInfoList.size(), addUserFakeFriendInfo);
            friendInfoList.add(friendInfoList.size(), removeUserFakeFriendInfo);
        }
    }

    // 是否是群组创建者
    private boolean isGroupAdmin(GroupInfo group) {
        if (group == null)
            return false;
        if (mSession.getMySelfInfo().getU_id().equals(group.getCreatorid()))
            return true;
        return  false;
    }

    // 是否是组织架构内的会议室
    private boolean isOrgGroup(GroupInfo group) {
        if (group == null)
            return false;
        if (group.getZ_type() != null) {
            if (group.getZ_type().equals("0")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isContainsFakeUser(List<UserInfo> list) {
        if (list != null && list.size()>2) {
            for (UserInfo item : list) {
                if (item.getUserFlag() == 1 || item.getUserFlag() == 2) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.i("volleyError" + volleyError.toString());
        // todo: 没有查询到群成员也应该做处理。
    }

    @Override
    public void onResponse(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        //查询成员
        if (jsonObject.getString("u_list") != null) {
            friendInfoList = (ArrayList<UserInfo>) JSON.parseArray(jsonObject.getString("u_list"), UserInfo.class);
            if(friendInfoList!=null){
                if("0".equals(group.getZ_type())){
                    initAddAndRemoveUser();
                }
                adapter.setList(friendInfoList);
            }

        }else if(jsonObject.getString("group") != null){
            group=JSON.parseObject(jsonObject.getString("group"),GroupInfo.class);
        }else {

        }
    }
}