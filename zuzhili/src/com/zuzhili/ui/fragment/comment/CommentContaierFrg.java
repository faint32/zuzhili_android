package com.zuzhili.ui.fragment.comment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PagerSlidingTabStrip;
import com.zuzhili.ui.views.PullRefreshListView;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liutao on 14-3-15.
 */
public class CommentContaierFrg extends FixedOnActivityResultBugFragment {
    @ViewInject(R.id.tabs)
    private PagerSlidingTabStrip tabStrip;

    @ViewInject(R.id.view_pager)
    private ViewPager pager;

    private PagerAdapter pagerAdapter;

    private Map<Integer, PullRefreshListView> pullRefreshListViewMap;

    private Map<Integer, ResultsAdapter> adapterMap;

    private MenuDrawer menuDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewpager, null);
        activity = (BaseActivity) getActivity();
        ViewUtils.inject(this, view);
        tabStrip.setUnderlineHeight(1);
        tabStrip.setDividerColorResource(R.color.slide_filler);
        tabStrip.setDividerPadding(DensityUtil.dip2px(activity, 4));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());// it's import to set the right argument when fragment is nested.
        pager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(pager);

        pullRefreshListViewMap = new HashMap<Integer, PullRefreshListView>(2);
        adapterMap = new HashMap<Integer, ResultsAdapter>(2);

        menuDrawer = activity.getMenuDrawer();
        if (menuDrawer != null) {
            menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
        }

        activity.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.detachFragment(CommentContaierFrg.this);
                if (onActionBarUpdateListener != null) {
                    onActionBarUpdateListener.shouldUpdateActionBar();
                }
            }
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"收到的评论", "发出的评论"};

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
            BaseCommentFrg frg = null;
            switch (position) {
                case 0:
                    frg = new ReceivedCommentFrg();
                    break;
                case 1:
                    frg = new SendedCommentFrg();
                    break;

                default:
                    break;
            }
            return frg;
        }
    }
}
