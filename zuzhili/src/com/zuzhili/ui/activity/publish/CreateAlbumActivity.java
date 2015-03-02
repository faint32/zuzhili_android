package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.HashMap;

/**
 * Created by addison on 2/20/14.
 * 创建图片册
 */
public class CreateAlbumActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, Response.Listener<String>, Response.ErrorListener {

    @ViewInject(R.id.edit_album_name)           //图片册名称
    private EditText albumNameEdit;

    @ViewInject(R.id.edit_album_desc)           //图片册描述
    private EditText albumDsecEdit;

//    @ViewInject(R.id.only_for_mem_cbx)          //仅成员可见（只在空间发布时显示）
//    private CheckBox authorityCbx;

    @ViewInject(R.id.cbx_available)
    private CheckBox available;

    @ViewInject(R.id.layout_available_container)
    private ViewGroup layoutAvailableContainer;

    private String spaceid;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_create_album);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        initData();
        initView();
    }

    private void initView() {
//        if(ValidationUtils.validationString(spaceid)){
//            authorityCbx.setVisibility(View.VISIBLE);
//        } else {
//            authorityCbx.setVisibility(View.GONE);
//        }
    }

    private void initData() {
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        //个人发布不显示仅成员可见权限
        if (!ValidationUtils.validationString(spaceid)) {
            layoutAvailableContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean performClickOnRight() {
        if(checkInput()){
            Task.createNewAlbum(getParams(),this, this);
            showLoading(null);
        }
        return super.performClickOnRight();
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_done_top, getString(R.string.create_new_album) , false);
        return true;
    }

    /**
     * 验证输入
     */
    private boolean checkInput(){
        String albumNameStr = albumNameEdit.getText().toString();
        if(ValidationUtils.validationString(albumNameStr)){
            if(albumNameStr.length() > 20){
                Utils.makeEventToast(this, getString(R.string.album_name_length_hint), false);
                return false;
            } else {
                String albumDescStr = albumDsecEdit.getText().toString();
                if(ValidationUtils.validationString(albumDescStr)){
                    if(albumDescStr.length() > 200){
                        Utils.makeEventToast(this, getString(R.string.album_desc_length_hint), false);
                        return false;
                    }
                } else {
                    return true;
                }
            }
        } else {
            Utils.makeEventToast(this, getString(R.string.album_name_str_hint), false);
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
        params.put("name", albumNameEdit.getText().toString());
        if(ValidationUtils.validationString(albumDsecEdit.getText().toString())){
            params.put("desc", albumDsecEdit.getText().toString());
        }
        params.put("from", Constants.APP_FROM_ANDROID);
        if(ValidationUtils.validationString(spaceid)){
            params.put("spaceid", spaceid);
            params.put("authority", available.isChecked() ? "1" : "0");
//            params.put("authority", authorityCbx.isChecked() ? "1" : "0");
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
