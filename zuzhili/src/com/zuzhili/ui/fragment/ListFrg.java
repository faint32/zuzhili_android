package com.zuzhili.ui.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class ListFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback {

    @ViewInject(R.id.listView)
    protected PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

    public ListFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        activity = (BaseActivity) getActivity();
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

}
