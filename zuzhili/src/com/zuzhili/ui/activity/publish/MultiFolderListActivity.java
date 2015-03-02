package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.controller.MutilFolderAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.folder.MutilFolder;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by addison on 2/21/14.
 * 音视频文件夹列表
 */
public class MultiFolderListActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {
    @ViewInject(R.id.list_album)
    private PullRefreshListView multiFolderListLV;

    private String spaceid;
    private String folderType;
    private MutilFolderAdapter adapter;
    private static final int FLAG_ACTIVIY_CREATE_FOLDER = 1;
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_album_list);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        folderType = intent.getStringExtra(Constants.MULTI_FOLDER_TYPE);
        adapter = new MutilFolderAdapter(this, multiFolderListLV, ImageCacheManager.getInstance().getImageLoader(), MutilFolder.class, getParams(), folderType);
        multiFolderListLV.setAdapter(adapter);
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_write, "选择目录", false);
        return true;
    }

    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent(this, CreateFolderActivity.class);
        intent.putExtra(Constants.MULTI_FOLDER_TYPE, folderType);
        startActivityForResult(intent, FLAG_ACTIVIY_CREATE_FOLDER);
        return super.performClickOnRight();
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @OnItemClick(R.id.list_album)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        MutilFolder folder = (MutilFolder) parent.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra(Constants.MULTI_FOLDER_ITEM, folder);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 获取请求列表参数
     * @return
     */
    private HashMap<String, String> getParams(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ids", mSession.getIds());
        params.put("listid", mSession.getListid());
        if(ValidationUtils.validationString(spaceid))
            params.put("spaceid", spaceid);
        return params;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVIY_CREATE_FOLDER){
            if(resultCode == Activity.RESULT_OK){
                adapter.clearList();
                adapter = new MutilFolderAdapter(this, multiFolderListLV, ImageCacheManager.getInstance().getImageLoader(), MutilFolder.class, getParams(), folderType);
                multiFolderListLV.setAdapter(adapter);
            }
        }
    }
}
