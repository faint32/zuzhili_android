package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.OnNetListener;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.controller.FileSelectedAdapter;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.folder.FileFolder;
import com.zuzhili.model.multipart.FileUpload;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.FileListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishFileActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, OnNetListener {

    private static final String FOLDER_ID = "the.last.publish.folder.id";
    private static final String FOLDER_NAME = "the.last.publish.folder.name";

    @ViewInject(R.id.lin_select_folder)
    private LinearLayout selectFolderLin;

    @ViewInject(R.id.txt_folder_name)
    private TextView folderNameTxt;

    @ViewInject(R.id.gd_music_selected)
    private GridView fileSelectedGrid;

    @ViewInject(R.id.cbx_available)
    private CheckBox available;

    @ViewInject(R.id.layout_available_container)
    private ViewGroup layoutAvailableContainer;


    private FileSelectedAdapter adapter;
    private List<FileUpload> fileList = new ArrayList<FileUpload>();
    private FileFolder curFolder;

    private static final int FLAG_ACTIVITY_SELECT_FOLDER = 1;
    private static final int FLAG_ACTIVITY_SELECT_FILE = 2;
    private static final int FLAG_ACTIVITY_EDIT_FILE = 3;
    private String spaceid;

    private String lastFolderName;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_publish_music);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        initData();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lastFolderName = preferences.getString(FOLDER_NAME, null);
        if (!TextUtils.isEmpty(lastFolderName)) {
            folderNameTxt.setText(lastFolderName);

        }

        String lastFolderId = preferences.getString(FOLDER_ID, null);
        if (!TextUtils.isEmpty(lastFolderId)) {
            if (curFolder == null) {
                curFolder = new FileFolder();
                curFolder.setId(lastFolderId);
                curFolder.setFoldername(lastFolderName);
            }
        }
    }

    private void initData() {
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);

        //个人发布不显示仅成员可见权限
        if (spaceid == null || spaceid.trim().equals("")) {
            layoutAvailableContainer.setVisibility(View.GONE);
        }

        adapter = new FileSelectedAdapter(fileList, this);
        fileSelectedGrid.setAdapter(adapter);
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_publish_file), false);
        return true;
    }

    @Override
    public boolean performClickOnRight() {
        if (checkInput()) {
            Task.upload(this, getParams());
            Utils.makeEventToast(this, getString(R.string.sending_hint), true);
            setResult(RESULT_OK);
            finish();
        }
        return super.performClickOnRight();
    }

    @Override
    public boolean performClickOnLeft() {
        if (adapter.getCount() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PublishFileActivity.this);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            performClickOnLeft();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 验证输入
     */
    private boolean checkInput() {
        if (curFolder == null) {
            Utils.makeEventToast(this, getString(R.string.select_folder_hint), false);
            return false;
        }

        if (fileList.size() == 0) {
            Utils.makeEventToast(this, getString(R.string.select_music_hint), false);
            return false;
        }
        return true;
    }

    /**
     * 获取发布参数
     */
    private RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.setListener(this);
        params.setTask(Task.API_URLS.get(Task.ACTION_PUBLISH_FILE));
        params.addBodyParameter("ids", mSession.getIds());
        params.addBodyParameter("listid", mSession.getListid());
        params.addBodyParameter("from", Constants.APP_FROM_ANDROID);
        if (ValidationUtils.validationString(spaceid))
            params.addBodyParameter("spaceid", spaceid);
        params.addBodyParameter("folderid", curFolder.getId());
        params.addBodyParameter("desc", JSON.toJSONString(fileList));
        for (FileUpload item : fileList) {
            params.addBodyParameter(item.getFileidentity(), new File(item.getFilepath()));
        }
        params.addBodyParameter("content", fileList.get(0).getNewfilename());

        params.addBodyParameter("authority", available.isChecked() ? "1" : "0");

        return params;
    }

    @OnClick(R.id.lin_select_folder)
    public void selectFolder(View view) {
        Intent intent = new Intent(this, FileFolderListActivity.class);
        intent.putExtra(Constants.MULTI_FOLDER_TYPE, Constants.FOLDER_TYPE_FILE);
        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_FOLDER);
    }

    @OnItemClick(R.id.gd_music_selected)
    public void selectedFileItem(AdapterView<?> adapter, View view, int position, long id) {
        if (position == fileList.size()) {
            addFile();
        } else {
            FileUpload item = fileList.get(position);
            Intent intent = new Intent(this, FileEditActivity.class);
            intent.putExtra(Constants.FILE_NAME, item.getFilename());
            intent.putExtra(Constants.FILE_DESC, item.getDesc());
            intent.putExtra(Constants.FILE_TYPE, Constants.FILE_TYPE_MUSIC);
            intent.putExtra(Constants.MUSIC_EDIT_POSITION, position);
            startActivityForResult(intent, FLAG_ACTIVITY_EDIT_FILE);
        }
    }

    private void addFile() {
        Intent intent = new Intent(this, FileListActivity.class);
        intent.putExtra(Constants.SELECT_TYPE, Constants.SELECT_TYPE_FILE);
        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FLAG_ACTIVITY_SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = null;
                if (data != null && (bundle = data.getExtras()) != null) {
                    String path = bundle.getString("file");
                    File file = new File(path);
                    long size = file.length();
                    if (((size / 1024) / 1024) > 10) {
                        Utils.makeEventToast(this, getString(R.string.file_too_large_hint), false);
                        return;
                    }
                    String name = bundle.getString("name");
                    FileUpload item = new FileUpload();
                    item.setFilename(name);
                    item.setNewfilename(name);
                    item.setFilepath(path);
                    item.setFileidentity(TextUtil.getUniqueFileName(name));
                    fileList.add(item);
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == FLAG_ACTIVITY_SELECT_FOLDER) {
            if (resultCode == Activity.RESULT_OK) {
                FileFolder folder = (FileFolder) data.getSerializableExtra(Constants.MULTI_FOLDER_ITEM);
                if (folder != null) {
                    curFolder = folder;
                    String folderName = curFolder.getFoldername();
                    folderNameTxt.setText(folderName);
                    if (folderName != null && !folderName.equals("")) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(FOLDER_NAME, folderName);
                        editor.putString(FOLDER_ID, folder.getId());
                        editor.commit();
                    }
                }
            } else if (requestCode == FLAG_ACTIVITY_EDIT_FILE) {
                if (resultCode == Activity.RESULT_OK) {
                    boolean flag = data.getBooleanExtra(Constants.FILE_IS_DELETED, false);
                    int position = data.getIntExtra(Constants.MUSIC_EDIT_POSITION, 0);
                    if (flag) {
                        fileList.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        String name = data.getStringExtra(Constants.FILE_NAME);
                        String desc = data.getStringExtra(Constants.FILE_DESC);
                        if (ValidationUtils.validationString(name))
                            fileList.get(position).setNewfilename(name);
                        if (ValidationUtils.validationString(desc))
                            fileList.get(position).setDesc(desc);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void OnNetSuccess(RequestParams params) {
        Utils.makeEventToast(this, getString(R.string.publish_success_hint), false);
        mSession.setUIShouldUpdate(Constants.PAGE_TREND);
    }

    @Override
    public void OnNetFailure(RequestParams params) {

    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {

    }
}
