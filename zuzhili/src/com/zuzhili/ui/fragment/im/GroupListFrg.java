package com.zuzhili.ui.fragment.im;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.IMConversationAdapter;
import com.zuzhili.db.DBHelper;
import com.zuzhili.db.IMGroupInfoTable;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.db.IMUserInfoTable;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.im.CCPIntentUtils;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.quickreturn.AbsListViewQuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.QuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.widget.AbsListViewScrollTarget;
import com.zuzhili.ui.views.quickreturn.widget.QuickReturnAdapter;

import java.sql.SQLException;

public class GroupListFrg extends FixedOnActivityResultBugFragment implements IMConversationAdapter.OnItemSelectedForDrawListener<IMConversation>,IMConversationAdapter.OnItemDeleteListener<IMConversation>, AbsListView.OnScrollListener {

    @ViewInject(android.R.id.list)
    private PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    // Search
    @ViewInject(R.id.quickReturnTopTarget)
    private SearchView topSearchView;

    private IMConversationAdapter adapter;

    private ShouldUpdateListReceiver receiver;

    /** 主动解散群组，返回到该界面时，会调用update方法，加载数据。但接收到的解散群广播也会第二次更新数据，该变量会阻止二次更新数据，否则会造成多线程问题 */
    private boolean shouldUpdate = true;

    @Override
    public void onItemSelected(int position, IMConversation item,Drawable drawable) {
        Intent it = new Intent(activity, GroupChatActivity.class);
        it.putExtra(Constants.EXTRA_IM_GROUPID, item.getGroupId());
        it.putExtra(Constants.EXTRA_IM_GROUP_NAME, item.getUserName());
        it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, item.getGroupUserCount());
        it.putExtra(Constants.EXTRA_IM_YGROUPID, item.getId());

        if (item.getContact().startsWith("g")) {
            it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
            it.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setU_icon(item.getUserAvatar());
            userInfo.setU_listid(mSession.getListid());
            userInfo.setU_name(item.getUserName());
            userInfo.setY_voip(item.getId());
            it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
            it.putExtra(Constants.EXTRA_IM_CONTACT, (java.io.Serializable) userInfo);
        }

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setG_name(item.getUserName());
        groupInfo.setU_listid(mSession.getListid());
        groupInfo.setG_type("2");
        groupInfo.setG_ucount(item.getGroupUserCount());
        groupInfo.setId(item.getGroupId());
        groupInfo.setY_gid(item.getId());
        groupInfo.setCreatorid(item.getOwner());
        groupInfo.setZ_type(item.getGroupType());

        it.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) groupInfo);
        activity.startActivity(it);
    }

    @Override
    public void onItemDelete(final int position, final IMConversation item) {
        IMUserInfoTable imUserInfoTable = DBHelper.getInstance(activity).getUserInfoTable();
        IMGroupInfoTable imGroupInfoTable = DBHelper.getInstance(activity).getGroupInfoTable();
        final IMMessageTable messageTable = DBHelper.getInstance(activity).getMessageTable();
        UserInfo friend = imUserInfoTable.get(item.getId(), item.getListId(), Utils.getIdentity(Session.get(activity)));
        GroupInfo groupInfo = null;
        try {
            groupInfo = imGroupInfoTable.queryGroup(item.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (friend != null) {
            builder.setTitle(friend.getU_name());
        }

        if(groupInfo !=null ){
            builder.setTitle(groupInfo.getG_name());
        }

        builder.setPositiveButton(activity.getString(R.string.delete_conversation),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    messageTable.deleteIMMessage(item.getId(),Session.get(activity).getListid());
                    dbHelper.getGroupInfoTable().deleteGroupByGroupId(item.getGroupId());
                    adapter.removeItem(position+1);

                    SyncUtils.TriggerRefresh();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        listView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }


    private class ShouldUpdateListReceiver extends BroadcastReceiver {

        private Handler handler;

        public ShouldUpdateListReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, final Intent intent) {

            if (handler != null) {
                if (intent.getAction().equals(CCPIntentUtils.INTENT_IM_RECIVE)) {
                    update();
                } else if (intent.getAction().equals(CCPIntentUtils.INTENT_REMOVE_FROM_GROUP)) {
                    update();
                } else if (intent.getAction().equals(CCPIntentUtils.INTENT_DISMISS_GROUP)) {
                    update();
                }
            }
        }
    }


    //删除通知
    private void clearNotification(Context context){
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        CCPHelper.getInstance().MSG_NUM=0;
        CCPHelper.getInstance().senderList.clear();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearNotification(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        clearNotification(activity);
        shouldUpdate = true;
        update();
        registerReceiver(new String[]{CCPIntentUtils.INTENT_IM_RECIVE
                , CCPIntentUtils.INTENT_REMOVE_FROM_GROUP
                , CCPIntentUtils.INTENT_DISMISS_GROUP});
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterReceiver();
        shouldUpdate = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void update() {

        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_CHAT)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_CHAT);
        }

        if(adapter == null) {
            adapter = new IMConversationAdapter(getActivity()
                    , listView
                    , ImageCacheManager.getInstance().getImageLoader()
                    ,false);
        } else {
            progressBar.setVisibility(View.GONE);
            if (shouldUpdate) {
                adapter.update();
            }
        }

        adapter.setOnItemClickedForDrawListener(this);
        adapter.setOnItemDeleteListener(this);
        listView.setOnItemLongClickListener(adapter);
        // Search
        if (listView.getHeaderViewsCount() < 2) {// 加入占位的header
            View head = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
            listView.addHeaderView(head);
        }

        listView.setDivider(getResources().getDrawable(R.drawable.divider));
        listView.setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, activity.getResources().getDisplayMetrics()));
        listView.setAdapter(new QuickReturnAdapter(adapter, 1));
        adapter.setListView(listView);

        topSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterData(query);
                if (TextUtils.isEmpty(query)) {
                    topSearchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterData(newText);
                if (TextUtils.isEmpty(newText)) {
                    topSearchView.clearFocus();
                }
                return true;
            }
        });

        QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(listView);
        quickReturnAttacher.addTargetView(topSearchView, AbsListViewScrollTarget.POSITION_TOP, DensityUtil.dip2px(getActivity(), 50));
        if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
            final AbsListViewQuickReturnAttacher attacher = (AbsListViewQuickReturnAttacher) quickReturnAttacher;
            attacher.addOnScrollListener(this);
            attacher.setOnItemClickListener(adapter);
        }

    }

    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        if (receiver == null) {
            receiver = new ShouldUpdateListReceiver(new Handler());
        }
        IntentFilter intentfilter = new IntentFilter(CCPIntentUtils.INTENT_CONNECT_CCP);
        intentfilter.addAction(CCPIntentUtils.INTENT_DISCONNECT_CCP);
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        activity.registerReceiver(receiver, intentfilter);
    }

    public void unRegisterReceiver() {
        activity.unregisterReceiver(receiver);
    }
}
