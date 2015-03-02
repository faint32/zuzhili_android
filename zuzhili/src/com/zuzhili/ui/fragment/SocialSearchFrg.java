package com.zuzhili.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.SocialAdapter;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.model.social.Social;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.social.SocialManagerActivity;
import com.zuzhili.ui.views.PullRefreshListView;
import com.zuzhili.ui.views.quickreturn.AbsListViewQuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.QuickReturnAttacher;
import com.zuzhili.ui.views.quickreturn.widget.AbsListViewScrollTarget;
import com.zuzhili.ui.views.quickreturn.widget.QuickReturnAdapter;
import com.zuzhili.ui.views.quickreturn.widget.QuickReturnTargetView;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-11.
 */
public class SocialSearchFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback
        , OnItemClickListener
        , PullRefreshListView.OnRefreshStateChangeListener
        , AbsListView.OnScrollListener, View.OnTouchListener {

    private PullRefreshListView pullRefreshListView;

    private ProgressBar progressBar;

    private SearchView topSearchView;

    private QuickReturnTargetView topTargetView;

    private SocialAdapter adapter;

    private String queryText;

    private boolean isNeedUpdate = false;

    // 获取相应的适配器
    private BaseActivity context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        pullRefreshListView = (PullRefreshListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        topSearchView = (SearchView) view.findViewById(R.id.quickReturnTopTarget);
        pullRefreshListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update(isNeedUpdate);
    }

    protected HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (queryText != null) {
            params.put("name", queryText);
        }
        params.put("start", "0");
        params.put("length", String.valueOf(Constants.PAGE_SIZE));
        params.put("userid", mSession.getUid());
        return params;
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    public void update(boolean isNeedUpdate) {
        if (adapter != null && isNeedUpdate) {
            adapter.clearList();
            adapter = null;
        }
        if (adapter == null) {
            adapter = new SocialAdapter(getActivity()
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams(), mSession, CacheType.CACHE_ALL_SOCIALS, this, isNeedUpdate);
        }

        if (pullRefreshListView.getHeaderViewsCount() < 2) {
            // 加入占位的header
            View head = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
            pullRefreshListView.addHeaderView(head);
        }
        pullRefreshListView.setOnScrollListener(this);
        pullRefreshListView.setOnRefreshStateChangeListener(this);
        pullRefreshListView.setAdapter(new QuickReturnAdapter(adapter, 1));
        pullRefreshListView.setDividerHeight(DensityUtil.dip2px(activity, 0.5f));
        adapter.setListView(pullRefreshListView);
        topSearchView.setOnQueryTextListener(new OnQuerySocialListener());

        final QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(pullRefreshListView);
        topTargetView = quickReturnAttacher.addTargetView(topSearchView, AbsListViewScrollTarget.POSITION_TOP, DensityUtil.dip2px(getActivity(), 50));

        if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
            // This is the correct way to register an OnScrollListener.
            // You have to add it on the QuickReturnAttacher, instead
            // of on the viewGroup directly.
            final AbsListViewQuickReturnAttacher attacher = (AbsListViewQuickReturnAttacher) quickReturnAttacher;
            attacher.addOnScrollListener(this);
            attacher.setOnItemClickListener(this);
        }
    }

    @Override
    public void onRefreshStateChanged(int state) {
        if (state == PullRefreshListView.DONE) {
            topSearchView.setVisibility(View.VISIBLE);
        } else {
            topSearchView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        pullRefreshListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if (v instanceof ListView) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                mInputMethodManager.hideSoftInputFromWindow(topSearchView.getWindowToken(), 0);
//            }
//        }
        return false;
    }

    public class OnQuerySocialListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String s) {
            queryText = s;
            update(true);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            queryText = s;
            update(true);
            return false;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		context=(BaseActivity)super.getActivity();
		Social social=(Social) parent.getAdapter().getItem(position);
		Intent it = new Intent(context,SocialManagerActivity.class);
		it.putExtra("social", social);
		context.startActivity(it);
	}

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        mInputMethodManager.hideSoftInputFromWindow(topSearchView.getWindowToken(), 0);
//    }
}
