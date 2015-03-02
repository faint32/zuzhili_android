package com.zuzhili.controller;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.draftbox.DraftContract;
import com.zuzhili.model.MoreItem;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.List;

public class MoreAdapter extends BaseAdapter {
    private List<MoreItem> moreList;
    private BaseActivity c;//基础Activity事件
    private ImageLoader mImageLoader;

    //更多的适配器
    public MoreAdapter(BaseActivity c, List<MoreItem> list, ImageLoader mImageLoader) {
        this.c = c;
        this.moreList = list;
        this.mImageLoader = mImageLoader;
    }

    @Override
    public int getCount() {
        return moreList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return moreList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    class ViewHolder {
        ImageView iconImg;//图标
        TextView itemnameTxt;
        ImageView goImg;//跳转图标
        Button hintBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View retV = convertView;
        try {
            if (retV == null) {
                retV = LayoutInflater.from(c).inflate(R.layout.activity_more_list_item, parent, false);
            }
            final ViewHolder holder = new ViewHolder();
            holder.iconImg = (ImageView) retV.findViewById(R.id.iv_icon);
            holder.itemnameTxt = (TextView) retV.findViewById(R.id.tv_text);
            holder.goImg = (ImageView) retV.findViewById(R.id.iv_go);
            holder.hintBtn = (Button) retV.findViewById(R.id.bt_hint);
            retV.setTag(holder);
            MoreItem item = moreList.get(position);
            holder.iconImg.setBackgroundResource(item.getResourceId());
            holder.itemnameTxt.setText(item.getItemName());

            if (item.getResourceId() == R.drawable.more_icon_draft) {
                String listId = c.mSession.getListid();
                String ids = c.mSession.getIds();
                final String selection = "(" + DraftContract.Draft.COLUMN_NAME_LIST_ID + " = ? AND " + DraftContract.Draft.COLUMN_NAME_IDS + " = ? )";
                final String[] selectionArgs = new String[]{listId, ids};
                final Cursor cursor = c.getContentResolver().query(DraftContract.Draft.CONTENT_URI, new String[]{DraftContract.Draft._ID}, selection, selectionArgs, DraftContract.Draft.DEFAULT_SORT_ORDER);

//                Handler handler = new Handler();
//                cursor.registerContentObserver(new ContentObserver(handler) {
//                    @Override
//                    public void onChange(boolean selfChange) {
//                        Cursor acursor = c.getContentResolver().query(DraftContract.Draft.CONTENT_URI, new String[]{DraftContract.Draft._ID}, selection, selectionArgs, DraftContract.Draft.DEFAULT_SORT_ORDER);
//                        int count = acursor.getCount();
//                        if (count == 0) {
//                            holder.hintBtn.setVisibility(View.GONE);
//                        } else {
//                            holder.hintBtn.setText(String.valueOf(count));
//                        }
//                    }
//
//                    @Override
//                    public boolean deliverSelfNotifications() {
//                        return true;
//                    }
//                });

                int count = cursor.getCount();
                if (count == 0) {
                    holder.hintBtn.setVisibility(View.GONE);
                } else {
                    holder.hintBtn.setText(String.valueOf(count));
                    holder.hintBtn.setVisibility(View.VISIBLE);
                }

                // observe the content provider
                c.getContentResolver().registerContentObserver(DraftContract.Draft.CONTENT_URI, true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);

                        cursor.requery();
                        int count = cursor.getCount();
                        if (count == 0) {
                            holder.hintBtn.setVisibility(View.GONE);
                        } else {
                            holder.hintBtn.setText(String.valueOf(count));
                            holder.hintBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

            } else {
                holder.hintBtn.setVisibility(View.GONE);
            }

            holder.goImg.setVisibility(View.VISIBLE);
        } catch (InflateException e) {

        }
        return retV;
    }


}
