package com.zuzhili.ui.fragment;
import java.util.HashMap;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.controller.ChatListAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Common;
import com.zuzhili.model.msg.MsgPairRec;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.CustomDialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ProgressBar;

import com.zuzhili.ui.views.PullRefreshListView;

public class ChatListFrg extends FixedOnActivityResultBugFragment implements
     AdapterView.OnItemClickListener, OnItemLongClickListener, BaseActivity.HandleProgressBarVisibilityCallback{
	protected ChatListAdapter chatListAdapter;

    @ViewInject(R.id.listView)
    protected PullRefreshListView mlistview;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

    protected  int pos;

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
		View view = inflater.inflate(R.layout.listview_layout, container, false);
		activity = (BaseActivity) getActivity();
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        update(false);
	}

    public void update(boolean isNeedUpdate) {
        if (chatListAdapter != null && isNeedUpdate) {
            chatListAdapter.clearList();
            chatListAdapter = null;
        }
        if(chatListAdapter == null) {
            chatListAdapter = new ChatListAdapter(getActivity()
                    , mlistview
                    , ImageCacheManager.getInstance().getImageLoader()
                    , MsgPairRec.class, buildRequestParams(), this, false);
        }
        chatListAdapter.setListView(mlistview);
        chatListAdapter.setOnRefreshListener();
        mlistview.setDividerHeight(DensityUtil.dip2px(activity, 1));
        mlistview.setOnItemClickListener(this);
        mlistview.setOnItemLongClickListener(this);
        mlistview.setAdapter(chatListAdapter);
    }
	
    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("listid", getSession().getListid());
            params.put("ids", getSession().getIds());
            params.put("start", "0");
        }
        return params;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MsgPairRec rec= (MsgPairRec)parent.getItemAtPosition(position);
//		Intent it=new Intent(activity,TalkDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.EXTRA_MEMBER, rec.getIdentity());
//        it.putExtras(bundle);
//		super.startActivity(it);
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position,
			long id) {
		boolean b=true;
		final CustomDialog dlg=new CustomDialog(activity, R.style.popDialog);
		dlg.setDisplayView(null, "删除该条私信所有内容?", "确定", "取消");
		dlg.setLBtnListner(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String,String> params=new HashMap<String,String>();
				params.put("ids", getSession().getIds());
				params.put("rids", String.valueOf(((MsgPairRec)parent.getAdapter().getItem(position)).getIdentity().getId()));
				Task.delMsgById(params, new Listener<Common>(){
					@Override
					public void onResponse(Common response) {
                        if(pos>=0){
                        chatListAdapter.removeItem(position);
                        }
						dlg.cancel();
					}
					
				}  , new ErrorListener(){

					@Override
					public void onErrorResponse(VolleyError error) {
						Utils.makeEventToast(activity, error.getMessage(), true);
						LogUtils.i(error.getMessage());
					}
					
				});
				
			}
		});
		dlg.setRBtnListner(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.cancel();
			}
		});
		dlg.show();
		return b;
	}

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }
}
