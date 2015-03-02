package com.zuzhili.ui.activity.article;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.model.article.Article;
import com.zuzhili.ui.activity.BaseActivity;

public class ArticleDetailActivity extends BaseActivity implements  Listener<String>, ErrorListener,BaseActivity.TimeToShowActionBarCallback {
	
	@ViewInject(R.id.title)
	private TextView title;
	
	@ViewInject(R.id.webview)
	private WebView webView;
	
    //文章详情
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		super.setContentView(R.layout.activity_articledetail);
	    ViewUtils.inject(this);
	    setCustomActionBarCallback(this);
	    String apptypeid = getIntent().getStringExtra("apptypeid");
	    HashMap<String,String> params=new HashMap<String,String>();
	    params.put("apptype", "3");
	    params.put("apptypeid", apptypeid);
		if (apptypeid != null && apptypeid.length() > 0) {
			Task.getArticleDetail(params, this, this);
		} 
	}
	
	@Override
	public boolean performClickOnLeft() {
		finish();
		return super.performClickOnLeft();
	}

	@Override
	public boolean showCustomActionBar() {
		initActionBar(R.drawable.icon_back, 0, getString(R.string.articledetail),false);
		return true;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
     callback.onException(error);		
	}
    //初始化视图
	public void initView(){
    title.setVisibility(View.GONE);		
	}
	@Override
	public void onResponse(String response) {
		JSONObject jsonObj = JSON.parseObject(response);
		Article article=jsonObj.getObject("json", Article.class);
		title.setText(article.getTitle());
		webView.setVisibility(View.VISIBLE);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.loadDataWithBaseURL("", article.getContent(), "text/html",
				"UTF-8", "");
	}
	
	
}
