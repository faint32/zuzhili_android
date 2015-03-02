package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import com.zuzhili.controller.MusicSelectedAdapter;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.folder.MutilFolder;
import com.zuzhili.model.multipart.MultiUpload;
import com.zuzhili.model.multipart.MusicLocal;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.MusicListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishMusicActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, OnNetListener {
    @ViewInject(R.id.lin_select_folder)
    private LinearLayout selectFolderLin;       //选择文件夹

    @ViewInject(R.id.txt_folder_name)
    private TextView folderNameTxt;         //文件夹名称

    @ViewInject(R.id.gd_music_selected)
    private GridView musicSelectedGrid;     //待上传music

    private String spaceid;
    private MutilFolder curFolder;              //当前使用文件夹
    private List<MultiUpload> musicList = new ArrayList<MultiUpload>();
    private MusicSelectedAdapter adapter;

    /**requestCode**/
    private static final int FLAG_ACTIVITY_SELECT_FOLDER = 1;       //选择文件夹
    private static final int FLAG_ACTIVITY_ADD_MUSIC = 2;           //添加音频
    private static final int FLAG_ACTIVITY_EDIT_MUSIC = 3;          //编辑音频

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_publish_music);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        initData();
        initView();
    }

    private void initView() {
        adapter = new MusicSelectedAdapter(musicList, this);
        musicSelectedGrid.setAdapter(adapter);
    }

    private void initData() {
        Intent intent = getIntent();
        spaceid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_publish_music), false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        if(checkInput()){
            Task.upload(this, getParams());
            Utils.makeEventToast(this, getString(R.string.sending_hint), true);
            setResult(RESULT_OK);
            finish();
        }
        return super.performClickOnRight();
    }

    /**
     * 验证输入
     */
    private boolean checkInput(){
        if(curFolder == null){
            Utils.makeEventToast(this, getString(R.string.select_folder_hint), false);
            return false;
        }
        if(musicList.size() == 0){
            Utils.makeEventToast(this, getString(R.string.select_music_hint), false);
            return false;
        }
        return true;
    }

    /**
     * 获取发布参数
     */
    private RequestParams getParams(){
        RequestParams params = new RequestParams();
        params.setListener(this);
        params.setTask(Task.API_URLS.get(Task.ACTION_PUBLISH_MUSIC));
        params.addBodyParameter("ids", mSession.getIds());
        params.addBodyParameter("listid", mSession.getListid());
        params.addBodyParameter("from", Constants.APP_FROM_ANDROID);
        if(ValidationUtils.validationString(spaceid))
            params.addBodyParameter("spaceid", spaceid);
        if(TextUtils.isEmpty(spaceid)){
            params.addBodyParameter("folderid", curFolder.getId());
        }
        params.addBodyParameter("desc", JSON.toJSONString(musicList));
        for(MultiUpload item : musicList){
            params.addBodyParameter(item.getFileidentity(), new File(item.getFilepath()));
        }
        params.addBodyParameter("content", musicList.get(0).getNewfilename());
        return params;
    }

    /**
     * 选择文件夹
     * @param view
     */
    @OnClick(R.id.lin_select_folder)
    public void selectFolder(View view){
        Intent intent = new Intent(this, MultiFolderListActivity.class);
        intent.putExtra(Constants.MULTI_FOLDER_TYPE, Constants.MULTI_FOLDER_TYPE_MUSIC);
        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_FOLDER);
    }

    @OnItemClick(R.id.gd_music_selected)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        if(position == musicList.size()){
            addMusic();
        } else {
            MultiUpload item = musicList.get(position);
            Intent intent = new Intent(this, FileEditActivity.class);
            intent.putExtra(Constants.FILE_NAME, item.getNewfilename());
            intent.putExtra(Constants.FILE_DESC, item.getDesc());
            intent.putExtra(Constants.FILE_TYPE, Constants.FILE_TYPE_MUSIC);
            intent.putExtra(Constants.MUSIC_EDIT_POSITION, position);
            startActivityForResult(intent, FLAG_ACTIVITY_EDIT_MUSIC);
        }
    }

    /**
     * 添加音频文件
     */
    private void addMusic() {
        Intent intent = new Intent(this, MusicListActivity.class);
        intent.putExtra(Constants.MUSIC_CHOOSED_COUNT, musicList.size());
        startActivityForResult(intent, FLAG_ACTIVITY_ADD_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVITY_SELECT_FOLDER){
            if(resultCode == Activity.RESULT_OK){
                MutilFolder folder = (MutilFolder) data.getSerializableExtra(Constants.MULTI_FOLDER_ITEM);
                if(folder != null){
                    curFolder = folder;
                    folderNameTxt.setText(curFolder.getName());
                }
            }
        } else if(requestCode == FLAG_ACTIVITY_ADD_MUSIC){
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = null;
                if (data != null && (bundle = data.getExtras()) != null) {
                    ArrayList<MusicLocal> list = data.getParcelableArrayListExtra(Constants.MUSIC_CHOOSED_LIST);
                    convertObj(list);
                }
            }
        } else if(requestCode == FLAG_ACTIVITY_EDIT_MUSIC){
            if(resultCode == Activity.RESULT_OK){
                boolean flag = data.getBooleanExtra(Constants.FILE_IS_DELETED, false);
                int position = data.getIntExtra(Constants.MUSIC_EDIT_POSITION, 0);
                if(flag){
                    musicList.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    String name = data.getStringExtra(Constants.FILE_NAME);
                    String desc = data.getStringExtra(Constants.FILE_DESC);
                    if(ValidationUtils.validationString(name))
                        musicList.get(position).setNewfilename(name);
                    if(ValidationUtils.validationString(desc))
                        musicList.get(position).setDesc(desc);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }


    /**
     * 对象转换
     * @param list
     */
    private void convertObj(List<MusicLocal> list){
        for(MusicLocal item : list){
            MultiUpload music = new MultiUpload();
            music.setFilepath(item.getPath());
            music.setNewfilename(item.getName());
            music.setFileidentity(TextUtil.getUniqueFileName(item.getPath()));
            musicList.add(musicList.size(), music);
        }
        adapter.notifyDataSetChanged();
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
