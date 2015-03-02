package com.zuzhili.ui.activity.more;
import java.util.HashMap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

public class ModifyPasActivity extends BaseActivity  implements Listener<String>, ErrorListener{
	//历史密码
	@ViewInject(R.id.old_passwordtxt)
    private EditText oldPassTxt;
	//新密码
	@ViewInject(R.id.new_passwordtxt)
    private EditText newpasswordTxt;
	//重复密码
	@ViewInject(R.id.repeat_newpasswordtxt)
    private EditText repeatnewpasswordTxt;
	
	private String oldLocalPassword = "";
	//更改账号按钮
	@ViewInject(R.id.btn_submit)
	private Button btn_submit;

    private String phoneNum;

    private int from;   // 从哪个页面跳转过来
	
    @Override
    protected void onCreate(Bundle inState) {
    	super.onCreate(inState);
    	super.setContentView(R.layout.activity_modify_pas);
    	ViewUtils.inject(this);
    	oldLocalPassword = mSession.getPassword();
        phoneNum = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUM);
        if (phoneNum == null || TextUtils.isEmpty(phoneNum)) {
            phoneNum = mSession.getEmail();
        }
        from = getIntent().getIntExtra(Constants.EXTRA_FROM_WHICH_PAGE, 0);
        if (from == Constants.PAGE_CHECK_PHONE) {
            oldPassTxt.setVisibility(View.GONE);
        }
    }
    //修改
    @OnClick(R.id.btn_submit)
    public void changePass(View view){
    	if(isMatch()){
    		showLoading(null);
        	Task.modifyPass(getParams(phoneNum, newpasswordTxt.getText().toString().trim()), this, this);
         }
    }
    public HashMap<String,String> getParams(String username,String password){
    	HashMap<String,String> params=new HashMap<String,String> ();
    	params.put("password", password);
    	params.put("mobilephone", username);
    	return params;
    }
    @Override
    public boolean performClickOnLeft() {
    	finish();
    	return super.performClickOnLeft();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	super.initActionBar(R.drawable.icon_back, 0, getString(R.string.changepass), false);
    	return true;
    }
	@Override
	public void onErrorResponse(VolleyError error) {
		callback.onException(error);		
	}
	
	private boolean isMatch() {
		boolean ret = false;
		String pass=repeatnewpasswordTxt.getText().toString().trim();
		String rpass=newpasswordTxt.getText().toString().trim();
		if (from != Constants.PAGE_CHECK_PHONE && TextUtils.isEmpty(oldPassTxt.getText().toString().trim())) {
            Utils.makeEventToast(this, getString(R.string.inputoldpass), false);
        } else if(from != Constants.PAGE_CHECK_PHONE && !oldLocalPassword.equals(oldPassTxt.getText().toString().trim())) {
            Utils.makeEventToast(this, getString(R.string.passrestart), false);
        } else if(TextUtils.isEmpty(newpasswordTxt.getText().toString().trim())) {
			Utils.makeEventToast(this, getString(R.string.inputnewpass), false);
		} else if(newpasswordTxt.length() < 6) {
			Utils.makeEventToast(this, getString(R.string.passlimite), false);
		} else if(newpasswordTxt.getText().toString().trim().equals(oldLocalPassword)) {
			Utils.makeEventToast(this, getString(R.string.diffnumerror), false);
		} else if(TextUtils.isEmpty(repeatnewpasswordTxt.getText())) {
			Utils.makeEventToast(this, getString(R.string.reinputpass), false);
		} else if(repeatnewpasswordTxt.length() < 6) {
			Utils.makeEventToast(this, getString(R.string.passlimite), false);
		} 
		else if(Utils.isContainChineseCharacters(pass)) {
			Utils.makeEventToast(this, getString(R.string.passtypeerror), false);
			}
		else if(!pass.equals(rpass)) {
			Utils.makeEventToast(this, getString(R.string.passsameerror), false);
		} else {
			ret = true;
		}
		return ret;
	}
	@Override
	public void onResponse(String response) {
		mSession.setPassword(newpasswordTxt.getText().toString().trim());
		super.removeLoading();
		Utils.makeEventToast(this, getString(R.string.changesuccess), false);
        setResult(RESULT_OK);
        finish();
	}
    
}
