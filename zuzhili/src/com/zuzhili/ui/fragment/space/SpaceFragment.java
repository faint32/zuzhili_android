package com.zuzhili.ui.fragment.space;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.PopupWindowBuilder;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.controller.TrendAdapter;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.Member;
import com.zuzhili.model.MiniBlog;
import com.zuzhili.model.space.UserInfoSummary;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.PersonalDetailActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by liutao on 14-3-20.
 */
public class SpaceFragment extends FixedOnActivityResultBugFragment implements Response.Listener<String>, Response.ErrorListener
        , BaseActivity.HandleProgressBarVisibilityCallback {

    @ViewInject(R.id.listView)
    private PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    @ViewInject(R.id.img_avatar)
    private NetworkImageView avatarImg;

    @ViewInject(R.id.btn_fans)
    private Button fansBtn;

    @ViewInject(R.id.btn_followers)
    private Button followersBtn;

    @ViewInject(R.id.btn_apply)
    private Button applyBtn;

    @ViewInject(R.id.btn_person_detail)
    private Button personDetailBtn;

    @ViewInject(R.id.btn_personal_detail)
    private Button personalDetailBtn;

    @ViewInject(R.id.btn_file_library)
    private Button fileLibraryBtn;

    @ViewInject(R.id.btn_collect)
    private Button collectBtn;

    @ViewInject(R.id.btn_album)
    private Button albumBtn;

    @ViewInject(R.id.btn_audio)
    private Button audioBtn;

    @ViewInject(R.id.btn_more)
    private Button moreBtn;

    @ViewInject(R.id.txt_total_count)
    private TextView totalCountTxt;

    @ViewInject(R.id.btn_audio_more_group)
    private Button audioBtnMore;

    @ViewInject(R.id.btn_video_more_group)
    private Button vedioBtnMore;

    @ViewInject(R.id.btn_todo_more_group)
    private Button todoBtnMore;

    private View moreGroup;

    private View headView;

    private ResultsAdapter<MiniBlog> adapter;

    private Member member;

    private UserInfoSummary userInfoSummary;

    private String totalCount;

    /** indicates whether it's my own space or not */
    private boolean isMyOwnSpace = true;

    private PopupWindowBuilder popupWindowBuilder;

    public static SpaceFragment newInstance(Serializable serializable) {
        SpaceFragment f = new SpaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.EXTRA_MEMBER, serializable);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (onActionBarUpdateListener != null) {
            onActionBarUpdateListener.shouldUpdateActionBar();
        }
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        headView = View.inflate(activity, R.layout.listview_item_space_head, null);
        ViewUtils.inject(this, view);
        ViewUtils.inject(this, headView);
        listView.addHeaderView(headView);
        updateHeadView();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Task.getSpecificUserTrends(buildRequestParams(), this, this);
        update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("personal space frg destroyed!");
    }

    public void update() {
        if (adapter != null && mSession.isUIShouldUpdate(Constants.PAGE_PERSONAL_SPACE)) {
            adapter.clearList();
            adapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_PERSONAL_SPACE);
        }
        if (adapter == null) {
            adapter = new TrendAdapter(getActivity()
                    , listView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams(), mSession, member, this, CacheType.CACHE_GET_SPECIFIC_USER_TRENDS, TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS, false);
        }
        adapter.setListView(listView);
        adapter.setOnRefreshListener();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(((TrendAdapter) adapter).trendOnItemClickListener);
        // for test crash
//        int[] arr = new int[2];
//        arr[-1] = 0;
    }

    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (member != null) {
            params.put("listid", member.getListid());
            params.put("ids", member.getId());
            params.put("curids", member.getListid());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        } else {
            params.put("listid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("curids", mSession.getListid());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    private void updateHeadView() {

        if (getArguments() != null) {   // 其他用户的个人空间
            member = (Member) getArguments().get(Constants.EXTRA_MEMBER);
            if (member.getId().equals(mSession.getIds())) {
                isMyOwnSpace = true;
            } else {
                isMyOwnSpace = false;
            }
        } else {                        // 个人空间
            member = new Member();
            member.setListid(mSession.getListid());
            member.setName(mSession.getUserName());
            member.setId(mSession.getIds());
            member.setUserhead(mSession.getUserhead());
            isMyOwnSpace = true;
        }

//        hideOrDisplaySomeButtons(isMyOwnSpace);
        avatarImg.setImageUrl(TextUtil.processNullString(member.getUserhead()), ImageCacheManager.getInstance().getImageLoader());
        avatarImg.setDefaultImageResId(R.drawable.default_user_head_small);
        avatarImg.setErrorImageResId(R.drawable.default_user_head_small);

        if (isMyOwnSpace) {
            applyBtn.setVisibility(View.INVISIBLE);
        } else {
            applyBtn.setVisibility(View.VISIBLE);
            applyBtn.setText(activity.getString(R.string.action_follow));
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    private void hideOrDisplaySomeButtons(boolean isMyOwnSpace) {
        personalDetailBtn.setVisibility(View.VISIBLE);
        fileLibraryBtn.setVisibility(View.VISIBLE);
        albumBtn.setVisibility(View.VISIBLE);
        moreBtn.setVisibility(View.VISIBLE);
        if (isMyOwnSpace) {
            collectBtn.setVisibility(View.VISIBLE);
            audioBtn.setVisibility(View.GONE);
            applyBtn.setVisibility(View.INVISIBLE);
        } else {
            collectBtn.setVisibility(View.GONE);
            audioBtn.setVisibility(View.VISIBLE);
            applyBtn.setVisibility(View.VISIBLE);
        }
    }

    private void displayUserInfoSummary(boolean isMyOwnSpace, UserInfoSummary userInfoSummary) {
        SpannableString ss = new SpannableString(getString(R.string.desc_front)  + totalCount + getString(R.string.desc_end));
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 2, 2 + totalCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        totalCountTxt.setText(ss);
        ss = new SpannableString(userInfoSummary.getMylovercount() + " " + getString(R.string.followers));
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getMylovercount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        followersBtn.setText(ss);
        ss = new SpannableString(userInfoSummary.getLovemecount() + " " +getString(R.string.fans));
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getLovemecount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fansBtn.setText(ss);
        if (isMyOwnSpace) {
//            ss = new SpannableString(userInfoSummary.getCollectioncount() + "\n" + getString(R.string.collect));
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getCollectioncount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            collectBtn.setText(ss);
//            ss = new SpannableString(userInfoSummary.getPhotocount() + "\n" + getString(R.string.pictures));
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getPhotocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            albumBtn.setText(ss);

        } else {
//            ss = new SpannableString(userInfoSummary.getPhotocount() + "\n" + getString(R.string.pictures));
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getPhotocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            albumBtn.setText(ss);
//            ss = new SpannableString(userInfoSummary.getAudiocount() + "\n" + getString(R.string.audio));
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getAudiocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            audioBtn.setText(ss);
            applyBtn.setText(userInfoSummary.getIsmyfocus().equals("0") ? activity.getString(R.string.action_unfollow) : activity.getString(R.string.action_follow));
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if(progressBar.VISIBLE==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            userInfoSummary = JSON.parseObject(jsonObject.getString("usersummary"), UserInfoSummary.class);
            totalCount = jsonObject.getString("count");
        } catch (JSONException e) {
            userInfoSummary = new UserInfoSummary();
            totalCount = "0";
        }
        if (isAdded()) displayUserInfoSummary(isMyOwnSpace, userInfoSummary);
        return;
    }

    @OnClick(R.id.btn_more)
    public void moreBtnOnClick(View view) {
        popupWindowBuilder = new PopupWindowBuilder(activity, moreBtn);
        moreGroup = View.inflate(activity, R.layout.view_space_more, null);
        ViewUtils.inject(this, moreGroup);
        popupWindowBuilder.setContentView(moreGroup);
        popupWindowBuilder.popupSet();
        popupWindowBuilder.showAtDown();
        SpannableString ss;
        if (isMyOwnSpace) {
            ss = new SpannableString(userInfoSummary.getAudiocount() + "\n" + getString(R.string.audio));
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getAudiocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            audioBtnMore.setText(ss);
            ss = new SpannableString(userInfoSummary.getVediocount() + "\n" + getString(R.string.video));
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getVediocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vedioBtnMore.setText(ss);
        } else {
            audioBtnMore.setVisibility(View.GONE);
            ss = new SpannableString(userInfoSummary.getVediocount() + "\n" + getString(R.string.video));
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_blue)), 0, userInfoSummary.getVediocount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vedioBtnMore.setText(ss);
        }
    }

    @OnClick(R.id.btn_person_detail)
    public void personDetailBtnOnClick(View view) {
        Intent it = new Intent(activity, PersonalDetailActivity.class);
        if (member != null) {
            it.putExtra(Constants.EXTRA_IDS, member.getId());
            it.putExtra(Constants.EXTRA_USER_NAME, member.getName());
        }
        activity.startActivity(it);

    }

}
