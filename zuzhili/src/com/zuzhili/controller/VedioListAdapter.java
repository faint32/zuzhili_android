package com.zuzhili.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.bussiness.helper.VedioHelper;
import com.zuzhili.bussiness.utility.PublicTools;
import com.zuzhili.model.multipart.VedioLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by addison on 2/21/14.
 */
public class VedioListAdapter extends BaseAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = "VedioAdapter";

    private List<VedioLocal> vedioItemList = new ArrayList<VedioLocal>();
    private List<ViewHolder> mViewHolderList = new ArrayList<ViewHolder>();
    private LayoutInflater mInflater;
    private Drawable mDefaultDrawable;
    private Bitmap mDefaultBitmap = null;
    private Hashtable<Integer, Bitmap> mThumbHash = null;
    public VedioListAdapter(Context context) {
        mDefaultDrawable = context.getResources().getDrawable(R.drawable.video_default_icon);
        mInflater = LayoutInflater.from(context);
    }

    public void setListItems(List<VedioLocal> list) {
        vedioItemList = list;
    }

    @Override
    public int getCount() {
        return vedioItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return vedioItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressWarnings("unchecked")
    public Object[] getHolderObjects() {
        Object[] objs = mViewHolderList.toArray();

        if (objs != null)
            Arrays.sort(objs, new SortArray());
        return objs;
    }

    public void sendRefreshMessage(ViewHolder holder) {
        Message msg = new Message();
        msg.obj = holder;
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((ViewHolder) msg.obj).refreshThumbnail();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_local_vedio, null);
            holder = new ViewHolder(convertView);
            holder.refresh(position);
            convertView.setTag(holder);

            mViewHolderList.add(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.refresh(position);
        }

        return convertView;
    }

    public class ViewHolder {
        int COLOR_BLACK = Color.argb(255, 85, 86, 90);
        public VedioLocal mItem;
        public Bitmap mBitmap;
        public boolean mUseDefault;
        public int mPosition;
        private ImageView thumbnailIV;
        private TextView vedioNameTV;
        private TextView vedioDurationTV;
        private TextView vedioSizeTV;

        public ViewHolder(View convertView) {
            thumbnailIV = (ImageView)convertView.findViewById(R.id.vedio_thumbnail_iv);
            vedioNameTV = (TextView)convertView.findViewById(R.id.vedio_name_tv);
            vedioDurationTV = (TextView)convertView.findViewById(R.id.vedio_duration_tv);
            vedioSizeTV = (TextView)convertView.findViewById(R.id.vedio_size_tv);
        }

        public void refresh(int pos) {
            mPosition = pos;
            mItem = vedioItemList.get(pos);

            if (PublicTools.THUMBNAIL_PREPARED == mItem.vedio.getThumbnailState()) {
                mBitmap = mItem.vedio.miniThumbBitmap(false, VedioListAdapter.this.mThumbHash, mDefaultBitmap);
                mItem.cover = mBitmap;
//                mItem.bitmapArray = PublicTools.getBytes(mBitmap);
                if (!mBitmap.isRecycled()) {
                    thumbnailIV.setImageBitmap(mBitmap);
                    mUseDefault = false;
                } else {
                    thumbnailIV.setImageDrawable(mDefaultDrawable);
                    mUseDefault = true;
                }
            } else {
                thumbnailIV.setImageDrawable(mDefaultDrawable);
                mUseDefault = true;
            }

            vedioNameTV.setText(mItem.name);
            vedioDurationTV.setText(mItem.duration);
            vedioSizeTV.setText(mItem.size);

        }

        public void refreshThumbnail() {
            if ((PublicTools.THUMBNAIL_PREPARED == mItem.vedio.getThumbnailState() || mBitmap != null) && !mBitmap.isRecycled()) {
                mItem.cover = mBitmap;
//                mItem.bitmapArray = PublicTools.getBytes(mBitmap);
                thumbnailIV.setImageBitmap(mBitmap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public class SortArray implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            if (obj1 == null || obj1 == null)
                return 0;
            ViewHolder v1 = (ViewHolder) obj1;
            ViewHolder v2 = (ViewHolder) obj2;
            return (v1.mPosition < v2.mPosition ? -1 : (v1.mPosition == v2.mPosition ? 0 : 1));
        }
    }
    public void setThumbHashtable(Hashtable<Integer, Bitmap> ht, Bitmap defaultThumb) {
        this.mThumbHash = ht;
        this.mDefaultBitmap = defaultThumb;
    }

}
