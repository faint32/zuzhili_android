package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.zuzhili.controller.ImageUploadAdapter;
import com.zuzhili.draftbox.DraftBoxActivity;
import com.zuzhili.draftbox.DraftContract;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.folder.Album;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.model.multipart.ImageUpload;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.ShowImageActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PublishImageActivity extends BaseActivity implements OnNetListener {

    public static final String KEY_LAST_ALBUM_NAME = "publish.image.last.album.name";
    public static final String KEY_LAST_ALBUM_ID = "publish.image.last.album.id";

    @ViewInject(R.id.lin_select_ablum)
    private LinearLayout selectAblumLin;        //选择图片册layout

    @ViewInject(R.id.txt_album_name)
    private TextView ablumNameTxt;            //图片册名称

    @ViewInject(R.id.gd_pic_selected)
    private GridView picSelectedGrid;            //已经选择的图片

    @ViewInject(R.id.cbx_available)
    private CheckBox onlyVisibleForMembersBox;

    @ViewInject(R.id.layout_available_container)
    private ViewGroup visibleLayout;


    private String spaceid;

    /**
     * --------requestCode--------*
     */
    private static final int FLAG_ACTIVITY_ALBUM = 4;        //选择图片册
    private static final int FLAG_ACTIVITY_SELECT_IMAGE = 1;        //选择图片
    private static final int FLAG_ACTIVITY_CAMERA = 2;      //系统相机
    private static final int FLAG_ACTIVITY_EDIT_IMAGE = 3;      //编辑图片

    private ImageUploadAdapter adapter;
    private List<ImageUpload> imageList = new ArrayList<ImageUpload>();

    private Album curAlbum;

    private List<ImageItem> items;
    private String draftId;
    private String draftContent;
    private boolean onlyVisibleForMembers;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_publish_pic);
        ViewUtils.inject(this);
        initView();
        initDatas();

        // restore the last selected album.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lastAlbumId = preferences.getString(KEY_LAST_ALBUM_ID, null);
        String lastAlbumName = preferences.getString(KEY_LAST_ALBUM_NAME, null);
        if (!TextUtils.isEmpty(lastAlbumId) && !TextUtils.isEmpty(lastAlbumName)) {
            curAlbum = new Album();
            curAlbum.setId(lastAlbumId);
            curAlbum.setName(lastAlbumName);
            ablumNameTxt.setText(lastAlbumName);
        }


    }

    private void initView() {
        adapter = new ImageUploadAdapter(this, imageList);
        picSelectedGrid.setAdapter(adapter);
    }

    private void initDatas() {
        spaceid = getIntent().getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);

        draftId = getIntent().getStringExtra("draft_id");
        if (draftId != null) {
            draftContent = getIntent().getStringExtra(DraftBoxActivity.DRAFTBOX_CONTENT);
            JSONObject jsonObject = JSON.parseObject(draftContent);

            // inflate album
            JSONObject entry = (JSONObject) jsonObject.get("albumid");
            if (entry != null) {
                String albumId = (String) entry.get("value");
                if (!TextUtils.isEmpty(albumId)) {
                    entry = (JSONObject) jsonObject.get("album_name");
                    String albumName = (String) entry.get("value");
                    curAlbum = new Album();
                    curAlbum.setId(albumId);
                    curAlbum.setName(albumName);
                    ablumNameTxt.setText(albumName);
                }
            }

            // inflate images
            entry = (JSONObject) jsonObject.get("desc");
            String desc = (String) entry.get("value");
            JSONArray array = JSON.parseArray(desc);
            Iterator it = array.iterator();
            ImageUpload imageUpload = null;
            while (it.hasNext()) {
                JSONObject item = (JSONObject) it.next();
                imageUpload = new ImageUpload();
                imageUpload.setFilepath((String) item.get("filepath"));
                imageUpload.setDesc((String) item.get("desc"));
                imageUpload.setFileidentity((String) item.get("fileidentity"));
                imageUpload.setNewfilename((String) item.get("newfilename"));
                imageList.add(imageUpload);
            }
            adapter.notifyDataSetChanged();


            // visible for member
            entry = (JSONObject) jsonObject.get("authority");
            if (entry != null) {
                String authority = (String) entry.get("value");
                this.onlyVisibleForMembers = authority.equals("1") ? true : false;
                onlyVisibleForMembersBox.setChecked(onlyVisibleForMembers);
                visibleLayout.setVisibility(View.VISIBLE);
            }

            // restore space id from draft
            entry = (JSONObject) jsonObject.get("spaceid");
            if (entry != null && spaceid == null) {
                spaceid = (String) entry.get("value");
            }
        }

        //个人发布不显示仅成员可见权限
        if (spaceid == null || spaceid.trim().equals("")) {
            visibleLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 返回
     */
    @Override
    public boolean performClickOnLeft() {
        if (adapter.getCount() > 1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(PublishImageActivity.this);

            Map map = getParams().getBodyParams();
            final String jsonStr = new JSONObject(map).toJSONString();
            if (draftId != null) {
                if (draftContent.equals(jsonStr)) {
                    finish();
                    return true;
                } else {
                    builder.setMessage("改草稿已存在，是否覆盖？");
                }
            } else {
                builder.setMessage(R.string.if_save_to_draftbox).setTitle(getString(R.string.publish_image));
            }

            builder.setTitle("保存草稿");

            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    //save content to draft box.
                    ContentValues values = new ContentValues();
                    values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
                    values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_PICTURE);
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonStr);

                    if (draftId != null) {
                        getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
                    } else {
                        getContentResolver().insert(DraftContract.Draft.CONTENT_URI, values);
                    }

                    Toast.makeText(PublishImageActivity.this, "已保存至草稿箱", Toast.LENGTH_LONG).show();

                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
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
     * 发布
     */
    @Override
    public boolean performClickOnRight() {
        if (checkInput()) {
            publishImage();
        }
        return super.performClickOnRight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.publish_image), false);
        return true;
    }


    /**
     * 选择图片或者编辑图片
     */
    @OnItemClick(R.id.gd_pic_selected)
    public void picItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == imageList.size()) {
            addImage();

        } else {
//            ImageUpload item = imageList.get(position);
//            Intent intent = new Intent(PublishImageActivity.this, ImageEditActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(Constants.IMAGE_ITEM, (Serializable) item);
//            intent.putExtra(Constants.ACTION, Constants.ACTION_EDIT);
//            intent.putExtra(Constants.IMAGE_EDIT_POSITION, position);
//            intent.putExtras(bundle);
            Intent intent = new Intent(this, BigImageActivity.class);
            Bundle bundle = new Bundle();
            if (items.size() > 1) {
                intent.putExtra(BigImageActivity.IMAGE_TYPE, BigImageActivity.TYPE_ALBUM);
                intent.putExtra(Constants.IMAGE_POSITION, position);
                bundle.putSerializable(Constants.BIGIMAGE_PHOTOS, (Serializable) items);
                bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) items);
                intent.putExtras(bundle);
            } else {
                intent.putExtra(BigImageActivity.IMAGE_TYPE, BigImageActivity.TYPE_PHOTO);
                intent.putExtra(Constants.BIGIMAGE_PHOTO, items.get(0));
                bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) items);
                intent.putExtras(bundle);
            }
            overridePendingTransition(R.anim.zoom_enter, 0);
            startActivityForResult(intent, FLAG_ACTIVITY_EDIT_IMAGE);
        }
    }

    /**
     * 添加图片（手机相册，相机拍照）
     */
    private void addImage() {
        String[] hints = {getString(R.string.phone_cemara), getString(R.string.phone_albums)};
        new AlertDialog.Builder(this).setTitle(getString(R.string.select_resource)).setItems(hints, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                switch (which) {
                    case 0:
                        //相机拍照
                        intent.setClass(PublishImageActivity.this, ImageEditActivity.class);
                        intent.putExtra(Constants.ACTION, Constants.ACTION_CEMARA);
                        startActivityForResult(intent, FLAG_ACTIVITY_CAMERA);
                        break;
                    case 1:
                        //手机相册选择 ShowImageActivity  ImageBucketListAvtivity
                        intent.setClass(PublishImageActivity.this, ShowImageActivity.class);
                        Bundle bundle = new Bundle();
                        List<ImageItem> imageChoosedList = new ArrayList<ImageItem>();
                        ImageItem imageItem;
                        for (ImageUpload item : imageList) {
                            imageItem = new ImageItem();
                            imageItem.setImagePath(item.getFilepath());
                            imageItem.setDesc(item.getDesc());
                            imageChoosedList.add(imageItem);
                        }
                        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, FLAG_ACTIVITY_SELECT_IMAGE);
                        break;
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FLAG_ACTIVITY_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                items = (List<ImageItem>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                convertObj(items);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == FLAG_ACTIVITY_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                ImageUpload item = (ImageUpload) data.getSerializableExtra(Constants.IMAGE_ITEM);
                if (item != null) {
                    imageList.add(imageList.size(), item);
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == FLAG_ACTIVITY_EDIT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
//                boolean deleteFlag = data.getBooleanExtra(Constants.IMAGE_DELETE, false);
//                int position = data.getIntExtra(Constants.IMAGE_EDIT_POSITION, 0);
//                if(deleteFlag){
//                    //删除对应的图片
//                    imageList.remove(position);
//                } else {
//                    //更新已经编辑的图片
//                    imageList.remove(position);
//                    ImageUpload item = (ImageUpload) data.getSerializableExtra(Constants.IMAGE_ITEM);
//                    imageList.add(position, item);
//                }
//                adapter.notifyDataSetChanged();
                items = (List<ImageItem>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                convertObj(items);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == FLAG_ACTIVITY_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                Album album = (Album) data.getSerializableExtra(Constants.ALBUM_SELECTED);
                if (album != null) {
                    curAlbum = album;
                    String albumName = album.getName();
                    if (ValidationUtils.validationString(albumName))
                        ablumNameTxt.setText(album.getName());

                    // save into preference.
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(KEY_LAST_ALBUM_NAME, albumName);
                    editor.putString(KEY_LAST_ALBUM_ID, curAlbum.getId());
                    editor.commit();
                }
            }
        }
    }


    /**
     * 对象转换
     *
     * @param items
     */
    private void convertObj(List<ImageItem> items) {
        imageList.clear();
        for (ImageItem item : items) {
            ImageUpload img = new ImageUpload();
            File file = new File(item.getImagePath());
            if (!TextUtils.isEmpty(item.getDesc())) {
                img.setDesc(item.getDesc());
            } else {
                img.setDesc(getString(R.string.no_description));
            }

            img.setFilepath(item.getImagePath());
            img.setFileidentity(TextUtil.getUniqueFileName(item.getImagePath()));
            img.setNewfilename(file.getName());
            imageList.add(img);
        }
    }

    private void publishImage() {
        Task.upload(this, getParams());
        Utils.makeEventToast(this, getString(R.string.sending_hint), true);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 验证输入
     */
    private boolean checkInput() {
        if (curAlbum == null) {
            Utils.makeEventToast(this, getString(R.string.select_album_hint), false);
            return false;
        }

        if (imageList.size() == 0) {
            Utils.makeEventToast(this, getString(R.string.select_image_hint), false);
            return false;
        }
        return true;
    }


    /**
     * 获取发布图片参数
     *
     * @return
     */
    private RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.setListener(this);
        params.setTask(Task.API_URLS.get(Task.ACTION_PUBLISH_PIC));
        params.addBodyParameter("ids", mSession.getIds());
        params.addBodyParameter("listid", mSession.getListid());
        params.addBodyParameter("from", Constants.APP_FROM_ANDROID);
        if (ValidationUtils.validationString(spaceid))
            params.addBodyParameter("spaceid", spaceid);

        if (curAlbum != null) {
            params.addBodyParameter("albumid", curAlbum.getId());
            params.addBodyParameter("album_name", curAlbum.getName());
        }

        params.addBodyParameter("desc", JSON.toJSONString(imageList));
        for (ImageUpload item : imageList) {
            params.addBodyParameter(item.getFileidentity(), new File(item.getFilepath()));
        }

        params.addBodyParameter("content", imageList.size() + "张图片");

        if (visibleLayout.getVisibility() == View.VISIBLE) {
            params.addBodyParameter("authority", onlyVisibleForMembersBox.isChecked() ? "1" : "0");
        }

        return params;
    }


    /**
     * 选择图片册
     */
    @OnClick(R.id.lin_select_ablum)
    public void selectAlbum(View view) {
        Intent intent = new Intent(this, AlbumListActivity.class);
        if (ValidationUtils.validationString(spaceid))
            intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, spaceid);
        startActivityForResult(intent, FLAG_ACTIVITY_ALBUM);
    }

    @Override
    public void OnNetSuccess(RequestParams params) {
        Utils.makeEventToast(this, getString(R.string.publish_success_hint), false);
        mSession.setUIShouldUpdate(Constants.PAGE_TREND);
        // delete draft if the publish action success.
        if (draftId != null) {
            Uri uri = ContentUris.withAppendedId(DraftContract.Draft.CONTENT_ID_URI_BASE, Long.parseLong(draftId));
            getContentResolver().delete(uri, null, null);
        }
    }

    @Override
    public void OnNetFailure(RequestParams params) {

        Map map = getParams().getBodyParams();
        final String jsonStr = new JSONObject(map).toJSONString();

        //save content to draft box.
        ContentValues values = new ContentValues();
        values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
        values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
        values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_PICTURE);
        values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonStr);

        if (draftId != null) {
            getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
        } else {
            getContentResolver().insert(DraftContract.Draft.CONTENT_URI, values);
        }
    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {

    }

}
