package com.zuzhili.ui.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.utility.BackgroudTask;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.IMGroupAdapter;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-4-22.
 * 群聊 kj
 */
public class GroupsActivity extends BaseActivity implements AdapterView.OnItemClickListener, BaseActivity.TimeToShowActionBarCallback, Response.Listener<String>, Response.ErrorListener {

    private static final int REQ_CODE_GROUP_CHAT = 0;

    @ViewInject(R.id.listView)
    private PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private List<GroupInfo> list;

    List<GroupInfo> groupInfoList;

    private IMGroupAdapter adapter;
    private int msgType;
    private String content;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.listview_layout);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    public void update() {
        content = getIntent().getStringExtra(Constants.MSG_FORWORD);
        msgType = getIntent().getIntExtra(Constants.MSG_TYPE, 0);
        progressBar.setVisibility(View.VISIBLE);

        if(adapter == null) {
            adapter = new IMGroupAdapter(this
                    , listView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , msgType
                    , content);
            Task.getGroups(buildRequestParams(), this, this);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        adapter.setListView(listView);
        adapter.setOnRefreshListener();
        listView.setDividerHeight(DensityUtil.dip2px(this, 1));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (getIntent().getBooleanExtra(Constants.EXTRA_FINISH_ACTIVITY, false)) {
            String groupId=getIntent().getStringExtra("groupId");
            list = adapter.getDataList();
            if(list!=null){
                GroupInfo groupInfo = mSession.getGroupInfoById(groupId);
                if(list.contains(groupInfo)){
                    list.remove(groupInfo);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent it = new Intent(this, GroupChatActivity.class);
//        it.putExtra(Constants.EXTRA_IM_GROUP, (GroupInfo) parent.getAdapter().getItem(position));
//        it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, ((GroupInfo) parent.getAdapter().getItem(position)).getG_ucount());
//        it.putExtra(Constants.EXTRA_IM_GROUPID, ((GroupInfo) parent.getAdapter().getItem(position)).getId());
//        it.putExtra(Constants.EXTRA_IM_YGROUPID, ((GroupInfo) parent.getAdapter().getItem(position)).getY_gid());
//        it.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
//        it.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, ((GroupInfo) parent.getAdapter().getItem(position)).getG_type());
//        it.putExtra(Constants.EXTRA_IM_GROUP_NAME, ((GroupInfo) parent.getAdapter().getItem(position)).getG_name());
//        it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
//        startActivityForResult(it, REQ_CODE_GROUP_CHAT);

        GroupInfo info = (GroupInfo) parent.getAdapter().getItem(position);

        Intent intent = new Intent(this, ChatRoomSettingsActivity.class);
        intent.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, info.getG_type());
        intent.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
        intent.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) info);
        intent.putExtra(Constants.EXTRA_IM_GROUPID, info.getId());
        intent.putExtra(Constants.EXTRA_IM_YGROUPID, info.getY_gid());
        intent.putExtra(Constants.EXTRA_IM_GROUPNNAME, info.getG_name());
        intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GROUP_CHAT) {
            if (data != null) {
                if (resultCode == RESULT_OK && data.getBooleanExtra(Constants.EXTRA_FINISH_ACTIVITY, false)) {
                    String groupId=data.getStringExtra("groupId");
                    list = adapter.getDataList();
                    if(list!=null){
                        GroupInfo groupInfo = mSession.getGroupInfoById(groupId);
                        if(list.contains(groupInfo)){
                            list.remove(groupInfo);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else if (resultCode == RESULT_OK  && data.getStringExtra(Constants.EXTRA_IM_GROUPID) != null) {
                    String groupId = data.getStringExtra(Constants.EXTRA_IM_GROUPID);
                    String groupName = data.getStringExtra(Constants.EXTRA_IM_GROUP_NAME);
                    //&& data.getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS) != null
//                    ArrayList<UserInfo> friendList = data.getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
                    String gSize=data.getStringExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
                    list = adapter.getDataList();
                    int position = findGroupInfoPositionById(list, groupId);
                    if(!TextUtils.isEmpty(gSize)){
                        list.get(position).setG_ucount(gSize);
                    }
                    if (position != -1) {
                        list.get(position).setG_name(groupName);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, getString(R.string.select_group), false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    public HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("u_id", mSession.getUid());
            params.put("u_listid", mSession.getListid());
            //params.put("g_want", "0");
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.i("volleyError"+volleyError.toString());
    }

    @Override
    public void onResponse(String s) {
        try {
            JSONObject jsonObject = JSON.parseObject(s);
            if(jsonObject.getString("glist") != null){
                groupInfoList = JSON.parseArray(jsonObject.getString("glist"), GroupInfo.class);
                if (groupInfoList != null) {
                    progressBar.setVisibility(View.GONE);
                    Collections.sort(groupInfoList, new Comparator<GroupInfo>() {
                        @Override
                        public int compare(GroupInfo lhs, GroupInfo rhs) {
                            if (Integer.valueOf(lhs.getZ_type()) > 0 && Integer.valueOf(rhs.getZ_type()) > 0) {
                                return 0;
                            } else if (Integer.valueOf(lhs.getZ_type()) == 0 && Integer.valueOf(rhs.getZ_type()) > 0) {
                                return 1;
                            } else if (Integer.valueOf(lhs.getZ_type()) > 0 && Integer.valueOf(rhs.getZ_type()) == 0) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    for (GroupInfo g : groupInfoList) {
                        g.setIdentityId(Utils.getIdentity(Session.get(getApplicationContext())));
                    }
                    mSession.setGroupInfoList(groupInfoList);
                    adapter.setList(groupInfoList);

                    Runnable dbWorker = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dbHelper.getGroupInfoTable().insertIMGroupInfos(groupInfoList);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new BackgroudTask().execute(dbWorker);
                }
            }
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
        }
    }


    private int findGroupInfoPositionById(List<GroupInfo> list, String groupId) {
        if (list != null && groupId != null) {
            for (int i = 0; i < list.size(); i++) {
                GroupInfo groupInfo = list.get(i);
                if (groupId.equals(groupInfo.getId())) {
                    return i;
                }
            }
        } else {
            return -1;
        }
        return -1;
    }
}
