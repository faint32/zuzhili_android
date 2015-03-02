package com.zuzhili.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.Task;
import com.zuzhili.controller.helper.TalkDetailHolder;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.controller.helper.TalkDetailViewHelper;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.views.PagingListView;

/**
 * Created by lt on 14-2-21.
 */
public class TalkDetailListAdapter extends NonPagingResultsAdapter<IMChatMessageDetail> implements NonPagingResultsAdapter.ShowLastItemViewCallback, PagingListView.Pagingable, Response.Listener<String>, Response.ErrorListener {

    public static final int ITEM_VIEW_TYPE_COUNT = 2;        // listView 中item view的类型数目

    protected GroupChatActivity mGroupChatActivity;

    private Session session;

    private String groupId;

    private TalkDetailViewHelper talkDetailViewHelper;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    private TalkDetailHolder holder;

    private IMMessageTable messageTable;

    /**
     * 服务端是否有消息记录,如果没有不能获取
     */
    private boolean hasHistory = true;
    /**
     * 本地无当天消息记录,直接从服务器获取昨天记录
     */
    private boolean getHistory = false;
    /**
     * 从服务器获取记录完成,不进行限制查询
     */
    private boolean downLoadOver = false;

    public TalkDetailListAdapter(Context context,
                                 ListView listView,
                                 ImageLoader imageLoader,
                                 Session session,
                                 String groupId,
                                 BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback) {
        super(context, listView, imageLoader);
        mGroupChatActivity = (GroupChatActivity) context;
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        this.session = session;
        this.groupId = groupId;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        talkDetailViewHelper = new TalkDetailViewHelper(context, imageLoader,session);
        setShowLastItemViewCallback(this);
        messageTable = dbHelper.getMessageTable();
        mListView.setPagingableListener(this);
        mListView.setHasMoreItems(true);
        mListView.setIsLoading(true);
        new IMListSyncTask().execute(groupId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final IMChatMessageDetail item = getItem(position);

        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        if (convertView == null) {
            convertView = talkDetailViewHelper.populateFitItemView(getItemViewType(position), parent);
            holder = new TalkDetailHolder();
            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (TalkDetailHolder) convertView.getTag();
        }

        String lastMessageCreateDate;

        if (position == 0) {
            lastMessageCreateDate = "0";
        } else {
            lastMessageCreateDate = getItem(position - 1).getCurDate();
        }

        if (getItemViewType(position) == TalkDetailViewHelper.VIEW_TYPE_OTHER) {
            //语音
            if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_VOICE) {
                talkDetailViewHelper.showLeftVoiceDetail(item, holder, position, mGroupChatActivity, lastMessageCreateDate, groupId);
                //图片
            } else if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_PIC) {
                talkDetailViewHelper.showLeftPicDetail(item, holder, mGroupChatActivity, lastMessageCreateDate, groupId);
            } else {
                talkDetailViewHelper.showLeftMsgDetail(item, holder, mGroupChatActivity, lastMessageCreateDate, groupId);
            }
        } else {
            if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_VOICE) {
                talkDetailViewHelper.showRightVoiceDetail(item, holder, position, mGroupChatActivity, lastMessageCreateDate);
            } else if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_PIC) {
                talkDetailViewHelper.showRightPicDetail(item, holder, mGroupChatActivity, lastMessageCreateDate);
            } else {
                talkDetailViewHelper.showRightMsgDetail(item, holder, mGroupChatActivity, lastMessageCreateDate);
            }
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        super.getViewTypeCount();
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        return getViewType(getItem(position));
    }

    private int getViewType(IMChatMessageDetail item) {
        int type = TalkDetailViewHelper.VIEW_TYPE_MYSELF;
        if (item.getImState() == IMChatMessageDetail.STATE_IM_RECEIVEED) {
            type = TalkDetailViewHelper.VIEW_TYPE_OTHER;
        }
        return type;
    }

    @Override
    public void setLastItemViewSelectd(ListView listView, NonPagingResultsAdapter adapter) {
        listView.setSelection(adapter.getCount());
    }

    /**
     * @param item 该item可能存在与集合中，则更新数据。否则加入到集合中
     * @throws IndexOutOfBoundsException
     */
    public void addItem(IMChatMessageDetail item) throws IndexOutOfBoundsException {
        if (!mDataList.isEmpty()) {
            int position = findChatMessageById(item.getMessageId());
            if (position != -1) {
                mDataList.remove(position);
                mDataList.add(position, item);
            } else {
                mDataList.add(mDataList.size(), item);
            }
        } else {
            mDataList.add(item);
        }
        notifyDataSetChanged();
    }

    /**
     * 根据messageId找到该条chat message在集合中的位置
     *
     * @param messageId
     * @return
     */
    private int findChatMessageById(String messageId) {
        if (messageId == null) {
            LogUtils.e("[findChatMessageById] messageId is null");
            return -1;
        }
        if (mDataList != null) {
            for (int i = mDataList.size() - 1; i >= 0; i--) {
                if (mDataList.get(i).getMessageId().equals(messageId)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public void onLoadMoreItems() {
        //根据当前时间查询昨天记录(无今天记录查询,只进行一次)
        if (getHistory) {
            new IMListSyncTask().execute(new String[]{groupId, String.valueOf(System.currentTimeMillis())});
            getHistory = false;
        } else {
            //根据展示的最后一条消息时间查询记录
            if (mDataList.size() > 0) {
                new IMListSyncTask().execute(new String[]{groupId, mDataList.get(0).getCurDate()});
            }
        }
    }

    /**
     * @param type 1为有记录时查询
     * @return
     */
    private HashMap<String, String> buildRequestParams(int type) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (session != null) {
            params.put("s_voip", session.getVoipId());
            params.put("u_listid", session.getListid());
            params.put("size", 60 + "");
            if (1 == type) {
                params.put("endTime", mDataList.get(0).getCurDate());
            } else {
                params.put("endTime", String.valueOf(System.currentTimeMillis()));
            }

            if (groupId.startsWith("g")) {
                params.put("y_gid", groupId);
            } else {
                params.put("r_voip", groupId);
            }
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //不可查询
        mListView.setHasMoreItems(false);
        mListView.setIsLoading(false);
        hasHistory = false;
    }

    @Override
    public void onResponse(final String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        //查询成员
        if (jsonObject.getString("m_list") != null) {
            final ArrayList<IMChatMessageDetail> imChatMessageDetails = (ArrayList<IMChatMessageDetail>) JSON.parseArray(jsonObject.getString("m_list"), IMChatMessageDetail.class);
            if (imChatMessageDetails != null && imChatMessageDetails.size() > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int index;
                        String fileName;
                        File file;
                        for (final IMChatMessageDetail item : imChatMessageDetails) {
                            try {
//                                if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_FILE) {
//                                    continue;
//                                }
                                if(item.getMessageType()==IMChatMessageDetail.TYPE_MSG_VOICE || "amr".equals(item.getFileExt()) ||"jpg".equals(item.getFileExt()) ||"jpeg".equals(item.getFileExt()) || "png".equals(item.getFileExt())){
                                    if ("jpg".equals(item.getFileExt()) ||"jpeg".equals(item.getFileExt())|| "png".equals(item.getFileExt())) {
                                        item.setMessageType(IMChatMessageDetail.TYPE_MSG_PIC);
                                    }
                                    if ("amr".equals(item.getFileExt())) {
                                        item.setMessageType(IMChatMessageDetail.TYPE_MSG_VOICE);
                                    }
                                    index = item.getMessageContent().indexOf("fileName=");
                                    fileName = item.getMessageContent().substring(index+9, item.getMessageContent().length());
                                    file = new File(TaskApp.getInstance().getVoiceStore(), fileName);
                                    final String localPath=file.getAbsolutePath();
                                    item.setFilePath(localPath);

                                    //不存在就下载
                                    if (!file.exists()) {
                                        ((BaseActivity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HttpUtils http = new HttpUtils();
                                                //是否断点下载或存在是否再次下载; 下载后是否改名
                                                http.download(item.getQ_fileurl(), localPath, false, false, new RequestCallBack<File>() {
                                                    @Override
                                                    public void onSuccess(ResponseInfo<File> responseInfo) {
                                                        LogUtils.i("onSuccess:"+responseInfo);
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException error, String msg) {
                                                        LogUtils.i("onFailure:"+msg);
                                                        item.setFilePath(null);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                }
                                else if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_FILE) {
                                    continue;
                                }

                                //从服务器获取的消息完善
                                //展示截取字符串
                                if (item.getMessageId().contains("@")) {
                                    item.setMessageId(item.getMessageId().substring(0, item.getMessageId().indexOf("@")));
                                }
                                item.setIdentityId(Utils.getIdentity(session));
                                item.setSessionId(groupId);
                                item.setReadStatus(IMChatMessageDetail.STATE_READED);
                                if (session.getVoipId().equals(item.getGroupSender())) {
                                    item.setImState(IMChatMessageDetail.STATE_IM_SEND_SUCCESS);
                                } else {
                                    item.setImState(IMChatMessageDetail.STATE_IM_RECEIVEED);
                                }
                                //是否存在,如果存在则更新
                                if (!TextUtils.isEmpty(messageTable.isExistsIMmessageId(item.getMessageId()))) {
                                    messageTable.updateIMMessage(item);
                                } else {
                                    messageTable.insertIMMessage(item);
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        downLoadOver = true;
                        onLoadMoreItems();
                    }
                }).start();

            } else {
                //不可查询
                mListView.setHasMoreItems(false);
                mListView.setIsLoading(false);
                hasHistory = false;
            }
        }
    }

    /**
     * 聊天记录
     */
    class IMListSyncTask extends AsyncTask<String, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(String... params) {
            if (params != null && params.length > 0) {
                try {
                    updateReadStatus();
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    if (params.length > 1) {
                        if (downLoadOver) {
                            //查询从服务器下载的记录
                            ArrayList<IMChatMessageDetail> imChatMessageDetails = messageTable.queryIMMessages(params[0], session.getListid(), params[1], "");
                            map.put("insert_into_head", true);
                            map.put("array", imChatMessageDetails);
                        } else {
                            //查询本地当天更多记录
                            ArrayList<IMChatMessageDetail> imChatMessageDetails = messageTable.queryIMMessages(params[0], session.getListid(), params[1], getZeroTime());
                            map.put("insert_into_head", true);
                            map.put("array", imChatMessageDetails);
                        }
                    } else {
                        //查询本地当天记录
                        ArrayList<IMChatMessageDetail> imChatMessageDetails = messageTable.queryIMMessages(params[0], session.getListid(), String.valueOf(System.currentTimeMillis()), getZeroTime());
                        map.put("insert_into_head", false);
                        map.put("array", imChatMessageDetails);
                    }
                    return map;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            super.onPostExecute(result);

            if (result != null) {
                Boolean flag = (Boolean) result.get("insert_into_head");
                final ArrayList<IMChatMessageDetail> imChatMessageDetails = (ArrayList<IMChatMessageDetail>) result.get("array");

                if (flag) {
                    mListView.setIsLoading(true);
                    //获取聊天记录
                    if ((imChatMessageDetails == null || imChatMessageDetails.size() == 0) && hasHistory) {
                        Task.getHistoryMsgs(buildRequestParams(1), TalkDetailListAdapter.this, TalkDetailListAdapter.this);
                        return;
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addAll(0, imChatMessageDetails);
                        }
                    }, 300);

                } else {
                    if (imChatMessageDetails == null) {
                        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
                        getHistory = true;
                        //无当天记录,从服务器获取聊天记录
                        Task.getHistoryMsgs(buildRequestParams(2), TalkDetailListAdapter.this, TalkDetailListAdapter.this);
                    } else {
                        setList(imChatMessageDetails);
                        mListView.setIsLoading(false);
                    }
                }
            }
        }
    }

    private void updateReadStatus() {
        try {
            messageTable.updateIMMessageUnreadStatusToRead(groupId, session.getListid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当天零点时间戳
     *
     * @return
     */
    private String getZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        LogUtils.i("cal.getTimeInMillis()=" + cal.getTimeInMillis());
        return String.valueOf(cal.getTimeInMillis());
    }

}
