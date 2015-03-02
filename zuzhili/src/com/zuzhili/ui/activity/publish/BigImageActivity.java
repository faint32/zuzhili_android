package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ImageBrowserAdapter;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.ZoomOutPageTransformer;
import com.zuzhili.ui.views.ScrollViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kj on 2014/8/21.
 */
public class BigImageActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener ,BaseActivity.TimeToShowActionBarCallback, View.OnTouchListener {

    private ScrollViewPager mSvpPager;
    private ProgressBar progressbar;
    private LinearLayout ll_text;
    private EditText description;
    private ImageBrowserAdapter mAdapter;
    private RelativeLayout buttom;
    private ImageView turnleft;
    private ImageView turnRight;
    private ImageView check;
    private String mType;
    private int mPosition;
    private int mTotal;
    private List<ImageItem> photos;
    private List<ImageItem> imageChoosedList = new ArrayList<ImageItem>();
    private ArrayList<String> photosPath;
    private boolean hide=false;
    private boolean frist=true;
    private long lastTime;
    private boolean isChat;
    public static final String IMAGE_TYPE = "image_type";
    public static final String TYPE_ALBUM = "image_album";
    public static final String TYPE_PHOTO = "image_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        ll_text = (LinearLayout) findViewById(R.id.ll_text);
        turnleft=(ImageView)findViewById(R.id.im_turnleft);
        turnleft.setOnTouchListener(this);
        turnRight=(ImageView)findViewById(R.id.im_turnright);
        turnRight.setOnTouchListener(this);
        check=(ImageView)findViewById(R.id.im_check);
        check.setOnTouchListener(this);
        buttom=(RelativeLayout)findViewById(R.id.rl_buttom);
        description = (EditText) findViewById(R.id.et_description);
        mSvpPager = (ScrollViewPager) findViewById(R.id.imagebrowser_svp_pager);
        mSvpPager.setOnPageChangeListener(this);
        mSvpPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setCustomActionBarCallback(this);
        setLeftImgBtnOnTouchListener(this);
        setRightBtnOnTouchListener(this);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>200){
                    Toast.makeText(getBaseContext(),getString(R.string.ndescription_tolong),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(imageChoosedList.contains(photos.get(mPosition % photos.size()))){
                    if(s.toString().length()>200){
                        photos.get(mPosition % photos.size()).setDesc(s.toString().substring(0,200));
                    }else {
                        photos.get(mPosition % photos.size()).setDesc(s.toString());
                    }

                }
            }
        });
        init();
        isChat = getIntent().getBooleanExtra(Constants.TAG_CHAT_CONTACTS,false);

        if(isChat){
            ll_text.setVisibility(View.GONE);
            turnleft.setVisibility(View.GONE);
            turnRight.setVisibility(View.GONE);
        }
    }

    private void init() {
        mType = getIntent().getStringExtra(IMAGE_TYPE);
        imageChoosedList = (List<ImageItem>) getIntent().getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
        if (TYPE_ALBUM.equals(mType)) {
            mPosition = getIntent().getIntExtra(Constants.IMAGE_POSITION, 0);
            //图片地址数组
            photos = (List<ImageItem>) getIntent().getSerializableExtra(Constants.BIGIMAGE_PHOTOS);
            photosPath =new ArrayList<String>();
            for(ImageItem item:photos){
                photosPath.add(item.getImagePath());
            }
            mTotal = photos.size();
            if (mPosition > mTotal) {
                mPosition = mTotal - 1;
            }
            if (mTotal > 1) {
                mPosition += 1000 * mTotal;
                mAdapter = new ImageBrowserAdapter(this,photosPath, mType,progressbar,new OnPhotoClickListener());
                mSvpPager.setAdapter(mAdapter);
                mSvpPager.setCurrentItem(mPosition, false);
            }
        } else if (TYPE_PHOTO.equals(mType)) {
            ImageItem item = (ImageItem)getIntent().getSerializableExtra(Constants.BIGIMAGE_PHOTO);
            photos=new ArrayList<ImageItem>();
            photos.add(imageChoosedList.get(0));
            photosPath = new ArrayList<String>();
            photosPath.add(item.getImagePath());
            frist=false;
            if(!TextUtils.isEmpty(item.getDesc()) && !item.getDesc().equals(getString(R.string.no_description))){
                description.setText(item.getDesc());
            }else {
                description.setText("");
                description.setHint(getString(R.string.add_image_desc));
            }
            mAdapter = new ImageBrowserAdapter(this, photosPath, mType,progressbar,new OnPhotoClickListener());
            mSvpPager.setAdapter(mAdapter);
        }
    }


    @Override
    public boolean showCustomActionBar() {
        if(photos.size()>1){
            if(imageChoosedList.size() == 0){
                initActionBar(R.drawable.icon_back, getString(R.string.send) , getString(R.string.preview)+String.valueOf((mPosition % photos.size()+1)+"/"+mTotal));
            }else {
                initActionBar(R.drawable.icon_back, getString(R.string.send)+"("+imageChoosedList.size()+"/9)" , getString(R.string.preview)+String.valueOf((mPosition % photos.size()+1)+"/"+mTotal));
            }
        }else {
            if(imageChoosedList.size() == 0){
                initActionBar(R.drawable.icon_back, getString(R.string.send), getString(R.string.preview)+"1/1");
            }else {
                initActionBar(R.drawable.icon_back, getString(R.string.send)+"("+imageChoosedList.size()+"/9)", getString(R.string.preview)+"1/1");
            }
        }

        if(frist){
            for(int i=0;i<imageChoosedList.size();i++){
                for(ImageItem item: photos){
                    if(imageChoosedList.get(i).getImagePath().equals(item.getImagePath())){
                        item.setDesc(imageChoosedList.get(i).getDesc());
                        imageChoosedList.remove(i);
                        imageChoosedList.add(item);
                    }
                }
            }
            frist=false;
        }

        if(imageChoosedList.contains(photos.get(mPosition % photos.size()))){
            check.setBackgroundResource(R.drawable.im_choose);
        }else {
            check.setBackgroundResource(R.drawable.im_notchoose);
        }
        return true;
    }

    public void finishSearch() {
        Intent intent= new Intent();
        Bundle bundle= new Bundle();
        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean performClickOnRight() {
        Intent intent= new Intent();
        Bundle bundle= new Bundle();
        bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
        intent.putExtras(bundle);
        intent.putExtra(Constants.FINISH_ACTIVITY,true);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    /**
     * 为了拦截点击事件,避免事件向上分发
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.imgBtn_left:
                if(isClick()){
                    finishSearch();
                }
                break;

            case R.id.btn_right:
                if(isClick()){
                    Intent intent= new Intent();
                    Bundle bundle= new Bundle();
                    bundle.putSerializable(Constants.IMAGE_CHOOSED_LIST, (Serializable) imageChoosedList);
                    intent.putExtras(bundle);
                    intent.putExtra(Constants.FINISH_ACTIVITY,true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

                break;

            case R.id.im_turnleft:
                if(isClick()){
                    mAdapter.turn(-90,mPosition);
                }

                break;

            case R.id.im_turnright:
                if(isClick()){
                    mAdapter.turn(90,mPosition);
                }
                break;

            case R.id.im_check:
                if(isClick()){
                    if(imageChoosedList.contains(photos.get(mPosition % photos.size()))){
                        imageChoosedList.remove(photos.get(mPosition % photos.size()));
                        showCustomActionBar();
                    }else {
                        if(imageChoosedList.size()<9){
                            imageChoosedList.add(photos.get(mPosition % photos.size()));
                            showCustomActionBar();
                            String desc = description.getText().toString().trim();
                            if(!TextUtils.isEmpty(desc) && desc.length()>1){
                                photos.get(mPosition % photos.size()).setDesc(desc);
                            }
                        }else {
                            Toast.makeText(getBaseContext(), getString(R.string.image_out), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK==keyCode){
            finishSearch();
        }
        return super.onKeyDown(keyCode, event);
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
                check.setVisibility(View.VISIBLE);
                if(isChat){
                    turnleft.setVisibility(View.GONE);
                    turnRight.setVisibility(View.GONE);
                }else {
                    turnleft.setVisibility(View.VISIBLE);
                    turnRight.setVisibility(View.VISIBLE);
                }
                description.setVisibility(View.VISIBLE);
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
                        check.setVisibility(View.GONE);
                        turnleft.setVisibility(View.GONE);
                        turnRight.setVisibility(View.GONE);
                        description.setVisibility(View.GONE);
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
        showCustomActionBar();
        if(!TextUtils.isEmpty(photos.get(mPosition % photos.size()).getDesc()) &&
                !photos.get(mPosition % photos.size()).getDesc().equals(getString(R.string.no_description))){
            description.setText(photos.get(mPosition % photos.size()).getDesc());
        }else {
            description.setText("");
            description.setHint(getString(R.string.add_image_desc));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.getMap().clear();
    }
}
