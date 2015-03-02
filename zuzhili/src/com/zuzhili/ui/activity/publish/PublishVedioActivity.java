package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.OnNetListener;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.folder.MutilFolder;
import com.zuzhili.model.multipart.MultiUpload;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.VedioListActivity;
import com.zuzhili.ui.views.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishVedioActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, OnNetListener {

    @ViewInject(R.id.lin_select_folder)
    private LinearLayout selectFolderLin;

    @ViewInject(R.id.img_file_icon)
    private ImageView coverImg;

    @ViewInject(R.id.txt_folder_name)
    private TextView folderNameTxt;

    @ViewInject(R.id.edit_name)
    private EditText nameEdit;

    @ViewInject(R.id.edit_desc)
    private EditText descEdit;

    @ViewInject(R.id.ibtn_delete)
    private ImageButton deleteIbtn;

    private String from;
    private MultiUpload curVedio;       //  待上传vedio
    private String spaceid;
    private MutilFolder curFolder;      //当前文件夹\
    private List<MultiUpload> datalist = new ArrayList<MultiUpload>();

    private static final int FLAG_ACTIVIY_SELECT_FOLDER = 1;
    private static final int FLAG_ACTIVITY_SELECT_VEDIO = 2;


    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_publish_vedio);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        from = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE);
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        if(ValidationUtils.validationString(from) && from.equals(Constants.VEDIO_LIST)){
           fillDatas(intent);
        }
    }

    private void fillDatas(Intent intent) {
        datalist.clear();
        Bundle bundle = intent.getExtras();
        curVedio = new MultiUpload();
        curVedio.setNewfilename(bundle.getString(Constants.VEDIO_NAME));
        curVedio.setDesc("暂无描述");
        curVedio.setFilepath(bundle.getString(Constants.VEDIO_PATH));
        curVedio.setSrc((Bitmap)bundle.getParcelable(Constants.VEDIO_COVER));
        curVedio.setFileidentity(TextUtil.getUniqueFileName(bundle.getString(Constants.VEDIO_PATH)));
        datalist.add(curVedio);
        fillView();
    }

    /**
     * 填充数据
     */
    private void fillView() {
        if(ValidationUtils.validationString(curVedio.getNewfilename()))
            nameEdit.setText(curVedio.getNewfilename());
        if(ValidationUtils.validationString(curVedio.getDesc()))
            descEdit.setText(curVedio.getDesc());
        if(curVedio.getSrc() != null)
            coverImg.setImageBitmap(curVedio.getSrc());
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_publish_vedio), false);
        return true;
    }

    @Override
    public boolean performClickOnRight() {
        if(checkInput()){
            Task.upload(this, getParams());
            Utils.makeEventToast(this, getString(R.string.sending_hint), false);
            setResult(RESULT_OK);
            finish();
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
        if(curFolder == null && TextUtils.isEmpty(spaceid)){
            Utils.makeEventToast(this, getString(R.string.select_folder_hint), false);
            return false;
        }
        if(curVedio == null){
            Utils.makeEventToast(this, getString(R.string.select_vedio_hint), false);
            return false;
        }
        String nameStr = nameEdit.getText().toString().trim();
        if(!ValidationUtils.validationString(nameStr)){
            Utils.makeEventToast(this, getString(R.string.input_vedio_name_hint), false);
            return false;
        }
        String descStr = descEdit.getText().toString().trim();
        if(!ValidationUtils.validationString(descStr)){
            curVedio.setDesc("暂无描述");
        } else {
            curVedio.setDesc(descStr);
        }
        return true;
    }

    /**
     * 获取发布参数
     */
    private RequestParams getParams(){
        RequestParams params = new RequestParams();
        params.setListener(this);
        params.setTask(Task.API_URLS.get(Task.ACTION_PUBLISH_VEDIO));
        params.addBodyParameter("ids", mSession.getIds());
        params.addBodyParameter("listid", mSession.getListid());
        params.addBodyParameter("from", Constants.APP_FROM_ANDROID);
        if(ValidationUtils.validationString(spaceid))
            params.addBodyParameter("spaceid", spaceid);
        if(TextUtils.isEmpty(spaceid)){
            params.addBodyParameter("folderid", curFolder.getId());
        }
        params.addBodyParameter("desc", JSON.toJSONString(datalist));
        for(MultiUpload item : datalist){
            params.addBodyParameter(item.getFileidentity(), new File(item.getFilepath()));
        }
        params.addBodyParameter("content", datalist.get(0).getNewfilename());
        return params;
    }

    /**
     * 选择文件夹
     */
    @OnClick(R.id.lin_select_folder)
    public void selectFolder(View view){
        Intent intent = new Intent(this, MultiFolderListActivity.class);
        intent.putExtra(Constants.MULTI_FOLDER_TYPE, Constants.MULTI_FOLDER_TYPE_VEDIO);
        if(ValidationUtils.validationString(spaceid))
            intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, spaceid);
        startActivityForResult(intent, FLAG_ACTIVIY_SELECT_FOLDER);
    }

    /**
     * 删除该视频
     * @param view
     */
    @OnClick(R.id.delete_ibtn)
    public void deleteVedio(View view){
        final CustomDialog dialog = new CustomDialog(this, R.style.popDialog);
        dialog.setDisplayView(null, getString(R.string.delete_vedio_hint), getString(R.string.cancel), getString(R.string.confirm));
        dialog.setRBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setLBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 选择视频
     * @param view
     */
    @OnClick(R.id.img_file_icon)
    public void selectVedio(View view){
        Intent intent = new Intent(this, VedioListActivity.class);
        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_VEDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVIY_SELECT_FOLDER){
            if(resultCode == Activity.RESULT_OK){
                MutilFolder folder = (MutilFolder) data.getSerializableExtra(Constants.MULTI_FOLDER_ITEM);
                if(folder != null){
                    curFolder = folder;
                    folderNameTxt.setText(curFolder.getName());
                }
            }
        } else if(requestCode == FLAG_ACTIVITY_SELECT_VEDIO){
            if(resultCode == Activity.RESULT_OK){
                fillDatas(data);
            }
        }
    }

    @Override
    public void OnNetSuccess(RequestParams params) {
        Utils.makeEventToast(this, getString(R.string.publish_success_hint), false);
    }

    @Override
    public void OnNetFailure(RequestParams params) {

    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {

    }
}
