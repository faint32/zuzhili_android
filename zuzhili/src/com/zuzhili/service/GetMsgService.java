package com.zuzhili.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.MainSocket;
import com.zuzhili.bussiness.socket.OnReceiveDataListener;
import com.zuzhili.bussiness.socket.RetrieveIMSessionTask;
import com.zuzhili.bussiness.socket.model.FriendInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.socket.model.Users;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.task.GetGroupInfoTask;
import com.zuzhili.bussiness.socket.task.GetGroupUserTask;
import com.zuzhili.bussiness.socket.task.GetUserInfoTask;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.DBHelper;
import com.zuzhili.db.model.GroupChatInfo;
import com.zuzhili.db.model.Speech;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.HomeTabActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 收取消息服务
 *
 * @author liutao
 *
 */
public class GetMsgService extends Service {

    private static final int MSG = 1;

    private NotificationManager mNotificationManager;

    private Notification mNotification;

    /** 最近联系人列表（联系人或者群组） */
    private List<GroupChatInfo> lastestContactsList;

    /** all message info list */
    private List<Speech> allSpeechList;

    private GroupChatInfo groupChatInfo;

    private String lastTalkJson;

    private String groupId;

    private DBHelper dbHelper;

    private ServiceBinder mBinder = new ServiceBinder();

    private RetrieveIMSessionTask retrieveIMSessionTask;

    private Session mSession;

    private Map<String, Integer> unreadMsgCountMap;

    private MainSocket mainSocket;

    /** 需要进行数据库更新操作 */
    private boolean needWriteToDbFlag;

    private AtomicBoolean isP2pChat = new AtomicBoolean(false);

    // 用来更新通知栏消息的handler
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG:
                    GroupChatInfo groupChatInfo =  msg.getData().getParcelable(Constants.EXTRA_PARCELABLE_GROUP_CHAT_INFO);
                    if (groupChatInfo != null) {
                        String content = JSON.parseObject(groupChatInfo.getLastTalkJson()).getString("body");

                        // 更新通知栏
                        int icon = R.drawable.icon;
                        CharSequence tickerText = content;
                        long when = System.currentTimeMillis();
                        mNotification = new Notification(icon, tickerText, when);

                        mNotification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                        // 设置默认声音
                        mNotification.defaults |= Notification.DEFAULT_SOUND;
                        // 设定震动(需加VIBRATE权限)
                        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
                        mNotification.defaults |= Notification.DEFAULT_LIGHTS;
                        mNotification.contentView = null;

                        Intent intent = new Intent(GetMsgService.this, HomeTabActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(GetMsgService.this, 0, intent, 0);
                        mNotification.setLatestEventInfo(GetMsgService.this, "新消息", content, contentIntent);
                    }
                    mNotificationManager.notify(Integer.valueOf(groupId), mNotification);// 通知一下才会生效哦
                    break;

                default:
                    break;
            }
        }
    };

	@Override
	public IBinder onBind(Intent intent) {
        LogUtils.e("--------------onBind, (intent == null)" + (intent == null) + "-------------------");
        if (intent != null) {
            final String ids = intent.getStringExtra(Constants.EXTRA_IDS);
            Runnable startSocket = new Runnable() {
                @Override
                public void run() {
                    mainSocket = MainSocket.getInstance(ids);
                    // TODO: socket建立链接成功后通知登陆操作
                    mainSocket.setOnReceiveDataListener(new OnReceiveDataListener() {
                        @Override
                        public void onReceiveData(String[] result) {
                            if (!result[0].equals(Constants.IM_CMD_SET_USER_INFO)) {
                                Intent broadCast = new Intent();
                                broadCast.setAction(Constants.ACTION_RECEIVE_IM_MSG);
                                broadCast.putExtra(Constants.BROADCAST_RECEIVE_IM_MSG, result);
                                sendBroadcast(broadCast);// 把收到的消息已广播的形式发送出去
                                // TODO:单线程模型，后期考虑多线程
                                new SynchronizationTask(result).execute();
                            }
                        }
                    });
                }
            };
            new Thread(startSocket).start();

            Runnable dbWorker = new Runnable() {
                @Override
                public void run() {
                    if (needWriteToDbFlag && lastestContactsList != null && lastestContactsList.size() > 0) {
                        dbHelper.getImChatRoomInfoTable().update(lastestContactsList);
                        needWriteToDbFlag = false;
                        /**
                         *
                         *   @test code
                         *
                         *   List<GroupChatInfo> groupChatInfos = dbHelper.getImChatRoomInfoTable().get(Utils.getIdentity(mSession));
                         *   LogUtils.e("-----------------query latest contacts, size = " + groupChatInfos.size() + "------------------");
                         *   int i = 1;
                         *   for(GroupChatInfo item : groupChatInfos) {
                         *       LogUtils.e("item " + (i++) + ", toString: " + item.toString());
                         *   }
                         *
                         */
                    }
//                    if (needWriteToDbFlag && allSpeechList != null && allSpeechList.size() > 0) {
//                        dbHelper.getImChatHistoryTable().insert(allSpeechList);
//                        clearAllMessage();
//                    }
                }
            };

            Runnable pulse = new Runnable() {
                @Override
                public void run() {
                    retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                    retrieveIMSessionTask.execute(new GetUserInfoTask(mSession.getIds()));
                }
            };

            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
            long dbOperationPeriod = 200; // the period between successive executions
            exec.scheduleAtFixedRate(dbWorker, 0, dbOperationPeriod, TimeUnit.MILLISECONDS);
            long pulseOperationPeriod = 10 * 60 * 1000;
            exec.scheduleAtFixedRate(pulse, pulseOperationPeriod, pulseOperationPeriod, TimeUnit.MILLISECONDS);
        }
		return mBinder;
	}

	@Override
	public void onCreate() {// 在onCreate方法里面注册广播接收者
		super.onCreate();
        mSession = Session.get(this);
        dbHelper = ((TaskApp) getApplication()).getDbHelper();
        unreadMsgCountMap = new HashMap<String, Integer>();
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        lastestContactsList = dbHelper.getImChatRoomInfoTable().get(Utils.getIdentity(mSession));
        allSpeechList = new ArrayList<Speech>();

        if (lastestContactsList != null) {
            for (GroupChatInfo item : lastestContactsList) {
                unreadMsgCountMap.put(item.getGroupId(), item.getUnreadMsgCount());
                needWriteToDbFlag = true;
            }
        }
	}

    private synchronized void addMessage(Speech item) {
        allSpeechList.add(item);
    }

    private synchronized void clearAllMessage() {
        allSpeechList.clear();
    }

    private void synchronization(String[] result) {
        if (lastestContactsList == null) {
            lastestContactsList = new ArrayList<GroupChatInfo>();
        }
        if (result != null && result[0].equals(Constants.IM_CMD_TALK) && result[1].equals(Constants.IM_STATE_REC_MSG) && result.length == 5) {

            // 发送消息操作成功：
            // talk -100 FriendID
            // 操作失败：
            // talk -200 FriendID
            //
            // 对方收到的消息格式为：
            // talk -300 Listid userid content
            groupId = result[3];
            if (unreadMsgCountMap.get(groupId) == null) {
                unreadMsgCountMap.put(groupId, 0);
            }
            unreadMsgCountMap.put(groupId, unreadMsgCountMap.get(groupId) + 1);
            groupChatInfo = getGroupChatInfo(groupId);
            FriendInfo friendInfo = dbHelper.getImContactTable().get(groupId, Utils.getIdentity(mSession));
            if (groupChatInfo != null) {
                lastTalkJson = result[4].replaceAll("□■", Constants.BLANK);
                groupChatInfo.setLastTalkJson(lastTalkJson);
                groupChatInfo.setTime(System.currentTimeMillis());
                groupChatInfo.setUnreadMsgCount(unreadMsgCountMap.get(groupId));
                if (friendInfo != null) {
                    groupChatInfo.setSpeakerJson(JSONObject.toJSONString(friendInfo));
                    lastestContactsList.remove(groupChatInfo);
                    lastestContactsList.add(0, groupChatInfo);
                    dbHelper.getImChatHistoryTable().insert(buildSpeech(groupChatInfo, friendInfo, true));
                    needWriteToDbFlag = true;
                    sendResfreshLatestContactListBroadcast();
                } else {
                    retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                    retrieveIMSessionTask.execute(new GetUserInfoTask(groupId));
                    isP2pChat.compareAndSet(false, true);
                }
//                sendMsgForNotification(groupChatInfo);
            } else {
                groupChatInfo = new GroupChatInfo();
                // 查本地
                if (friendInfo != null) {
                    lastTalkJson = result[4].replaceAll("□■", Constants.BLANK);;
                    setP2pGroupChatInfo(friendInfo, lastTalkJson);
                    sendResfreshLatestContactListBroadcast();
//                    sendMsgForNotification(groupChatInfo);
                } else {
                    // 拉取组用户
                    lastTalkJson = result[4].replaceAll("□■", Constants.BLANK);;
                    retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                    retrieveIMSessionTask.execute(new GetUserInfoTask(groupId));
                    isP2pChat.compareAndSet(false, true);
                }
            }
        } else if (result != null && result[0].equals(Constants.IM_CMD_TALK_ALL) && result[1].equals(Constants.IM_STATE_REC_MSG) && result.length == 6) {
            // 聊天室接受的内容为:
            // talkAll -300 Listid Gid Userid Content
            // 保存最近聊天群组，保存最新聊天信息
            groupId = result[3];
            if (unreadMsgCountMap.get(groupId) == null) {
                unreadMsgCountMap.put(groupId, 0);
            }
            unreadMsgCountMap.put(groupId, unreadMsgCountMap.get(groupId) + 1);
            groupChatInfo = getGroupChatInfo(groupId);
            lastTalkJson = result[5].replaceAll("□■", Constants.BLANK);
            if (groupChatInfo != null) {

                groupChatInfo.setLastTalkJson(lastTalkJson);
                groupChatInfo.setTime(System.currentTimeMillis());
                groupChatInfo.setUnreadMsgCount(unreadMsgCountMap.get(groupId));

                lastestContactsList.remove(groupChatInfo);
                lastestContactsList.add(0, groupChatInfo);
                needWriteToDbFlag = true;
                sendResfreshLatestContactListBroadcast();

                FriendInfo friendInfo = dbHelper.getImContactTable().get(result[4], Utils.getIdentity(mSession));
                if (friendInfo != null) {
//                    addMessage(buildSpeech(groupChatInfo, friendInfo, false));
                    dbHelper.getImChatHistoryTable().insert(buildSpeech(groupChatInfo, friendInfo, false));
                } else {
                    retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                    retrieveIMSessionTask.execute(new GetUserInfoTask(result[4]));
                    isP2pChat.compareAndSet(true, false);
                }
//                sendMsgForNotification(groupChatInfo);
            } else {
                // 极端情况：1.获取组信息；2.拉取组用户;3.获取到发言者的用户信息
                groupChatInfo = new GroupChatInfo();
                lastTalkJson = result[5].replaceAll("□■", Constants.BLANK);
                groupChatInfo.setLastTalkJson(lastTalkJson);

                // 获取组信息
                retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                retrieveIMSessionTask.execute(new GetGroupInfoTask(mSession.getIds(), groupId));

                // 拉取组用户
                retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                retrieveIMSessionTask.execute(new GetGroupUserTask(mSession.getIds(), groupId));

                // 获取到发言者的用户信息
                retrieveIMSessionTask = new RetrieveIMSessionTask(mSession.getIds());
                retrieveIMSessionTask.execute(new GetUserInfoTask(result[4]));
                isP2pChat.compareAndSet(true, false);
            }

        } else if (result != null
                && result[0].equals(Constants.IM_CMD_GET_GROUP_USER)
                && groupId != null
                && !mSession.getCurrentActivityName().equals("ui.activity.im.GroupChatActivity")) { // 从群组列表进入聊天对话页面会拉取群组成员信息，此时不能视作创建群组操作。
            Users users = new Users();
            users.parse(result, mSession, Constants.IM_CMD_GET_GROUP_USER, false);
            ArrayList<UserInfo> friendsList = users.getUsersList();
            if (groupChatInfo == null) {
                groupChatInfo = new GroupChatInfo();
            }
            groupChatInfo.setGroupId(groupId);
            if (groupChatInfo.getName() == null || TextUtils.isEmpty(groupChatInfo.getName())) {
                groupChatInfo.setName(getTempGroupChatRoomName(friendsList));
            }

            groupChatInfo.setIdentity(Utils.getIdentity(mSession));
            groupChatInfo.setTime(System.currentTimeMillis());
            groupChatInfo.setGroupType(Constants.IM_TYPE_GROUP_CHAT);
            groupChatInfo.setChatRoomNum(friendsList.size());
            groupChatInfo.setContactJson(JSONObject.toJSONString(friendsList));
            if (unreadMsgCountMap.get(groupId) == null) {
                unreadMsgCountMap.put(groupId, 0);
            }
            groupChatInfo.setUnreadMsgCount(unreadMsgCountMap.get(groupId));
            lastestContactsList.add(0, groupChatInfo);
            needWriteToDbFlag = true;
            // 通知更新最近联系人列表
            Intent broadCast = new Intent();
            broadCast.setAction(Constants.ACTION_REFRESH_LATEST_CONTACT_LIST);
            broadCast.putExtra(Constants.BROADCAST_RECEIVE_IM_MSG_GROUP_TALK, true);
            sendBroadcast(broadCast);
        } else if (result != null && result[0].equals(Constants.IM_CMD_GET_USER_INFO)) {
            if (result.length == 8 && !result[1].equals(mSession.getIds())) {   // 正确返回数据，查到用户
//                FriendInfo friendInfo = new FriendInfo();
//                friendInfo = friendInfo.parse(result);
//                groupChatInfo.setSpeakerJson(JSONObject.toJSONString(friendInfo));
//
//                if (isP2pChat.get()) {
//                    setP2pGroupChatInfo(friendInfo, lastTalkJson);
//                    sendResfreshLatestContactListBroadcast();
//                } else {
////                    addMessage(buildSpeech(groupChatInfo, friendInfo, false));
//                    dbHelper.getImChatHistoryTable().insert(buildSpeech(groupChatInfo, friendInfo, false));
//
//                }
//                sendMsgForNotification(groupChatInfo);
            }
        } else if (result != null && result.length == 10 && result[0].equals(Constants.IM_CMD_GET_GROUP_INFO)) {
            groupChatInfo.setName(result[2]);
            groupChatInfo.setIcon(result[3]);
            groupChatInfo.setChatRoomNum(Integer.valueOf(result[4]));
            GroupInfo group = mSession.getGroupInfoById(result[1]);
            if (group != null) {
                groupChatInfo.setChatRoomType(group.getG_type());
            } else {
                groupChatInfo.setChatRoomType(Constants.IM_GROUP_CUSTOM);
            }
        }
    }

    private GroupChatInfo getGroupChatInfo(String groupId) {
        if (lastestContactsList == null) return null;
        for (GroupChatInfo item : lastestContactsList) {
            if (item.getGroupId() != null && item.getGroupId().equals(groupId)) {
                return item;
            }
        }
        return null;
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

    private void setP2pGroupChatInfo(FriendInfo friendInfo, String lastTalkJson) {
        groupChatInfo.setGroupId(groupId);
        groupChatInfo.setLastTalkJson(lastTalkJson);
        groupChatInfo.setIdentity(Utils.getIdentity(mSession));
        groupChatInfo.setIcon(friendInfo.getUserAvatar());
        groupChatInfo.setGroupType(Constants.IM_TYPE_P2P_CHAT);
        groupChatInfo.setChatRoomNum(2);
        groupChatInfo.setContactJson(JSONObject.toJSONString(friendInfo));
        groupChatInfo.setSpeakerJson(JSONObject.toJSONString(friendInfo));
        groupChatInfo.setName(friendInfo.getUserName());
        groupChatInfo.setTime(System.currentTimeMillis());
        groupChatInfo.setUnreadMsgCount(unreadMsgCountMap.get(groupId));

        if (getGroupChatInfo(groupId) != null) {
            lastestContactsList.remove(groupChatInfo);
            lastestContactsList.add(0, groupChatInfo);
        } else {
            lastestContactsList.add(0, groupChatInfo);
        }
//        addMessage(buildSpeech(groupChatInfo, friendInfo, true));
        dbHelper.getImChatHistoryTable().insert(buildSpeech(groupChatInfo, friendInfo, true));
        needWriteToDbFlag = true;
    }

    private Speech buildSpeech(GroupChatInfo groupChatInfo, FriendInfo friendInfo, boolean isP2pChat) {
        Speech speech = new Speech();
        speech.setIdentity(groupChatInfo.getIdentity());
        speech.setGroupType(groupChatInfo.getGroupType());
        speech.setGroupId(groupChatInfo.getGroupId());
        if (isP2pChat) {
            speech.setSessionId(mSession.getIds() + Constants.SYMBOL_PERIOD + speech.getGroupId());
        } else {
            speech.setSessionId(speech.getGroupId());
        }
        speech.setSpeaker(JSONObject.toJSONString(friendInfo));
        speech.setMsgEntity(groupChatInfo.getLastTalkJson());
        speech.setTime(groupChatInfo.getTime());
        return speech;
    }

    private Speech buildSpeech(GroupChatInfo groupChatInfo, boolean isP2pChat) {
        Speech speech = new Speech();
        speech.setIdentity(groupChatInfo.getIdentity());
        speech.setGroupType(groupChatInfo.getGroupType());
        speech.setGroupId(groupChatInfo.getGroupId());
        if (isP2pChat) {
            speech.setSessionId(mSession.getIds() + Constants.SYMBOL_PERIOD + speech.getGroupId());
        } else {
            speech.setSessionId(speech.getGroupId());
        }
        speech.setSpeaker(groupChatInfo.getSpeakerJson());
        speech.setMsgEntity(groupChatInfo.getLastTalkJson());
        speech.setTime(groupChatInfo.getTime());
        return speech;
    }

    // 通知更新最近联系人列表
    private void sendResfreshLatestContactListBroadcast() {
        Intent broadCast = new Intent();
        broadCast.setAction(Constants.ACTION_REFRESH_LATEST_CONTACT_LIST);
        broadCast.putExtra(Constants.BROADCAST_RECEIVE_IM_MSG_GROUP_TALK, true);
        sendBroadcast(broadCast);
    }

    private void sendMsgForNotification(GroupChatInfo groupChatInfo) {
        Message message = handler.obtainMessage();
        message.what = MSG;
        message.getData().putParcelable(Constants.EXTRA_PARCELABLE_GROUP_CHAT_INFO, groupChatInfo);
        handler.sendMessage(message);
    }

    // 在服务被摧毁时，做一些事情
	@Override
	public void onDestroy() {
		super.onDestroy();
        if (mainSocket != null) {
            mainSocket.onClose(true);
        }
        LogUtils.e("-------------------exec replace sql! service destroyed!--------------------");
        dbHelper.getImChatRoomInfoTable().update(lastestContactsList);

	}

    public class ServiceBinder extends Binder {

        /**
         * 获取所有最近联系人
         * @return
         */
        public List<GroupChatInfo> getGroupChatInfoList() {
            return lastestContactsList;
        }

        /**
         * 增加新的最近联系人
         * @param groupChatInfo
         */
        public void addGroupChatInfo(GroupChatInfo groupChatInfo, boolean isP2pChat) {
            if (!isP2pChat && groupChatInfo.getChatRoomType() == null) {
                GroupInfo groupInfoById = mSession.getGroupInfoById(groupChatInfo.getGroupId());
                if (groupInfoById != null) {
                    groupChatInfo.setChatRoomType(groupInfoById.getG_type());
                } else {
                    groupChatInfo.setChatRoomType(Constants.IM_GROUP_CUSTOM);
                }
            }
            // 创建群时，或有人说话时添加最近联系人
            if (lastestContactsList == null) {
                lastestContactsList = new ArrayList<GroupChatInfo>();
                lastestContactsList.add(0, groupChatInfo);

            } else {
                GroupChatInfo existedGroupChatInfo = getGroupChatInfo(groupChatInfo.getGroupId());
                if (existedGroupChatInfo != null) {
                    lastestContactsList.remove(existedGroupChatInfo);
                    lastestContactsList.add(0, groupChatInfo);
                } else {
                    lastestContactsList.add(0, groupChatInfo);
                }
            }
            if (groupChatInfo.getLastTalkJson() != null) {
//                addMessage(buildSpeech(groupChatInfo, isP2pChat));
                dbHelper.getImChatHistoryTable().insert(buildSpeech(groupChatInfo, isP2pChat));

            }
            needWriteToDbFlag = true;
        }

        /**
         * 清空未读消息提示
         * @param groupChatInfo
         */
        public void clearUnreadMsgCount(GroupChatInfo groupChatInfo) {
            unreadMsgCountMap.put(groupChatInfo.getGroupId(), 0);
            if (groupChatInfo.getGroupId() != null && getGroupChatInfo(groupChatInfo.getGroupId()) != null) {
                getGroupChatInfo(groupChatInfo.getGroupId()).setUnreadMsgCount(0);
            }
        }

        public void removeGroupChatInfo(String groupId) {
            if (unreadMsgCountMap.get(groupId) != null) {
                unreadMsgCountMap.remove(groupId);
            }
            GroupChatInfo search = getGroupChatInfo(groupId);
            if (search != null) {
                lastestContactsList.remove(search);
                needWriteToDbFlag = true;
            }
        }
    }

    private class SynchronizationTask extends AsyncTask<Void, Void, Void> {

        private String[] result;

        public SynchronizationTask(String[] result) {
            this.result = result;
        }

        @Override
        protected Void doInBackground(Void... params) {
            synchronization(result);
            return null;
        }
    }

}
