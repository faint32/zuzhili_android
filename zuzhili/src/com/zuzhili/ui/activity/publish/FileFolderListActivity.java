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
import com.zuzhili.controller.FileFolderAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.folder.FileFolder;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * Created by addison on 2/25/14.
 */
public class FileFolderListActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.list_album)
    private PullRefreshListView folderListLv;

    private String spaceid;
//    private List<FileFolder> folderList = new ArrayList<FileFolder>();
    private FileFolderAdapter adatper;
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
        spaceid = getIntent().getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
        adatper = new FileFolderAdapter(this, folderListLv, ImageCacheManager.getInstance().getImageLoader(), FileFolder.class, getParams());
        folderListLv.setAdapter(adatper);
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_write, getString(R.string.select_folder_hint), false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent(this, CreateFolderActivity.class);
        intent.putExtra(Constants.MULTI_FOLDER_TYPE, Constants.FOLDER_TYPE_FILE);
        startActivityForResult(intent, FLAG_ACTIVIY_CREATE_FOLDER);
        return super.performClickOnRight();
    }

    @OnItemClick(R.id.list_album)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        FileFolder folder = (FileFolder) parent.getItemAtPosition(position);
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
                adatper.clearList();
                adatper = new FileFolderAdapter(this, folderListLv, ImageCacheManager.getInstance().getImageLoader(), FileFolder.class, getParams());
                folderListLv.setAdapter(adatper);
            }
        }
    }
}
