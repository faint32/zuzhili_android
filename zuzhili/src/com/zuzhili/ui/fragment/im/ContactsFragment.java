package com.zuzhili.ui.fragment.im;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.AllContactsAdapter;
import com.zuzhili.controller.FriendFaceAdapter;
import com.zuzhili.controller.GroupListAdapter;
import com.zuzhili.controller.IMContactAdapter;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.GroupsQueryActivity;
import com.zuzhili.ui.activity.im.ChatRoomSettingsActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.im.GroupsActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.SideBar;
import com.zuzhili.ui.views.gridview.TwoWayGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends FixedOnActivityResultBugFragment
        implements BaseActivity.HandleProgressBarVisibilityCallback,
                   Response.Listener<String>,
                   Response.ErrorListener {


    @ViewInject(R.id.sidebar)
    private SideBar sideBar;

    @ViewInject(R.id.txt_alphabetic_hint)
    private TextView alphabeticHint;

    @ViewInject(R.id.listView)
    private PullRefreshListView mListView;

    @ViewInject(R.id.grouplistView)
    private PullRefreshListView grouplistView;

    @ViewInject(R.id.topEdittext)
    private EditText topEdittext;

    @ViewInject(R.id.quickReturnTopTarget)
    private SearchView searchView;

    @ViewInject(R.id.gridView)
    private TwoWayGridView gridView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    @ViewInject(R.id.rla_container)
    private LinearLayout rlaContainer;

    private ProgressDialog progressDialog;

    private IMContactAdapter contactAdapter;
    private FriendFaceAdapter friendFaceAdapter;

    // intent extras
    private List<UserInfo> alreadyInChatRoomFriends;
    private String pageFrom;
    private String groupId;
    private String content;
    private int msgType;

    private Session mSession;
    private String y_groupId;

    private BaseActivity parentActivity;

    private ContactMode mode = ContactMode.MODE_VIEW;

    private boolean isQueryAllGroupsAndUsers = true;

    private InputMethodManager mInputMethodManager;

    private GroupListAdapter groupAdapter;

    private List<View> headViews =new ArrayList<View>();

    private IMContactAdapter.OnContactSelectedListener listener;

    public static enum ContactMode {
        MODE_VIEW, MODE_SELECTION
    }

    @Deprecated
    class ViewHolder {
        @ViewInject(R.id.group_icon)
        ImageView icon;

        @ViewInject(R.id.group_name)
        TextView title;

        @ViewInject(R.id.group_entry)
        ImageView entry;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentActivity = (BaseActivity) getActivity();
        Bundle args = getArguments();
        Serializable contact_mode = args.getSerializable("CONTACT_MODE");
        isQueryAllGroupsAndUsers = args.getBoolean("isQueryAllGroupsAndUsers", true);
        if (contact_mode != null) {
            mode = (ContactMode) contact_mode;
        }

        View view = inflater.inflate(R.layout.activity_im_add_contact, container, false);
        ViewUtils.inject(this, view);
        mInputMethodManager= (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        init();

        initListMenu(inflater);
        return view;
    }

    private void initListMenu(LayoutInflater inflater) {
        int[] icons = new int[]{R.drawable.my_group_chat,
                R.drawable.org,
                R.drawable.group,
                R.drawable.project,
                R.drawable.activity};

        String[] titles = new String[]{"我的群聊", "机构", "群组", "项目", "活动"};

        String title = null;
        int icon = 0;
        ImageView imageView = null;
        TextView textView = null;
        for (int i = 0; i < icons.length; i++) {
            title = titles[i];
            icon = icons[i];
            View item = inflater.inflate(R.layout.listview_item_group_menu, null);
            item.setBackgroundColor(Color.WHITE);
            imageView = (ImageView) item.findViewById(R.id.group_icon);
            imageView.setImageResource(icon);
            textView = (TextView) item.findViewById(R.id.group_name);
            textView.setText(title);

            item.setTag(icon);
            headViews.add(item);
            mListView.addHeaderView(item);
        }
    }

    private void init() {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }

        // Session
        if (getActivity() instanceof BaseActivity) {
            mSession = ((BaseActivity) getActivity()).getSession();
        } else {
            throw new RuntimeException("parent activity must extend from BaseActivity, Session is needed!");
        }

        alreadyInChatRoomFriends = intent.getParcelableArrayListExtra(
                Constants.EXTRA_PARCELABLE_CONTACTS);
        pageFrom = intent.getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE);
        groupId = intent.getStringExtra(Constants.EXTRA_IM_GROUPID);

//        progressBar.setVisibility(View.VISIBLE);

        sideBar.setVisibility(View.VISIBLE);
        sideBar.setTextView(alphabeticHint);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    LogUtils.d("header count:" + mListView.getHeaderViewsCount());
                    mListView.setSelection(position + mListView.getHeaderViewsCount());
                }
            }
        });

        // Contact adapter
        if (alreadyInChatRoomFriends != null && alreadyInChatRoomFriends.size() > 0) {
            contactAdapter = new IMContactAdapter(getActivity(),
                    mListView,
                    ImageCacheManager.getInstance().getImageLoader(),
                    mSession,
                    alreadyInChatRoomFriends,
                    buildRequestParams(1),
                    this,
                    mode, isQueryAllGroupsAndUsers);
        } else {
            contactAdapter = new IMContactAdapter(getActivity(),
                    mListView,
                    ImageCacheManager.getInstance().getImageLoader(),
                    mSession,
                    buildRequestParams(1),
                    this,
                    mode, isQueryAllGroupsAndUsers);
        }

        listener=new IMContactAdapter.OnContactSelectedListener() {
            @Override
            public void onContactSelected(UserInfo userInfo) {
                friendFaceAdapter.addOrRemoveItem(userInfo);

                //限制gridView的宽度
                int windowWidth = DensityUtil.getScreenWidth(parentActivity);
                int px = DensityUtil.dip2px(parentActivity, 40);
                int num=(windowWidth/px)*2/3;
                int listSize=friendFaceAdapter.getUserInfoList().size();
                ViewGroup.LayoutParams lp=gridView.getLayoutParams();
                if(listSize>=num){
                    lp.width=num*px+num-1;
                }else if(0<listSize && listSize<num){
                    lp.width= listSize*px+listSize-1;
                }else {
                    lp.width= ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                gridView.setLayoutParams(lp);

                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        gridView.smoothScrollBy(1, 1);
                        gridView.smoothScrollToPosition(friendFaceAdapter.getCount());
                    }
                });

                setActionBar();
            }

            @Override
            public void onHeadViewClicked() {// group chat
                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                startActivity(intent);
            }
        };
        contactAdapter.setOnContactSelectedListener(listener);

//        mListView.setDividerHeight(DensityUtil.dip2px(activity, 0.5f));

        // fetch user list in extra and config data source of adapter
        List<UserInfo> userInfoList = intent.getParcelableArrayListExtra(Constants.EXTRA_IM_ALL_USER);
        if (userInfoList != null) {
            contactAdapter.setList(userInfoList);
            contactAdapter.setSourceList(userInfoList);
        }

        contactAdapter.setListView(mListView);
        contactAdapter.setOnRefreshListener();

        mListView.setDivider(null);

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (topEdittext.isFocused()) {
                    mInputMethodManager.hideSoftInputFromWindow(topEdittext.getWindowToken(), 0);
                    topEdittext.clearFocus();
                }
                return false;
            }
        });
        mListView.setAdapter(contactAdapter);
        mListView.setOnItemClickListener(contactAdapter);

        final Drawable search=getResources().getDrawable(R.drawable.icon_action_search);
        search.setBounds(0, 0, search.getMinimumWidth(), search.getMinimumHeight());
        topEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListView.setIsRefreshable(false);
                topEdittext.setCompoundDrawables(null,null,null,null);
                contactAdapter.filterData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mListView.setIsRefreshable(true);
                    topEdittext.setCompoundDrawables(search,null,null,null);

                    initListMenu(getActivity().getLayoutInflater());
                }else{
                    if(headViews !=null && headViews.size()>0){
                        for(View item:headViews){
                            mListView.removeHeaderView(item);
                        }
                        headViews.clear();
                    }
                }
            }
        });
        // init friend face grid view
        if (mode == ContactMode.MODE_SELECTION) {
            friendFaceAdapter = new FriendFaceAdapter(getActivity(),
                    ImageCacheManager.getInstance().getImageLoader());
            gridView.setAdapter(friendFaceAdapter);
            friendFaceAdapter.notifyDataSetChanged();
        }


        content = intent.getStringExtra(Constants.MSG_FORWORD);
        msgType = intent.getIntExtra(Constants.MSG_TYPE, 0);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    sideBar.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    grouplistView.setAdapter(null);
                }else {
                    sideBar.setVisibility(View.GONE);
                    HashMap<String, String> params=buildRequestParams(4);
                    params.put("keyword",newText);
                    Task.getGroupsUsersByKey(params, ContactsFragment.this, ContactsFragment.this);
                }
                return false;
            }
        });
        if (mode == ContactMode.MODE_VIEW) {
            rlaContainer.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }else {
            rlaContainer.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }

    }


    public void onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DEL){
            if(TextUtils.isEmpty(topEdittext.getText().toString())){
                ArrayList<UserInfo> userInfoList = friendFaceAdapter.getUserInfoList();
                if(userInfoList !=null && userInfoList.size()>0 ){
                    UserInfo userInfo = userInfoList.get(userInfoList.size() - 1);
                    contactAdapter.checkBoxStatus.put(userInfo.getU_id(),false);
                    listener.onContactSelected(userInfo);
                    contactAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private HashMap<String, String> buildRequestParams(int mode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            switch (mode) {
                case 1:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("client", "1");
                    break;
                case 2:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    List<UserInfo> temp = new ArrayList<UserInfo>();
                    temp.addAll(friendFaceAdapter.getUserInfoList());
                    temp.add(0, mSession.getMySelfInfo());
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

                case 4:
                    params.put("u_id", mSession.getUid());
                    params.put("u_listid", mSession.getListid());
                    params.put("g_want", "0");
                    break;
            }
        }
        return params;
    }

    private String getTempGroupChatRoomName(List<UserInfo> userInfoList) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        if (userInfoList != null) {
            for (UserInfo item : userInfoList) {
                if ((i++) < 3) {
                    builder.append(item.getU_name()).append(Constants.SYMBOL_COMMA_CHN);
                } else {
                    break;
                }
            }
            if (builder.length() > 50) {
                builder.deleteCharAt(50);
            } else {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.i("volleyError" + volleyError.toString());
        progressBar.setVisibility(View.GONE);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (volleyError instanceof TimeoutError) {
            Utils.makeEventToast(getActivity(), getString(R.string.timeout_create_group_failed), false);
        } else if (volleyError instanceof BusinessError) {
            Utils.makeEventToast(getActivity(), getString(R.string.hint_create_group_failed), false);
        }
    }

    List<GroupInfo> groups = null;
    List<UserInfo> users = null;
    private GroupInfo selectedGroup;
    @Override
    public void onResponse(String s) {
        progressBar.setVisibility(View.GONE);

        JSONObject jsonObject = JSONObject.parseObject(s);
        if(mode == ContactMode.MODE_VIEW){
            if(jsonObject.getString("rInfo").equals("成功")){
                String glist = jsonObject.getString("glist");
                String ulist = jsonObject.getString("ulist");
                if (glist != null) {
                    groups = JSON.parseArray(glist, GroupInfo.class);
                    //群聊在前面
                    for(int i=1;i<groups.size();i++){
                        if(groups.get(i).getZ_type().equals("0")){
                            GroupInfo groupInfo = groups.get(i);
                            groups.remove(i);
                            groups.add(0, groupInfo);
                        }
                    }
                }

                if(ulist != null){
                    users=JSON.parseArray(ulist, UserInfo.class);
                }

                List<Object>  serachList = new ArrayList<Object>();
                serachList.addAll(groups);
                serachList.addAll(serachList.size(),users);
                AllContactsAdapter serachAdapter = new AllContactsAdapter(getActivity(),mListView,ImageCacheManager.getInstance().getImageLoader(),null);
                grouplistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object obj = parent.getAdapter().getItem(position);
                        if(obj instanceof  UserInfo){
                            UserInfo user=(UserInfo) obj;
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), CommonSpaceActivity.class);
                            intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                            getActivity().startActivity(intent);

                        }else {
                            selectedGroup = (GroupInfo) obj;
//
                            if (selectedGroup.getZ_type().equals("0")) {
                                Intent intent = new Intent(getActivity(), ChatRoomSettingsActivity.class);
                                intent.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, selectedGroup.getG_type());
                                intent.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
                                intent.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) selectedGroup);
                                intent.putExtra(Constants.EXTRA_IM_GROUPID, selectedGroup.getId());
                                intent.putExtra(Constants.EXTRA_IM_YGROUPID, selectedGroup.getY_gid());
                                intent.putExtra(Constants.EXTRA_IM_GROUPNNAME, selectedGroup.getG_name());
                                intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
                                startActivity(intent);
                            }else if (selectedGroup.getZ_type().equals("1")) {
                                final HashMap params  = new HashMap();
                                params.put("u_id", mSession.getUid());
                                params.put("u_listid", mSession.getListid());
                                if (!isQueryAllGroupsAndUsers)
                                    params.put("g_want", "2");
                                params.put("z_type", "1");
                                params.put("parentid", selectedGroup.getZ_gid());

                                // Check out if there are children. if true, go ahead, otherwise, toast a message.
                                Task.getAllGroupsUsersByKey(params, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        JSONObject jsonObject = JSON.parseObject(s);
                                        String glist = jsonObject.getString("glist");
                                        if (!isQueryAllGroupsAndUsers) {
                                            grouplistView.setEnabled(true);
                                            return;
                                        }
                                        if (glist != null) {
                                            List<GroupInfo> temp = JSON.parseArray(glist, GroupInfo.class);
                                            boolean hasChild = temp != null && temp.size() > 0;
                                            if (!hasChild) {
                                                Toast.makeText(getActivity(), "已是最后一级", Toast.LENGTH_SHORT).show();
                                                grouplistView.setEnabled(true);
                                                return;
                                            }

                                            Intent intent = new Intent(getActivity(), GroupsQueryActivity.class);
                                            intent.putExtra("params", params);
                                            intent.putExtra("group.name", selectedGroup.getG_name());
                                            intent.putExtra("group.id", "group.org");
                                            intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                                            if (isQueryAllGroupsAndUsers) {
                                                getActivity().startActivity(intent);
                                                grouplistView.setEnabled(true);
                                            } else {
                                                grouplistView.setEnabled(true);
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                });

                                grouplistView.setEnabled(false);

                            }else if (selectedGroup.getZ_type().equals("2")) {
                                accessGroupSpace(selectedGroup);
                            } else if (selectedGroup.getZ_type().equals("3")) {
                                accessGroupSpace(selectedGroup);
                            } else if (selectedGroup.getZ_type().equals("4")) {
                                accessGroupSpace(selectedGroup);
                            }
                        }
                    }
                });

                serachAdapter.setList(serachList);
                grouplistView.setAdapter(serachAdapter);
                mListView.setVisibility(View.GONE);
            }

        }else {
            String gid = jsonObject.getString("groupId");
            String ygid = jsonObject.getString("y_gid");


            if (gid != null && ygid != null) {
                groupId = gid;
                y_groupId = ygid;
                Task.inviteJoinGroup(buildRequestParams(3), ContactsFragment.this, ContactsFragment.this);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                ArrayList<UserInfo> contacts = new ArrayList<UserInfo>();
                contacts.addAll(friendFaceAdapter.getUserInfoList());

                if (pageFrom != null && pageFrom.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {
                    Intent it = new Intent(getActivity(), ChatRoomSettingsActivity.class);
                    it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, contacts);
                    getActivity().setResult(getActivity().RESULT_OK, it);
                    parentActivity.finish();
                } else {
                    // 加上自己
                    contacts.add(0, mSession.getMySelfInfo());

                    Intent it = new Intent(getActivity(), GroupChatActivity.class);
                    it.putExtra(Constants.EXTRA_IM_GROUPID, groupId);
                    it.putExtra(Constants.EXTRA_IM_YGROUPID, y_groupId);
                    it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, String.valueOf(contacts.size()));
                    it.putExtra(Constants.EXTRA_IM_GROUP_NAME, getTempGroupChatRoomName(contacts));
                    it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
                    it.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, "0");
                    it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, contacts);
                    if (msgType != 0 && IMChatMessageDetail.TYPE_MSG_TEXT == msgType) {
                        it.putExtra(Constants.MSG_FORWORD, content);
                        it.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_TEXT);
                    } else {
                        it.putExtra(Constants.MSG_FORWORD, content);
                        it.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_PIC);
                    }
                    startActivity(it);
                    parentActivity.finish();
                }
            }
        }
    }


    private void setActionBar(){
        if (pageFrom != null && pageFrom.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS)) {
            parentActivity.initActionBar(R.drawable.icon_back, "确认(" + friendFaceAdapter.getUserInfoList().size() + ")", getString(R.string.title_create_chat_room));
        } else {
            parentActivity.initActionBar(R.drawable.icon_back, "确认(" + friendFaceAdapter.getUserInfoList().size() + ")", getString(R.string.create_new_chat));
        }
    }

    private void accessGroupSpace(GroupInfo group) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) group);
        intent.setClass(getActivity(), CommonSpaceActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public boolean showCustomActionBar() {
        setActionBar();

        parentActivity.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.finish();
            }
        });

        parentActivity.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = friendFaceAdapter.getUserInfoList().size();

                if (selected > 1) {// group chat
                    if (pageFrom != null && pageFrom.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {// join
                        Task.inviteJoinGroup(buildRequestParams(3), ContactsFragment.this, ContactsFragment.this);
                    } else {// create chat room and join
                        Task.getCreateGroup(buildRequestParams(2), ContactsFragment.this, ContactsFragment.this);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("正在发起群聊");
                        progressDialog.show();
                    }
                } else if (selected == 1) {// p 2 p
                    if (pageFrom != null && pageFrom.equals(Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS) && groupId != null) {
                        Task.inviteJoinGroup(buildRequestParams(3), ContactsFragment.this, ContactsFragment.this);
                    } else {// 单聊
                        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                        intent.putExtra(Constants.EXTRA_IM_YGROUPID, friendFaceAdapter.getUserInfoList().get(0).getY_voip());
                        intent.putExtra(Constants.EXTRA_IM_GROUP_NAME, friendFaceAdapter.getUserInfoList().get(0).getU_name());
                        intent.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊
                        intent.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) friendFaceAdapter.getUserInfoList().get(0));
                        intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);

                        if (msgType != 0 && IMChatMessageDetail.TYPE_MSG_TEXT == msgType) {
                            intent.putExtra(Constants.MSG_FORWORD, content);
                            intent.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_TEXT);
                        } else {
                            intent.putExtra(Constants.MSG_FORWORD, content);
                            intent.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_PIC);
                        }

                        startActivity(intent);
                        parentActivity.finish();
                    }
                }
            }
        });


        return false;
    }
}
