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
import com.zuzhili.controller.AlbumAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.folder.Album;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.ImageGridActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

/**
 * 图片册
 */
public class AlbumListActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {
    @ViewInject(R.id.list_album)
    private PullRefreshListView albumsListLV;

    private String spaceid;
    private String userid;
    private String title;
    private boolean fromSpace;
    private AlbumAdapter adapter;

    private static final int FLAG_ACTIVITY_CREATE_ALBUM = 1;

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
        userid = intent.getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_USERID);
        fromSpace = intent.getBooleanExtra(Constants.FROM_SPACE,false);
        title = intent.getStringExtra(Constants.SPACE_ALBUM_NAME);
        adapter = new AlbumAdapter(this, albumsListLV, ImageCacheManager.getInstance().getImageLoader(), getParams());
        adapter.setOnRefreshListener();
        albumsListLV.setAdapter(adapter);
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent(this, CreateAlbumActivity.class);
        intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, spaceid);
        startActivityForResult(intent, FLAG_ACTIVITY_CREATE_ALBUM);
        return super.performClickOnRight();
    }


    @Override
    public boolean showCustomActionBar() {
        if(fromSpace){
            initActionBar(R.drawable.icon_back, 0,title+getString(R.string.album) , false);
        }else {
            initActionBar(R.drawable.icon_back, R.drawable.icon_write, getString(R.string.title_select_album), false);
        }

        return true;
    }

    @OnItemClick(R.id.list_album)
    public void onAlbumItemClick(AdapterView<?> parent, View view, int position, long id){
        Album item = (Album) parent.getItemAtPosition(position);
        if(fromSpace){
            Intent intent=new Intent(this, ImageGridActivity.class);
            intent.putExtra(Constants.ALBUM_SELECTED_ID,item.getId());
            intent.putExtra(Constants.ALBUM_SELECTED_NAME,item.getName());
            startActivity(intent);
        }else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ALBUM_SELECTED, item);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }


    }
    /**
     * 获取请求列表参数
     * @return
     */
    private HashMap<String, String> getParams(){
        HashMap<String, String> params = new HashMap<String, String>();
        if(fromSpace){
            params.put("ids", userid);
        }else {
            params.put("ids", mSession.getIds());
        }
        params.put("listid", mSession.getListid());
        if(ValidationUtils.validationString(spaceid)){
            params.put("spaceid", spaceid);
            params.put("ids", mSession.getIds());
        }

        return params;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FLAG_ACTIVITY_CREATE_ALBUM) {
                adapter.onRefresh();
            }
        }
    }
}
