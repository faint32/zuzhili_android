package com.zuzhili.ui.activity.im;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.GroupAdapter;
import com.zuzhili.model.ImageBean;
import com.zuzhili.ui.activity.BaseActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by liutao on 14-6-20.
 */
public class GalleryActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;
    private GroupAdapter adapter;
    private GridView mGroupGridView;
    private List<String> imageChoosedList = new ArrayList<String>();
    private ArrayList<String> images = new ArrayList<String>();

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();

                    adapter = new GroupAdapter(GalleryActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_groups);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        mGroupGridView = (GridView) findViewById(R.id.gridView);

        getImages();

        mGroupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());

                Intent mIntent = new Intent(GalleryActivity.this, ShowImageActivity.class);
//                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
                mIntent.putStringArrayListExtra("data", images);
                mIntent.putStringArrayListExtra(Constants.IMAGE_CHOOSED_LIST, (ArrayList<String>)imageChoosedList);
                startActivityForResult(mIntent, 0);

            }
        });
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, "完成", "图片",false);
        setCompoundTextVisible("完成 ("+imageChoosedList.size()+"/" + 5 + ")");
        return true;
    }

    //点击右侧按钮事件
    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                setResult(Activity.RESULT_OK, data);
                finish();
            } else if(resultCode == 10){
                if(null != data) {
                    List<String> imageList = (List<String>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                    imageChoosedList.clear();
                    imageChoosedList.addAll(imageList);
                    setCompoundTextVisible("完成 ("+imageChoosedList.size()+"/" + 5 + ")");
                }
            }
        }
    }

    private void getImages() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, "");

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = GalleryActivity.this.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    images.add(0,path);
                    String parentName = new File(path).getParentFile().getName();


                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }

                mCursor.close();

                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }

    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
        if(mGruopMap.size() == 0){
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));

            list.add(mImageBean);
        }

        return list;

    }


//    public ArrayList<Gallery> getGalleryPhotos() {
//        ArrayList<Gallery> galleryList = new ArrayList<Gallery>();
//
//        try {
//            final String[] columns = { MediaStore.Images.Media.DATA,
//                    MediaStore.Images.Media._ID };
//            final String orderBy = MediaStore.Images.Media._ID;
//
//            Cursor imagecursor = managedQuery(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
//                    null, null, orderBy);
//
//            if (imagecursor != null && imagecursor.getCount() > 0) {
//
//                while (imagecursor.moveToNext()) {
//                    Gallery item = new Gallery();
//
//                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
//
//                    item.setSdcardPath(imagecursor.getString(dataColumnIndex));
//
//                    galleryList.add(item);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // show newest photo at beginning of the list
//        Collections.reverse(galleryList);
//        return galleryList;
//    }

}
