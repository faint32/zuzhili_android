package com.zuzhili.ui.fragment.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.MemberAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.model.Member;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.SpaceActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by liutao on 14-3-4.
 */
public abstract class BaseMemberFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback {

    private PullRefreshListView pullRefreshListView;

    private ProgressBar progressBar;

    protected String cacheType;
    
    protected MemberAdapter memberAdapter;


    private OnMemberSelectedListener onMemberSelectedListener;


    public interface OnMemberSelectedListener {
        public void onMemberSelected(Member member);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onMemberSelectedListener = (OnMemberSelectedListener) activity;
        } catch (ClassCastException e) {
            onMemberSelectedListener = null;
        }
    }

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

    /**
     * 切换社区后需要更新数据
     */
    public void update() {
        setCacheType();
        reset();

        if (memberAdapter == null) {
            memberAdapter = new MemberAdapter(getActivity()
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams(), mSession, cacheType, this, false);
        }
        pullRefreshListView.setDividerHeight(DensityUtil.dip2px(activity, 0.5f));
        memberAdapter.setListView(pullRefreshListView);
        memberAdapter.setOnRefreshListener();
        memberAdapter.setOnItemClickListener();
        if (onMemberSelectedListener != null) {
            memberAdapter.setOnMemberSelectedListener(onMemberSelectedListener);


        }
        pullRefreshListView.setAdapter(memberAdapter);
        if (onFrgmentInstantiationListener != null) {
            onFrgmentInstantiationListener.onFragmentInstantiation(this);
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    protected abstract HashMap<String, String> buildRequestParams();

    protected abstract void setCacheType();

    protected abstract void reset();

    public PullRefreshListView getPullRefreshListView() {
        return pullRefreshListView;
    }

    public MemberAdapter getMemberAdapter() {
        return memberAdapter;
    }

}
