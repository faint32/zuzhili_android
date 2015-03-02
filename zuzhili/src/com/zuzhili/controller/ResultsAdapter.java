package com.zuzhili.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.bussiness.utility.BackgroudTask;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.DBCache;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ResultsAdapter<T> extends BaseAdapter implements Listener<String>, ErrorListener,
        PullRefreshListView.OnRefreshRequestListener {
	
	protected Context mContext;
	
	protected PullRefreshListView mListView;
	
	/** 网络请求的结果集 */
	protected List<T> mDataList = new ArrayList<T>();
	
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

    /**
     * 子类需要对数据集进行排序需要实现该接口中的方法
     */
    public interface SortDataListCallback<T> {
        public void sortList(List<T> dataList);
        public void updateSortList(List<T> dataList);
    }

    public interface ShowLastItemViewCallback<T> {
        public void setLastItemViewSelectd(ListView listView, ResultsAdapter<T> adapter);
    }

    public ResultsAdapter(Context context, ListView listView, ImageLoader imageLoader,
                          HashMap<String, String> params) {
        this.mContext = context;
        this.mListView = (PullRefreshListView) listView;
        this.mImageLoader = imageLoader;
        this.mParams = params;
        this.isWriteToCache = false;
        this.dbHelper = ((BaseActivity) context).getDbHelper();
        this.mInflater = LayoutInflater.from(context);
    }

	public ResultsAdapter(Context context, ListView listView, ImageLoader imageLoader,
                          HashMap<String, String> params, String cacheType) {
		this.mContext = context;
		this.mListView = (PullRefreshListView) listView;
		this.mImageLoader = imageLoader;
		this.mParams = params;
        this.isWriteToCache = true;
        this.cacheType = cacheType;
        this.dbHelper = ((BaseActivity) context).getDbHelper();
		this.mInflater = LayoutInflater.from(context);
	}

    public void setListView(PullRefreshListView listView) {
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

    /**
     * 解析响应数据
     * @param response
     * @return
     */
	public abstract List<T> parseList(String response);

    public abstract void loadNextPage();

    /**
     * 更新请求参数
     * @param params
     */
    public void updateRequestParams(Map<String, String> params) {
        params.put("start", String.valueOf(loadedPage * Constants.PAGE_SIZE));
        return;
    }

    /**
     *
     * @param session
     * @return
     */
    public String getIdentity(Session session) {
        StringBuilder builder = new StringBuilder();
        return builder.append(session.getListid())
                .append(Constants.SYMBOL_PERIOD)
                .append(session.getIds()).toString();
    }

    public void setSortDataListCallback(SortDataListCallback sortDataListCallback) {
        this.sortDataListCallback = sortDataListCallback;
    }

    public void setShowLastItemViewCallback(ShowLastItemViewCallback showLastItemViewCallback) {
        this.showLastItemViewCallback = showLastItemViewCallback;
    }

	/**
	 * 检测是否加载下一页数据
	 * @param data	已获取的数据集
	 * @param position	listView中的item位置
	 * @return
	 */
	public boolean shouldLoadNextPage(List<T> data, int position) {
		boolean scrollRangeReached = (position > (data.size() - pageSize));
		return (scrollRangeReached && !isLoading && moreDataToLoad && isPullOnRefreshEnd);
	}
	
	public void setList(List<T> data) {
        if (data != null) {
            mDataList.clear();
            mDataList.addAll(data);

        } else {
            mDataList.clear();
        }
        notifyDataSetChanged();
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
        }else{
            mDataList.clear();
        }
    }

	/**
	 * 处理网络请求到的数据
     * 如果有缓存，不再从服务器拉新数据，需要手动下拉刷新取新数据。
     * 如果没有缓存，表示用户第一次登录到该社区，从服务器拉取新数据并存放到缓存中。
	 */
	@Override
	public void onResponse(String response) {
        isPullOnRefreshEnd = true;
        mListView.onPullRefreshEnd();
        mListView.onFooterRefreshEnd();
        List<T> tempList = new ArrayList<T>();
        if (response != null) {
            tempList = parseList(response);

            if (isLoadingFirstPage()) {
                // 下拉刷新拉取新数据，或第一次取数据时需要写入缓存
                updateDataList(tempList);
                loadedPage = 0;
                // 写入到数据库
                if (isWriteToCache && mDataList.size() > 0) {
                    long start = System.currentTimeMillis();
                    writeToDatabase(response, cacheType, getIdentity(((BaseActivity) mContext).getSession()));
                    long end = System.currentTimeMillis();
                    Utils.printComsumeTime(start, end);
                }
            } else {
                if (tempList != null) {
                    mDataList.addAll(tempList);
                } else {
                    tempList = new ArrayList<T>();
                }
                if (sortDataListCallback != null) {
                    sortDataListCallback.sortList(mDataList);
                    sortDataListCallback.updateSortList(mDataList);
                }
            }
            int listSize = 0;
            if (tempList != null)
                listSize = tempList.size();
            if(listSize > 0) {
                pageSize = (pageSize > listSize) ? pageSize : listSize;
                if(pageSize > listSize) {
                    moreDataToLoad = false;
                } else {
                    moreDataToLoad = true;
                }
            } else {
                moreDataToLoad = false;
            }
            notifyDataSetChanged();
            if (showLastItemViewCallback != null) {
                showLastItemViewCallback.setLastItemViewSelectd(mListView, this);
            }
            isLoading = false;
        } else {
            if (isLoadingFirstPage()) {
                loadNextPage();
            }
        }
	}
	
	/**
	 * 处理错误请求
	 */
	@Override
	public void onErrorResponse(VolleyError error) {
        mListView.onFooterRefreshEnd();
		isLoading = false;
		moreDataToLoad = false;
	}

    public void setOnRefreshListener() {
        mListView.setOnRefreshListener(this);
    }

    private void writeToDatabase(String response, String cacheType, String identify) {
        final DBCache cache = new DBCache();
        cache.setCachetype(cacheType);
        cache.setIdentify(identify);
        cache.setJsondata(response);
        Runnable dbWorker = new Runnable() {
            @Override
            public void run() {
                dbHelper.getCacheDB().insertCacheData(cache);
            }
        };
        new BackgroudTask().execute(dbWorker);
    }

    /**
     * loading 第一页
     * @return
     */
    private boolean isLoadingFirstPage() {
        if (mParams != null) {
            return mParams.get("start") != null && mParams.get("start").equals(FIRST_PAGE);
        } else {
            return false;
        }
    }
    
    public void removeItem(int position){
       if(!mDataList.isEmpty()){
           mDataList.remove(position-1);
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
            notifyDataSetChanged();
            if (showLastItemViewCallback != null) {
                showLastItemViewCallback.setLastItemViewSelectd(mListView, this);
            }
        }
    }

}