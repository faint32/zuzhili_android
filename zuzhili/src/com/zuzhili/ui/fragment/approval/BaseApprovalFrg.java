package com.zuzhili.ui.fragment.approval;

import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.controller.approval.ApprovalAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.views.PullRefreshListView;

public abstract class BaseApprovalFrg extends FixedOnActivityResultBugFragment implements BaseActivity.HandleProgressBarVisibilityCallback{

	protected PullRefreshListView pullRefreshListView;//下拉进度

    protected ProgressBar progressBar;//进度条

    protected String requestType;//请求类型

    protected ResultsAdapter adapter;//适配器
	
	@Override
	public void setProgressBarVisibility(int visibility) {
		 progressBar.setVisibility(visibility);	
	}
             
	@Override    //展示
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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

	protected abstract void setRequestType();

    protected abstract void reset();

    public void update() {
        setRequestType();
        reset();
        if (adapter == null) {
            adapter = new ApprovalAdapter(activity
                    , pullRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    ,buildRequestParams());
        }
        adapter.setListView(pullRefreshListView);
        adapter.setOnRefreshListener();
        pullRefreshListView.setAdapter(adapter);
//        pullRefreshListView.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position,
//					long id) {
//				Approval approval= (Approval)parent.getItemAtPosition(position);
//				Intent it=new Intent(activity,ApprovalDetailActivity.class);
//		        Bundle bundle = new Bundle();
//		        bundle.putSerializable("approval", approval);
//		        it.putExtras(bundle);
//				startActivity(it);
//			}
//
//        });
    }
    //重建参数
    protected HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
        	params.put("ids", mSession.getIds());
        	params.put("listid", mSession.getListid());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
            params.put("type", requestType);
        }
        return params;
    }
    
    public PullRefreshListView getPullRefreshListView() {
        return pullRefreshListView;
    }
	
}
