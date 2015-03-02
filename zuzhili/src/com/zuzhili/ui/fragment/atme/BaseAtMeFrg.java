package com.zuzhili.ui.fragment.atme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zuzhili.R;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-13.
 */
public abstract class BaseAtMeFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback {

    protected PullRefreshListView pullRefreshListView;

    protected ProgressBar progressBar;

    protected String cacheType;

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

    public abstract void update();

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    protected abstract HashMap<String, String> buildRequestParams();

    protected abstract void setCacheType();

    public PullRefreshListView getPullRefreshListView() {
        return pullRefreshListView;
    }

}
