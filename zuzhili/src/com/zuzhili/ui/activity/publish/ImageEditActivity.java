package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.multipart.ImageUpload;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.CustomDialog;
import com.zuzhili.ui.views.ImageViewTouch;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by addison on 2/18/14.
 */
public class ImageEditActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, ViewSwitcher.ViewFactory {

//    @ViewInject(R.id.image_img)
//    private ImageView imageImg;
    @ViewInject(R.id.switcher)
    private ViewSwitcher switcher;

    @ViewInject(R.id.delete_ibtn)           //删除
    private ImageButton deleteIbtn;

    @ViewInject(R.id.turnleft_ibtn)         //左旋转
    private ImageButton turnLeftIbtn;

    @ViewInject(R.id.turnright_ibtn)            //右旋转
    private ImageButton turnRightIbtn;

    @ViewInject(R.id.desc_edit)             //描述
    private EditText descEdit;
    public int screenWidth = 0;
    public int screenHeight = 0;

    private String action;
    private Bitmap bitmap;
    private static String localTempImageFileName = "";
    public static final String IMAGE_PATH = "zhiliren/images";
    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    public static final File FILE_LOCAL = new File(FILE_SDCARD,IMAGE_PATH);
    public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "zhiliren/images/screenshots");
    private static final int FLAG_ACTIVITY_CEMERA = 1;          //拍照

    private ImageUpload image;
    private CustomDialog dialog;
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_image_edit);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);
        getWindowWH();
        initView();
        if(inState == null){
            initData();
        } else {
            deleteIbtn.setVisibility(View.GONE);
            localTempImageFileName = inState.getString(Constants.TEMP_IMAGE_PATH);
            bitmap = inState.getParcelable("bitmap");
            if(bitmap != null){
//                imageImg.setImageBitmap(bitmap);
                showNext(bitmap);
            }
        }
    }

    private void initView() {
        switcher.setFactory(this);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        switcher.setLongClickable(true);
    }

    private void initData() {
        Intent intent = getIntent();
        action = intent.getStringExtra(Constants.ACTION);
        //拍照
        if(ValidationUtils.validationString(action) && action.equals(Constants.ACTION_CEMARA)){
            //如果是相机拍照，不需要有删除键
            deleteIbtn.setVisibility(View.GONE);
            goCamara();
        } else if(ValidationUtils.validationString(action) && action.equals(Constants.ACTION_EDIT)){
            image = (ImageUpload) intent.getSerializableExtra(Constants.IMAGE_ITEM);
            fillData();
        }
    }

    /**
     * 填充数据
     */
    private void fillData() {
        if(image != null){
            if(ValidationUtils.validationString(image.getFilepath())){
                Bitmap bp = createBitmap(image.getFilepath(), screenWidth, screenHeight);
                if(bp != null){
                    showNext(bp);
                }
            }

            if(ValidationUtils.validationString(image.getDesc())){
                descEdit.setText(image.getDesc());
            }
        }
    }

    /**
     * 点用内容提供者拍照
     */
    private void goCamara(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                localTempImageFileName = "";
                localTempImageFileName = String.valueOf((new Date()).getTime()) + ".png";
                File filePath = FILE_PIC_SCREENSHOT;
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(filePath, localTempImageFileName);
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(intent, FLAG_ACTIVITY_CEMERA);
            } catch (ActivityNotFoundException e) {
                //
            }
        }
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if(ValidationUtils.validationString(action) && action.equals(Constants.ACTION_CEMARA)){
            if(checkImageDesc()){
                ImageUpload item = new ImageUpload();
                item.setDesc(descEdit.getText().toString());
                File file = new File(FILE_PIC_SCREENSHOT,localTempImageFileName);
                item.setNewfilename(file.getName());
                item.setFileidentity((TextUtil.getUniqueFileName(file.getAbsolutePath())));
                item.setFilepath(file.getAbsolutePath());
                bundle.putSerializable(Constants.IMAGE_ITEM, (Serializable) item);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        } else if(ValidationUtils.validationString(action) && action.equals(Constants.ACTION_EDIT)){
            if(image != null){
                if(checkImageDesc()){
                    image.setDesc(descEdit.getText().toString());
                    bundle.putSerializable(Constants.IMAGE_ITEM, (Serializable) image);
                    intent.putExtra(Constants.IMAGE_EDIT_POSITION,getIntent().getIntExtra(Constants.IMAGE_EDIT_POSITION, 0 ));
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
        return super.performClickOnRight();
    }

    /**
     * 获取图片描述
     * @return
     */
    private boolean checkImageDesc(){
        String descStr = descEdit.getText().toString();
        if(ValidationUtils.validationString(descStr) && descStr.length() > 200){
            Utils.makeEventToast(this, getString(R.string.image_desc_str_hint), false);
            return false;
        } else if(!ValidationUtils.validationString(descStr)){
            Utils.makeEventToast(this, getString(R.string.image_desc_add_hint), false);
            return false;
        }
        return true;
    }

    /**
     * 删除当前图片
     * @param view
     */
    @OnClick(R.id.delete_ibtn)
    public void deleteImage(View view){
        dialog = new CustomDialog(this, R.style.popDialog);
        dialog.setDisplayView(null, getString(R.string.image_delete_hint), getString(R.string.confirm), getString(R.string.cancel));
        dialog.setLBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Intent intent = getIntent();
                intent.putExtra(Constants.IMAGE_DELETE, true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        dialog.setRBtnListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * 向左旋转
     * @param view
     */
    @OnClick(R.id.turnleft_ibtn)
    public void turnLeft(View view){
        turn(-90);
    }

    /**
     * 向右旋转
     * @param view
     */
    @OnClick(R.id.turnright_ibtn)
    public void turnRight(View view){
        turn(90);
    }

    private void showNext(Bitmap bitmap){
        ImageViewTouch nextImage = (ImageViewTouch)switcher.getNextView();
        nextImage.setImageBitmap(bitmap);
        Animation inAnim = switcher.getInAnimation();
        if(inAnim!=null){
            inAnim.setAnimationListener(animationListener);
        }
        switcher.showNext();
        ImageViewTouch curImage = (ImageViewTouch)switcher.getCurrentView();
        curImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    /**
     * 根据角度旋转
     * @param rolate
     */
    private void turn(float rolate){
        Matrix matrix = new Matrix();
        matrix.setRotate(rolate);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)((ImageViewTouch)switcher.getCurrentView()).getDrawable();
        if(bitmapDrawable == null){
            Utils.makeEventToast(ImageEditActivity.this, getString(R.string.load_image_first_hint), false);
            return;
        }
        Bitmap mBitmap = (bitmapDrawable).getBitmap();
        mBitmap = Bitmap.createBitmap(mBitmap, 0,0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        ImageViewTouch nextImage = (ImageViewTouch)switcher.getNextView();
        nextImage.setImageBitmap(mBitmap);
        Animation inAnim = switcher.getInAnimation();
        if(inAnim!=null){
            inAnim.setAnimationListener(animationListener);
        }
        switcher.showNext();
        ImageViewTouch curImage = (ImageViewTouch)switcher.getCurrentView();
        curImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FLAG_ACTIVITY_CEMERA)
            if(resultCode == Activity.RESULT_OK){
                File f = new File(FILE_PIC_SCREENSHOT,localTempImageFileName);
                bitmap =  createBitmap(f.getAbsolutePath(), screenWidth,screenHeight);
                if(null == bitmap){
                    Utils.makeEventToast(this, getString(R.string.not_found_image), false);
                } else {
                    showNext(bitmap);
                }
            } else {
                finish();
            }
    }

    /**
     * 获取屏幕的高和宽
     */
    private void getWindowWH(){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        screenHeight=dm.heightPixels;
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back,R.drawable.icon_done_top, getString(R.string.image_edit), false);
        return true;
    }

    /**
     * 针对三星手机拍照功能特殊性，采取此方式解决
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isCreated", true);
        outState.putString(Constants.TEMP_IMAGE_PATH, localTempImageFileName);
        if(bitmap != null){
            outState.putParcelable("bitmap", bitmap);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * 根据窗口大小加载bitmap
     * @param path      路径
     * @param w         屏幕宽度
     * @param h         屏幕高度
     * @return      bitmap
     */
    public Bitmap createBitmap(String path,int w,int h){
        try{
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int destWidth = 0;
            int destHeight = 0;
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            if(ratio == 0.0){
                ratio = 1.0;
            }
            newOpts.inSampleSize = (int) ratio;
//			newOpts.inSampleSize = 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public View makeView() {
        ImageViewTouch imageViewTouch = new ImageViewTouch(this);
        imageViewTouch.setBackgroundColor(0xFF000000);
        imageViewTouch.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageViewTouch.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        return imageViewTouch;
    }

    /**
     * 动画结束后bitmap制空
     */
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ImageViewTouch nextImage = (ImageViewTouch)switcher.getNextView();
            nextImage.setImageBitmap(null);
            System.gc();
        }
    };

}
