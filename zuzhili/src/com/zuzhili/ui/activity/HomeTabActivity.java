package com.zuzhili.ui.activity;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.menu.SubMenu;
import com.zuzhili.provider.Contract;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.im.NewConversationActivity;
import com.zuzhili.ui.activity.loginreg.WebViewActivity;
import com.zuzhili.ui.activity.publish.PublishMainActivity;
import com.zuzhili.ui.activity.social.ConstructSociaActivity;
import com.zuzhili.ui.activity.social.SocialsActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.fragment.MessageListFrg;
import com.zuzhili.ui.fragment.NewsTimeLineFrg;
import com.zuzhili.ui.fragment.SocialFrg;
import com.zuzhili.ui.fragment.im.ContactsFragment;
import com.zuzhili.ui.fragment.im.GroupListFrg;
import com.zuzhili.ui.views.BadgeView;
import com.zuzhili.ui.views.CustomDialog;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeTabActivity extends MenuDrawerActivity implements
        BaseActivity.TimeToShowActionBarCallback,
        FixedOnActivityResultBugFragment.OnActionBarUpdateListener,
        BaseActivity.HandleChatConnectionCallback,
        SocialFrg.OnChangeSocialListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "HomeTabActivity";

    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;		// bottom tabs

    /** 动态一级筛选条件, "1"表示我关注的， "2"表示全部动态 */
    private int region;
    /** 动态二级筛选条件，"0"全部 "3"文字 "6"图片 "9"文件 "16"音频 "19"视频 */
    private int appType;
    /** 请求是否已完成标志 */
    private int currentRegion;
    private int currentAppType;
    private boolean isFilterChanged = false;

    /** 筛选对话框 */
    private CustomDialog filterDialog;

    private NewsFilterOnClickListener newsFilterOnClickListener;

    /** 定义数组来存放Fragment界面 */
    @SuppressWarnings("rawtypes")
    private Class fragmentArray[];

    private ArrayList<BadgeView> badgeViews = new ArrayList<BadgeView>();

    /** 定义数组来存放按钮图片 */
    private int mDrawableArray[] = {
            R.drawable.btn_trend_selector
            , R.drawable.btn_letter_selector
            , R.drawable.btn_personal_selector
            , R.drawable.btn_message_selector};

    /** Tab选项卡的文字 */
    private int mTabTextResIdArray[];

    /**
     * Projection for querying the content provider.
     */
    private static final String[] PROJECTION = new String[]{
            Contract.Entry._ID,
            Contract.Entry.COLUMN_NAME_LIST_ID,
            Contract.Entry.COLUMN_NAME_IDS,
            Contract.Entry.COLUMN_NAME_REFRESH_COUNT,
            Contract.Entry.COLUMN_NAME_MSG_COUNT,
            Contract.Entry.COLUMN_NAME_AT_FEED_COUNT,
            Contract.Entry.COLUMN_NAME_AT_COMMENT_COUNT,
            Contract.Entry.COLUMN_NAME_COMMENT_COUNT,
            Contract.Entry.COLUMN_NAME_NOTIFY_COUNT,
            Contract.Entry.COLUMN_NAME_APPROVAL_COUNT,
            Contract.Entry.COLUMN_NAME_APPROVAL_REPLY_COUNT,
            Contract.Entry.COLUMN_NAME_ALL_COUNT
    };

    // Column indexes. The index of a column in the Cursor is the same as its relative position in
    // the projection.
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_LIST_ID = 1;
    private static final int COLUMN_IDS = 2;
    private static final int COLUMN_REFRESH_COUNT = 3;
    private static final int COLUMN_MSG_COUNT = 4;
    private static final int COLUMN_AT_FEED_COUNT = 5;
    private static final int COLUMN_AT_COMMENT_COUNT = 6;
    private static final int COLUMN_COMMENT_COUNT = 7;
    private static final int COLUMN_NOTIFY_COUNT = 8;
    private static final int COLUMN_APPROVAL_COUNT = 9;
    private static final int COLUMN_APPROVAL_REPLY_COUNT = 10;
    private static final int COLUMN_ALL_COUNT = 11;

    private static final String STATE_CURRENT_FRAGMENT = "net.simonvt.menudrawer.samples.FragmentSample";

    public ToggleMenuImgBtnOnClickListener toggleMenuImgBtnOnClickListener = new ToggleMenuImgBtnOnClickListener();

    public PublishBtnOnClickListener publishBtnOnClickListener = new PublishBtnOnClickListener();

    public SelectMemberOnClickListener selectMemberOnClickListener = new SelectMemberOnClickListener();

    public TrendsFilterTitleOnClickListener trendsFilterTitleOnClickListener = new TrendsFilterTitleOnClickListener();

    public SelectSocialTitleOnClickListener selectSocialTitleOnClickListener = new SelectSocialTitleOnClickListener();

    public SearchButtonOnClickListener searchButtonOnClickListener = new SearchButtonOnClickListener();

    //发布审批监听器
    public PublishApprovalListener publishApprovalListener =new PublishApprovalListener();

    private List<TabSpec> tabSpecs;

    private Map<String, Fragment> fragmentsInTabHost;

    private String currentFragmentTag;

    private String bottomTabFragmentTag;

    /** 按返回键退出应用的时间 */
    private long exitTime;

    private BadgeView badgeView;

    String atFeedCount = "0";
    String atCommentCount = "0";
    String commentCount = "0";
    String messageCount = "0";
    String unreadChatMsgCount = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer.setContentView(R.layout.activity_home);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        setChatConnectionCallback(this);
        initData(savedInstanceState);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSession.setCurrentActivityName(this.getLocalClassName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment != null) {
            if (fragmentsInTabHost == null) {
                fragmentsInTabHost = new HashMap<String, Fragment>();
            }
            if (fragment instanceof NewsTimeLineFrg) {
                fragmentsInTabHost.put(Constants.TAG_FEED, fragment);
            }
            if (fragment instanceof GroupListFrg) {
                fragmentsInTabHost.put(Constants.TAG_CHAT_CONTACTS, fragment);
            }
            if (fragment instanceof ContactsFragment) {
                fragmentsInTabHost.put(Constants.TAG_ALL_CONTACTS, fragment);
            }
            if (fragment instanceof MessageListFrg) {
                fragmentsInTabHost.put(Constants.TAG_MESSAGE_LIST, fragment);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        mSession.setCurrentActivityName(this.getLocalClassName());
        detachFragment(getFragment(currentFragmentTag));
        mTabHost.setCurrentTab(0);
        currentFragmentTag = Constants.TAG_FEED;
        bottomTabFragmentTag = Constants.TAG_FEED;
        updateActionBar();
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);

        // 更新tabHost里4个fragment数据
        if (fragmentsInTabHost.get(Constants.TAG_FEED) != null) {
            ((NewsTimeLineFrg)fragmentsInTabHost.get(Constants.TAG_FEED)).update();
            setOnClickMiddleListener(new TrendsFilterTitleOnClickListener());
        }

        //是否跳转会话列表
        if(getIntent() != null && "ok".equals(getIntent().getStringExtra(Constants.TO_GROUPSLISTFRG))){
            String changeListId=getIntent().getStringExtra(Constants.CHANGE_SOCIAL);
            if(!TextUtils.isEmpty(changeListId)){
                //切换社区
                Intent it=new Intent(this,SocialsActivity.class);
                it.putExtra(Constants.CHANGE_SOCIAL,changeListId);
                startActivity(it);
            }else {
                currentFragmentTag = Constants.TAG_MESSAGE_LIST;
                bottomTabFragmentTag = currentFragmentTag;
                updateActionBar();
                mTabHost.setCurrentTab(1);
            }
        }
    }

    /**
     * 数据初始化
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initData(Bundle inState) {
        String[] tags = new String[] {Constants.TAG_FEED, Constants.TAG_MESSAGE_LIST, Constants.TAG_ALL_CONTACTS, Constants.TAG_CHAT_CONTACTS};
        mTabTextResIdArray = new int[] {R.string.home_tab_feed, R.string.home_tab_message, R.string.home_tab_contacts,R.string.home_tab_msg_list };
        fragmentArray = new Class[] {NewsTimeLineFrg.class, GroupListFrg.class,ContactsFragment.class, MessageListFrg.class };
        tabSpecs = new ArrayList<TabSpec>();
        fragmentsInTabHost = new HashMap<String, Fragment>();
        // 为每一个Tab按钮设置图标、文字和内容
        for (int i = 0; i < fragmentArray.length; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(getString(mTabTextResIdArray[i])).setIndicator(getTabItemView(i));
            tabSpecs.add(tabSpec);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mMenuDrawer.setDrawerIndicatorEnabled(true);

        View titleRight=getTitleTxtxV();
        badgeView = new BadgeView(HomeTabActivity.this, titleRight);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
        mMenuDrawer.openMenu();
        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_CLOSED) {
                    badgeView.hide();
                    if (currentFragmentTag.equals(Constants.TAG_FEED)
                            || currentFragmentTag.equals(Constants.TAG_CHAT_CONTACTS)
                            || currentFragmentTag.equals(Constants.TAG_MEMBERS)
                            || currentFragmentTag.equals(Constants.TAG_MORE)
                            || currentFragmentTag.equals(Constants.TAG_MESSAGE_LIST)
                            || currentFragmentTag.equals(Constants.TAG_APPROVAL)
                            || currentFragmentTag.equals(Constants.TAG_ALL_CONTACTS)
                            || currentFragmentTag.equals(Constants.TAG_PERSONAL_SPACE)) {
                        updateActionBar();
                        return;
                    } else {    // 只在home页面才能拉动抽屉
                        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
                    }
                } else if (newState == MenuDrawer.STATE_OPEN) {
                    SyncUtils.TriggerRefresh();
//                    Bundle args = new Bundle();
//                    args.putString("url", Task.API_SANWEI_HOST + "account/mobileLoginAction.json?userid=" + mSession.getUid());
//                    SyncUtils.requestManualSync(args);
                    initActionBar(R.drawable.icon_navigation, R.drawable.icon_action_search, getString(R.string.title_vips_hall), true);
                    setOnClickMiddleListener(selectSocialTitleOnClickListener);
                    setOnClickRightListener(searchButtonOnClickListener);

                    return;
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                // Do nothing
            }
        });

        getLoaderManager().initLoader(0, null, this);
        // Create account, if needed
        SyncUtils.CreateSyncAccount(this);
        SyncUtils.TriggerRefresh();

        Bundle args = new Bundle();
        args.putString("url", Task.API_SANWEI_HOST + "account/mobileLoginAction.json?userid=" + mSession.getUid());
        SyncUtils.requestManualSync(args);
    }

    private void updateMenuView() {
        if(mMenuDrawer.getDrawerState()==MenuDrawer.STATE_OPEN ){
            int message=Integer.valueOf(messageCount);
            if(TextUtils.isEmpty(unreadChatMsgCount)){
                unreadChatMsgCount="0";
            }
            int chat=Integer.valueOf(unreadChatMsgCount);
            if((message+chat)>0){
                badgeView.setText(String.valueOf(message+chat));
                badgeView.show();
            } else {
                badgeView.hide();
            }
        }
    }

    /**
     * 初始化组件 
     */
    private void initView() {
        currentFragmentTag = Constants.TAG_FEED;
        bottomTabFragmentTag = Constants.TAG_FEED;
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //得到fragment的个数
        int count = fragmentArray.length;
        Bundle bundle = null;
        for(int i = 0; i < count; i++) {
            // 将Tab按钮添加进Tab选项卡中
            if (i == 2) {
                bundle = new Bundle();
                bundle.putSerializable("CONTACT_MODE", ContactsFragment.ContactMode.MODE_VIEW);
            }
            mTabHost.addTab(tabSpecs.get(i), fragmentArray[i], bundle);

            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.home_tab_selector);
            // hide tab strip
            mTabHost.getTabWidget().setStripEnabled(false);
//          mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }
        mTabHost.setOnTabChangedListener(new TouchTabListener());
        mTabHost.setCurrentTabByTag(currentFragmentTag);
        initFilterDialog();
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_navigation, R.drawable.icon_write, getString(R.string.home_tab_feed), true);
        setOnClickLeftListener(new ToggleMenuImgBtnOnClickListener());
        setOnClickMiddleListener(new TrendsFilterTitleOnClickListener());
        setOnClickRightListener(new PublishBtnOnClickListener());
        return false;
    }

    @Override
    public void shouldUpdateActionBar() {
        currentFragmentTag = bottomTabFragmentTag;
        updateActionBar();
    }

    public String getCurrentFragmentTag() {
        return currentFragmentTag;
    }

    public void setCurrentFragmentTag(String currentFragmentTag) {
        this.currentFragmentTag = currentFragmentTag;
    }

    /**
     * 切换社区
     */
    @Override
    public void onSocialChange() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void handleChatConnection() {
        updateActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = "listid = ? AND ids = ?";
        String[] selectionArgs = new String[] {mSession.getListid(), mSession.getIds()};
        return new CursorLoader(this, Contract.Entry.CONTENT_URI, PROJECTION, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogUtils.i("CursorLoader load data finished");
        assert data != null;

        if (data.moveToNext()) {
            atFeedCount = data.getString(COLUMN_AT_FEED_COUNT);
            atCommentCount = data.getString(COLUMN_AT_COMMENT_COUNT);
            commentCount = data.getString(COLUMN_COMMENT_COUNT);
            unreadChatMsgCount =  data.getString(COLUMN_MSG_COUNT);
            messageCount = String.valueOf(Integer.valueOf(atFeedCount) + Integer.valueOf(atCommentCount) + Integer.valueOf(commentCount));
        }
        updateMenuView();
        updateTabItemView(1, unreadChatMsgCount);
        updateTabItemView(3, messageCount);

        if((getAtFeedCount()+getAtCommentCount()+getCommentCount())>0){
            mSession.setUIShouldUpdate(Constants.PAGE_AT_CONTENT);
        }
    }


    public int getAtFeedCount(){
        return Integer.valueOf(atFeedCount);
    }
    public int getAtCommentCount(){
        return Integer.valueOf(atCommentCount);
    }
    public int getCommentCount(){
        return Integer.valueOf(commentCount);
    }
    public List<Integer> getMessageCount(){
        List<Integer> messageCount=new ArrayList<Integer>();
        messageCount.add(getAtFeedCount() + getAtCommentCount());
        messageCount.add(getCommentCount());

        return  messageCount;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class TouchTabListener implements OnTabChangeListener {

        @Override
        public void onTabChanged(String tabId) {
            if(tabId.equals(getString(mTabTextResIdArray[0]))) {
                currentFragmentTag = Constants.TAG_FEED;

            } else if(tabId.equals(getString(mTabTextResIdArray[1]))) {
                currentFragmentTag = Constants.TAG_MESSAGE_LIST;

            } else if (tabId.equals(getString(mTabTextResIdArray[2]))) {
                currentFragmentTag = Constants.TAG_ALL_CONTACTS;

            } else {
                currentFragmentTag = Constants.TAG_CHAT_CONTACTS;
            }
            bottomTabFragmentTag = currentFragmentTag;
            updateActionBar();
            mTabHost.setCurrentTabByTag(currentFragmentTag);
        }

    }

    /**
     * 给Tab按钮设置图标和文字 
     */
    private View getTabItemView(int index){
        View view = mLayoutInflater.inflate(R.layout.view_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_tab_widget_icon);
        imageView.setImageResource(mDrawableArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.txt_tab_widget_content);
        textView.setText(getString(mTabTextResIdArray[index]));

        View viewHolder = view.findViewById(R.id.view_holder);
        BadgeView badgeView = new BadgeView(this, viewHolder);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeViews.add(badgeView);
        return view;
    }

    private void updateTabItemView(int index, String count) {
        BadgeView badgeView = badgeViews.get(index);

        if(index==3 && fragmentsInTabHost.get(Constants.TAG_MESSAGE_LIST)!=null){
            ((MessageListFrg)fragmentsInTabHost.get(Constants.TAG_MESSAGE_LIST)).reFresh();
        }

        if (count != null && Integer.valueOf(count) > 0) {
            badgeView.setText(count);
            badgeView.show();
        } else {
            badgeView.hide();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_FRAGMENT, currentFragmentTag);
    }
    //覆写父类方法,执行左侧菜单项点击事件
    @Override
    protected void onMenuItemClicked(final SubMenu menu) {

        if (menu.getTag().equals(Constants.TAG_WEBVIEW)) {
            Intent it = new Intent(HomeTabActivity.this, WebViewActivity.class);
            it.putExtra("url", menu.lurl);
            it.putExtra("searchUrl", menu.searchurl);
            startActivity(it);
            return;
        }

        if (menu.getTag().equals(Constants.TAG_MENU_RESOURCE)) {
            Intent it = new Intent(HomeTabActivity.this, FourthCategoryListActivity.class);
            it.putExtra("parent_id", String.valueOf(menu.id));
            it.putExtra("title", menu.lname);
            startActivity(it);
            return;
        }

        if (currentFragmentTag != null) {
            // 移除上一个fragment
            if (!currentFragmentTag.equals(menu.getTag())) {
                if (currentFragmentTag.equals(Constants.TAG_VOTE)) {
                    Bundle args = new Bundle();
                    String url = String.format("http://www.zuzhili.com/vote/%1$s/%2$s/votePhone_list.shtml", mSession.getListid(), mSession.getIds());
                    args.putString(Constants.EXTRA_URL, url);
                    args.putString(Constants.EXTRA_FRAGMENT_TAG, Constants.TAG_VOTE);
                    detachFragment(getFragment(args));
                } else {
                    detachFragment(getFragment(currentFragmentTag));
                }
            }
            currentFragmentTag = menu.getTag();
            mMenuDrawer.closeMenu();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!currentFragmentTag.equals(Constants.TAG_FEED)) {
                        updateActionBar();
                        if (currentFragmentTag.equals(Constants.TAG_VOTE)) {
                            Bundle args = new Bundle();
                            String url = String.format("http://test.zuzhili.com/vote/%1$s/%2$s/votePhone_list.shtml", mSession.getListid(), mSession.getIds());
                            args.putString(Constants.EXTRA_URL, url);
                            args.putString(Constants.EXTRA_FRAGMENT_TAG, Constants.TAG_VOTE);
                            attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(args), menu.getTag());
                        } else {
                            attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(menu.getTag()), menu.getTag());
                        }
                    } else {
                        mTabHost.setCurrentTab(0);
                        updateActionBar();
                        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                        currentFragmentTag = Constants.TAG_FEED;
                    }
                }
            }, Constants.SHORT_DELAY);

        }
    }

    /**
     * 抽屉抽出内容（MenuDrawer.MENU_DRAG_WINDOW 对应滑动整个窗口）
     */
    @Override
    protected int getDragMode() {
        return MenuDrawer.MENU_DRAG_CONTENT;
    }

    /**
     * 抽屉从左侧划出
     */
    @Override
    protected Position getDrawerPosition() {
        return Position.LEFT;
    }

    /**
     * 返回关闭drawer，或者移除最上层fragment
     */
    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }
        if (!(currentFragmentTag.equals(Constants.TAG_FEED)
                || currentFragmentTag.equals(Constants.TAG_MESSAGE_LIST)
                || currentFragmentTag.equals(Constants.TAG_ALL_CONTACTS)
                || currentFragmentTag.equals(Constants.TAG_CHAT_CONTACTS))) {

            if (currentFragmentTag.equals(Constants.TAG_VOTE)) {
                Bundle args = new Bundle();
                String url = String.format("http://www.zuzhili.com/vote/%1$s/%2$s/votePhone_list.shtml", mSession.getListid(), mSession.getIds());
                args.putString(Constants.EXTRA_URL, url);
                args.putString(Constants.EXTRA_FRAGMENT_TAG, Constants.TAG_VOTE);
                removeFragment(getFragment(args));
            } else {
                detachFragment(getFragment(currentFragmentTag));
            }
            currentFragmentTag = bottomTabFragmentTag;
            updateActionBar();
            return;
        }
        // not actually exit app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_ANIM_DEFAULT, true);
        startActivity(intent);
//        if((System.currentTimeMillis() - exitTime) > 2000) {
//            exitTime = System.currentTimeMillis();
//            Utils.makeEventToast(this, "再按一次退出程序", false);
//
//        } else{
//            finish();
//            ((TaskApp) getApplication()).exitApp();
//        }
    }

    public void updateActionBar() {
        if (currentFragmentTag.equals(Constants.TAG_FEED)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, R.drawable.icon_write, getString(R.string.home_tab_feed), true);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);
            setOnClickMiddleListener(trendsFilterTitleOnClickListener);
            setOnClickRightListener(publishBtnOnClickListener);

        } else if (currentFragmentTag.equals(Constants.TAG_AT_ME)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
            initActionBar(R.drawable.icon_back, 0, getString(R.string.title_at_me), false);

        } else if (currentFragmentTag.equals(Constants.TAG_COMMENT)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
            initActionBar(R.drawable.icon_back, 0, getString(R.string.title_my_comments), false);

        } else if (currentFragmentTag.equals(Constants.TAG_CHAT_CONTACTS)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, 0, getString(R.string.home_tab_msg_list), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);

        } else if (currentFragmentTag.equals(Constants.TAG_MEMBERS)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, 0, getString(R.string.title_member), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);

        } else if (currentFragmentTag.equals(Constants.TAG_MORE)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, 0, getString(R.string.title_more), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);

        } else if(currentFragmentTag.equals(Constants.TAG_APPROVAL)){
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, R.drawable.icon_write, getString(R.string.contentapproval), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);
            setOnClickRightListener(publishApprovalListener);

        } else if(currentFragmentTag.equals(Constants.TAG_MESSAGE_LIST)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
            String title = "";
            if (mSession.isChatLogin()) {
                title = getString(R.string.home_tab_message);
            } else {
                title = getString(R.string.home_tab_message) + "(" + getString(R.string.chat_not_login) + ")";
            }
            initActionBar(R.drawable.icon_navigation, R.drawable.icon_write, title, false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);
            setOnClickRightListener(selectMemberOnClickListener);

        } else if (currentFragmentTag.equals(Constants.TAG_PERSONAL_SPACE)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, 0, mSession.getUserName(), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);
        } else if (currentFragmentTag.equals(Constants.TAG_SOCIAL_SEARCH)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
            initActionBar(R.drawable.icon_back, R.drawable.constructsocia, getString(R.string.titile_social_manage), false);
            setOnClickLeftListener(new OnDetachFragmentListener());
            setOnClickRightListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Utils.isThirdPackage(HomeTabActivity.this)){
                        Intent intent=new Intent(HomeTabActivity.this,ConstructSociaActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else if (currentFragmentTag.equals(Constants.TAG_ALL_CONTACTS)) {
            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            initActionBar(R.drawable.icon_navigation, 0, getString(R.string.home_tab_contacts), false);
            setOnClickLeftListener(toggleMenuImgBtnOnClickListener);
        }
    }

    public class ToggleMenuImgBtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            getMenuDrawer().toggleMenu();
        }
    }

    public class PublishBtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(HomeTabActivity.this, PublishMainActivity.class);
            startActivity(intent);

        }
    }
    //发布审批
    public class PublishApprovalListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(HomeTabActivity.this, PublishMainActivity.class);
            startActivity(intent);
        }
    }
    public class TrendsFilterTitleOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            newsFilterOnClickListener.setConfirmBtnOnClickListener(new ConfirmButtonOnClickListener());
            newsFilterOnClickListener.setCancelBtnOnClickListener(new CancelButtonOnClickListener());
            filterDialog.show();
        }
    }

    // 点击切换社区
    public class SelectSocialTitleOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
//            detachFragment(getFragment(currentFragmentTag));
//            currentFragmentTag = Constants.TAG_SOCIALS;
//            badgeView.hide();
//            mMenuDrawer.closeMenu();
//            mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
//            initActionBar(R.drawable.icon_back, R.drawable.icon_action_search, getString(R.string.choicesocial), false);
//            setOnClickLeftListener(new OnDetachFragmentListener());
//            setOnClickRightListener(searchButtonOnClickListener);
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(Constants.TAG_SOCIALS), Constants.TAG_SOCIALS, AnimType.SLIDE_DOWN_UP);
//                }
//            }, Constants.SHORT_DELAY);
            mMenuDrawer.closeMenu();
        }
    }

    public class SearchButtonOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mMenuDrawer.closeMenu();
            detachFragment(getFragment(currentFragmentTag));
            currentFragmentTag = Constants.TAG_SOCIAL_SEARCH;
            updateActionBar();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attachFragment(mMenuDrawer.getContentContainer().getId(), getFragment(currentFragmentTag), currentFragmentTag);
                }
            }, Constants.LONG_DELAY);
        }
    }

    public class SelectMemberOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
//            Intent it = new Intent(HomeTabActivity.this, AddContactsActivity.class);
            Intent it = new Intent(HomeTabActivity.this, NewConversationActivity.class);
            startActivity(it);
        }
    }

    public class OnDetachFragmentListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            detachFragment(getFragment(currentFragmentTag));
            currentFragmentTag = bottomTabFragmentTag;
            updateActionBar();
        }
    }

    public class OnHomeButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            detachFragment(getFragment(currentFragmentTag));
            currentFragmentTag = bottomTabFragmentTag;
            updateActionBar();
        }
    }

    public MenuDrawer getMenuDrawer() {
        return mMenuDrawer;
    }

    /**
     * 初始化筛选对话框
     */
    private void initFilterDialog() {
        filterDialog = new CustomDialog(this, R.style.popDialog);
        View filterView = mLayoutInflater.inflate(R.layout.dialog_filter_news, null);
        newsFilterOnClickListener = new NewsFilterOnClickListener(this, filterView);
        filterDialog.setDisplayView(filterView, null);
        filterDialog.setPropertyTop(0, DensityUtil.dip2px(this, 100), 0.80);
    }

    private void setCurrentRegionAndAppType() {
        currentRegion = region;
        currentAppType = appType;
    }

    private class ConfirmButtonOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (currentAppType != appType
                    || currentRegion != region) {
                isFilterChanged = true;
            } else {
                isFilterChanged = false;
            }
            setCurrentRegionAndAppType();
            if(isFilterChanged) {
                NewsTimeLineFrg newsTimeLineFrg = (NewsTimeLineFrg) fragmentsInTabHost.get(currentFragmentTag);
                if (newsTimeLineFrg != null) {
                    newsTimeLineFrg.onFilterChanged(isFilterChanged);
                }
            }
            filterDialog.dismiss();
        }
    }

    private class CancelButtonOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            filterDialog.dismiss();
        }
    }

    public class NewsFilterOnClickListener implements OnClickListener {
        private View view;
        private Button btnRowOneNewsFilterMyFocus;
        private Button btnRowOneNewsFilterAll;
        private Button btnRowTwoFilterAllType;
        private Button btnRowTwoFilterTextType;
        private Button btnRowTwoFilterPicsType;
        private Button btnRowTwoFilterFileType;
        private Button btnRowThreeFilterMusicType;
        private Button btnRowThreeFilterVideoType;
        private Button btnConfirm;
        private Button btnCancel;
        private NewsFilterOnClickListener(Context context, View view) {
            this.view = view;
            initViews();
        }

        public void setConfirmBtnOnClickListener(OnClickListener listener) {
            if (view != null) {
                btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
                btnConfirm.setOnClickListener(listener);
            }
        }

        public void setCancelBtnOnClickListener(OnClickListener listener) {
            if (view != null) {
                btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(listener);
            }
        }

        private void initViews() {
            btnRowOneNewsFilterMyFocus = (Button) view.findViewById(R.id.btn_news_my_focus);
            btnRowOneNewsFilterAll = (Button) view.findViewById(R.id.btn_news_all);
            btnRowTwoFilterAllType = (Button) view.findViewById(R.id.btn_filter_all);
            btnRowTwoFilterTextType = (Button) view.findViewById(R.id.btn_filter_text);
            btnRowTwoFilterPicsType = (Button) view.findViewById(R.id.btn_filter_pics);
            btnRowTwoFilterFileType = (Button) view.findViewById(R.id.btn_filter_files);
            btnRowThreeFilterMusicType = (Button) view.findViewById(R.id.btn_filter_music);
            btnRowThreeFilterVideoType = (Button) view.findViewById(R.id.btn_filter_video);
            btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
            btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            setBtnOnClickListener();
            region = Integer.valueOf(mSession.getRegion());
            appType = Integer.valueOf(mSession.getAppType());
            setCurrentRegionAndAppType();
            if (region == 1) {
                setRowOneMyfocusBtnEnabled();
            } else if (region == 2) {
                setRowOneAllBtnEnabled();
            }
            if (appType == 0) {
                setRowTwoFilterAllEnabled();
            } else if (appType == 3) {
                setRowTwoFilterTextEnabled();
            } else if (appType == 6) {
                setRowTwoFilterPicsEnabled();
            } else if (appType == 9) {
                setRowTwoFilterFileEnabled();
            } else if(appType == 16) {
                setRowThreeFilterMusicEnabled();
            } else if(appType == 19){
                setRowThreeFilterVideoEnabled();
            }
        }

        private void setRowOneMyfocusBtnEnabled() {
            btnRowOneNewsFilterMyFocus.setEnabled(false);
            btnRowOneNewsFilterAll.setEnabled(true);
        }

        private void setRowOneAllBtnEnabled() {
            btnRowOneNewsFilterMyFocus.setEnabled(true);
            btnRowOneNewsFilterAll.setEnabled(false);
        }

        private void setRowTwoFilterAllEnabled() {
            btnRowTwoFilterAllType.setEnabled(false);
            btnRowTwoFilterTextType.setEnabled(true);
            btnRowTwoFilterPicsType.setEnabled(true);
            btnRowTwoFilterFileType.setEnabled(true);
            btnRowThreeFilterMusicType.setEnabled(true);
            btnRowThreeFilterVideoType.setEnabled(true);
        }

        private void setRowTwoFilterTextEnabled() {
            btnRowTwoFilterAllType.setEnabled(true);
            btnRowTwoFilterTextType.setEnabled(false);
            btnRowTwoFilterPicsType.setEnabled(true);
            btnRowTwoFilterFileType.setEnabled(true);
            btnRowThreeFilterMusicType.setEnabled(true);
            btnRowThreeFilterVideoType.setEnabled(true);
        }

        private void setRowTwoFilterPicsEnabled() {
            btnRowTwoFilterAllType.setEnabled(true);
            btnRowTwoFilterTextType.setEnabled(true);
            btnRowTwoFilterPicsType.setEnabled(false);
            btnRowTwoFilterFileType.setEnabled(true);
            btnRowThreeFilterMusicType.setEnabled(true);
            btnRowThreeFilterVideoType.setEnabled(true);
        }

        private void setRowTwoFilterFileEnabled() {
            btnRowTwoFilterAllType.setEnabled(true);
            btnRowTwoFilterTextType.setEnabled(true);
            btnRowTwoFilterPicsType.setEnabled(true);
            btnRowTwoFilterFileType.setEnabled(false);
            btnRowThreeFilterMusicType.setEnabled(true);
            btnRowThreeFilterVideoType.setEnabled(true);
        }

        private void setRowThreeFilterMusicEnabled() {
            btnRowTwoFilterAllType.setEnabled(true);
            btnRowTwoFilterTextType.setEnabled(true);
            btnRowTwoFilterPicsType.setEnabled(true);
            btnRowTwoFilterFileType.setEnabled(true);
            btnRowThreeFilterMusicType.setEnabled(false);
            btnRowThreeFilterVideoType.setEnabled(true);
        }

        private void setRowThreeFilterVideoEnabled() {
            btnRowTwoFilterAllType.setEnabled(true);
            btnRowTwoFilterTextType.setEnabled(true);
            btnRowTwoFilterPicsType.setEnabled(true);
            btnRowTwoFilterFileType.setEnabled(true);
            btnRowThreeFilterMusicType.setEnabled(true);
            btnRowThreeFilterVideoType.setEnabled(false);
        }

        private void setBtnOnClickListener() {
            btnRowOneNewsFilterMyFocus.setOnClickListener(this);
            btnRowOneNewsFilterAll.setOnClickListener(this);
            btnRowTwoFilterAllType.setOnClickListener(this);
            btnRowTwoFilterTextType.setOnClickListener(this);
            btnRowTwoFilterPicsType.setOnClickListener(this);
            btnRowTwoFilterFileType.setOnClickListener(this);
            btnRowThreeFilterMusicType.setOnClickListener(this);
            btnRowThreeFilterVideoType.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_news_my_focus:
                    region = 1;
                    setRowOneMyfocusBtnEnabled();
                    break;
                case R.id.btn_news_all:
                    region = 2;
                    setRowOneAllBtnEnabled();
                    break;
                case R.id.btn_filter_all:
                    appType =0;
                    setRowTwoFilterAllEnabled();
                    break;
                case R.id.btn_filter_text:
                    appType =3;
                    setRowTwoFilterTextEnabled();
                    break;
                case R.id.btn_filter_pics:
                    appType =6;
                    setRowTwoFilterPicsEnabled();
                    break;
                case R.id.btn_filter_files:
                    appType =9;
                    setRowTwoFilterFileEnabled();
                    break;
                case R.id.btn_filter_music:
                    appType = 16;
                    setRowThreeFilterMusicEnabled();
                    break;
                case R.id.btn_filter_video:
                    appType = 19;
                    setRowThreeFilterVideoEnabled();
                    break;
                default:
                    break;
            }
            mSession.setRegion(String.valueOf(region));
            mSession.setAppType(String.valueOf(appType));
        }
    }

}
