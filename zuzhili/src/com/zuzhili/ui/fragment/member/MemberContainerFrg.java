package com.zuzhili.ui.fragment.member;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.MemberAdapter;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.CustomSearchView;
import com.zuzhili.ui.views.PagerSlidingTabStrip;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.SideBar;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liutao on 14-2-28.
 */
public class MemberContainerFrg extends FixedOnActivityResultBugFragment implements
        FixedOnActivityResultBugFragment.OnFrgmentInstantiationListener {

    @ViewInject(R.id.tabs)
    private PagerSlidingTabStrip tabStrip;

    @ViewInject(R.id.view_pager)
    private ViewPager pager;

    @ViewInject(R.id.sidebar)
    private SideBar sideBar;

    @ViewInject(R.id.txt_alphabetic_hint)
    private TextView alphabeticHint;

    private PagerAdapter pagerAdapter;

    private Map<Integer, PullRefreshListView> pullRefreshListViewMap;

    private Map<Integer, MemberAdapter> memberAdapterMap;

    private MenuDrawer menuDrawer;

    private String from;

    public static MemberContainerFrg newInstance(String text) {
        MemberContainerFrg f = new MemberContainerFrg();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_FROM_WHICH_PAGE, text);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onActionBarUpdateListener = (OnActionBarUpdateListener) activity;
        } catch (ClassCastException e) {
            onActionBarUpdateListener = null;
        }
    }

    //创建视图
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from = getArguments().getString(Constants.EXTRA_FROM_WHICH_PAGE);
    }

    //创建视图格式
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewpager, null);
        activity = (BaseActivity) getActivity();
        ViewUtils.inject(this, view);
        if (from != null && from.equals(Constants.EXTRA_FROM_COMMENT_EDIT)
                || from.equals(Constants.EXTRA_FROM_FRAGMENT_MESSAGE)) {
            activity.initActionBar(R.drawable.icon_back, 0, getString(R.string.title_member), false);
            activity.setOnClickLeftListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.detachFragment(MemberContainerFrg.this);
                    if (onActionBarUpdateListener != null) {
                        onActionBarUpdateListener.shouldUpdateActionBar();
                    }
                }
            });
        } else if (from.equals(Constants.EXTRA_FROM_HOME)) {
            activity.initActionBar(R.drawable.icon_navigation, 0, getString(R.string.title_member), false);
        }
        setSearchView();
        sideBar.setVisibility(View.VISIBLE);
        sideBar.setTextView(alphabeticHint);
        tabStrip.setUnderlineHeight(1);
        tabStrip.setDividerColorResource(R.color.slide_filler);
        tabStrip.setDividerPadding(DensityUtil.dip2px(activity, 4));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(pager);
        tabStrip.setOnPageChangeListener(new OnPageScrollListener());

        pullRefreshListViewMap = new HashMap<Integer, PullRefreshListView>(3);
        memberAdapterMap = new HashMap<Integer, MemberAdapter>(3);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = memberAdapterMap.get(pager.getCurrentItem()).getPositionForSection(s.charAt(0));
                if (position != -1) {
                    pullRefreshListViewMap.get(pager.getCurrentItem()).setSelection(position);
                }
            }
        });
        menuDrawer = activity.getMenuDrawer();
        if (menuDrawer != null) {
            menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setSearchView() {
        if (activity == null)
            activity = (BaseActivity) getActivity();

//        if (activity.getSearchView() != null) {
//            activity.getSearchView().setOnQueryTextListener(new OnQueryMemberListener());
//            CustomSearchView searchView = activity.getSearchView();
//            searchView.setOnSearchClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    activity.setTitleInvisible();
//                }
//            });
//
//            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//                @Override
//                public boolean onClose() {
//                    activity.setTitleVisiable();
//                    return false;
//                }
//            });
//
//        }
    }

    @Override
    public void onFragmentInstantiation(FixedOnActivityResultBugFragment baseFragment) {
        BaseMemberFrg baseMemberFrg = (BaseMemberFrg) baseFragment;
        PullRefreshListView pullRefreshListView = baseMemberFrg.getPullRefreshListView();
        MemberAdapter memberAdapter = baseMemberFrg.getMemberAdapter();

        if (baseMemberFrg instanceof AllFrg) {
            pullRefreshListViewMap.put(0, pullRefreshListView);
            memberAdapterMap.put(0, memberAdapter);
        } else if (baseMemberFrg instanceof FocusFrg) {
            pullRefreshListViewMap.put(1, pullRefreshListView);
            memberAdapterMap.put(1, memberAdapter);
        } else {
            pullRefreshListViewMap.put(2, pullRefreshListView);
            memberAdapterMap.put(2, memberAdapter);
        }
    }

    public class OnQueryMemberListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            memberAdapterMap.get(pager.getCurrentItem()).filterData(s.trim());
            return false;
        }

    }

    public class PagerAdapter extends FragmentPagerAdapter {

//        private final String[] TITLES = { "全部", "我关注的", "最近联系人"};
        private final String[] TITLES = { "全部", "我关注的"};
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            BaseMemberFrg frg = null;
            switch (position) {
                case 0:
                    frg = new AllFrg();
                    break;
                case 1:
                    frg = new FocusFrg();
                    break;
                case 2:
                    frg = new RecentContactFrg();
                    break;

                default:
                    break;
            }
            frg.setOnFrgmentInstantiationListener(MemberContainerFrg.this);
            return frg;
        }
    }

    public class OnPageScrollListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 0 && positionOffset < 0 && positionOffsetPixels > 50) {
                if (menuDrawer != null) {
                    menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                if (menuDrawer != null) {
                    menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                }
            } else {
                if (menuDrawer != null) {
                    menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
