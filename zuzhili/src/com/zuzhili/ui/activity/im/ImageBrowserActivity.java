package com.zuzhili.ui.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ImageBrowserAdapter;
import com.zuzhili.framework.utils.ImageUtils;
import com.zuzhili.model.folder.Photo;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.PhotoCommentActivity;
import com.zuzhili.ui.views.ScrollViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kj on 2014/8/19.
 */
public class ImageBrowserActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener ,BaseActivity.TimeToShowActionBarCallback, View.OnTouchListener,Response.Listener<String>, Response.ErrorListener{

    private ScrollViewPager mSvpPager;
    private ProgressBar progressbar;
    private ImageView save;
    private ImageView comment;
    private TextView comment_num;
    private TextView description;
    private ImageBrowserAdapter mAdapter;
    private RelativeLayout buttom;
    private LinearLayout ll_text;
    private String mType;
    private int mPosition;
    private int mTotal;
    private ArrayList<Photo> photoList;
    private ArrayList<String> photos;
    private boolean hide=false;
    public static final String IMAGE_TYPE = "image_type";
    public static final String TYPE_ALBUM = "image_album";
    public static final String TYPE_PHOTO = "image_photo";
    private long lastTime;
    private boolean isChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        save=(ImageView)findViewById(R.id.image_save);
        save.setOnTouchListener(this);
        comment=(ImageView)findViewById(R.id.image_comment);
        comment.setOnTouchListener(this);

        buttom=(RelativeLayout)findViewById(R.id.rl_buttom);
        ll_text=(LinearLayout)findViewById(R.id.ll_text);
        comment_num = (TextView) findViewById(R.id.tv_comment_num);
        description = (TextView) findViewById(R.id.tv_description);
        mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
        mSvpPager.setOnPageChangeListener(this);
        mSvpPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setCustomActionBarCallback(this);
        setLeftImgBtnOnTouchListener(this);
        init();
    }

    private void init() {
        mType = getIntent().getStringExtra(IMAGE_TYPE);

        if (TYPE_ALBUM.equals(mType)) {
            mPosition = getIntent().getIntExtra("position", 0);
            //图片地址数组
            photoList=(ArrayList<Photo>)getIntent().getSerializableExtra("photos");
            photos =new ArrayList<String>();
            for(Photo item:photoList){
                photos.add(item.getUrl_source());
            }

            mTotal = photos.size();
            if (mPosition > mTotal) {
                mPosition = mTotal - 1;
            }
            if (mTotal > 1) {
                mPosition += 1000 * mTotal;
                mAdapter = new ImageBrowserAdapter(this,photos, mType,progressbar,new OnPhotoClickListener());
                mSvpPager.setAdapter(mAdapter);
                mSvpPager.setCurrentItem(mPosition, false);
            }
        } else if (TYPE_PHOTO.equals(mType)) {
            isChat = getIntent().getBooleanExtra(Constants.TAG_CHAT_CONTACTS,false);
            Photo photo=(Photo)getIntent().getSerializableExtra("photo");
            photoList=new ArrayList<Photo>();
            photoList.add(photo);
            mPosition = 0;
            photos = new ArrayList<String>();
            photos.add(photo.getUrl_source());
            mAdapter = new ImageBrowserAdapter(this, photos, mType,progressbar,new OnPhotoClickListener());
            mSvpPager.setAdapter(mAdapter);

            if(isChat){
                buttom.setVisibility(View.GONE);
                mActionBar.hide();
                mAdapter.setListener(null);
            }
//            else{
//                buttom.setVisibility(View.VISIBLE);
//                mActionBar.show();
//                mAdapter.setListener(new OnPhotoClickListener());
//            }
        }
    }


    @Override
    public boolean showCustomActionBar() {
        if(photoList.size()>1){
            initActionBar(R.drawable.back_white, R.color.black, String.valueOf((mPosition % photos.size()+1)+"/"+mTotal));
            String text=photoList.get(mPosition % photos.size()).getDescription();
            if(!TextUtils.isEmpty(text) && !getString(R.string.no_description).equals(text)){
                ll_text.setVisibility(View.VISIBLE);
                description.setText(text);
            }else {
                ll_text.setVisibility(View.GONE);
                description.setText("");
            }
        }else {
            initActionBar(R.drawable.back_white, R.color.black, "1/1");
            String text=photoList.get(0).getDescription();
            if(!TextUtils.isEmpty(text) && !getString(R.string.no_description).equals(text)){
                ll_text.setVisibility(View.VISIBLE);
                description.setText(text);
            }
        }
        return true;
    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getString("list") != null){

        }
    }

    /**
     * 为了拦截点击事件,避免事件向上分发
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.imgBtn_left:
                finish();
                break;

            case R.id.image_save:
                if(isClick()){
                    String url=photoList.get(mPosition % photos.size()).getUrl_source();
                    String fileName=url.substring(url.indexOf("com/")+4,url.length());
                    final File directory = new File(Environment.getExternalStorageDirectory()+"/zhiliren/images/zuzhili",fileName);
                    LogUtils.i("File PAHT :"+directory.getAbsolutePath());
                    if (!directory.exists()) {
                        progressbar.setVisibility(View.VISIBLE);
                        HttpUtils http = new HttpUtils();
                        http.download(url,
                                directory.getAbsolutePath(),
                                false, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                                new RequestCallBack<File>() {

                                    @Override
                                    public void onStart() {
                                    }

                                    @Override
                                    public void onLoading(long total, long current, boolean isUploading) {
                                    }

                                    @Override
                                    public void onSuccess(ResponseInfo<File> responseInfo) {
                                        progressbar.setVisibility(View.GONE);
                                        ImageUtils.forceRefreshSystemAlbum(ImageBrowserActivity.this,directory.getAbsolutePath());
                                        Toast.makeText(ImageBrowserActivity.this,getString(R.string.alreadySave),Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(HttpException error, String msg) {
                                        LogUtils.i("onFailure : "+msg);
                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(ImageBrowserActivity.this,getString(R.string.image_save_file),Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }else {
                        Toast.makeText(this,getString(R.string.alreadySave),Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case R.id.image_comment:
                if(isClick()){
                    Intent intent=new Intent(this, PhotoCommentActivity.class);
                    intent.putExtra(Constants.PHOTO_ID,photoList.get(mPosition % photoList.size()).getId());
                    startActivity(intent);
                }

                break;
        }
        return true;
    }


    private class OnPhotoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Animation an;
            if(hide){
                mActionBar.show();
                an=new AlphaAnimation(0f,1.0f);
                an.setDuration(500);
                an.setFillAfter(true);
                buttom.startAnimation(an);

                save.setVisibility(View.VISIBLE);
                comment.setVisibility(View.VISIBLE);
                hide=false;
            }else {
                mActionBar.hide();
                an=new AlphaAnimation(1.0f,0f);
                an.setDuration(500);
                an.setFillAfter(true);
                buttom.startAnimation(an);
                an.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        save.setVisibility(View.GONE);
                        comment.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                hide=true;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        initActionBar(R.drawable.back_white, R.color.black, String.valueOf((mPosition % photos.size()+1)+"/"+mTotal));
        String text=photoList.get(mPosition % photos.size()).getDescription();
        if(!TextUtils.isEmpty(text) && !getString(R.string.no_description).equals(text)){
            ll_text.setVisibility(View.VISIBLE);
            description.setText(text);
        }else {
            ll_text.setVisibility(View.GONE);
            description.setText("");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        super.finish();
        overridePendingTransition(0, R.anim.zoom_exit);
    }

    public boolean isClick(){
        long time = System.currentTimeMillis();
        if(time - lastTime > 400){
            lastTime = time;
            return true;
        }
        return  false;
    }

}
