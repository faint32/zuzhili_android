package com.zuzhili.ui.activity.social;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.social.JoinedInSocial;
import com.zuzhili.model.social.Social;
import com.zuzhili.service.GetIMDataIntentService;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.CustomDialog;
public class SocialManagerActivity extends BaseActivity implements
		BaseActivity.TimeToShowActionBarCallback, Listener<String>, ErrorListener{
	//创建人
	@ViewInject(R.id.socialsummary_creater)
	private TextView creator;
	//简介
	@ViewInject(R.id.socialsummary_summary)
	private TextView summary;
	//社区名称
	@ViewInject(R.id.socialsummary_name)
	private TextView summary_name;
	//社区人数
	@ViewInject(R.id.socialmembercount)
	private TextView membercount;
	//加入社区
	@ViewInject(R.id.socialsummary_add)
	private Button socialsummary_add;

	@ViewInject(R.id.img)
	private ImageView socialImg;
	//点击添加按钮
	   /** 筛选对话框 */
    private CustomDialog filterDialog;
    private BaseActivity context=this;
    private int  activeType;
    private Social social=null;

	@OnClick(R.id.socialsummary_add)
	public void applySocialSummary(View view){
        if(getString(R.string.social_apply).equals(socialsummary_add.getText().toString())){
            initFilterDialog();
            filterDialog.show();
        }else {
            Intent it=new Intent(this,SocialsActivity.class);
            it.putExtra(Constants.CHANGE_SOCIAL,social.getId()+"");
            startActivity(it);
            finish();
        }

	}
	// 创建管理视图
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		super.setContentView(R.layout.activity_socialmanage);
		ViewUtils.inject(this);
		setCustomActionBarCallback(this);
		initData();
	}
	
  
	public void initData(){
		Intent it=super.getIntent();
		//活动类型
		activeType = it.getIntExtra("activeType", 0);

	    social=(Social) it.getSerializableExtra("social");
		//创建人
		SpannableString createrSpan = new SpannableString(getString(R.string.createuser)+":"+social.getNickname());
		createrSpan.setSpan(new ForegroundColorSpan(0xff006484), 4, social.getNickname().length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		creator.setText(createrSpan);
		//简介
		SpannableString descpSpan = new SpannableString(getString(R.string.intro)+":"+ social.getListdesc());
		descpSpan.setSpan(new ForegroundColorSpan(0xff006484), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		summary.setText(descpSpan);
		//社区名称
		SpannableString socialNameSpan = new SpannableString(getString(R.string.social_name)+":" +social.getListname());
		socialNameSpan.setSpan(new ForegroundColorSpan(0xff006484), 5, social.getListname().length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		summary_name.setText(socialNameSpan);
		//社区人数
		String usercount=""+social.getCountUser();
		SpannableString socialCountSpan = new SpannableString(getString(R.string.social_memeber_count) +":"+ social.getCountUser());
		socialCountSpan.setSpan(new ForegroundColorSpan(0xff006484), 5, usercount.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		membercount.setText(socialCountSpan);
		//社区图片
		ImageLoader imageLoader=ImageCacheManager.getInstance().getImageLoader();
		imageLoader.get(social.getLogo(), ImageLoader.getImageListener(socialImg, R.drawable.photo_200, R.drawable.photo_200));
		if(isJoinedSocail(social.getId())){
	        socialsummary_add.setText("进入社区");
		}
		
	}
	
	
	public void applySocial(EditText mreasoninput,EditText etName){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("listid", String.valueOf(social.getId()));
		params.put("userid", mSession.getUid());
		params.put("reason", mreasoninput.getText().toString().trim());
		params.put("name", etName.getText().toString().trim());
		Task.applySocial(params, this, this);

	}

    // TODO: 更新本地社区缓存
    public void updateJoinedSocialList() {
        Task.getMySocials(buildRequestSocialParams(), this, this);
    }

    private HashMap<String, String> buildRequestSocialParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("userid", mSession.getUid());
        }
        return params;
    }

	 /**
     * 初始化筛选对话框
     */
    private void initFilterDialog() {
        filterDialog = new CustomDialog(this, R.style.popDialog);
        View filterView = mLayoutInflater.inflate(R.layout.dialog_socialmanage_request, null);
        filterDialog.setDisplayView(filterView, null);
        filterDialog.setPropertyTop(0, DensityUtil.dip2px(this, 100), 0.80);
        //申请理由
        final EditText mreasoninput = (EditText) filterView.findViewById(R.id.input);
        //输入框
        final  EditText etName = (EditText) filterView.findViewById(R.id.input_name);
        //确认按钮
        Button btn = (Button) filterView.findViewById(R.id.ok);
		btn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				 String reason = mreasoninput.getText().toString().trim();
					if(reason == null || reason.length() == 0) {
						Utils.makeEventToast(context, getString(R.string.space_apply_reason_acquired), false);
						return;
					}
					String name = etName.getText().toString();
					if (name == null || name.length() == 0) {
						Utils.makeEventToast(context, getString(R.string.social_request_err_name), false);
						return;
					}
				applySocial(mreasoninput,etName);
			}
		});
	   Button	cancelbtn = (Button) filterView.findViewById(R.id.cancel);
	   cancelbtn.setOnClickListener(new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			filterDialog.dismiss();
		}
	   });
    }

	
	
	//判断用户是否加入了社区
	private boolean isJoinedSocail(int socialid){
		boolean flag=false;
		List<JoinedInSocial> socialList= mSession.getAccount().getList();
		for(JoinedInSocial socail:socialList){
		   if(socail.getId()==socialid){
			   flag=true;
			   break;
		   }	
		}
		return flag;
	}

	private JoinedInSocial getJoinedSocail(int socialid){
		List<JoinedInSocial> socialList= mSession.getAccount().getList();
		for(JoinedInSocial socail:socialList){
		   if(socail.getId()==socialid){
			   return socail;
		   }
		}
		return null;
	}

	@Override
	public boolean showCustomActionBar() {
		initActionBar(R.drawable.icon_back, null, getString(R.string.social_title_summary), false); 
		return true;
	}
	
	
	//点击左侧按钮返回上层
	@Override
	public boolean performClickOnLeft() {
		finish();
		return super.performClickOnLeft();
	}
	@Override
	public void onErrorResponse(VolleyError error) {
        if (error instanceof BusinessError) {
            if (error.getMessage() != null) {
                JSONObject jsonObject = JSON.parseObject(error.getMessage());
                if (jsonObject.getString("errmsg") != null) {
                    Utils.makeEventToast(this, jsonObject.getString("errmsg"), false);
                }
            }
        }
        filterDialog.dismiss();
	}
	@Override
	public void onResponse(String response) {
        if (response != null) {
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject.getString("errmsg") != null && jsonObject.getString("errmsg").equals("ok")) {
                if (jsonObject.getString("errinfo") != null) {
                    Utils.makeEventToast(this, jsonObject.getString("errinfo"), false);
                    filterDialog.dismiss();
                } else if (jsonObject.getString("list") != null) {
                    List<JoinedInSocial> list = JSON.parseArray(jsonObject.getString("list"), JoinedInSocial.class);
                    if (list != null) {
                        mSession.getAccount().setList(list);
                    }
                } else {
                    Utils.makeEventToast(this, getString(R.string.sendsuccess), false);
                    socialsummary_add.setVisibility(View.GONE);
                    filterDialog.dismiss();
                }
            }

        }
	}

}
