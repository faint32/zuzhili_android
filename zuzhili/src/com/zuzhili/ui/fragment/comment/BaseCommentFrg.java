package com.zuzhili.ui.fragment.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.AtMeCommentAdapter;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-15.
 */
public abstract class BaseCommentFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback {

    protected PullRefreshListView pullRefreshListView;

    protected ProgressBar progressBar;

    protected String cacheType;

    protected String requestType;

    protected ResultsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        pullRefreshListView = (PullRefreshListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
    }

    public void update() {
        setCacheType();
        setRequestType();
        reset();
        if (adapter == null) {
            adapter = new AtMeCommentAdapter(activity
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams(), mSession, cacheType, requestType, this, false);
        }
        adapter.setListView(pullRefreshListView);
        adapter.setOnRefreshListener();
        pullRefreshListView.setAdapter(adapter);
        pullRefreshListView.setOnItemClickListener(((AtMeCommentAdapter) adapter).atMeCommentOnClickListener);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    protected HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("listid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    protected abstract void setCacheType();

    protected abstract void setRequestType();

    protected abstract void reset();

    public PullRefreshListView getPullRefreshListView() {
        return pullRefreshListView;
    }
}
