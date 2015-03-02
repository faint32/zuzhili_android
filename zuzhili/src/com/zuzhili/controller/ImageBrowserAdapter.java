package com.zuzhili.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.SimpleBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.im.ImageBrowserActivity;
import com.zuzhili.ui.views.photoView.PhotoView;

public class ImageBrowserAdapter extends PagerAdapter {

	private Context mContext;
	private List<String> mPhotos = new ArrayList<String>();
	private String mType;
    private ProgressBar progressbar;
    private BitmapUtils bitmapUtils;
    private View.OnClickListener mClickListener;
    //判断是网络图片还是本地图片
    private PhotoView photoView;
    private Bitmap bitmap = null;
    private Map<Integer,PhotoView> map = new HashMap<Integer,PhotoView>();
	public ImageBrowserAdapter(Context context,List<String> photos, String type,ProgressBar progressbar1,View.OnClickListener clickListener) {
        this.mContext = context;
		if (photos != null) {
			mPhotos = photos;
		}
		mType = type;
        progressbar=progressbar1;
        bitmapUtils = new BitmapUtils(context);
        mClickListener=clickListener;
    }

	@Override
	public int getCount() {
		if (mPhotos.size() > 1) {
			return Integer.MAX_VALUE;
		}
		return mPhotos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
        photoView = new PhotoView(container.getContext());

        if (ImageBrowserActivity.TYPE_ALBUM.equals(mType)) {
            bitmapUtils.display(photoView, mPhotos.get(position% mPhotos.size()),new IconCallBack());
            LogUtils.i("bitmapUtils.display"+mPhotos.get(position% mPhotos.size()));
        } else if (ImageBrowserActivity.TYPE_PHOTO.equals(mType)) {
            bitmapUtils.display(photoView, mPhotos.get(position),new IconCallBack());
            LogUtils.i("bitmapUtils.display"+mPhotos.get(position));
        }
        if(bitmap!=null){
            photoView.setImageBitmap(bitmap);
        }
		container.addView(photoView, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(mClickListener);
        map.put(position,photoView);
        return photoView;
    }

    public void setListener(View.OnClickListener clickListener){
        mClickListener=clickListener;
    }

    class IconCallBack<T extends View> extends SimpleBitmapLoadCallBack<T>{
        @Override
        public void onPreLoad(T container, String uri, BitmapDisplayConfig config) {
            super.onPreLoad(container, uri, config);
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadCompleted(T container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            progressbar.setVisibility(View.GONE);
            this.setBitmap(container, bitmap);
            Animation animation = config.getAnimation();
            if (animation != null) {
                animationDisplay(container, animation);
            }
        }

        @Override
        public void onLoadFailed(View container, String uri, Drawable drawable) {
            Toast.makeText(mContext,R.string.timeout_icon_failed,Toast.LENGTH_SHORT).show();
        }

        private void animationDisplay(T container, Animation animation) {
            try {
                Method cloneMethod = Animation.class.getDeclaredMethod("clone");
                cloneMethod.setAccessible(true);
                container.startAnimation((Animation) cloneMethod.invoke(animation));
            } catch (Throwable e) {
                container.startAnimation(animation);
            }
        }
    }

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

    /**
     * 根据角度旋转
     * @param rolate
     */
    public void turn(float rolate,int position){
        Matrix matrix = new Matrix();
        matrix.setRotate(rolate);
        Drawable drawable=(Drawable)map.get(position).getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
        //BitmapDrawable bitmapDrawable = (BitmapDrawable)map.get(position).getDrawable();
        if(bitmapDrawable == null){
            Utils.makeEventToast(mContext,mContext.getString(R.string.load_image_first_hint), false);
            return;
        }
        Bitmap mBitmap = bitmapDrawable.getBitmap();
        mBitmap = Bitmap.createBitmap(mBitmap, 0,0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        map.get(position).setImageBitmap(mBitmap);
    }


    public Map<Integer,PhotoView> getMap(){
        return map;
    }
}
