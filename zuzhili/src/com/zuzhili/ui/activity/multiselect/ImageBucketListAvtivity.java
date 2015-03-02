package com.zuzhili.ui.activity.multiselect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ImageBucketAdapter;
import com.zuzhili.controller.ImageGalleryAdapter;
import com.zuzhili.framework.images.AlbumHelper;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.multipart.ImageBucket;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.ui.activity.BaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 
 * @Title: ImageBucketListAvtivity.java
 * @Package: com.zuzhili.ui.activity.multiselect
 * @Description: 相册列表
 * @author: gengxin
 * @date: 2014-2-13
 */
public class ImageBucketListAvtivity extends BaseActivity{
	
	@ViewInject(R.id.done_tv)		//完成
	private TextView doneTxt;
	
	@ViewInject(R.id.bucket_list_gv)		//图片册grid
	private GridView bucketListGrid;
	
	@ViewInject(R.id.gallery)
	private GridView galleryGrid;		//已经选择的图片
	
	private List<ImageBucket> bucketList;
	private ImageBucketAdapter bucketAdapter;// 自定义的适配器
	private AlbumHelper helper;
	public static Bitmap bimap;
    private List<ImageItem> imageChoosedList = new ArrayList<ImageItem>();
    private int addedCount;
    private ImageGalleryAdapter galleryAdapter;
    private static final int FLAG_ACTIVITY_SELECT_IMAGE = 1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bucket_list);
		ViewUtils.inject(this);
		initData();
		initView();
	}

	private void initData() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		bucketList = helper.getImagesBucketList(false);	
		bimap=BitmapFactory.decodeResource(getResources(),R.drawable.choosepicture_album);
        Intent intent = getIntent();
        addedCount = intent.getIntExtra(Constants.IMAGE_CHOOSED_COUNT, 0);
	}
	
	private void initView(){
		bucketAdapter = new ImageBucketAdapter(this, bucketList);
		bucketListGrid.setAdapter(bucketAdapter);
        galleryAdapter = new ImageGalleryAdapter(this, imageChoosedList);
        galleryGrid.setAdapter(galleryAdapter);
        doneTxt.setText("完成 ("+imageChoosedList.size()+"/" + (5 - addedCount) + ")");
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        initActionBar(R.drawable.icon_back, 0, getString(R.string.phone_albums), false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    /**
	 * 完成
	 * @param view
	 */
	@OnClick(R.id.done_tv)
    public void doneClick(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
	}
	
	/**
	 * 相册item点击
	 */
	@OnItemClick(R.id.bucket_list_gv)
	public void onBucketItemClick(AdapterView<?> parent, View view, int position, long id){
        ImageBucket bucket = bucketList.get(position);
        Intent intent = new Intent();
        intent.setClass(this,ImageGridActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_IMAGE_LIST, (Serializable) bucketList.get(position).getImageList());
        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
        intent.putExtras(bundle);
        intent.putExtra(Constants.IMAGE_CHOOSED_COUNT, addedCount);
        intent.putExtra(Constants.IMAGE_BUCKET_NAME,bucket.getBucketName());
        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_IMAGE);
	}

    /**
     * 删除已选图片
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.gallery)
    public void onGalleryItemClick(AdapterView<?> parent, View view, int position, long id){
        ImageItem image = imageChoosedList.get(position);
        imageChoosedList.remove(image);
        doneTxt.setText("完成 (" + imageChoosedList.size() + "/" + (5 - addedCount) + ")");
        Utils.makeEventToast(this, "当前选中"+imageChoosedList.size()+"张"+"(最多"+ Constants.IMAGE_COUNT_MAX +"张)", false);
        galleryAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVITY_SELECT_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                setResult(Activity.RESULT_OK, data);
                finish();
            } else if(resultCode == 10){
                if(null != data) {
                    List<ImageItem> imageList = (List<ImageItem>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                    imageChoosedList.clear();
                    imageChoosedList.addAll(imageList);
                    doneTxt.setText("完成 ("+imageChoosedList.size()+"/"+ (5 - addedCount ) +")");
                    galleryAdapter.notifyDataSetChanged();
                    galleryGrid.setAdapter(galleryAdapter);
                }
            }
        }
    }
}
