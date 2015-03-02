package com.zuzhili.ui.activity.loginreg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Account;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.HomeTabActivity;
import com.zuzhili.ui.activity.social.SocialsActivity;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements Listener<String>,
                                                                ErrorListener,
                                                                BaseActivity.TimeToShowActionBarCallback {

	@ViewInject(R.id.txt_login_username)
	private EditText  usernameTxt;

    @ViewInject(R.id.txt_login_password)
	private EditText passwordEdit;

	@ViewInject(R.id.btn_login_login)
	private Button loginBtn;

    private Account account;

	@OnClick(R.id.btn_login_login)
	public void loginButtonOnClick(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(),0);
		String email = usernameTxt.getText().toString().trim();
		String password = passwordEdit.getText().toString();
		final HashMap<String,String> params = new HashMap<String,String>();
        params.put("email", email);
        params.put("password", password);
		if(checkInput(email, password)) {
            showLoading(null);
            if (!Utils.isNetworkAvailable(this)) {
                Utils.makeEventToast(this, getString(R.string.check_network_connectivity), false);
                removeLoading();
                return;
            }
            if (Utils.isThirdPackage(this)) {
                params.put("listid", "11054");
                params.put("isAutoSave", "");
                params.put("token", "");
                Task.loginNonZuzhili(params, this, this);
            } else {
                Task.login(params, this, this);
            }
            loginBtn.setEnabled(false);
		}else {

        }
    }

	/**
	 * check account name and password format
	 * @param name
	 * @param password
	 * @return
	 */
	private boolean checkInput(String name, String password) {
		// 检测是否中文
		if(Utils.isContainChineseCharacters(name) || Utils.isContainChineseCharacters(password)) {
			Utils.makeEventToast(this, getString(R.string.input_characters_format_error), false);
			return false;
		}
		
		if(name == null || name.equals("")) {
			Utils.makeEventToast(this, getString(R.string.account_name_required), false);
			return false;
		}
		
		if(password == null || password.equals("")) {
			Utils.makeEventToast(this, getString(R.string.account_password_required), false);
			return false;
		}
		
		if(!ValidationUtils.validationName(name)) {
				Utils.makeEventToast(this, getString(R.string.right_phone_number_required), false);
				return false;
		}
		return true;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	super.setContentView(R.layout.activity_login);
    	ViewUtils.inject(this);
    	setCustomActionBarCallback(this);
        if (mSession.getEmail() != null) {
            usernameTxt.setText(mSession.getEmail());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginBtn.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

	//点击左侧按钮的事件
	@Override
	public boolean performClickOnLeft() {
        Intent intent=new Intent(this,WebViewActivity.class);
        intent.putExtra("url", Task.API_SANWEI_HOST + "/account/forgetPwdByTel.shtml");
        startActivity(intent);
        return super.performClickOnLeft();
	}
	//点击右侧按钮的事件
	@Override
     public boolean performClickOnRight() {
        Intent intent=new Intent(this,WebViewActivity.class);
        intent.putExtra("url", Task.API_SANWEI_HOST + "account/accessMobileRegister.shtml");
        startActivity(intent);
        return super.performClickOnRight();
    }

	@Override
	public boolean showCustomActionBar() {
		initActionBar(getString(R.string.forgetpass), getString(R.string.register), null, false);
		return true;
	}

    @Override
    public void onResponse(String result) {

        JSONObject jsonObject = JSONObject.parseObject(result);

        if (jsonObject.getString("user") != null) {
            JSONObject user = JSON.parseObject(jsonObject.getString("user"));
            if (user != null) {
                mSession.setVoipId(user.getString("y_voip"));
                mSession.setVoipPassword(user.getString("y_voippass"));
                mSession.setSubAccount(user.getString("y_subid"));
                mSession.setSubToken(user.getString("y_subpass"));
                doSDKRegist();
            }
            if(!account.isCache) {
                removeLoading();
                Intent intent = new Intent(this, SocialsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, HomeTabActivity.class);
                startActivity(intent);
            }
        } else {
            account = JSON.parseObject(result, Account.class);
            mSession.setAccount(account);
            mSession.setUid(account.getUserid());
            mSession.setEmail(usernameTxt.getText().toString().trim());
            mSession.setPassword(passwordEdit.getText().toString().trim());
            mSession.setAutoLogin(true);

            // 获取个人云通讯帐号相关信息
            Task.getYTXAccount(buildGetYTXAccountParams(), this, this);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        removeLoading();
        if (error instanceof BusinessError) {

            if (error.getMessage() != null) {
                JSONObject jsonObject = JSON.parseObject(error.getMessage());
                if (jsonObject != null && jsonObject.getString("errmsg") != null && !jsonObject.getString("errmsg").equals("ok")) {
                    Utils.makeEventToast(LoginActivity.this, jsonObject.getString("errmsg"), false);
                }
            }
        }
        loginBtn.setEnabled(true);
    }

    private HashMap<String, String> buildGetYTXAccountParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("u_listid", "0");  // no meaning, just a useless argument
            params.put("u_id", mSession.getUid());
        }
        return params;
    }
}
