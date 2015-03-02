package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.CustomDialog;

/**
 * Created by addison on 2/21/14.
 * 编辑文件、音视频
 */
public class FileEditActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.edit_name)
    private EditText fileNameEdit;              //文件名字

    @ViewInject(R.id.edit_desc)
    private EditText fileDescEdit;              //文件描述

    @ViewInject(R.id.img_file_icon)
    private ImageView fileCoverImg;             //文件图标

    @ViewInject(R.id.ibtn_delete)               //删除
    private ImageButton deleteIbtn;

    @ViewInject(R.id.lin_select_folder)
    private LinearLayout selectFolderLin;       //选择文件夹（在视频选择的时显示）


    private String fileType;
    private String fileName;
    private String fileDesc;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_edit_file);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);
        initData();
        initView();
    }

    private void initView() {
        if(ValidationUtils.validationString(fileName))
            fileNameEdit.setText(fileName);
        if(ValidationUtils.validationString(fileDesc))
            fileDescEdit.setText(fileDesc);
    }

    private void initData() {
        fileType = getIntent().getStringExtra(Constants.FILE_TYPE);
        fileName = getIntent().getStringExtra(Constants.FILE_NAME);
        fileDesc = getIntent().getStringExtra(Constants.FILE_DESC);
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        if(checkInput()){
            if(fileType.equals(Constants.FILE_TYPE_VEDIO)){

            } else {
                Intent intent = getIntent();
                intent.putExtra(Constants.FILE_NAME, fileNameEdit.getText().toString().trim());
                intent.putExtra(Constants.FILE_DESC, fileDescEdit.getText().toString().trim());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return super.performClickOnRight();
    }

    /**
     * 弹窗提示删除文件
     * @param view
     */
    @OnClick(R.id.ibtn_delete)
    public void deleteFile(View view){
        final CustomDialog dialog = new CustomDialog(this, R.style.popDialog);
        dialog.setDisplayView(null, getString(R.string.file_delete_hint), getString(R.string.confirm), getString(R.string.cancel));
        dialog.setLBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = getIntent();
                intent.putExtra(Constants.FILE_IS_DELETED, true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        dialog.setRBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean showCustomActionBar() {
        String title = null;
        if(fileType.equals(Constants.FILE_TYPE_FILE)){
            title = getString(R.string.title_edit_file);
            fileCoverImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_file_small));
        } else if(fileType.equals(Constants.FILE_TYPE_MUSIC)){
            title = getString(R.string.title_edit_music);
            fileCoverImg.setImageDrawable(getResources().getDrawable(R.drawable.music_default));
        } else if(fileType.equals(Constants.FILE_TYPE_VEDIO)){
            title = getString(R.string.title_edit_vedio);
            fileCoverImg.setImageDrawable(getResources().getDrawable(R.drawable.video_default_icon));
        }
        initActionBar(R.drawable.icon_back, R.drawable.icon_done_top, title, false);
        return true;
    }

    /**
     * 验证输入
     */
    private boolean checkInput(){
        String nameStr = fileNameEdit.getText().toString().trim();
        if(ValidationUtils.validationString(nameStr)){
            if(nameStr.length() > 30){
                Utils.makeEventToast(this, getString(R.string.file_edit_name_hint), false);
                return false;
            }
        } else {
            Utils.makeEventToast(this,getString(R.string.file_edit_name_null_hint), false);
            return false;
        }
        String descStr = fileDescEdit.getText().toString().trim();
        if(ValidationUtils.validationString(descStr)){
            if(descStr.length() > 200){
                Utils.makeEventToast(this, getString(R.string.file_edit_desc_hint), false);
                return false;
            }
        }
        return true;
    }

}
