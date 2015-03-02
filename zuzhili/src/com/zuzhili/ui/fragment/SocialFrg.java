package com.zuzhili.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.JoinedInSocialAdapter;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.model.social.JoinedInSocial;
import com.zuzhili.service.GetIMDataIntentService;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.HomeTabActivity;
import com.zuzhili.ui.views.PullRefreshListView;


public class SocialFrg extends FixedOnActivityResultBugFragment implements OnItemClickListener, PullRefreshListView.OnRefreshRequestListener, Response.Listener<String>, Response.ErrorListener {
	
	private PullRefreshListView listView;
	
	private static final String PARAM_POSITION = "param";

	private List<JoinedInSocial> socialList =new ArrayList<JoinedInSocial>();

    private OnChangeSocialListener onChangeSocialListener;

    private JoinedInSocialAdapter adapter;

    private ProgressDialog progressDialog;

    private boolean msgToChangeSociaFrg;

    private PullIMDataReceiver mReceiver;

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        listView.onPullRefreshEnd();
        JSONObject jsonObject = JSONObject.parseObject(s);

        if (jsonObject != null && jsonObject.getString("list") != null) {
            List<JoinedInSocial> list = JSON.parseArray(jsonObject.getString("list"), JoinedInSocial.class);
            if (list != null && adapter != null) {
                if (Utils.isThirdPackage(getActivity())) {
                    filter(list);
                }
                adapter.setList(list);
                mSession.getAccount().setList(list);
            }
            if (adapter.getItem(0) != null) {
                changeSocial((JoinedInSocial) adapter.getItem(0));
            }
            return;
        }
    }

    public void onRefresh() {
        Task.getMySocials(buildRequestParams(), this, this);
    }

    public interface OnChangeSocialListener {
        public void onSocialChange();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

    @Override
    public void onStart() {

        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mReceiver = new PullIMDataReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mReceiver,
                statusIntentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onStop();
    }

    public static SocialFrg newInstance(int position) {
		SocialFrg f = new SocialFrg();
		Bundle b = new Bundle();
		b.putInt(PARAM_POSITION, position);
		f.setArguments(b);
		return f;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onChangeSocialListener = (OnChangeSocialListener) activity;
        } catch (ClassCastException e) {
            onChangeSocialListener = null;
        }
    }

	private void initData() {
		socialList =((BaseActivity)this.getActivity()).mSession.getAccount().getList();

        if (Utils.isThirdPackage(getActivity())) {
            filter(socialList);
        }
        onRefresh();
	}

    private void filter(List<JoinedInSocial> socialList) {
        JoinedInSocial social = null;
        for (JoinedInSocial item : socialList) {
            if (item.getId()==113) {
                social = item;
            }
        }
        socialList.remove(social);
    }


    //添加创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if(adapter==null && !mSession.isUIShouldUpdate(Constants.PAGE_AT_CONTENT)){
            initData();
            adapter=new JoinedInSocialAdapter((BaseActivity) getActivity(), socialList,ImageCacheManager.getInstance().getImageLoader());
        }else if(adapter==null && mSession.isUIShouldUpdate(Constants.PAGE_AT_CONTENT)){
            adapter=new JoinedInSocialAdapter((BaseActivity) getActivity(), socialList,ImageCacheManager.getInstance().getImageLoader());
            onRefresh();
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_AT_CONTENT);
        }else if(adapter!=null && !mSession.isUIShouldUpdate(Constants.PAGE_AT_CONTENT)){
            initData();
            adapter.setList(socialList);
        }else if(adapter!=null && mSession.isUIShouldUpdate(Constants.PAGE_AT_CONTENT)){
            onRefresh();
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_AT_CONTENT);
        }

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);
		View view = inflater.inflate(R.layout.common_list, container, false);

        listView = (PullRefreshListView) view.findViewById(R.id.common_list);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        listView.onFooterRefreshEnd();

		return view;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        JoinedInSocial social = (JoinedInSocial) parent.getAdapter().getItem(position);
        changeSocial(social);

        if (onChangeSocialListener != null) {
            onChangeSocialListener.onSocialChange();
        }
	}

    /**
     * 切换社区
     * @param social
     */
    private void changeSocial(JoinedInSocial social){
        mSession.setListid(String.valueOf(social.getId()));
        mSession.setIds(String.valueOf(social.getIdentity().getId()));
        mSession.setSocialName(social.getListname());
        mSession.setSocialShortName(social.getShortname());
        mSession.setUserName(social.getIdentity().getName());
        mSession.setUserhead(social.getIdentity().getUserhead150());

        if (mSession.getMySelfInfo() == null) {
            UserInfo myInfo = new UserInfo();
            mSession.setMySelfInfo(myInfo);
        }
        mSession.getMySelfInfo().setU_id(mSession.getUid());
        mSession.getMySelfInfo().setU_listid(mSession.getListid());
        mSession.onSocialChanged();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.hint_wait_a_moment));
        progressDialog.show();

        Intent it = new Intent(getActivity(), GetIMDataIntentService.class);
        it.putExtra(Constants.ACTION, Task.ACTION_GET_ALL_USERS);
        getActivity().startService(it);
    }

    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("userid", mSession.getUid());
        }
        return params;
    }

    /**
     * 在用户的社区列表里切换社区
     * @param listId
     */
    public void checkSocial(int listId){
        if(socialList!=null && socialList.size()>1){
            for(JoinedInSocial item:socialList){
                if(listId==item.getId()){
                    if (onChangeSocialListener != null) {
                        onChangeSocialListener.onSocialChange();
                    }
                    msgToChangeSociaFrg=true;
                    changeSocial(item);
                    return;
                }
            }
        }
    }

    private class PullIMDataReceiver extends BroadcastReceiver {

        private PullIMDataReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.PULL_DATA_FAILED)) {
                case Constants.PULL_IM_USERS_FINISHED:
                    // 聊天成员拉取完毕，拉取群组信息
                    Intent it = new Intent(getActivity(), GetIMDataIntentService.class);
                    it.putExtra(Constants.ACTION, Task.ACTION_GET_GROUPS);
                    getActivity().startService(it);
                    break;
                case Constants.PULL_IM_GROUPS_FINISHED:
                    // 群组拉取完毕，跳转到首页
                    progressDialog.dismiss();
                    it = new Intent(activity, HomeTabActivity.class);
                    if(msgToChangeSociaFrg){
                        it.putExtra(Constants.TO_GROUPSLISTFRG,"ok");
                        msgToChangeSociaFrg=false;
                    }
                    activity.startActivity(it);
                    break;
            }

        }
    }
}
