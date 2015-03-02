package com.zuzhili.ui.activity.loginreg;

import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.zuzhili.bussiness.helper.SmsReceiverHelper;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.more.ModifyPasActivity;

public class CheckphoneActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, Listener<String>, ErrorListener {
	// 手机号码
	@ViewInject(R.id.txt_registe_phone)
	private EditText phoneTxt;
	// 验证码
	@ViewInject(R.id.txt_registe_code)
	private EditText codeTxt;
	// 获取手机验证码
	@ViewInject(R.id.btn_registe_request_code)
	private Button codeBtn;
	// 验证按钮
	@ViewInject(R.id.btn_registe_check_code)
	private Button checkcodeBtn;
    //获取上下文
	private Context context=this;
	//60s验证
	int msecondlimit = 60;

    private final int timeLimit = 60;

    private final int REQ_CODE_MOD_PAS = 0;
    private final int REQ_CODE_REG = 1;

	//网络请求的手机号
	String mphonenum;
	//是否停止
	boolean mbstop;

    private boolean isResgister;
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		super.setContentView(R.layout.activity_checkphone);
		ViewUtils.inject(this);
		initMessage();
		setCustomActionBarCallback(this);
        checkcodeBtn.setEnabled(false);
        isResgister = getIntent().getBooleanExtra(Constants.EXTRA_REGISTER, false);
	}

	//获取手机验证码方法
	@OnClick(R.id.btn_registe_request_code)
	public void requestCodeButtonOnClick(View view) {
		String phonenum = phoneTxt.getText().toString().trim();
		requestPhoneCode(phonenum);
	}

	//检测验证码是否正确
	@OnClick(R.id.btn_registe_check_code)
	public void checkcodeBtnOnClick(View view){
        String phonenum = phoneTxt.getText().toString().trim();
		String validatecode=codeTxt.getText().toString().trim();
        requestCheckCode(phonenum, validatecode);
	}
	
	private void initMessage() {
		ContentObserver co = new SmsReceiverHelper(new Handler(),
				CheckphoneActivity.this, codeTxt);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), true, co);

	}

	private void requestPhoneCode(final String phone) {
		if (phone == null || phone.equals("")) {
			Toast.makeText(this, getString(R.string.phone_number_required),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!ValidationUtils.validationPhone(phone)) {
			Toast.makeText(this,
					getString(R.string.right_phone_number_required),
					Toast.LENGTH_SHORT).show();
			return;
		}
		final HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("mobilephone", phone);
		showLoading(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				//RequestManager.getRequestQueue().cancelAll(tag);
			}
		});
		mphonenum = phone;
		mbstop = false;
        codeBtn.setEnabled(false);
		//成功回调电话号码验证

        if (isResgister) {
            Task.getRegiterVerificationCode(requestParams, this, this);
        } else {
            Task.getResetPasswordVerificationCode(requestParams, this, this);
        }
	}
	
	//验证手机号与验证码是否和服务器端一致
	private void requestCheckCode(final String phone, final String code) {
		if(phone == null || phone.equals("")) {
			Toast.makeText(this, getString(R.string.phone_number_required),	Toast.LENGTH_SHORT).show();
			return;
		}
		if (!ValidationUtils.validationPhone(phone)) {
			Toast.makeText(this, getString(R.string.right_phone_number_required),	Toast.LENGTH_SHORT).show();
			return;
		}
		if (code == null || code.length() != 6) {
			Toast.makeText(this, getString(R.string.validate_code_required), Toast.LENGTH_SHORT).show();
			return;
		}
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("mobilephone", phone);
		params.put("key", code);
		Task.validatePhoneCode(params, new Listener<String>(){
			@Override
			public void onResponse(String response) {
                JSONObject jsonObject = JSON.parseObject(response);
                if (isResgister) {
                    if (jsonObject != null && jsonObject.getString("errmsg") != null && jsonObject.getString("errmsg").equals("ok")) {
                        Intent intent = new Intent(context, RegisterActivity.class);
                        intent.putExtra(Constants.EXTRA_PHONE_NUM, phone);
                        startActivityForResult(intent, REQ_CODE_REG);
                    }
                } else {
                    if (jsonObject != null && jsonObject.getString("errmsg") != null && jsonObject.getString("errmsg").equals("ok")) {
                        Intent intent=new Intent(context, ModifyPasActivity.class);
                        intent.putExtra(Constants.EXTRA_PHONE_NUM, phone);
                        intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.PAGE_CHECK_PHONE);
                        startActivityForResult(intent, REQ_CODE_MOD_PAS);
                    }
                }
			}
			
		}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
                if (error instanceof BusinessError) {
                    if (error.getMessage() != null) {
                        JSONObject jsonObject = JSON.parseObject(error.getMessage());
                        if (jsonObject != null && jsonObject.getString("errmsg") != null && !jsonObject.getString("errmsg").equals("ok")) {
                            Utils.makeEventToast(CheckphoneActivity.this, jsonObject.getString("errmsg"), false);
                        }
                    }
                }
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    //点击左侧按钮的事件
	@Override
	public boolean performClickOnLeft() {
		finish();
		return super.performClickOnLeft();
	}
	@Override
	public boolean showCustomActionBar() {
		initActionBar(R.drawable.icon_back,0,getString(R.string.validatephone),false);
		return true;
	}

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        removeLoading();

        if (volleyError instanceof BusinessError) {

            if (volleyError.getMessage() != null) {
                JSONObject jsonObject = JSON.parseObject(volleyError.getMessage());
                if (jsonObject != null && jsonObject.getString("errmsg") != null && !jsonObject.getString("errmsg").equals("ok")) {
                    Utils.makeEventToast(CheckphoneActivity.this, jsonObject.getString("errmsg"), false);
                    mbstop = true;
                    codeBtn.setEnabled(true);
                    checkcodeBtn.setEnabled(false);
                    msecondlimit = timeLimit;
                }
            }

        }
    }

    @Override
    public void onResponse(String s) {
        removeLoading();
        checkcodeBtn.setEnabled(true);
        Utils.makeEventToast(CheckphoneActivity.this, getString(R.string.hint_verification_code_sended), false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mbstop) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeBtn.setText("("
                                    + String.valueOf(msecondlimit) + ")"
                                    + "获取手机验证码");
                        }
                    });
                    msecondlimit--;
                    if (msecondlimit == 0) {
                        break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        codeBtn.setEnabled(true);
                        msecondlimit = timeLimit;
                        codeBtn.setText("获取手机验证码");
                    }
                });

            }
        }).start();
    }
}
