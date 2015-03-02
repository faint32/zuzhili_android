package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.HashMap;

/**
 * Created by addison on 2/21/14.
 * 创建文件夹
 */
public class CreateFolderActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, Response.Listener<String>, Response.ErrorListener{
    @ViewInject(R.id.edit_folder_name)
    private EditText folderNameEdit;

    @ViewInject(R.id.edit_folder_desc)
    private EditText folderDescEdit;

    @ViewInject(R.id.only_for_mem_cbx)
    private CheckBox authorityCbx;

    private String spaceid;
    private String folderType;      //文件夹类型

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_create_folder);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);
        initData();
        initView();
    }

    private void initView() {
        if(ValidationUtils.validationString(spaceid)){
            authorityCbx.setVisibility(View.VISIBLE);
        } else {
            authorityCbx.setVisibility(View.GONE);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        folderType = intent.getStringExtra(Constants.MULTI_FOLDER_TYPE);
        LogUtils.e(folderType);
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_done_top, getString(R.string.title_create_folder), false);
        return true;
    }

    @Override
    public boolean performClickOnRight() {
        if(checkInput()){
            Task.createNewFolder(getParams(), this, this, folderType);
            showLoading(null);
        }
        return super.performClickOnRight();
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    /**
     * 验证输入
     */
    private boolean checkInput(){
        String albumNameStr = folderNameEdit.getText().toString();
        if(ValidationUtils.validationString(albumNameStr)){
            if(albumNameStr.length() > 20){
                Utils.makeEventToast(this, getString(R.string.folder_name_length_hint), false);
                return false;
            } else {
                String albumDescStr = folderDescEdit.getText().toString();
                if(ValidationUtils.validationString(albumDescStr)){
                    if(albumDescStr.length() > 200){
                        Utils.makeEventToast(this, getString(R.string.folder_desc_length_hint), false);
                        return false;
                    }
                } else {
                    return true;
                }
            }
        } else {
            Utils.makeEventToast(this, getString(R.string.folder_name_hint), false);
            return false;
        }
        return true;
    }

    /**
     * 获取请求参数
     * @return
     */
    private HashMap<String, String> getParams(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ids", mSession.getIds());
        params.put("listid", mSession.getListid());
        params.put("name", folderNameEdit.getText().toString());
        if(ValidationUtils.validationString(folderDescEdit.getText().toString())){
            params.put("desc", folderDescEdit.getText().toString());
        }
        params.put("from", Constants.APP_FROM_ANDROID);
        if(ValidationUtils.validationString(spaceid)){
            params.put("spaceid", spaceid);
            params.put("authority", authorityCbx.isChecked() ? "1" : "0");
        }
        return params;
    }

    @Override
    public void onResponse(String response) {
        removeLoading();
        Utils.makeEventToast(this, getString(R.string.create_success_hint), false);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        removeLoading();
        callback.onException(volleyError);
    }
}
