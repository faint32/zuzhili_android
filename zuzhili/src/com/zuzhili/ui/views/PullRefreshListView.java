package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.zuzhili.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PullRefreshListView extends ListView implements OnScrollListener {

	public final static int RELEASE_To_REFRESH = 0;
    public final static int PULL_To_REFRESH = 1;
    public final static int REFRESHING = 2;
    public final static int DONE = 3;
    public final static int LOADING = 4;

	private final static int RATIO = 3;
	
	private Context context;
	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private boolean isRecored;

	private int headContentHeight;
	private int startX;
	private int startY;
	private int firstItemIndex;

    private int mItemCount;
    private int mItemOffsetY[];
    private boolean scrollIsComputed = false;
    private int mHeight;

	private int state;

	private boolean isBack;

	private boolean isRefreshable;

	private LinearLayout footerLin;

    private TextView loadMoreTxtV;
    private LinearLayout loadingLin;

    private OnRefreshRequestListener refreshListener;

    private OnRefreshStateChangeListener refreshStateChangeListener;

    public interface OnRefreshRequestListener {
        public void onRefresh();
    }

    public interface OnRefreshStateChangeListener {
        public void onRefreshStateChanged(int state);
    }

	public PullRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

    private void init(Context context) {
		this.context = context;
		setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.listview_head, null);

		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		
		
		footerLin=(LinearLayout) inflater.inflate(R.layout.listview_footer, null);
		loadMoreTxtV =(TextView) footerLin.findViewById(R.id.footer_more);
		loadingLin =(LinearLayout)footerLin.findViewById(R.id.footer_loading);
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

    public int getListHeight() {
        return mHeight;
    }

    public void computeScrollY() {
        mHeight = 0;
        mItemCount = getAdapter().getCount();
        if (mItemOffsetY == null || mItemOffsetY.length != mItemCount) {
            mItemOffsetY = new int[mItemCount];
        }
        for (int i = 0; i < mItemCount; ++i) {
            View view = getAdapter().getView(i, null, this);
            view.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mHeight;
            mHeight += view.getMeasuredHeight();
        }
        scrollIsComputed = true;
    }

    public boolean scrollYIsComputed() {
        return scrollIsComputed;
    }

    public int getComputedScrollY() {
        int pos, nScrollY, nItemY;
        View view = null;
        pos = getFirstVisiblePosition();
        view = getChildAt(0);
        nItemY = view.getTop();
        nScrollY = mItemOffsetY[pos] - nItemY;
        return nScrollY;
    }

    public void setIsRefreshable(boolean flag){
        isRefreshable =  flag;
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startX = (int) event.getX();
					startY = (int) event.getY();
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
				
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();

					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}

				isRecored = false;
				isBack = false;
				break;

			case MotionEvent.ACTION_MOVE:
				int tempX = (int) event.getX();
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startX = tempX;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0 ) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
						else {
						}
					}
					if (state == PULL_To_REFRESH) {

						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
					}

					if (state == DONE) {
						if (tempY - startY > 0 && Math.abs(tempX - startX) < TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics())) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}

					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {

        if (refreshStateChangeListener != null) {
            refreshStateChangeListener.onRefreshStateChanged(state);
        }

		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开可以刷新...");

			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);

			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText("下拉刷新...");
			} else {
				tipsTextview.setText("下拉刷新...");
			}
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			tipsTextview.setText("下拉刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
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

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    private void changeToOnRefresh() {
        headView.setPadding(0, 0, 0, 0);
        progressBar.setVisibility(View.VISIBLE);
        arrowImageView.clearAnimation();
        arrowImageView.setVisibility(View.GONE);
        tipsTextview.setText("正在请求数据...");
        lastUpdatedTextView.setVisibility(View.VISIBLE);
        setFooterGone();
    }

    private void setFooterGone() {
        footerLin.setVisibility(View.GONE);
        loadMoreTxtV.setVisibility(View.GONE);
        loadingLin.setVisibility(View.GONE);
        if (footerLin != null) {
            removeFooterView(footerLin);
        }
    }

    private void setLoadingFooterVisible() {
        if (footerLin.getVisibility() == View.GONE || this.getFooterViewsCount() == 0) {
            addFooterView(footerLin);
            invalidateViews();
        }
        footerLin.setVisibility(View.VISIBLE);
        loadMoreTxtV.setVisibility(View.GONE);
        loadingLin.setVisibility(View.VISIBLE);
    }

    private void setLoadMoreFooterVisiable() {
        if (footerLin.getVisibility() == View.GONE) {
            addFooterView(footerLin);
        }
        footerLin.setVisibility(View.VISIBLE);
        loadingLin.setVisibility(View.GONE);
        loadMoreTxtV.setVisibility(View.VISIBLE);
    }

    private String getTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        return format.format(date);
    }

    public void setOnRefreshListener(OnRefreshRequestListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public void setOnRefreshStateChangeListener(OnRefreshStateChangeListener refreshStateChangeListener) {
        this.refreshStateChangeListener = refreshStateChangeListener;
    }

    /**
     * 下拉刷新结束，改变header和footer的状态
     */
	public void onPullRefreshEnd() {
		state = DONE;
		lastUpdatedTextView.setText("加载中" + getTime());
		changeHeaderViewByState();
	}

    /**
     * footer显示正在加载
     */
    public void onFooterRefreshBegin() {
        setLoadingFooterVisible();
    }

    /**
     * 加载完一页数据后隐藏footer
     */
    public void onFooterRefreshEnd() {
        setFooterGone();
    }

    /**
     * 加载数据失败
     */
    public void onFooterReset() {
        setLoadMoreFooterVisiable();
    }

    /**
     * 获取listView当前状态，LOADING, DONE, etc.
     * @return
     */
    public int getListViewState() {
        return state;
    }

    public void setHeadViewBackgroudRes(int resid) {
        if(headView != null) {
            headView.setBackgroundResource(resid);
        }
    }

    public void setHeadViewBackgroundColor(int color) {
        if (headView != null) {
            headView.setBackgroundColor(color);
        }
    }

	public void setFooterBackgroundResource(int resid) {
        if (footerLin != null) {
            footerLin.setBackgroundResource(resid);
        }
	}

	public void setFooterBackgroundColor(int color) {
        if (footerLin != null) {
            footerLin.setBackgroundColor(color);
        }
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("上次更新于" + getTime());
		super.setAdapter(adapter);
	}

    public void setFirstItemIndex(int firstItemIndex) {
        this.firstItemIndex = firstItemIndex;
    }

    public void reset() {
        mItemCount = 0;
        mItemOffsetY = null;
        scrollIsComputed = false;
        mHeight = 0;
    }
}
