package com.zuzhili.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.framework.images.NativeImageLoader;
import com.zuzhili.model.ImageBean;
import com.zuzhili.ui.views.MyImageView;

import java.util.List;

/**
 * Created by kj on 14-7-16.
 */
public class GalleryAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    protected ListView mListView;
    private Context context;
    private List<ImageBean> list;
    private Point mPoint = new Point(0, 0);
    protected LayoutInflater mInflater;
    protected OnItemSelectedListener onItemSelectedListener;
    public boolean notFirst = false;

    public GalleryAdapter(Context context, ListView listView, List<ImageBean> list) {
        this.context = context;
        this.mListView = listView;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        list.get(0).setSelect(true);
    }

    public void setOnItemClickListener() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_pic_gallery, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);

            holder.galleryAvatar.setOnMeasureListener(new MyImageView.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.galleryAvatar.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        ImageBean mImageBean = null;
        String path = null;
        if (position == 0) {
            mImageBean = list.get(0);
            path = mImageBean.getTopImagePath();

            holder.galleryName.setText("所有照片");
            int count = 0;
            for (ImageBean item : list) {
                count += item.getImageCounts();
            }
            holder.picCount.setText(count + "张");
        } else {
            mImageBean = list.get(position - 1);
            path = mImageBean.getTopImagePath();


            holder.galleryName.setText(mImageBean.getFolderName());
            holder.picCount.setText(mImageBean.getImageCounts() + "张");
        }

        holder.galleryAvatar.setTag(path);
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {

            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView mImageView = (ImageView) mListView.findViewWithTag(path);
                if (bitmap != null && mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });

        if (bitmap != null) {
            holder.galleryAvatar.setImageBitmap(bitmap);
        } else {
            holder.galleryAvatar.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        if (mImageBean.isSelect()) {
            if (notFirst) {
                if (position == 0) {
                    holder.choose.setVisibility(View.GONE);
                } else {
                    holder.choose.setVisibility(View.VISIBLE);
                }
            } else {
                if (position == 0) {
                    holder.choose.setVisibility(View.VISIBLE);
                } else {
                    holder.choose.setVisibility(View.GONE);
                }
            }

        } else {
            holder.choose.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemSelectedListener != null) {
            if (parent.getAdapter().getItem(position) instanceof ImageBean) {
                onItemSelectedListener.onItemSelected(position, parent.getAdapter().getItem(position));
            }
        }
        view.findViewById(R.id.img_gallery_choose).setVisibility(View.VISIBLE);
    }

    public void setOnItemClickedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener<T> {
        public void onItemSelected(int position, T item);
    }

    public List<ImageBean> getList() {
        return list;
    }

    class ViewHolder {

        @ViewInject(R.id.img_gallery_avatar)
        MyImageView galleryAvatar;

        @ViewInject(R.id.txt_gallery_name)
        TextView galleryName;

        @ViewInject(R.id.txt_pic_count)
        TextView picCount;

        @ViewInject(R.id.img_gallery_choose)
        ImageView choose;

    }

}
