package com.zuzhili.service;

import android.app.IntentService;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.im.BroadcastNotifier;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-7-18.
 */
public class GetIMDataIntentService extends IntentService implements Response.Listener<String>, Response.ErrorListener {

    private Session mSession;

    private DBHelper dbHelper;

    // Defines and instantiates an object for handling status updates.
    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    public GetIMDataIntentService() {
        super("getImDataIntentService");
        mSession = Session.get(this);
        dbHelper = DBHelper.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra(Constants.ACTION);
        if (action != null && action.equals(Task.ACTION_GET_ALL_USERS)) {
            // 获取所有社区聊天用户信息
            Task.getAllUser(buildRequestUserParams(), this, this);
        } else if (action != null && action.equals(Task.ACTION_GET_GROUPS)) {
            // 获取所有聊天群组信息
            Task.getGroups(buildRequestGroupsParams(), this, this);
        } else if (action != null && action.equals(Task.ACTION_GET_YTX_ACCOUNT)) {
            // 获取个人云通讯帐号相关信息
            Task.getYTXAccount(buildGetYTXAccountParams(), this, this);
        }
    }

    private HashMap<String, String> buildRequestUserParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("u_id", mSession.getUid());
            params.put("u_listid", mSession.getListid());
            params.put("client", "1");
        }
        return params;
    }

    private HashMap<String, String> buildRequestGroupsParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("u_id", mSession.getUid());
            params.put("u_listid", mSession.getListid());
            //params.put("g_want", "0");
        }
        return params;
    }

    private HashMap<String, String> buildGetYTXAccountParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("u_listid", "0");  // no meaning, just a useless argument
            params.put("u_id", mSession.getUid());
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        mBroadcaster.broadcastIntentWithState(Constants.PULL_DATA_FAILED);
    }

    @Override
    public void onResponse(String result) {
        final JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getString("user") != null) {
            JSONObject user = JSON.parseObject(jsonObject.getString("user"));
            if (user != null) {
                mSession.setVoipId(user.getString("y_voip"));
                mSession.setVoipPassword(user.getString("y_voippass"));
                mSession.setSubAccount(user.getString("y_subid"));
                mSession.setSubToken(user.getString("y_subpass"));
            }
        } else if (jsonObject.getString("ulist") != null) {

        } else if (jsonObject.getString("glist") != null){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final List<GroupInfo> groupInfoList = JSON.parseArray(jsonObject.getString("glist"), GroupInfo.class);
                        if (groupInfoList != null) {
                            for (GroupInfo g : groupInfoList) {
                                g.setIdentityId(Utils.getIdentity(Session.get(getApplicationContext())));
                            }
                            mSession.setGroupInfoList(groupInfoList);
                            dbHelper.getGroupInfoTable().insertIMGroupInfos(groupInfoList);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mBroadcaster.broadcastIntentWithState(Constants.PULL_IM_GROUPS_FINISHED);
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<UserInfo> userInfoList = JSON.parseArray(jsonObject.getString("ulist") == null ? "[]" : jsonObject.getString("ulist"), UserInfo.class);
                    if (userInfoList != null) {
                        UserInfo mySelf = null;
                        for (UserInfo item : userInfoList) {
                            updateSortKey(item);
                            item.setIdentity(Utils.getIdentity(mSession));
                            if (item.getU_id().equals(mSession.getUid())) {
                                mySelf = item;
                                mSession.setMySelfInfo(mySelf);
                                mSession.setUserhead(mySelf.getU_icon());
                            }
                        }
                    }
                    dbHelper.getUserInfoTable().insert(userInfoList, Utils.getIdentity(mSession));
                    mBroadcaster.broadcastIntentWithState(Constants.PULL_IM_USERS_FINISHED);
                }
            }).start();

        }
    }

    private void updateSortKey(UserInfo friendInfo) {
        //汉字转换成拼音
        String pinyin = CharacterParser.getInstance().getSelling(TextUtil.processNullString(friendInfo.getU_name()));
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            friendInfo.setSortKey(sortString.toUpperCase());
        } else {
            friendInfo.setSortKey("#");
        }
    }
}
