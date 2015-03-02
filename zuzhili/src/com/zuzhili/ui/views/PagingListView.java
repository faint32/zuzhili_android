package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zuzhili.controller.NonPagingResultsAdapter;

import java.util.List;


/**
 * Created by liutao on 14-4-14.
 */
public class PagingListView extends ListView {

	public interface Pagingable {
		void onLoadMoreItems();
	}

	private boolean isLoading;
	private boolean hasMoreItems;
	private Pagingable pagingableListener;
	private LoadingView loadingView;
    private int firstItemIndex;
    private boolean isRecored;
    private int startX;
    private int startY;
    public int state;
    private int headContentHeight;
    public final static int RELEASE_To_REFRESH = 0;
    public final static int REFRESHING = 1;
    public final static int NORMAL = 2;

	public PagingListView(Context context) {
		super(context);
		init();
	}

	public PagingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
        if(isLoading){
            loadingView.setPadding(0,0,0,0);
        }else {
            loadingView.setPadding(0, -1 * headContentHeight, 0, 0);
        }
	}

    public void removeHeadView(){
        removeHeaderView(loadingView);
    }
	public void setPagingableListener(Pagingable pagingableListener) {
		this.pagingableListener = pagingableListener;
	}

	public void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
	}

	public boolean hasMoreItems() {
		return this.hasMoreItems;
	}


	public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems) {
		setHasMoreItems(hasMoreItems);
		setIsLoading(false);
		if(newItems != null && newItems.size() > 0) {
			ListAdapter adapter = ((HeaderViewListAdapter)getAdapter()).getWrappedAdapter();
			if(adapter instanceof NonPagingResultsAdapter) {
//				((PagingBaseAdapter)adapter).addMoreItems(newItems);
			}
		}
	}


	private void init() {
		isLoading = false;
		loadingView = new LoadingView(getContext());

        measureView(loadingView);
        headContentHeight = loadingView.getMeasuredHeight();
        loadingView.setPadding(0, -1 * headContentHeight, 0, 0);
        loadingView.invalidate();

        addHeaderView(loadingView);
        state=NORMAL;
		setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItemIndex = firstVisibleItem;
			}
		});

	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (firstItemIndex == 0 && !isRecored) {
                    isRecored = true;
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (state != REFRESHING) {
                    if (state == RELEASE_To_REFRESH) {
                        state = REFRESHING;
                        //获取聊天记录
                        if (!isLoading && hasMoreItems) {
                            if(pagingableListener != null) {
                                isLoading = true;
                                pagingableListener.onLoadMoreItems();
                            }
                        }
                    }
                }

                isRecored = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int tempX = (int) event.getX();
                int tempY = (int) event.getY();

                if (!isRecored && firstItemIndex == 0) {
                    isRecored = true;
                    startX = tempX;
                    startY = tempY;
                }

                if (state != REFRESHING && isRecored) {
                    if (state == NORMAL) {
                        if (tempY - startY > 20 ) {
                            state = RELEASE_To_REFRESH;
                        }
                    }
                }
                break;

        }
            return super.onTouchEvent(event);
    }
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
}
