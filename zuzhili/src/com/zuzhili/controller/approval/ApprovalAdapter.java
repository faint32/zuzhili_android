package com.zuzhili.controller.approval;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.TimeUtils;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.model.approval.Approval;

public class ApprovalAdapter extends ResultsAdapter<Approval>{
	private final String STATUS_GRANTED = "已审批";
	private final String STATUS_WAIT = "待审批";
	public ApprovalAdapter(Context context, ListView listView,
                           ImageLoader imageLoader, HashMap<String, String> params) {
		super(context, listView, imageLoader, params);
		Task.getApprovals(params, this, this);
	}

	@Override
	public void onRefresh() {
		isPullOnRefreshEnd = false;
        mParams.put("start", FIRST_PAGE);
        Task.getApprovals(mParams, this, this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		Approval approval=super.getItem(position);
		try {
			view = mInflater.inflate(R.layout.approval_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.tvTitle = (TextView) view.findViewById(R.id.tv_approval_title);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
			holder.tvInitatorName = (TextView) view.findViewById(R.id.tv_initator);
			holder.btStatus = (Button) view.findViewById(R.id.bt_status);
			view.setTag(holder);
			String time = TimeUtils.getTimeMinute(approval.getTime());
			holder.tvTime.setText(time);
			holder.tvTitle.setText(approval.getTitle());
			if(null!=approval.getIdentity()){
			holder.tvInitatorName.setText(approval.getIdentity().getName());
			}
			else{
		    holder.tvInitatorName.setText("");	
			}
			int status = approval.getStatus();
			if(status == 0) {
				holder.btStatus.setText(STATUS_WAIT);
				holder.btStatus.setBackgroundResource(R.color.status_granted);
			} else {
				holder.btStatus.setText(STATUS_GRANTED);
				holder.btStatus.setBackgroundResource(R.color.status_wait);
			}
			//判断是否有分页如果有分页则加载下一页
			if(shouldLoadNextPage(mDataList, position)) {
					loadedPage++;
					updateRequestParams(mParams);
					loadNextPage();
					mListView.onFooterRefreshBegin();
					LogUtils.e("load page: " + loadedPage);
		     }
				
		}catch(InflateException e){
			
			
		}
		return view;
	}

	@Override
	public List<Approval> parseList(String response) {
	    JSONObject rst = (JSONObject) JSON.parseObject(response);
        List<Approval> approvalList = JSON.parseArray(rst.getString("list"), Approval.class);
		return approvalList;
	}

	@Override
	public void loadNextPage() {
		  isLoading = true;
		  Task.getApprovals(mParams, this, this);
	}

	class ViewHolder{
		TextView tvTitle;
		TextView tvTime;
		TextView tvInitatorName;
		Button btStatus;
	}
}
