package com.zuzhili.ui.activity.im;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.IMConversationAdapter;
import com.zuzhili.db.Table;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.List;

/**
 * Created by kj  on 2014/7/11.
 * 转发选择界面
 */
public class ForwardActivity extends BaseActivity implements  BaseActivity.TimeToShowActionBarCallback , IMConversationAdapter.OnItemSelectedForDrawListener<IMConversation> {
    @ViewInject(R.id.et_search)
    private EditText et_search;

    @ViewInject(R.id.listView)
    private PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private IMConversationAdapter adapter;

    private String content;
    private int msgType;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_forward_choose);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        update();
        initData();
    }


    public void update() {
        progressBar.setVisibility(View.VISIBLE);

        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_CHAT)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_CHAT);
        }

        if(adapter == null) {
            adapter = new IMConversationAdapter(this
                    , listView
                    , ImageCacheManager.getInstance().getImageLoader()
                    ,true);

        } else {
            progressBar.setVisibility(View.GONE);
        }
        new GetIMConversationTask().execute();
        adapter.setListView(listView);
        adapter.setOnItemClickedForDrawListener(this);
        listView.setDivider(getResources().getDrawable(R.drawable.divider));
        listView.setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        listView.setOnItemClickListener(adapter);
        listView.setAdapter(adapter);
    }


    private void initData(){
        content=getIntent().getStringExtra(Constants.MSG_FORWORD);
        msgType=getIntent().getIntExtra(Constants.MSG_TYPE,0);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, getString(R.string.home_tab_message_forward), false);
        setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return true;
    }


    @Override
    public void onItemSelected(int position, final IMConversation item,Drawable drawable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.forwardto));
        //头像
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.pic_dialog,
                (RelativeLayout) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.img_group_avatar);
        TextView group = (TextView) layout.findViewById(R.id.txt_count_in_group);
        TextView userName = (TextView) layout.findViewById(R.id.txt_contact_onename);
        userName.setText(item.getUserName());

        if(item.getId() != null && item.getId().startsWith("g")){
            group.setText(item.getGroupUserCount());
            group.setBackgroundDrawable(drawable);
        }else {
            image.setBackgroundDrawable(drawable);
        }

        builder.setView(layout);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it=new Intent(ForwardActivity.this,GroupChatActivity.class);
                if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_TEXT);
                }else {
                    it.putExtra(Constants.MSG_FORWORD,content);
                    it.putExtra(Constants.MSG_TYPE,IMChatMessageDetail.TYPE_MSG_PIC);
                }

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
                startActivity(it);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private class GetIMConversationTask extends AsyncTask<Void, Void, List<IMConversation>> {

        @Override
        protected List<IMConversation> doInBackground(Void... params) {
            List<IMConversation> conversationList;
            try {
                Table table = getDbHelper().getTable();
                table.setDbUtils(getDbHelper().getDbUtils());
                conversationList =  table.queryIMConversation(Utils.getIdentity(mSession));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return conversationList;
        }

        @Override
        protected void onPostExecute(List<IMConversation> conversationList) {
            super.onPostExecute(conversationList);
            progressBar.setVisibility(View.GONE);
            if (conversationList != null) {
                int nun=0;
                for(int i=0;i<conversationList.size();i++){
                    if(conversationList.get(i).getId().startsWith("g")){
                        conversationList.add(nun,conversationList.get(i));
                        conversationList.remove(i+1);

                        nun++;
                    }
                }
                adapter.clearList();
                adapter.setList(conversationList);
            } else {
                adapter.clearList();
            }
        }
    }
}
