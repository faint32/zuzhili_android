package com.zuzhili.ui.activity.multiselect;

import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ImageGridAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.folder.Album;
import com.zuzhili.model.folder.Photo;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.ImageBrowserActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * @Title: ImageGridActivity.java
 * @Package: com.zuzhili.multiselector.ui
 * @Description: 图片选择
 * @author: gengxin
 * @date: 2014-2-13
 */
public class ImageGridActivity extends BaseActivity implements Response.Listener<String>, Response.ErrorListener {
	
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	@ViewInject(R.id.image_list_gv)
	private GridView imageListGrid;
	
	@ViewInject(R.id.tv_description)
	private TextView descriptio;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

	private ImageGridAdapter imageAdapter;
    private String albumid;
    private ArrayList<Photo> photoList;
    private Album album;
    private String albumName;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_list);
		ViewUtils.inject(this);
		initData();
	}

	private void initData() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        albumid = intent.getStringExtra(Constants.ALBUM_SELECTED_ID);
        albumName = intent.getStringExtra(Constants.ALBUM_SELECTED_NAME);
        Task.getAlbumPhoto(getParams(),this,this);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        initActionBar(R.drawable.icon_back, 0, albumName, false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    /**
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.image_list_gv)
	public void imageGridItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent(this,ImageBrowserActivity.class);
        if(photoList != null && photoList.size()>1){
            intent.putExtra(ImageBrowserActivity.IMAGE_TYPE, ImageBrowserActivity.TYPE_ALBUM);
            intent.putExtra("position", position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos",photoList);
            intent.putExtras(bundle);
        }else {
            intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,ImageBrowserActivity.TYPE_PHOTO);
            intent.putExtra("photo", photoList.get(0));
        }

        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, 0);
	}


    /**
     * 获取请求列表参数
     * @return
     */
    private HashMap<String, String> getParams(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("length", "15");
        params.put("start", "0");
        params.put("albumid", albumid);
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(String s) {
        progressBar.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(jsonObject.getString("photos") != null ) {
            photoList = (ArrayList<Photo>) JSON.parseArray(jsonObject.getString("photos"), Photo.class);
            imageListGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
            imageAdapter = new ImageGridAdapter(ImageGridActivity.this, photoList, ImageCacheManager.getInstance().getImageLoader());
            imageListGrid.setAdapter(imageAdapter);
        }
        if(jsonObject.getString("album") != null ){
            album=JSON.parseObject(jsonObject.getString("album"),Album.class);
            if(!TextUtils.isEmpty(album.getDescription())){
                descriptio.setVisibility(View.VISIBLE);
                descriptio.setText(album.getDescription());
            }

        }
    }
}
