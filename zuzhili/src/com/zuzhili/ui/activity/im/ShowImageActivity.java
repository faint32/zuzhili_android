package com.zuzhili.ui.activity.im;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ChildAdapter;
import com.zuzhili.controller.GalleryAdapter;
import com.zuzhili.model.ImageBean;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.publish.BigImageActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowImageActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {
    private FrameLayout showImage;
	private GridView mGridView;
	private ChildAdapter adapter;
    private Button btn_sure;
    private Button btn_gallery;
    private List<ImageItem> imageChoosedList;

    private ArrayList<String> images = new ArrayList<String>();
    private ProgressBar progressBar;
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private PopupWindow pop;
    private ListView galleryList;
    private GalleryAdapter galleryAdapter;

    private static final int FLAG_ACTIVITY_CHOOSE_IMAGE = 1;

    private final static int SCAN_OK = 1;
    private boolean add;
    private Drawable up;
    private Drawable down;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    progressBar.setVisibility(View.GONE);
                    adapter.setList(images);
                    initPop();
                    break;
            }
        }

    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
        //imageChoosedList = new ArrayList<ImageItem>();
        setCustomActionBarCallback(this);
        showImage=(FrameLayout)findViewById(R.id.fl_show_image);
        //showImage.getForeground().setAlpha(0); // 背景无遮盖

        mGridView = (GridView) findViewById(R.id.child_grid);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        getImages();

        final boolean isChat = getIntent().getBooleanExtra(Constants.TAG_CHAT_CONTACTS,false);

        adapter = new ChildAdapter(this , mGridView ,isChat);
        mGridView.setAdapter(adapter);

        up=getResources().getDrawable(R.drawable.album_choose_top);
        up.setBounds(0, 0, up.getMinimumWidth(), up.getMinimumHeight());

        down=getResources().getDrawable(R.drawable.album_choose_down);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_gallery.setBackgroundResource(R.drawable.all_image_press);
                btn_gallery.setTextColor(Color.WHITE);

                btn_gallery.setCompoundDrawables(null, null, down, null);
//                if(pop!=null && pop.isShowing()) {
//                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
//                    pop.dismiss();
//
                pop.showAsDropDown(v);
                //showImage.getForeground().setAlpha(170); // 背景有遮盖
            }
        });

        btn_sure.setEnabled(false);
        adapter.setOnContactSelectedListener(new ChildAdapter.OnContactSelectedListener() {
            @Override
            public void onContactSelected(String path) {
                if(imageChoosedList.size()==9){
                    Toast.makeText(getBaseContext(),getString(R.string.image_out),Toast.LENGTH_SHORT).show();
                    return;
                }
                add=true;
                for(int i=0;i<imageChoosedList.size();i++){
                    if(imageChoosedList.get(i).getImagePath().equals(path)){
                        imageChoosedList.remove(imageChoosedList.get(i));
                        add=false;
                    }
                }
                if(add){
                    ImageItem image=new ImageItem();
                    image.setImagePath(path);
                    imageChoosedList.add(image);
                }

                resetUI();
            }
        });

        //浏览
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowImageActivity.this,BigImageActivity.class);
                Bundle bundle = new Bundle();
                if(imageChoosedList.size()>1){
                    intent.putExtra(BigImageActivity.IMAGE_TYPE, BigImageActivity.TYPE_ALBUM);
                    intent.putExtra(Constants.IMAGE_POSITION, 0);
                    bundle.putSerializable(Constants.BIGIMAGE_PHOTOS, (Serializable) imageChoosedList);
                    bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
                    intent.putExtras(bundle);
                }else if(imageChoosedList.size() == 1){
                    intent.putExtra(BigImageActivity.IMAGE_TYPE,BigImageActivity.TYPE_PHOTO);
                    intent.putExtra(Constants.BIGIMAGE_PHOTO, imageChoosedList.get(0));
                    bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
                    intent.putExtras(bundle);
                }
                if(isChat){
                    intent.putExtra(Constants.TAG_CHAT_CONTACTS,true);
                }
                startActivityForResult(intent, FLAG_ACTIVITY_CHOOSE_IMAGE);
                overridePendingTransition(R.anim.zoom_enter, 0);
            }
        });

        imageChoosedList = (List<ImageItem>) getIntent().getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
        if(imageChoosedList !=null && imageChoosedList.size()>0){
            adapter.setSelectItems(imageChoosedList);
            adapter.notifyDataSetChanged();

            btn_sure.setEnabled(true);
            btn_sure.setTextColor(Color.WHITE);
            btn_sure.setText("预览(" + imageChoosedList.size() + ")");
        }else {
            imageChoosedList = new ArrayList<ImageItem>();
        }

	}

    private void resetUI(){
        if (imageChoosedList.size() > 0) {
            btn_sure.setEnabled(true);
            btn_sure.setTextColor(Color.WHITE);
        } else {
            btn_sure.setEnabled(false);
            btn_sure.setTextColor(getResources().getColor(R.color.normal_but_text));
        }

        btn_sure.setText("预览(" + imageChoosedList.size() + ")");
        if(imageChoosedList.size() == 0){
            initActionBar(R.drawable.icon_back, getString(R.string.send), "图片");
        }else {
            initActionBar(R.drawable.icon_back, getString(R.string.send)+"("+imageChoosedList.size()+"/9)", "图片");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVITY_CHOOSE_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                imageChoosedList = (List<ImageItem>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                boolean finish=data.getBooleanExtra(Constants.FINISH_ACTIVITY,false);
                if(finish){
                    performClickOnRight();
                    return;
                }
                resetUI();
                adapter.setSelectItems(imageChoosedList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    ImageBean lastImageBean;

    /**
     * 创建popwindow
     */
    private void initPop(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.listview_social_view, null);
        galleryList = (ListView) view.findViewById(R.id.list_social);
        galleryAdapter = new GalleryAdapter(this,galleryList,subGroupOfImage(mGruopMap));
        galleryList.setAdapter(galleryAdapter);
        lastImageBean = (ImageBean)galleryAdapter.getItem(0);
        galleryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageBean imageBean;
                lastImageBean.setSelect(false);
                if(position==0){
                    galleryAdapter.notFirst=false;
                    imageBean = (ImageBean) parent.getAdapter().getItem(position);
                    imageBean.setSelect(true);
                    adapter.setList(images);
                    btn_gallery.setText(getString(R.string.choose_gallery));
                }else {
                    imageBean= (ImageBean) parent.getAdapter().getItem(position-1);
                    imageBean.setSelect(true);
                    galleryAdapter.notFirst=true;
                    btn_gallery.setText(imageBean.getFolderName());
                    adapter.setSelectItems(imageChoosedList);
                    adapter.setList(mGruopMap.get(imageBean.getFolderName()));
                }
                lastImageBean=imageBean;
                if(pop!=null && pop.isShowing()) {
                    pop.dismiss();
                }
            }
        });

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,windowManager.getDefaultDisplay().getHeight()/5*3, false);
         // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);

        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //showImage.getForeground().setAlpha(0); // 背景无遮盖
                btn_gallery.setBackgroundResource(R.drawable.all_image_nopress);
                btn_gallery.setTextColor(getResources().getColor(R.color.normal_but_text));

                btn_gallery.setCompoundDrawables(null, null, up, null);
            }
        });
    }

    @Override
    public boolean showCustomActionBar() {
        if(imageChoosedList.size() == 0){
            initActionBar(R.drawable.icon_back, getString(R.string.send), "图片");
        }else {
            initActionBar(R.drawable.icon_back, getString(R.string.send)+"("+imageChoosedList.size()+"/9)", "图片");
        }
        return true;
    }

    //点击左侧按钮事件
    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    //确定点击右侧按钮事件
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

    private void getImages() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ShowImageActivity.this.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    images.add(0,path);
                    String parentName = new File(path).getParentFile().getName();

                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(0,path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(0,path);
                    }
                }
                mCursor.close();
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    /**
     * 相册集合
     * @param mGruopMap
     * @return
     */
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

            list.add(0,mImageBean);
        }

        return list;

    }

}
