package com.zuzhili.ui.activity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.BackgroudTask;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.controller.GroupListAdapter;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.ui.activity.im.ChatRoomSettingsActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.gridview.ExpandableHeightGridView;
import com.zuzhili.ui.views.quickreturn.AbsListViewQuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.QuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.widget.AbsListViewScrollTarget;
import com.zuzhili.ui.views.quickreturn.widget.QuickReturnAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupsQueryActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    private String title;

    private static final int REQ_CODE_GROUP_CHAT = 0;

    @Override
    public DBHelper getDbHelper() {
        return super.getDbHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCustomActionBarCallback(this);

        title = getIntent().getStringExtra("group.name");
        String pageId = getIntent().getStringExtra("group.id");
        boolean isQueryAllGroupsAndUsers = getIntent().getBooleanExtra("isQueryAllGroupsAndUsers", true);
        HashMap params = (HashMap) getIntent().getSerializableExtra("params");

        setContentView(R.layout.activity_groups_query);
        if (savedInstanceState == null) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("params", params);
            bundle.putBoolean(Constants.ARG_SHOW_SEARCH_VIEW, true);
            bundle.putString("group.id", pageId);
            bundle.putBoolean("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
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
        return super.performClickOnLeft();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment
            implements Response.Listener<String>,
                       Response.ErrorListener,
                       AdapterView.OnItemClickListener,
                       AbsListView.OnScrollListener, PullRefreshListView.OnRefreshStateChangeListener {

        private HashMap params;
        private HashMap clone;
        private String pageId;
        private PullRefreshListView mListView;
        private GroupListAdapter adapter;
        private SearchView topSearchView;
        private boolean hasChild;
        private GroupInfo selectedGroup;
        private boolean isQueryAllGroupsAndUsers;
        private String parentId;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_list, null);
            topSearchView = (SearchView) view.findViewById(R.id.quickReturnTopTarget);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (pageId != null && pageId.equals("group.mychat")) {  // 我的群聊列表数据可能会变动
                try {
                    List<GroupInfo> groupInfos = ((BaseActivity) getActivity()).getDbHelper().getGroupInfoTable().queryGroups(Session.get(getActivity()).getListid(), "0");
                    if (groupInfos != null) {
                        LogUtils.i("get groups size: " + groupInfos.size());
                        adapter.setList(groupInfos);
                    } else {
                        adapter.clearList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mListView = (PullRefreshListView) getListView();
            mListView.setOnItemClickListener(this);

            adapter = new GroupListAdapter(getActivity(),
                    getListView(),
                    ImageCacheManager.getInstance().getImageLoader(),
                    params);
            setListAdapter(adapter);

            params = (HashMap) getArguments().getSerializable("params");
            parentId = (String) params.get("parentid");
            pageId = getArguments().getString("group.id");
            isQueryAllGroupsAndUsers = getArguments().getBoolean("isQueryAllGroupsAndUsers");
            LogUtils.d(params.toString());

            if (isQueryAllGroupsAndUsers) {
                Task.getAllGroupsUsersByKey(params, this, this);
            } else {
                Task.getGroupsUsersByKey(params, this, this);
            }

            // Search
            if (mListView.getHeaderViewsCount() < 2) {// 加入占位的header
                View head = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
                mListView.addHeaderView(head);
            }

            // avatar
            if (parentId != null && !parentId.equals("0")) {
                View grid = LayoutInflater.from(getActivity()).inflate(R.layout.admin_avatar, null);
                ExpandableHeightGridView gridView = (ExpandableHeightGridView) grid.findViewById(R.id.gv_admin_avatars);
                final AvatarAdapter avatarAdapter = new AvatarAdapter();
                gridView.setAdapter(avatarAdapter);
                HashMap map = new HashMap();
                map.put("spaceid", parentId);
                Task.querySpaceAdmins(map, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONObject obj = JSON.parseObject(s);
                        JSONArray admins = obj.getJSONArray("adminMapList");
                        Iterator it = admins.iterator();
                        while (it.hasNext()) {
                            JSONObject info = (JSONObject) it.next();
                            avatarAdapter.addItem(info);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                gridView.setExpanded(true);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject userJson = (JSONObject) parent.getAdapter().getItem(position);
                        String ids = (String) userJson.get("ids");
                        String listId = (String) userJson.get("listid");
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), CommonSpaceActivity.class);
                        UserInfo user = DBHelper.getInstance(getActivity()).getUserInfoTable().getUserByIds(ids, listId);
                        intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                        getActivity().startActivity(intent);
                    }
                });
                mListView.addHeaderView(grid);
            }


            mListView.setOnScrollListener(this);
            mListView.setOnRefreshStateChangeListener(this);
            mListView.setDividerHeight(DensityUtil.dip2px(getActivity(), 0.5f));

            mListView.setAdapter(new QuickReturnAdapter(adapter, 1));
            adapter.setListView(mListView);

            topSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(topSearchView.getWindowToken(), 0);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String key) {
                    HashMap clone = (HashMap) params.clone();

                    if (isQueryAllGroupsAndUsers) {
                        clone.put("nameKey", key);
                        Task.getAllGroupsUsersByKey(clone, PlaceholderFragment.this, PlaceholderFragment.this);
                    } else {
                        clone.put("keyword", key);
                        Task.getGroupsUsersByKey(clone, PlaceholderFragment.this, PlaceholderFragment.this);
                    }
                    return false;
                }
            });

            QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(mListView);
            quickReturnAttacher.addTargetView(topSearchView, AbsListViewScrollTarget.POSITION_TOP, DensityUtil.dip2px(getActivity(), 50));
            if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                final AbsListViewQuickReturnAttacher attacher = (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                attacher.addOnScrollListener(this);
                attacher.setOnItemClickListener(this);
            }
        }

        List<GroupInfo> groups = null;

        @Override
        public void onResponse(String s) {
            JSONObject jsonObject = JSON.parseObject(s);
            String glist = jsonObject.getString("glist");
            if (glist != null) {
                groups = JSON.parseArray(glist, GroupInfo.class);
                Runnable dbWorker = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((BaseActivity) getActivity()).getDbHelper().getGroupInfoTable().insertIMGroupInfos(groups);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new BackgroudTask().execute(dbWorker);
                adapter.setList(groups);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedGroup = (GroupInfo) parent.getAdapter().getItem(position);

            if (pageId.equals("group.mychat")) {
                Intent intent = new Intent(getActivity(), ChatRoomSettingsActivity.class);
                intent.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, selectedGroup.getG_type());
                intent.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
                intent.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) selectedGroup);
                intent.putExtra(Constants.EXTRA_IM_GROUPID, selectedGroup.getId());
                intent.putExtra(Constants.EXTRA_IM_YGROUPID, selectedGroup.getY_gid());
                intent.putExtra(Constants.EXTRA_IM_GROUPNNAME, selectedGroup.getG_name());
                intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);
                startActivityForResult(intent, REQ_CODE_GROUP_CHAT);
            } else if (pageId.equals("group.org")) {
                clone = (HashMap) params.clone();
                clone.put("parentid", selectedGroup.getZ_gid());

                // Check out if there are children. if true, go ahead, otherwise, toast a message.
                Task.getAllGroupsUsersByKey(clone, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONObject jsonObject = JSON.parseObject(s);
                        String glist = jsonObject.getString("glist");
                        if (!isQueryAllGroupsAndUsers) {
                            getListView().setEnabled(true);
                            return;
                        }
                        if (glist != null) {
                            List<GroupInfo> temp = JSON.parseArray(glist, GroupInfo.class);
                            hasChild = temp != null && temp.size() > 0;
                            if (!hasChild) {
                                Toast.makeText(getActivity(), "已是最后一级", Toast.LENGTH_SHORT).show();
                                getListView().setEnabled(true);
                                return;
                            }

                            Intent intent = new Intent(getActivity(), GroupsQueryActivity.class);
                            intent.putExtra("params", clone);
                            intent.putExtra("group.name", selectedGroup.getG_name());
                            intent.putExtra("group.id", "group.org");
                            intent.putExtra("isQueryAllGroupsAndUsers", isQueryAllGroupsAndUsers);
                            if (isQueryAllGroupsAndUsers) {
                                getActivity().startActivity(intent);
                                getListView().setEnabled(true);
                            } else {
                                getListView().setEnabled(true);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

                getListView().setEnabled(false);

            } else if (pageId.equals("group.group")) {
                accessGroupSpace(selectedGroup);
            } else if (pageId.equals("group.project")) {
                accessGroupSpace(selectedGroup);
            } else if (pageId.equals("group.activity")) {
                accessGroupSpace(selectedGroup);
            }
        }

        private void accessGroupSpace(GroupInfo group) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) group);
            intent.setClass(getActivity(), CommonSpaceActivity.class);
            getActivity().startActivity(intent);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        @Override
        public void onRefreshStateChanged(int state) {
            if (state == PullRefreshListView.DONE) {
                topSearchView.setVisibility(View.VISIBLE);
            } else {
                topSearchView.setVisibility(View.GONE);
            }
        }

        private class AvatarAdapter extends BaseAdapter {

            private List<JSONObject> items = new ArrayList<JSONObject>();

            public void setItems(List<JSONObject> items) {
                items.clear();
                items.addAll(items);
                notifyDataSetChanged();
            }

            public void addItem(JSONObject object) {
                items.add(object);
                notifyDataSetChanged();
            }

            public void removeItem(JSONObject object) {
                items.remove(object);
                notifyDataSetChanged();
            }

            private Context mContext = getActivity();

            public int getCount() {
                return items.size();
            }

            public JSONObject getItem(int position) {
                return items.get(position);
            }

            public long getItemId(int position) {
                return 0;
            }

            class ViewHolder {
                @ViewInject(R.id.avatar_image)
                ImageView imageView;

                @ViewInject(R.id.avatar_title)
                TextView titleView;

                @ViewInject(R.id.avatar_subtitle)
                TextView subTitleView;
            }

            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder;

                JSONObject item = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_view_item_admin_avatar, null);
                    holder = new ViewHolder();
                    ViewUtils.inject(holder, convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.titleView.setText(item.getString("name"));
                holder.subTitleView.setText(item.getString("chenghu"));
                String avatarUrl = item.getString("headimage");
                try {
                    ImageCacheManager.getInstance().getImageLoader()
                            .get(TextUtil.processNullString(avatarUrl),
                                    ImageLoader.getImageListener(
                                            holder.imageView,
                                            R.drawable.default_user_head_small,
                                            R.drawable.default_user_head_small));
                } catch (Exception e) {
                    holder.imageView.setImageResource(R.drawable.default_user_head_small);
                }

                return convertView;
            }
        }
    }

}
