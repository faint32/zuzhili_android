package com.zuzhili.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.db.DBHelper;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PagingListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-4-13.
 */
public abstract class NonPagingResultsAdapter<T> extends BaseAdapter {

    protected Context mContext;

    protected PagingListView mListView;

    /** 网络请求的结果集 */
    protected List<T> mDataList = new ArrayList<T>();

    /** 网络请求的原始结果集 */
    protected List<T> sourceList = new ArrayList<T>();

    /** 请求参数 */
    protected HashMap<String, String> mParams;

    protected ImageLoader mImageLoader;

    /** 页面大小 */
    protected int pageSize;

    /** 页面是否正在加载 */
    protected boolean isLoading;

    /** 若最后一次网络请求返回0个结果集, 则不需要再发送请求 */
    protected boolean moreDataToLoad;

    protected LayoutInflater mInflater;

    /** 已经加载的页面 */
    protected int loadedPage = 0;

    protected DBHelper dbHelper;

    /** 是否写入到数据库 */
    protected boolean isWriteToCache;

    /** 缓存类型 */
    protected String cacheType;

    /** 第一页 */
    public static final String FIRST_PAGE = "0";

    /** 是否正在下拉刷新 */
    protected boolean isPullOnRefreshEnd = true;

    protected SortDataListCallback sortDataListCallback;

    protected ShowLastItemViewCallback showLastItemViewCallback;

    protected OnItemSelectedListener onItemSelectedListener;

    protected OnItemSelectedForDrawListener onItemSelectedForDrawListener;

    public interface OnItemSelectedForDrawListener<T> {
        public void onItemSelected(int position, T item, Drawable drawable);
    }

    /**
     * 子类需要对数据集进行排序需要实现该接口中的方法
     */
    public interface SortDataListCallback<T> {
        public void sortList(List<T> dataList);
        public void updateSortList(List<T> dataList);
    }

    public interface ShowLastItemViewCallback<T> {
        public void setLastItemViewSelectd(ListView listView, NonPagingResultsAdapter<T> adapter);
    }

    public interface OnItemSelectedListener<T> {
        public void onItemSelected(int position, T item);
    }

    public void setOnItemClickedForDrawListener(OnItemSelectedForDrawListener onItemSelectedForDrawListener) {
        this.onItemSelectedForDrawListener = onItemSelectedForDrawListener;
    }

    public NonPagingResultsAdapter(Context context, ListView listView, ImageLoader imageLoader) {
        this.mContext = context;
        this.mListView = (PagingListView) listView;
        this.mImageLoader = imageLoader;
        this.dbHelper = ((BaseActivity) context).getDbHelper();
        this.mInflater = LayoutInflater.from(context);
    }

    public void setListView(PagingListView listView) {
        this.mListView = listView;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 子类必须复写该方法
     */
    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void setSortDataListCallback(SortDataListCallback sortDataListCallback) {
        this.sortDataListCallback = sortDataListCallback;
    }

    public void setShowLastItemViewCallback(ShowLastItemViewCallback showLastItemViewCallback) {
        this.showLastItemViewCallback = showLastItemViewCallback;
    }

    public void setOnItemClickedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setList(List<T> data) {
        if (data != null) {
            mDataList.addAll(data);
            sourceList.addAll(data);
            notifyDataSetChanged();
            if (showLastItemViewCallback != null) {
                showLastItemViewCallback.setLastItemViewSelectd(mListView, this);
            }
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void clearList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void updateDataList(List<T> updatedData) {
        if (updatedData != null && updatedData.size() > 0) {
            mDataList.clear();
            mDataList.addAll(updatedData);
        }
        if (showLastItemViewCallback != null) {
            showLastItemViewCallback.setLastItemViewSelectd(mListView, this);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if(!mDataList.isEmpty()){
            mDataList.remove(position - 1);
            notifyDataSetChanged();
        }
    }

    /**
     * Inserts the specified element at the specified position in this list
     * @param position
     * @param item
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    public void addItem(int position, T item) throws IndexOutOfBoundsException {
        if (!mDataList.isEmpty()) {
            mDataList.add(position, item);
        } else {
            mDataList.add(item);
        }
        notifyDataSetChanged();
        if (showLastItemViewCallback != null) {
            showLastItemViewCallback.setLastItemViewSelectd(mListView, this);
        }
    }

    public void addAll(int position, List<T> list) {
        if (list == null) {
            mListView.setHasMoreItems(false);
            mListView.setAdapter(this);
            mListView.setSelection(0);
            notifyDataSetChanged();
            return;
        }
        int currentPos = list.size();
        if (!mDataList.isEmpty()) {
            mDataList.addAll(position, list);
        } else {
            mDataList.addAll(list);
        }
        mListView.setIsLoading(false);
        mListView.setAdapter(this);
        mListView.state=mListView.NORMAL;
        mListView.setSelection(currentPos);
        notifyDataSetChanged();
    }

}
