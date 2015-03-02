package com.zuzhili.ui.activity.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.HashMap;

public class FeedBackActivity extends BaseActivity implements Listener<String>, ErrorListener{
	 @ViewInject(R.id.et_feedback)
	private EditText et_feedback;
    @Override
    protected void onCreate(Bundle inState) {
    	super.onCreate(inState);
    	super.setContentView(R.layout.activity_feedback);
    	ViewUtils.inject(this);
    }
    
    private  HashMap<String,String> buildParame(){
    	HashMap<String,String>  requestparams =new HashMap<String,String>();
    	requestparams.put("ids", mSession.getIds());
		requestparams.put("listsname", mSession.getSocialName());
		requestparams.put("username", mSession.getUserName());
		requestparams.put("phone", "");
		requestparams.put("from", "1");
		requestparams.put("content", et_feedback.getText().toString().trim());
		return requestparams;
    }
    @Override
    public boolean performClickOnLeft() {
        if (!TextUtils.isEmpty(et_feedback.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.if_save_to_draftbox)
                    .setTitle(getString(R.string.publish_text));
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.show();
        } else {
            finish();
        }

        return super.performClickOnLeft();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            performClickOnLeft();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean performClickOnRight() {
    	if(et_feedback.getText().toString().trim().length()>0){
            showLoading(null);
            Task.sendFeedBack(buildParame(), this, this);
    	}
    	else{
            Utils.makeEventToast(this, getString(R.string.hint_add_content), true);
    	}
    	return super.performClickOnRight();
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	super.initActionBar(R.drawable.icon_back, R.drawable.top_03,getString( R.string.adviseback), false);
    	return true;
    }
	@Override
	public void onErrorResponse(VolleyError error) {
		callback.onException(error);
	}
	@Override
	public void onResponse(String response) {
        removeLoading();
		Utils.makeEventToast(this, getString(R.string.publish_success_hint), true);
	}
}
