package com.zuzhili.ui.fragment.approval;

import java.util.HashMap;
import java.util.Map;
import net.simonvt.menudrawer.MenuDrawer;
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

public class ApprovalContainerFrg extends  FixedOnActivityResultBugFragment{

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
	        pagerAdapter = new PagerAdapter(getChildFragmentManager());// it's import to set the right argmument when fragment is nested.
	        pager.setAdapter(pagerAdapter);
	        tabStrip.setViewPager(pager);
	        tabStrip.setOnPageChangeListener(new OnPageScrollListener());

	        pullRefreshListViewMap = new HashMap<Integer, PullRefreshListView>(2);
	        adapterMap = new HashMap<Integer, ResultsAdapter>(2);

	        menuDrawer = activity.getMenuDrawer();
	        if (menuDrawer != null) {
	            menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
	        }
	    }

	    public class PagerAdapter extends FragmentPagerAdapter {

	        private final String[] TITLES = { "收到的审批", "发出的审批"};

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
	        	BaseApprovalFrg frg = null;
	            switch (position) {
	                case 0:
	                    frg = new MyReceivedApprovalFragment();
	                    break;
	                case 1:
	                    frg = new MySendedApprovalFragment();
	                    break;
	                default:
	                    break;
	            }
	            return frg;
	        }
	    }

	    /**
	     * when view pager scroll to first page, the menu drawer is enabled to slide open.
	     */
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
