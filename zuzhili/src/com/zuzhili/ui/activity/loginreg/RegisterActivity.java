package com.zuzhili.ui.activity.loginreg;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

public class RegisterActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, Listener<String>, ErrorListener{
	private String phoneNum;// 电话号码
	@ViewInject(R.id.registe)
    private Button registeBtn;//注册
	@ViewInject(R.id.txt_registe_username)
	private EditText usernameTxt;//用户名
	@ViewInject(R.id.txt_registe_password)
	private EditText passwdTxt;//注册密码
	//注册
	@OnClick(R.id.registe)
    public void regite(View view){
		String username = usernameTxt.getText().toString().trim();
		String password = passwdTxt.getText().toString();

        if (username == null || TextUtils.isEmpty(username)) {
            Utils.makeEventToast(this, getString(R.string.inputusername), false);
            return;
        } else if (password == null || TextUtils.isEmpty(password)) {
            Utils.makeEventToast(this, getString(R.string.inputpass), false);
            return;
        } else if (password != null && password.length() < 6) {
            Utils.makeEventToast(this, getString(R.string.passlimite), false);
            return;
        }

		final HashMap<String,String> params=new HashMap<String,String>();
		params.put("truename", username);
		params.put("password", password);
		params.put("phone", phoneNum);
//        params.put("listid", "644");  // 财经青年社区
        Task.register(params, this, this);
//	    Task.registerNonZuzhii(params, this, this);
    }
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		super.setContentView(R.layout.activity_register);
		ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        //获取的电话号码
		Intent it = getIntent();
		if (it != null) {
			phoneNum = it.getStringExtra(Constants.EXTRA_PHONE_NUM);
		}
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
        if (error instanceof BusinessError) {

            if (error.getMessage() != null) {
                JSONObject jsonObject = JSON.parseObject(error.getMessage());
                if (jsonObject != null && jsonObject.getString("errmsg") != null && !jsonObject.getString("errmsg").equals("ok")) {
                    Utils.makeEventToast(RegisterActivity.this, jsonObject.getString("errmsg"), false);
                }
            }

        }
	}
	@Override
	public void onResponse(String response) {
        setResult(RESULT_OK);
        finish();
	}

    @Override
    public boolean performClickOnLeft() {
        super.performClickOnLeft();
        finish();
    	return true;
    }

    @Override
         public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, getString(R.string.register), false);
        return false;
    }
}
