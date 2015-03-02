package com.zuzhili.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.MenuUtil;
import com.zuzhili.model.menu.Menu;
import com.zuzhili.model.menu.SubMenu;
import com.zuzhili.provider.Contract;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.views.MenuView;
import android.widget.ImageView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

public abstract class MenuDrawerActivity extends BaseActivity {

    private static final String TAG = "MenuDrawerActivity";
	
	private static final String STATE_ACTIVE_POSITION =
            "net.simonvt.menudrawer.samples.LeftDrawerSample.activePosition";

    protected PullToRefreshExpandableListView pullToRefreshExpandableListView;

    protected ProgressBar progressBar;

    protected LayoutInflater mLayoutInflater;

    private ExpandableListView expandableLV;

    private static final int[] commuCategory = new int[] {R.string.communication_category_commu};

    private static final int[] cooperationCategory = new int[] {R.string.coopration_category_vote};

    private static final int[] systemCategory = new int[] {R.string.system_category_more};

    private static final String[] commuCateTags = new String[] {"tag.feed"};

    private static final String[] cooperationCateTags = new String[] {"tag.vote"};

    private static final String[] systemCateTags = new String[] {"tag.more"};

    private static final String[] webCateTags = new String[] {"tag.webview"};

    /** 主菜单列表 */
    protected Map<Integer, Menu> menus = new HashMap<Integer, Menu>();

    /** 二级子菜单 */
    protected Map<Menu, Map<Integer, SubMenu>> subMenus = new HashMap<Menu, Map<Integer, SubMenu>>();

    private final float MENU_CHILD_HEIGHT = 38;

    protected MenuDrawer mMenuDrawer;

    /** 当前选中的菜单列表项 */
    private int mActivePosition = 0;
    
    /** 点击菜单展开标记 */
    protected final int CODE_EXPAND = 0;
    /** 点击菜单收缩 */
    protected final int CODE_COLLAPSE = 1;

    private MenuListener mListener;

    private ExpandableAdapter mAdapter;

    private View userBar;

    private int loadSubmenuCount = 0;

    public interface MenuListener {
        void onActiveViewChanged(View v);
    }

    interface MenuQuery {
        String[] PROJECTION = new String[] {
                BaseColumns._ID,
                Contract.Menu.MENU_COLUMN_VOTE,
                Contract.Menu.MENU_COLUMN_HALL,
                Contract.Menu.MENU_COLUMN_USERNAME,
                Contract.Menu.MENU_COLUMN_MORE,
                Contract.Menu.MENU_COLUMN_ROLEID,
                Contract.Menu.MENU_COLUMN_CONTACTS,
                Contract.Menu.MENU_COLUMN_LIST,
                Contract.Menu.MENU_COLUMN_SANWEI_USERID
        };

        final int _ID = 0;
        final int VOTE = 1;
        final int HALL = 2;
        final int USERNAME = 3;
        final int MORE = 4;
        final int ROLEID = 5;
        final int CONTACTS = 6;
        final int LIST = 7;
        final int SANWEI_USERID = 8;
    }

    interface SubMenuQuery {
        String[] PROJECTION = new String[] {
                BaseColumns._ID,
                Contract.SubMenu.SUBMENU_COLUMN_ID,
                Contract.SubMenu.SUBMENU_COLUMN_NAME,
                Contract.SubMenu.SUBMENU_COLUMN_LEVEL,
                Contract.SubMenu.SUBMENU_COLUMN_PARENTID,
                Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL,
                Contract.SubMenu.SUBMENU_COLUMN_ICON,
                Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL
        };

        final int _ID = 0;
        final int SUBMENU_ID = 1;
        final int NAME = 2;
        final int LEVEL = 3;
        final int PARENTID = 4;
        final int SEARCHURL = 5;
        final int ICON = 6;
        final int CATEGORYURL = 7;
    }

    public void setListener(MenuListener listener) {
        mListener = listener;
    }

    public void setActivePosition(int activePosition) {
        mActivePosition = activePosition;
    }

    @Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
        }
        initData();
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, getDrawerPosition(), getDragMode());
        mMenuDrawer.setMenuView(R.layout.listview_expandable_layout2);

        pullToRefreshExpandableListView = (PullToRefreshExpandableListView) mMenuDrawer.getMenuView().findViewById(R.id.exp_list_view);
        progressBar = (ProgressBar) mMenuDrawer.getMenuView().findViewById(R.id.progressbar);
        pullToRefreshExpandableListView.setShowIndicator(false);
        progressBar.setVisibility(View.VISIBLE);
        // 菜单下拉刷新
        pullToRefreshExpandableListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {

                Bundle args = new Bundle();
                args.putString("url", Task.API_SANWEI_HOST + "account/mobileLoginAction.json?userid=" + mSession.getUid() + "&headurl=" + mSession.getUserhead());
                SyncUtils.requestManualSync(args);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int j = 3;
                        for (Map.Entry<Integer, Menu> entry : menus.entrySet()) {
                            if (entry.getKey() == 0 || entry.getKey() == 1) {
                                continue;
                            }
                            Bundle args = new Bundle();
                            args.putString("parentid", String.valueOf(entry.getValue().id));
                            args.putInt("menuPosition", entry.getKey());
                            getLoaderManager().restartLoader(j++, args, new ThirdLevelSubmenusLoaderCallback());
                        }
                    }
                }, 3000);
            }
        });
        expandableLV = pullToRefreshExpandableListView.getRefreshableView();

        userBar = getLayoutInflater().inflate(R.layout.user_bar, null);
        userBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuDrawerActivity.this, CommonSpaceActivity.class);
                intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) mSession.getMySelfInfo());
                MenuDrawerActivity.this.startActivity(intent);
            }
        });

        expandableLV.addHeaderView(userBar);
        updateUserBar();
        mAdapter = new ExpandableAdapter();
        expandableLV.setAdapter(mAdapter);
        expandableLV.setGroupIndicator(null);
        mMenuDrawer.getMenuContainer().setBackgroundResource(R.drawable.menu_bg);
        mMenuDrawer.setDropShadow(R.color.light_grey);
        mMenuDrawer.setDropShadowSize(2);
        mMenuDrawer.setDrawOverlay(true);
        mMenuDrawer.setHorizontalFadingEdgeEnabled(true);

        expandableLV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                onMenuItemClicked(subMenus.get(menus.get(groupPosition)).get(childPosition));
                v.setSelected(true);
                return true;
            }
        });
        getLoaderManager().initLoader(1, null, new MenuLoaderCallback());
        getLoaderManager().restartLoader(2, null, new SecondLevelSubmenusLoaderCallback());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int j = 3;
                for (Map.Entry<Integer, Menu> entry : menus.entrySet()) {
                    if (entry.getKey() == 0 || entry.getKey() == 1) {
                        continue;
                    }
                    Bundle args = new Bundle();
                    args.putString("parentid", String.valueOf(entry.getValue().id));
                    args.putInt("menuPosition", entry.getKey());
                    getLoaderManager().restartLoader(j++, args, new ThirdLevelSubmenusLoaderCallback());
                }
            }
        }, 3000);
	}

    private void updateUserBar() {
        TextView userName = (TextView) userBar.findViewById(R.id.bar_user_name);
        userName.setText(mSession.getUserName());

//        TextView userPosition = (TextView) userBar.findViewById(R.id.bar_user_position);

        ImageView avatarImg = (ImageView) userBar.findViewById(R.id.bar_user_avator);
        ImageCacheManager.getInstance().getImageLoader().get(TextUtil.processNullString(mSession.getUserhead()), ImageLoader.getImageListener(avatarImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserBar();
    }

    private void initData() {

        List<Menu> menuList= MenuUtil.getMenuList();
        for (int i = 0; i < menuList.size(); i++) {
            // 初始化一级菜单列表
            Menu menu=menuList.get(i);
            menus.put(i, menu);

            if (menu.lname.equals(getString(R.string.category_communication))) {
                Map<Integer, SubMenu> children = new HashMap<Integer, SubMenu>();
                for (int j = 0; j < commuCategory.length; j++) {
                    // 初始化二级菜单列表
                    SubMenu subMenu = new SubMenu();
                    subMenu.lname = getString(commuCategory[j]);
                    subMenu.setTag(commuCateTags[j]);
                    children.put(j, subMenu);
                    subMenus.put(menu, children);
                }
            }

            if (menu.lname.equals(getString(R.string.category_cooperation))) {

                Map<Integer, SubMenu> children = new HashMap<Integer, SubMenu>();
                for (int j = 0; j < cooperationCategory.length; j++) {
                    // 初始化二级菜单列表
                    SubMenu subMenu = new SubMenu();
                    subMenu.lname = getString(cooperationCategory[j]);
                    subMenu.setTag(cooperationCateTags[j]);
                    children.put(j, subMenu);
                    subMenus.put(menu, children);
                }
            }

            if (menu.lname.equals(getString(R.string.category_system))) {
                Map<Integer, SubMenu> children = new HashMap<Integer, SubMenu>();
                for (int j = 0; j < systemCategory.length; j++) {
                    // 初始化二级菜单列表
                    SubMenu subMenu = new SubMenu();
                    subMenu.lname = getString(systemCategory[j]);
                    subMenu.setTag(systemCateTags[j]);
                    children.put(j, subMenu);
                    subMenus.put(menu, children);
                }
            }
        }

        mLayoutInflater = LayoutInflater.from(this);
    }

    class ExpandableAdapter extends BaseExpandableListAdapter {

        public ExpandableAdapter() {
            super();
        }

        public Object getChild(int groupPosition, int childPosition) {
            return subMenus.get(menus.get(groupPosition)).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            if(subMenus != null && subMenus.size() > 0) {
                if (subMenus.get(menus.get(groupPosition)) != null && subMenus.get(menus.get(groupPosition)).size() > 0) {
                    return subMenus.get(menus.get(groupPosition)).size();
                } else {
                    return 0;
                }
            }
            return 0;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            MenuView subMenu = (MenuView) convertView;
            if (subMenu == null) {
                View container = mLayoutInflater.inflate(R.layout.submenu_item, null);
                subMenu = (MenuView) container.findViewById(R.id.submenu);
            }

            subMenu.setName(((SubMenu) getChild(groupPosition, childPosition)).lname);
            return subMenu;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            MenuView menu = (MenuView) convertView;
            View container = null;
            if (menu == null) {
                if (groupPosition == 0) {
                    container = mLayoutInflater.inflate(R.layout.menu_item_holder, null);
                } else {
                    container = mLayoutInflater.inflate(R.layout.menu_item, null);
                }
                menu = (MenuView) container.findViewById(R.id.menu);
            }

            menu.setName(menus.get(groupPosition).lname);
            expandableLV.expandGroup(groupPosition);
            LogUtils.i("position: " + groupPosition + ", title: " + menus.get(groupPosition).lname);
            return menu;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public Object getGroup(int groupPosition) {
            return menus.get(groupPosition);
        }

        public int getGroupCount() {
            return menus.size();
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
	
	protected abstract void onMenuItemClicked(SubMenu menu);

    protected abstract int getDragMode();

    protected abstract Position getDrawerPosition();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
    }

    class SecondLevelSubmenusLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selectionClause = "submenu_column_level=?";
            String[] selectionArgs = new String[] {"2"};
            return new CursorLoader(MenuDrawerActivity.this,
                    Contract.SubMenu.CONTENT_URI,
                    SubMenuQuery.PROJECTION,
                    selectionClause,
                    selectionArgs,
                    Contract.SubMenu.SUBMENU_COLUMN_ID + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                data.moveToPosition(-1);
                int i = 2;
                while (data.moveToNext()) {
                    int id = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ID));
                    String name = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_NAME));
                    int level = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_LEVEL));
                    int parentId = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_PARENTID));
                    String icon = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ICON));
                    Menu menu = new Menu();
                    menu.id = id;
                    menu.lname = name;
                    menu.llevel = level;
                    menu.lparentid = parentId;
                    menu.licon = icon;
                    menus.put(i++, menu);
                }
            }
            Log.d(TAG, "Menus size is: " + menus.size());

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    class ThirdLevelSubmenusLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private int menuPosition = 0;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            menuPosition = args.getInt("menuPosition");
            String parentId = args.getString("parentid");
            String selectionClause = "submenu_column_parentid=?";
            String[] selectionArgs = new String[] {parentId};
            return new CursorLoader(MenuDrawerActivity.this,
                    Contract.SubMenu.CONTENT_URI,
                    SubMenuQuery.PROJECTION,
                    selectionClause,
                    selectionArgs,
                    Contract.SubMenu.SUBMENU_COLUMN_ID + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            progressBar.setVisibility(View.GONE);
            pullToRefreshExpandableListView.onRefreshComplete();
            if (data != null) {
                data.moveToPosition(-1);
                int i = 0;
                Map<Integer, SubMenu> children = new HashMap<Integer, SubMenu>();
                while (data.moveToNext()) {
                    int id = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ID));
                    String name = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_NAME));
                    int level = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_LEVEL));
                    int parentId = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_PARENTID));
                    String icon = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ICON));
                    String searchUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL));
                    String categoryUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL));
                    SubMenu subMenu = new SubMenu();
                    subMenu.id = id;
                    subMenu.lname = name;
                    subMenu.llevel = level;
                    subMenu.lparentid = parentId;
                    subMenu.licon = icon;
                    subMenu.searchurl = searchUrl;
                    subMenu.lurl = categoryUrl;
                    if (subMenu.lname.equals("动态") || subMenu.lname.equals("申报") || subMenu.lname.equals("咨询")) {
                        subMenu.setTag(Constants.TAG_MENU_RESOURCE);
                    } else {
                        subMenu.setTag(webCateTags[0]);
                    }
                    children.put(i++, subMenu);
                }
                subMenus.put(menus.get(menuPosition), children);
            }
            Log.d(TAG, "Menu name is :" + menus.get(menuPosition).lname + ", SubMenus size is: " + subMenus.get(menus.get(menuPosition)).size());
            mAdapter = new ExpandableAdapter();
            expandableLV.setAdapter(mAdapter);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    class SubMenuLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MenuDrawerActivity.this, Contract.SubMenu.CONTENT_URI, SubMenuQuery.PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                data.moveToPosition(-1);
                if (data.moveToFirst()) {
                    String id = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ID));
                    String name  = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_NAME));
                    String level  = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_LEVEL));
                    String parentId = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_PARENTID));
                    String searchUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL));
                    String icon = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ICON));
                    String categoryUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL));

                    Toast.makeText(MenuDrawerActivity.this, "id=" + id +
                                    ", name=" + name +
                                    ", level=" + level +
                                    ", parentId=" + parentId +
                                    ", searchUrl=" + searchUrl +
                                    ", icon=" + icon +
                                    ", categoryUrl=" + categoryUrl,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "id=" + id +
                            ", name=" + name +
                            ", level=" + level +
                            ", parentId=" + parentId +
                            ", searchUrl=" + searchUrl +
                            ", icon=" + icon +
                            ", categoryUrl=" + categoryUrl);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    class MenuLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MenuDrawerActivity.this, Contract.Menu.CONTENT_URI, MenuQuery.PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                data.moveToPosition(-1);
                if (data.moveToFirst()) {
                    String vote = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_VOTE));
                    String username = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_USERNAME));
                    String sanweiUserId = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_SANWEI_USERID));
                    String roleId = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_ROLEID));
                    String list = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_LIST));
                    String hall = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_HALL));
                    String userName = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_USERNAME));
                    String contact = data.getString(data.getColumnIndex(Contract.Menu.MENU_COLUMN_CONTACTS));
                    Log.d(TAG, "vote=" + vote +
                            ", username=" + username +
                            ", sanweiUserId=" + sanweiUserId +
                            ", roleId=" + roleId +
                            ", list=" + list +
                            ", hall=" + hall +
                            ", userName=" + userName +
                            ", contact=" + contact);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

}
